package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.HashMap;
import java.util.Map;

import com.github.marcoral.versatia.core.Initializer;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandFamilyBuilder;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandsManager.RegisteringResult;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders.GenericCommandFamilyBuilder;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders.PlayerCommandFamilyBuilder;

public class ModuleCommandsManager {
    private Map<String, CommandRoot> registeredCommands = new HashMap<>();
    
    private final VersatiaModule parent;
    public ModuleCommandsManager(VersatiaModule parent) {
    	this.parent = parent;
    }

    public void registerGenericCommand(VersatiaGenericCommand command, CommandPriority priority) {
    	registerCommandAndAliases(priority, new GenericCommandCore(parent, command, 0));
    }

    public void registerPlayerOnlyCommand(VersatiaPlayerCommand command, CommandPriority priority) {
    	registerCommandAndAliases(priority, new PlayerCommandCore(parent, command, 0));
    }

    public VersatiaCommandFamilyBuilder registerGenericCommandsFamily(VersatiaCommand descriptor, CommandPriority priority) {
    	GenericCommandFamily family = new GenericCommandFamily(parent, descriptor, 0);
    	registerCommandAndAliases(priority, family.getCore());
    	return new GenericCommandFamilyBuilder(parent, family);
    }

    public VersatiaPlayerCommandFamilyBuilder registerPlayerOnlyCommandsFamily(VersatiaCommand descriptor, CommandPriority priority) {
    	PlayerCommandFamily family = new PlayerCommandFamily(parent, descriptor, 0);
    	registerCommandAndAliases(priority, family.getCore());
    	return new PlayerCommandFamilyBuilder(parent, family);
    }
    
	public void unregisterRootCommand(String name) {
		CommandRoot command = registeredCommands.remove(name);
		if(command != null)
			CommandsManager.unregisterCommand(command);
	}

    public void shutdown() {
        registeredCommands.values().forEach(CommandsManager::unregisterCommand);
    }
    
    private void registerCommandAndAliases(CommandPriority priority, CommandCore<?> commandCore) {
    	VersatiaCommand descriptor = commandCore.getDescriptor();
    	CommandRoot commandRoot = new CommandRoot(priority, descriptor.getName(), commandCore, parent);
    	registerCommandAccessor(priority, commandRoot);
    	for(String alias : descriptor.getAliases())
    		registerCommandAccessor(priority, new CommandRoot(priority, alias, commandCore, parent));
    }
    
    private void registerCommandAccessor(CommandPriority priority, CommandRoot commandRoot) {
    	RegisteringResult result = CommandsManager.registerCommand(commandRoot);
    	switch (result) {
			case FAILED_LOW_PRIORITY:
				Initializer.logIfPossible(logger -> logger.warning("CommandRegisterErrorLowPriority", commandRoot.getName()));
				break;
			case SUCCESS:
				Initializer.logIfPossible(logger -> logger.fine("CommandRegisterSuccess", commandRoot.getName()));
				break;
			case SUCCESS_OVERRIDED_LOWER_PRIORITY:
				Initializer.logIfPossible(logger -> logger.info("CommandRegisterOverridenAnother", commandRoot.getName()));
				break;
			default:
				break;
		}
    }
}
