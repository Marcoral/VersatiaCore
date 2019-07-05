package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;

public class CommandRoot<T extends CommandCore> extends BukkitCommand implements Comparable<CommandRoot<?>> {
    private final CommandPriority priority;
    private final T core;
    private final VersatiaModule registerer;
    protected CommandRoot(String name, CommandPriority priority, T core, VersatiaModule registerer) {
        super(name);
        this.priority = priority;
        this.core = core;
        this.registerer = registerer;
    }

    public final CommandPriority getPriority() {
        return priority;
    }

	@Override
	public final boolean execute(CommandSender commandSender, String accessor, String[] args) {
		return core.execute(commandSender, accessor, args);
	}
	
	public final T getCore() {
		return core;
	}
	
	public final VersatiaModule getRegisterer() {
		return registerer;
	}

	@Override
	public int compareTo(CommandRoot<?> secondObject) {
		return priority.getLevel() - secondObject.priority.getLevel();
	}
}