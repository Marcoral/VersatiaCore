package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.Map;

import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;

public class CommandFamily {
	private final CommandFamilyCore core;
    public CommandFamily(CommandFamilyCore core) {
    	this.core = core;
    }

    public void addChild(CommandCore<?> command) {
    	VersatiaCommand descriptor = command.getDescriptor();
    	String commandName = descriptor.getName();
    	Map<String, CommandCore<?>> rootChildren = core.getRootChildren();
    	Map<String, CommandCore<?>> children = core.getChildren();
		if(children.containsKey(commandName))
		    throw new IllegalArgumentException(String.format("There already exist command with name \"%s\"!", commandName));
		children.put(commandName, command);
		rootChildren.put(commandName, command);
		for(String alias : descriptor.getAliases()) {
			if(children.containsKey(alias))
			    throw new IllegalArgumentException(String.format("There already exist command with alias \"%s\"!", alias));
			children.put(alias, command);
		}
    }

	public int getNestingLevel() {
		return core.getNestingLevel();
	}
	
	public CommandFamilyCore getCore() {
		return core;
	}
}