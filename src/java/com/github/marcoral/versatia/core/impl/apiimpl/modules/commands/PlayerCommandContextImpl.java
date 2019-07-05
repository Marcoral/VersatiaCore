package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;


import org.bukkit.entity.Player;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandContext;

public class PlayerCommandContextImpl extends CommandContextImpl implements VersatiaPlayerCommandContext {
    public PlayerCommandContextImpl(VersatiaModule module, Player executor, String[] args, int argsOffset) {
        super(module, executor, args, argsOffset);
    }

    @Override
    public Player getExecutor() {
        return (Player) super.getExecutor();
    }
}