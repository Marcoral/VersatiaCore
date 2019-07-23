package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerTabCompletionContext;

public class PlayerTabCompletionContextImpl extends TabCompletionContextImpl implements VersatiaPlayerTabCompletionContext {
	public PlayerTabCompletionContextImpl(CommandSender executor, String[] args, int argsOffset, Location location) {
		super(executor, args, argsOffset, location);
	}
	
	@Override
	public Player getExecutor() {
		return (Player) executor;
	}
}
