package com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.github.marcoral.versatia.core.Initializer;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;

public class ModuleSubmodulesManager {
    private List<VersatiaSubmodule> setupOrder = new ArrayList<>();
    private Map<String, VersatiaSubmodule> reloadableSubmodules = new HashMap<>();
    private Map<String, Set<String>> reloadableGroups = new HashMap<>();

    private final String parentModuleName;
    public ModuleSubmodulesManager(String parentModuleName) {
    	this.parentModuleName = parentModuleName;
    }
    
    public void addSubmodule(VersatiaSubmodule submodule) {
    	String name = submodule.getName();
		Initializer.logIfPossible(logger -> logger.finer("RequestedSubmoduleAdd", parentModuleName, name));
    	validateNameNotReserved(name);
    	try {
    		submodule.load();
    		Initializer.logIfPossible(logger -> logger.finer("SubmoduleAddedSuccessfully", parentModuleName, name));
        	reloadableSubmodules.put(name, submodule);
            setupOrder.add(submodule);
    	} catch(Exception e) {
    		Initializer.logIfPossible(logger -> logger.severe("SubmoduleAddingError", parentModuleName, name));
    		e.printStackTrace();
    		return;
    	}
    }

    public void groupSubmodules(String groupKey, String... submodulesNames) {
		Initializer.logIfPossible(logger -> logger.finest("RequestedSubmodulesGroupAdd", parentModuleName, groupKey));
    	validateNameNotReserved(groupKey);
    	Set<String> members = new HashSet<>(Arrays.asList(submodulesNames));
    	members.remove(groupKey);
    	reloadableGroups.put(groupKey, members);
		Initializer.logIfPossible(logger -> logger.fine("SubmodulesGroupFormed", parentModuleName, groupKey));
    }

    private void validateNameNotReserved(String name) {
    	if(reloadableSubmodules.containsKey(name))
    		throw new RuntimeException(String.format("There already exists submodule with reload key: %s!", name));
    	if(reloadableGroups.containsKey(name))
    		throw new RuntimeException(String.format("There already exists submodules group with reload key: %s!", name));
    }
    
    public VersatiaModuleReloadResult reloadEverySubmodule() {
    	Initializer.logIfPossible(logger -> logger.finest("SubmoduleReloadEveryRequest", parentModuleName));
        VersatiaModuleReloadResultImpl result = new VersatiaModuleReloadResultImpl();
        reloadableSubmodules.forEach((submoduleName, submodule) -> handleSubmoduleReload(submodule, result));
        return result;
    }

    public VersatiaModuleReloadResultImpl reloadSubmodules(String... submodulesNames) {
        VersatiaModuleReloadResultImpl result = new VersatiaModuleReloadResultImpl();
        Iterator<String> iterator = Arrays.asList(submodulesNames).iterator();
        
        Set<String> namesToReload = new HashSet<>();
        while(iterator.hasNext()) {
            String key = iterator.next();
            Set<String> namesSet = reloadableGroups.get(key);
            if(namesSet != null)
                namesToReload.addAll(namesSet);
            else if(reloadableSubmodules.containsKey(key))
            	namesToReload.add(key);
            else {
            	result.unknownName(key);
    			Initializer.logIfPossible(logger -> logger.warning("SubmoduleUnknown", parentModuleName, key));
            }
        }
        
        Set<VersatiaSubmodule> orderedSubmodulesToReload = new LinkedHashSet<>();
		for(VersatiaSubmodule submodule : setupOrder) {
        	String submoduleName = submodule.getName();
        	if(namesToReload.remove(submoduleName)) {
        		submodule.unload();
        		orderedSubmodulesToReload.add(submodule);
        	}
        }
        
        orderedSubmodulesToReload.forEach(submodule -> handleSubmoduleReload(submodule, result));
        return result;
    }
    
    private void handleSubmoduleReload(VersatiaSubmodule submodule, VersatiaModuleReloadResultImpl result) {
    	String name = submodule.getName();
    	try {
			submodule.reload();
			result.reloaded(name);
			Initializer.logIfPossible(logger -> logger.fine("SubmoduleReloaded", parentModuleName, name));
		} catch (Exception e) {
			result.reloadingError(name);
			Initializer.logIfPossible(logger -> logger.error("SubmoduleReloadingError", parentModuleName, name));
			e.printStackTrace();
		}
    }

    public void shutdown() {
        //Shutdown modules in order reverse to initialization
        ListIterator<VersatiaSubmodule> submodulesIterator = setupOrder.listIterator(setupOrder.size());
        while(submodulesIterator.hasPrevious()) {
        	VersatiaSubmodule submodule = submodulesIterator.previous();
			try {
				submodule.shutdown();
			} catch (Exception e) {
				Initializer.logIfPossible(logger -> logger.error("SubmoduleShuttingDownError", parentModuleName, submodule.getName()));
				e.printStackTrace();
			}
        }
    }

	public void validate() {
		reloadableGroups.forEach((groupName, groupMembers) -> {
			for(Iterator<String> membersIterator = groupMembers.iterator(); membersIterator.hasNext();) {
				String groupMember = membersIterator.next();
				if(!reloadableSubmodules.containsKey(groupMember)) {
					membersIterator.remove();
					Initializer.logIfPossible(logger -> logger.warning("UnknownSubmoduleInGroup", parentModuleName, groupName, groupMember));
				}
			}
		});
	}
}
