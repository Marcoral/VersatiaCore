package com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;

public class ModuleSubmodulesManager {
    private List<VersatiaSubmodule> setupOrder = new ArrayList<>();
    private Map<String, VersatiaSubmodule> reloadableSubmodules = new HashMap<>();
    private Map<String, VersatiaSubmodule[]> reloadableGroups = new HashMap<>();

    public void addSubmodule(String reloadKey, VersatiaSubmodule submodule) {
    	if(reloadableSubmodules.containsKey(reloadKey))
    		throw new RuntimeException(String.format("There already exists submodule with reload key: %s!", reloadKey));
        reloadableSubmodules.put(reloadKey, submodule);
        setupOrder.add(submodule);
    }

    public void groupSubmodules(String groupKey, VersatiaSubmodule[] submodules) {
    	reloadableGroups.put(groupKey, submodules);
    }
    
    public VersatiaModuleReloadResult reloadEverySubmodule() {
        VersatiaModuleReloadResultImpl result = new VersatiaModuleReloadResultImpl();
        reloadableSubmodules.values().forEach(VersatiaSubmodule::reload);
        reloadableSubmodules.keySet().forEach(result::reloaded);
        return result;
    }

    public VersatiaModuleReloadResultImpl reloadSubmodules(String... submodules) {
        VersatiaModuleReloadResultImpl result = new VersatiaModuleReloadResultImpl();
        Iterator<String> iterator = Arrays.asList(submodules).iterator();
        Set<VersatiaSubmodule> modulesToReload = new LinkedHashSet<>();
        while(iterator.hasNext()) {
            String key = iterator.next();
            VersatiaSubmodule[] assignedGroup = reloadableGroups.get(key);
            if(assignedGroup != null)
                for(VersatiaSubmodule submodule : assignedGroup) {
                    modulesToReload.add(submodule);
                    result.reloaded(key);
                }
            else {
                VersatiaSubmodule submodule = reloadableSubmodules.get(key);
                if(submodule == null)
                    result.unknownName(key);
                else {
                    result.reloaded(key);
                    modulesToReload.add(submodule);
                }
            }
        }
        modulesToReload.forEach(VersatiaSubmodule::reload);
        return result;
    }

    public void shutdown() {
        //Shutdown modules in order reverse to initialization
        ListIterator<VersatiaSubmodule> submodulesIterator = setupOrder.listIterator(setupOrder.size());
        while(submodulesIterator.hasPrevious())
            submodulesIterator.previous().shutdown();
    }
}
