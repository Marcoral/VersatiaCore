package com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;

public class VersatiaModuleReloadResultImpl implements VersatiaModuleReloadResult {
    private Set<String> reloadedSubmodulesNames = new HashSet<>();
    private Set<String> errorSubmodulesNames = new HashSet<>();
    private Set<String> unknownSubmodulesNames = new HashSet<>();
    private Set<String> reloadedSubmodulesNamesUnmodifable = Collections.unmodifiableSet(reloadedSubmodulesNames);
    private Set<String> unknownSubmodulesNamesUnmodifable = Collections.unmodifiableSet(unknownSubmodulesNames);

    @Override
    public Set<String> getReloadedSubmodulesNames() {
        return reloadedSubmodulesNamesUnmodifable;
    }
    
    @Override
    public Set<String> getSubmodulesNamesReloadingError() {
    	return errorSubmodulesNames;
    }

    @Override
    public Set<String> getUnknownSubmodulesNames() {
        return unknownSubmodulesNamesUnmodifable;
    }

    public void reloaded(String key) {
        reloadedSubmodulesNames.add(key);
    }
    
    public void reloadingError(String key) {
    	errorSubmodulesNames.add(key);
    }
    
    public void unknownName(String key) {
        unknownSubmodulesNames.add(key);
    }
}