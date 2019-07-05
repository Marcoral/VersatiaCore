package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.colors.VersatiaChat;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.impl.tools.CollectionsUtils;

public abstract class CommandCore {
	private final VersatiaModule module;
    private String permission;
    private String description;
    private String[] usageHint;
    private String[] usageFlags;
    private List<String> currentAliases = new ArrayList<>();

    public CommandCore(VersatiaModule module) {
    	this.module = module;
    }
    
    protected VersatiaModule getModule() {
    	return module;
    }
    
    public boolean execute(CommandSender commandSender, String accessor, String[] args) {
        if(!handleConditionsCheck(commandSender, accessor, args))
            return true;
        boolean toDisplayHint = !passedConditionsCheck(commandSender, accessor, args);
        if(toDisplayHint) {
        	if(usageHint == null)
        		VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, VersatiaModules.getModule(VersatiaConstants.VERSATIA_CORE_NAME).getMessageTemplate("GenericErrorIncorrectUse"));
        	else {
        		VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, VersatiaModules.getModule(VersatiaConstants.VERSATIA_CORE_NAME).getMessageTemplate("GenericErrorIncorrectUseInfo"));
        		for(String hintTemplate : usageHint)
            		VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, module.getMessageTemplate(hintTemplate));
        	}
        	
        	if(usageFlags != null) {
        		VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, VersatiaModules.getModule(VersatiaConstants.VERSATIA_CORE_NAME).getMessageTemplate("GenericErrorIncorrectUseAvailableFlags"));
        		for(String flagTemplate : usageFlags)
            		VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, module.getMessageTemplate(flagTemplate));
        	}
        }
        return true;
    }

    protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
        if(permission == null || commandSender.hasPermission(permission))
            return true;
        VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, VersatiaModules.getModule(VersatiaConstants.VERSATIA_CORE_NAME).getMessageTemplate("GenericErrorNoPermission"));
        return false;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
    
	public void setDescription(String descriptionMessageTemplate) {
		this.description = descriptionMessageTemplate;
	}
	
	public void setUsageHint(String... hintMessageTemplates) {
		this.usageHint = hintMessageTemplates;
	}
	
	public void setUsageFlags(String... flagsMessageTemplates) {
		this.usageFlags = flagsMessageTemplates;		
	}
	
	public String getPermission() {
		return permission;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<String> getAliases() {
		return currentAliases;
	}

    public final void setAliases(String... aliases) {
        List<String> secondList = Arrays.stream(aliases).map(String::toLowerCase).collect(Collectors.toList());
        Collection<String> removedAliases = CollectionsUtils.collectionMinus(currentAliases, secondList);
        Collection<String> addedAliases = CollectionsUtils.collectionMinus(secondList, currentAliases);
        try {
            onAliasesChanged(removedAliases, addedAliases);
            this.currentAliases = secondList;
        } catch(AliasAlreadyExistsException e) {
            CommandTools.throwElementAlreadyExistsException(e.getAlias());
        }
    }

    protected abstract void onAliasesChanged(Collection<String> removedAliases, Collection<String> addedAliases) throws AliasAlreadyExistsException;
    protected abstract boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args);

    public static class AliasAlreadyExistsException extends Exception {
		private static final long serialVersionUID = 1L;
		private final String alias;
        public AliasAlreadyExistsException(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }
}
