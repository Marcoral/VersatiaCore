package com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.Initializer;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.modules.VersatiaSubmoduleHandlerProvider;

public class ModuleSubmodulesManager {
	private static final Map<Class<? extends VersatiaSubmoduleHandlerProvider>, VersatiaSubmoduleHandlerProvider> specialSubmodules = new HashMap<>();
	private static final Map<String, List<Class<? extends VersatiaSubmoduleHandlerProvider>>> specialSubmodulesRegisterers = new HashMap<>();
	public static <T extends VersatiaSubmoduleHandlerProvider> void registerSubmoduleHandlerProvider(Class<T> interfaceClass, T provider) {
		VersatiaSubmoduleHandlerProvider currentProvider = specialSubmodules.get(interfaceClass);
		if(currentProvider != null)
			throw new RuntimeException(String.format("Provider %s already registered! Provider classes: \n%s\nand\n%s", currentProvider, provider));
		String registererName = provider.getRegisterer().getName();
		specialSubmodules.put(interfaceClass, provider);
		specialSubmodulesRegisterers.computeIfAbsent(registererName, key -> new LinkedList<>())
			.add(interfaceClass);
	}
	
	public static void unregisterEverySubmoduleHandlerProviders(Plugin registerer) {
		String registererName = registerer.getName();
		List<Class<? extends VersatiaSubmoduleHandlerProvider>> registeredInterfaces = specialSubmodulesRegisterers.get(registererName);
		if(registeredInterfaces == null)
			return;
		registeredInterfaces.forEach(interfaceClass -> specialSubmodules.remove(interfaceClass));
		specialSubmodulesRegisterers.remove(registererName);
	}
	
    private List<VersatiaSubmodule> setupOrder = new ArrayList<>();
	private Set<String> reservedNames = new HashSet<>();
    private Map<String, VersatiaSubmodule> reloadableSubmodules = new HashMap<>();
    private Map<String, Set<String>> reloadableGroups = new HashMap<>();

    private final VersatiaModule parentModule;
    public ModuleSubmodulesManager(VersatiaModule parentModule) {
    	this.parentModule = parentModule;
    }
    
    public void addSubmodule(VersatiaSubmodule submodule) {
    	String moduleName = parentModule.getCorrespondingPlugin().getName();
    	String name = submodule.getName();
		Initializer.logIfPossible(logger -> logger.finer("RequestedSubmoduleAdd", moduleName, name));
    	validateNameNotReserved(name);
    	try {
    		submodule.load();
    		Initializer.logIfPossible(logger -> logger.finer("SubmoduleAddedSuccessfully", moduleName, name));
        	reloadableSubmodules.put(name, submodule);
            setupOrder.add(submodule);
            reservedNames.add(name);
    	} catch(Exception e) {
    		Initializer.logIfPossible(logger -> logger.severe("SubmoduleAddingError", moduleName, name));
    		e.printStackTrace();
    		return;
    	}
    }
    
	@SuppressWarnings("unchecked")
	public <T extends VersatiaSubmodule> T addServicedSubmodule(Class<? extends VersatiaSubmoduleHandlerProvider> handlerClass, boolean hardDepend) {
		VersatiaSubmoduleHandlerProvider handler = specialSubmodules.get(handlerClass);
		if(handler == null)
			if(hardDepend)
				throw new NullPointerException("No submodule handler provider for %s have been found!");
			else
				return null;
		VersatiaSubmodule submodule = handler.createHandler(parentModule);
		addSubmodule(submodule);
		return (T) submodule;
	}

    public void groupSubmodules(String groupKey, String... submodulesNames) {
    	String moduleName = parentModule.getCorrespondingPlugin().getName();
		Initializer.logIfPossible(logger -> logger.finest("RequestedSubmodulesGroupAdd", moduleName, groupKey));
    	validateNameNotReserved(groupKey);
    	Set<String> members = new HashSet<>(Arrays.asList(submodulesNames));
    	members.remove(groupKey);
    	reloadableGroups.put(groupKey, members);
    	reservedNames.add(groupKey);
		Initializer.logIfPossible(logger -> logger.fine("SubmodulesGroupFormed", moduleName, groupKey));
    }

    private void validateNameNotReserved(String name) {
    	if(reloadableSubmodules.containsKey(name))
    		throw new RuntimeException(String.format("There already exists submodule with reload key: %s!", name));
    	if(reloadableGroups.containsKey(name))
    		throw new RuntimeException(String.format("There already exists submodules group with reload key: %s!", name));
    }
    
    public VersatiaModuleReloadResult reloadEverySubmodule() {
    	String moduleName = parentModule.getCorrespondingPlugin().getName();
    	Initializer.logIfPossible(logger -> logger.finest("SubmoduleReloadEveryRequest", moduleName));
        VersatiaModuleReloadResultImpl result = new VersatiaModuleReloadResultImpl();
        reloadableSubmodules.forEach((submoduleName, submodule) -> handleSubmoduleReload(submodule, result));
        return result;
    }
    
	public Set<String> getReloadableNames() {
		return Collections.unmodifiableSet(reservedNames);
	}

    public VersatiaModuleReloadResultImpl reloadSubmodules(String... submodulesNames) {
    	String moduleName = parentModule.getCorrespondingPlugin().getName();
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
    			Initializer.logIfPossible(logger -> logger.warning("SubmoduleUnknown", moduleName, key));
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
    	String moduleName = parentModule.getCorrespondingPlugin().getName();
    	String name = submodule.getName();
    	try {
    		submodule.unload();
			submodule.reload();
			result.reloaded(name);
			Initializer.logIfPossible(logger -> logger.fine("SubmoduleReloaded", moduleName, name));
		} catch (Exception e) {
			result.reloadingError(name);
			Initializer.logIfPossible(logger -> logger.error("SubmoduleReloadingError", moduleName, name));
			e.printStackTrace();
		}
    }

    public void shutdown() {
    	String moduleName = parentModule.getCorrespondingPlugin().getName();
        //Shutdown modules in order reverse to initialization
        ListIterator<VersatiaSubmodule> submodulesIterator = setupOrder.listIterator(setupOrder.size());
        while(submodulesIterator.hasPrevious()) {
        	VersatiaSubmodule submodule = submodulesIterator.previous();
			try {
				submodule.shutdown();
			} catch (Exception e) {
				Initializer.logIfPossible(logger -> logger.error("SubmoduleShuttingDownError", moduleName, submodule.getName()));
				e.printStackTrace();
			}
        }
    }

	public void validate() {
    	String moduleName = parentModule.getCorrespondingPlugin().getName();
		reloadableGroups.forEach((groupName, groupMembers) -> {
			for(Iterator<String> membersIterator = groupMembers.iterator(); membersIterator.hasNext();) {
				String groupMember = membersIterator.next();
				if(!reloadableSubmodules.containsKey(groupMember)) {
					membersIterator.remove();
					Initializer.logIfPossible(logger -> logger.warning("UnknownSubmoduleInGroup", moduleName, groupName, groupMember));
				}
			}
		});
	}
}
