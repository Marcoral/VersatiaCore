package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.Collection;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;

public abstract class AbstractIntermediateCommand extends CommandCore {
    private final AbstractCommandFamily parent;
    public AbstractIntermediateCommand(VersatiaModule module, AbstractCommandFamily parent) {
    	super(module);
        this.parent = parent;
    }

    @Override
    protected void onAliasesChanged(Collection<String> removedAliases, Collection<String> addedAliases) throws AliasAlreadyExistsException {
        parent.modifyChildren(removedAliases, addedAliases, () -> this);
    }
}