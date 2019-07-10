package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;

public class CommandFamilyCore extends CommandCore<VersatiaCommand> {
	private Map<String, CommandCore<?>> rootChildren = new HashMap<>();
	private Map<String, CommandCore<?>> children = new HashMap<>();
	private int nestingLevel;
    public CommandFamilyCore(VersatiaModule module, VersatiaCommand descriptor, int nestingLevel) {
		super(module, descriptor);
		this.nestingLevel = nestingLevel;
	}

	public Map<String, CommandCore<?>> getRootChildren() {
		return rootChildren;
	}

	public Map<String, CommandCore<?>> getChildren() {
		return children;
	}
	
	public int getNestingLevel() {
		return nestingLevel;
	}

	@Override
    protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
        if(!super.handleConditionsCheck(commandSender, accessor, args))
            return false;
        if(args.length <= nestingLevel) {
        	printErrorMessage(commandSender, "CommandUseErrorNoArguments");
            return false;
        }
        if(!children.containsKey(args[nestingLevel].toLowerCase())) {
        	printErrorMessage(commandSender, "CommandUseErrorNoCommand");
            return false;
        }
        return true;
    }
    
    private void printErrorMessage(CommandSender commandSender, String templateEntryKey) {
    	VersatiaModule versatia = VersatiaConstants.VERSATIA;
    	VersatiaMessageEntry entry = versatia.getMessageTemplateEntry(templateEntryKey);
    	VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, entry.getTemplateString());
    	if((Boolean) entry.getMetadataObjectOrThrow("PrintCommands")) {
    		rootChildren.forEach((name, child) -> {
    			VersatiaCommand descriptor = child.getDescriptor();
    			String requiredPermission = descriptor.getPermission();
    			if(requiredPermission != null
    					&& !(Boolean) entry.getMetadataObjectOrThrow("PrintForbiddenCommands")
    					&& !commandSender.hasPermission(requiredPermission))
    				return;
    			String displayedName = formatCommandDisplay(name, descriptor);
    			String description = descriptor.getDescription();
    			if(description == null)
    				VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, versatia.getMessageTemplate("CommandUseDescriptionFormat"), displayedName);
    			else
    				VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, versatia.getMessageTemplate("CommandUseRichDescriptionFormat"), displayedName, module.getMessageTemplate(description));
    		});
    	}
    }
    
    private String formatCommandDisplay(String commandName, VersatiaCommand descriptor) {
		Collection<String> aliases = Arrays.asList(descriptor.getAliases());
		if(aliases.size() == 0)
			return commandName;
		StringBuilder result = new StringBuilder(commandName).append(" (");
		Iterator<String> aliasesIterator = aliases.iterator();
		for(int i = 0; i < aliases.size() - 1; ++i)
			result.append(aliasesIterator.next()).append(", ");
		result.append(aliasesIterator.next()).append(")");
		return result.toString();
    }
	
	@Override
	public boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
        return children.get(args[nestingLevel].toLowerCase()).execute(commandSender, accessor, args);
	}
}
