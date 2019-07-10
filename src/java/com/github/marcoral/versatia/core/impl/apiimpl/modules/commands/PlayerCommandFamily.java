package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;

public class PlayerCommandFamily extends CommandFamily {
	public PlayerCommandFamily(VersatiaModule module, VersatiaCommand descriptor, int nestingLevel) {
		super(new CommandFamilyCore(module, descriptor, nestingLevel) {
			@Override
			protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {				
		        return CommandTools.handleConditionCheckExecutorIsPlayer(commandSender)
		        		&& super.handleConditionsCheck(commandSender, accessor, args);
			}
		});
	}
}
