package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.List;

import org.bukkit.Location;
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
	
	@Override
	public List<String> tabComplete(CommandSender sender, String commandLabel, String[] args, Location location) throws IllegalArgumentException {
		if(sender == null || commandLabel == null || args == null)
			super.tabComplete(sender, commandLabel, args, location);
		return core.tabComplete(sender, commandLabel.toLowerCase(), args, location);
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