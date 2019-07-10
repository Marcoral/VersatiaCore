package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;

public class CommandRoot extends BukkitCommand implements Comparable<CommandRoot> {
	private final CommandPriority priority;
	private final CommandCore<?> core;
	private final VersatiaModule registerer;

	protected CommandRoot(CommandPriority priority, String accessorName, CommandCore<?> commandCore, VersatiaModule registerer) {
		super(accessorName);
		this.priority = priority;
		this.core = commandCore;
		this.registerer = registerer;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		return core.execute(sender, commandLabel.toLowerCase(), args);
	}

	public final CommandPriority getPriority() {
		return priority;
	}

	public final VersatiaModule getRegisterer() {
		return registerer;
	}

	@Override
	public int compareTo(CommandRoot secondObject) {
		return priority.getLevel() - secondObject.priority.getLevel();
	}
}