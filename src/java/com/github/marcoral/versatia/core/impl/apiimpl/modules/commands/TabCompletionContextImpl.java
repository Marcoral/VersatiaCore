package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.modules.commands.VersatiaTabCompletionContext;

public class TabCompletionContextImpl extends GenericContext implements VersatiaTabCompletionContext {
	protected final Location location;
	public TabCompletionContextImpl(CommandSender executor, String[] args, int argsOffset, Location location) {
		super(executor, args, argsOffset);
		this.location = location;
	}
	
	@Override
	public Location getLocation() {
		return location;
	}
}
