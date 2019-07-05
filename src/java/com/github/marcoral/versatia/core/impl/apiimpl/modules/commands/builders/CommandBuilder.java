package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders;


import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandBuilder;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandCore;

public class CommandBuilder implements VersatiaCommandBuilder {
    private final CommandCore command;
    public CommandBuilder(CommandCore command) {
        this.command = command;
    }

    @Override
    public VersatiaCommandBuilder withPermission(String permission) {
        command.setPermission(permission);
        return this;
    }

    @Override
    public VersatiaCommandBuilder withAliases(String... aliases) {
        getObjectBeingBuilt().setAliases(aliases);
        return this;
    }
    
    @Override
    public VersatiaCommandBuilder withDescription(String description) {
        getObjectBeingBuilt().setDescription(description);
        return this;
    }
    
    @Override
    public VersatiaCommandBuilder withUsageHint(String... hintMessageTemplates) {
        getObjectBeingBuilt().setUsageHint(hintMessageTemplates);
        return this;
    }
    
	@Override
	public VersatiaCommandBuilder withUsageFlags(String... flagsMessageTemplates) {
        getObjectBeingBuilt().setUsageFlags(flagsMessageTemplates);
		return this;
	}

    protected CommandCore getObjectBeingBuilt() {
        return command;
    }
}