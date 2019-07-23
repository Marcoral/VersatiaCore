package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageDescriptor;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;

public class CommandContextImpl extends GenericContext implements VersatiaCommandContext {
	protected final VersatiaModule module;
    public CommandContextImpl(VersatiaModule module, CommandSender executor, String[] args, int argsOffset) {
    	super(executor, args, argsOffset);
    	this.module = module;
    }

	@Override
	public void replyToExecutor(String messageTemplateKey, String... args) {
		VersatiaMessages.sendVersatiaMessageToCommandSender(executor, module.getMessageTemplate(messageTemplateKey), args);
	}

	@Override
	public void replyToExecutor(VersatiaMessageDescriptor messageDescriptor, String... args) {
		VersatiaMessages.sendVersatiaMessageToCommandSender(executor, messageDescriptor.supplyMessageTemplate(), args);
	}
}