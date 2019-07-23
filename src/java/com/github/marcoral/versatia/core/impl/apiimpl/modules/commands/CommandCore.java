package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;

public abstract class CommandCore<T extends VersatiaCommand> {
	protected final VersatiaModule module;
	protected final T descriptor;

    public CommandCore(VersatiaModule module, T descriptor) {
    	this.module = module;
    	this.descriptor = descriptor;
    }
    
    public final boolean execute(CommandSender commandSender, String accessor, String[] args) {
        if(!handleConditionsCheck(commandSender, accessor, args))
            return true;
        boolean toDisplayHint = !passedExecutionConditionsCheck(commandSender, accessor, args);
        if(toDisplayHint) {
        	String[] usageHints = descriptor.getUsageHints();
        	if(usageHints == null)
        		VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, VersatiaConstants.VERSATIA.getMessageTemplate("CommandUseErrorIncorrectUse"));
        	else {
        		VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, VersatiaConstants.VERSATIA.getMessageTemplate("CommandUseErrorIncorrectUseInfo"));
        		for(String hintTemplate : usageHints)
            		VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, module.getMessageTemplate(hintTemplate));
        	}
        	
        	String[] usageFlags = descriptor.getUsageFlags();
        	if(usageFlags != null) {
        		VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, VersatiaConstants.VERSATIA.getMessageTemplate("CommandUseErrorIncorrectUseAvailableFlags"));
        		for(String flagTemplate : usageFlags)
            		VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, module.getMessageTemplate(flagTemplate));
        	}
        }
        return true;
    }
    
	public List<String> tabComplete(CommandSender commandSender, String lowerCase, String[] args, Location location) {
        if(!handleTabCompletionConditionsCheck(commandSender, lowerCase, args, location))
            return new ArrayList<>();
        else
        	return passedTabCompletionConditionsCheck(commandSender, lowerCase, args, location);
	}

    protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
    	String permission = descriptor.getPermission();
        if(permission == null || commandSender.hasPermission(permission))
            return true;
        VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, VersatiaConstants.VERSATIA.getMessageTemplate("CommandUseErrorNoPermission"));
        return false;
    }
    
    protected boolean handleTabCompletionConditionsCheck(CommandSender commandSender, String accessor, String[] args, Location location) {
    	String permission = descriptor.getPermission();
        if(permission == null || commandSender.hasPermission(permission))
            return true;
        return false;
    }

    public abstract boolean passedExecutionConditionsCheck(CommandSender commandSender, String accessor, String[] args);
    public abstract List<String> passedTabCompletionConditionsCheck(CommandSender commandSender, String accessor, String[] args, Location location);
    
    public T getDescriptor() {
    	return descriptor;
    }
}
