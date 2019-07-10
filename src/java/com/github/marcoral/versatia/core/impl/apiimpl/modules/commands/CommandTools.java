package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;

class CommandTools {
    public static boolean handleConditionCheckExecutorIsPlayer(CommandSender commandSender) {
        if(commandSender instanceof Player)
            return true;
        VersatiaMessages.sendVersatiaMessageToCommandSender(commandSender, VersatiaConstants.VERSATIA.getMessageTemplate("CommandUseErrorPlayersOnly"));
        return false;
    }
}
