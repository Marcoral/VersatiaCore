package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

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
	public boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
		return descriptor.invoked(new CommandContextImpl(module, commandSender, args, argsOffset));
	}
}