package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;

public class GenericCommandCore extends CommandCore<VersatiaGenericCommand> {
	private int argsOffset;
	public GenericCommandCore(VersatiaModule module, VersatiaGenericCommand descriptor, int argsOffset) {
		super(module, descriptor);
		this.argsOffset = argsOffset;
	}

	@Override
	public boolean passedExecutionConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
		return descriptor.invoked(new CommandContextImpl(module, commandSender, args, argsOffset));
	}

	@Override
	public List<String> passedTabCompletionConditionsCheck(CommandSender commandSender, String accessor, String[] args, Location location) {
		return descriptor.tabComplete(new TabCompletionContextImpl(commandSender, args, argsOffset, location));
	}
}