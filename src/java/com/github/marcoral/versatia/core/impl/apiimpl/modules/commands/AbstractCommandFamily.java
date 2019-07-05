package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.colors.VersatiaChat;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;

public abstract class AbstractCommandFamily extends CommandCore {
    private final int nestingLevel;
    private Map<String, CommandCore> children = new HashMap<>();
    private Map<String, CommandCore> rootChildren = new HashMap<>();

    public AbstractCommandFamily(VersatiaModule module, int nestingLevel) {
    	super(module);
        this.nestingLevel = nestingLevel;
    }

    protected Map<String, CommandCore> getChildren() {
        return children;
    }

    public int getNestingLevel() {
        return nestingLevel;
    }

    public <T extends CommandCore> T addChild(String name, Supplier<T> implementationSupplier) throws AliasAlreadyExistsException {
		if(children.containsKey(name))
		    throw new CommandCore.AliasAlreadyExistsException(name);
		T command = implementationSupplier.get();
		children.put(name, command);
		rootChildren.put(name, command);
		return command;
    }

    public void modifyChildren(Collection<String> removedAliases, Collection<String> addedAliases, Supplier<CommandCore> implementationSupplier) {
        try {
            CommandTools.onAliasesChanged(removedAliases, addedAliases, getChildren(), alias -> implementationSupplier.get(), c -> {}, c -> {});
        } catch (AliasAlreadyExistsException e) {
            CommandTools.throwElementAlreadyExistsException(e.getAlias());
        }
    }

    @Override
    protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
        if(!super.handleConditionsCheck(commandSender, accessor, args))
            return false;
        if(args.length <= nestingLevel) {
        	printErrorMessage(commandSender, "GenericErrorNoArguments");
            return false;
        }
        if(!children.containsKey(args[nestingLevel].toLowerCase())) {
        	printErrorMessage(commandSender, "GenericErrorNoCommand");
            return false;
        }
        return true;
    }

    @Override
    protected boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
        return children.get(args[nestingLevel].toLowerCase()).execute(commandSender, accessor, args);
    }
    
    private void printErrorMessage(CommandSender commandSender, String templateEntryKey) {
    	VersatiaModule versatia = VersatiaModules.getModule(VersatiaConstants.VERSATIA_CORE_NAME);
    	VersatiaMessageEntry entry = versatia.getMessageTemplateEntry(templateEntryKey);
    	VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, entry.getTemplateString());
    	if((Boolean) entry.getMetadataObjectOrThrow("PrintCommands")) {
    		rootChildren.forEach((name, child) -> {
    			String requiredPermission = child.getPermission();
    			if(requiredPermission != null
    					&& !(Boolean) entry.getMetadataObjectOrThrow("PrintForbiddenCommands")
    					&& !commandSender.hasPermission(requiredPermission))
    				return;
    			String displayedName = formatCommandDisplay(name, child);
    			String description = child.getDescription();
    			if(description == null)
    				VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, versatia.getMessageTemplate("CommandsDescriptionFormat"), displayedName);
    			else
    				VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, versatia.getMessageTemplate("CommandsRichDescriptionFormat"), displayedName, getModule().getMessageTemplate(description));
    		});
    	}
    }
    
    private String formatCommandDisplay(String commandName, CommandCore core) {
		Collection<String> aliases = core.getAliases();
		if(aliases.size() == 0)
			return commandName;
		StringBuilder result = new StringBuilder(commandName).append(" (");
		Iterator<String> aliasesIterator = aliases.iterator();
		for(int i = 0; i < aliases.size() - 1; ++i)
			result.append(aliasesIterator.next()).append(", ");
		result.append(aliasesIterator.next()).append(")");
		return result.toString();
    }
}