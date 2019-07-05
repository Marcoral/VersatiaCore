package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.colors.VersatiaChat;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;

public class CommandContextImpl implements VersatiaCommandContext {
	private VersatiaModule module;
    private CommandSender executor;
    private String[] args;
    private int argsOffset;
    public CommandContextImpl(VersatiaModule module, CommandSender executor, String[] args, int argsOffset) {
    	this.module = module;
        this.executor = executor;
        this.args = args;
        this.argsOffset = argsOffset;
    }

    @Override
    public int getArgsCount() {
        return args.length - argsOffset;
    }

    @Override
    public int getFamilyNameAccessorsUsedCount() {
        return argsOffset;
    }

    @Override
    public String getArgument(int index) {
        if(index < 0)
            throw new ArrayIndexOutOfBoundsException();
        return args[index + argsOffset];
    }

    @Override
    public String getFamilyNameAccessorUsed(int index) {
        if(index >= argsOffset || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        return args[index];
    }

    @Override
    public CommandSender getExecutor() {
        return executor;
    }

	@Override
	public void replyToExecutor(String messageTemplateKey, String... args) {
		VersatiaChat.sendVersatiaMessageToCommandSender(executor, module.getMessageTemplate(messageTemplateKey), args);
	}
}