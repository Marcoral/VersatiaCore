package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandHandler;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandHandler;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders.CommandBuilder;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders.GenericCommandFamilyBuilder;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders.PlayerCommandFamilyBuilder;

public class ModuleCommandsManager {
    private Map<String, CommandRoot<?>> registeredCommands = new HashMap<>();
    
    private final VersatiaModule parent;
    public ModuleCommandsManager(VersatiaModule parent) {
    	this.parent = parent;
    }

    public VersatiaCommandBuilder registerGenericCommand(String commandName, VersatiaCommandHandler handler, CommandPriority priority, Consumer<VersatiaCommandBuilder> registerer) {
    	CommandRoot<RootGenericCommand> command = registerCommand(commandName, priority, () -> new RootGenericCommand(priority, handler));
        return new CommandBuilder(command.getCore());
    }

    public VersatiaCommandBuilder registerPlayerOnlyCommand(String commandName, VersatiaPlayerCommandHandler handler, CommandPriority priority, Consumer<VersatiaCommandBuilder> registerer) {
    	CommandRoot<RootPlayerCommand> command = registerCommand(commandName, priority, () -> new RootPlayerCommand(priority, handler));
        return new CommandBuilder(command.getCore());
    }

    public VersatiaCommandFamilyBuilder registerGenericCommandsFamily(String commandFamilyLabel, CommandPriority priority, Consumer<VersatiaCommandFamilyBuilder> registerer) {
    	CommandRoot<RootCommandFamily> command = registerCommand(commandFamilyLabel, priority, () -> new RootCommandFamily(priority));
        return new GenericCommandFamilyBuilder(parent, command.getCore());
    }

    public VersatiaPlayerCommandFamilyBuilder registerPlayerOnlyCommandsFamily(String commandFamilyLabel, CommandPriority priority, Consumer<VersatiaPlayerCommandFamilyBuilder> registerer) {
    	CommandRoot<RootPlayerCommandFamily> command = registerCommand(commandFamilyLabel, priority, () -> new RootPlayerCommandFamily(priority));
        return new PlayerCommandFamilyBuilder(parent, command.getCore());
    }

    private <T extends CommandCore> CommandRoot<T> registerCommand(String name, CommandPriority priority, Supplier<T> coreSupplier) {
        try {
        	if(registeredCommands.containsKey(name))
                throw new CommandCore.AliasAlreadyExistsException(name);
            CommandRoot<T> command = new CommandRoot<T>(name, priority, coreSupplier.get(), parent);
            registeredCommands.put(name, command);
            CommandsManager.registerCommand(command);
            return command;
        } catch (CommandCore.AliasAlreadyExistsException e) {
            CommandTools.throwElementAlreadyExistsException(name);
            return null;
        }
    }

    public void shutdown() {
        registeredCommands.values().forEach(CommandsManager::unregisterCommand);
    }
    
    private abstract class AbstractRootCommandCore extends CommandCore {
    	private final CommandPriority priority;
    	public AbstractRootCommandCore(CommandPriority priority) {
    		super(parent);
			this.priority = priority;
		}
    	
		@Override
		protected void onAliasesChanged(Collection<String> removedAliases, Collection<String> addedAliases) throws AliasAlreadyExistsException {
			CommandTools.onAliasesChanged(removedAliases, addedAliases, registeredCommands,
	               name -> new CommandRoot<>(name, priority, this, parent), CommandsManager::unregisterCommand, CommandsManager::registerCommand);
		}
    }
    
    private class RootGenericCommand extends AbstractRootCommandCore {
    	private VersatiaCommandHandler handler;
		public RootGenericCommand(CommandPriority priority, VersatiaCommandHandler handler) {
			super(priority);
			this.handler = handler;
		}

		@Override
		protected boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
			return handler.invoked(new CommandContextImpl(parent, commandSender, args, 0));
		}
    }
    
    private class RootPlayerCommand extends AbstractRootCommandCore {
    	private VersatiaPlayerCommandHandler handler;
		public RootPlayerCommand(CommandPriority priority, VersatiaPlayerCommandHandler handler) {
			super(priority);
			this.handler = handler;
		}

		@Override
		protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
			return CommandTools.handleConditionCheckExecutorIsPlayer(commandSender) && super.handleConditionsCheck(commandSender, accessor, args);
		}

		@Override
		protected boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
			return handler.invoked(new PlayerCommandContextImpl(parent, (Player) commandSender, args, 0));
		}
    }
    
    private class RootCommandFamily extends AbstractCommandFamily {
    	private final CommandPriority priority;
    	public RootCommandFamily(CommandPriority priority) {
    		super(parent, 0);
			this.priority = priority;
		}

		@Override
		protected void onAliasesChanged(Collection<String> removedAliases, Collection<String> addedAliases)	throws AliasAlreadyExistsException {
			CommandTools.onAliasesChanged(removedAliases, addedAliases, registeredCommands,
		               name -> new CommandRoot<>(name, priority, this, parent), CommandsManager::unregisterCommand, CommandsManager::registerCommand);
		}
    }

    private class RootPlayerCommandFamily extends AbstractCommandFamily {
		private final CommandPriority priority;
		public RootPlayerCommandFamily(CommandPriority priority) {
			super(parent, 0);
			this.priority = priority;
		}


		@Override
		protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
			return CommandTools.handleConditionCheckExecutorIsPlayer(commandSender) && super.handleConditionsCheck(commandSender, accessor, args);
		}

		@Override
		protected void onAliasesChanged(Collection<String> removedAliases, Collection<String> addedAliases)	throws AliasAlreadyExistsException {
			CommandTools.onAliasesChanged(removedAliases, addedAliases, registeredCommands,
					name -> new CommandRoot<>(name, priority, this, parent), CommandsManager::unregisterCommand, CommandsManager::registerCommand);
		}
	}
}