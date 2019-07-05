package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.colors.VersatiaChat;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;

public class CommandTools {
    public static void throwElementAlreadyExistsException(String name) {
        throw new IllegalArgumentException(String.format("Defined at least two command accesors: \"%s\" at the same level of nesting", name));
    }

    public static boolean handleConditionCheckExecutorIsPlayer(CommandSender commandSender) {
        if(commandSender instanceof Player)
            return true;
        VersatiaChat.sendVersatiaMessageToCommandSender(commandSender, VersatiaModules.getModule(VersatiaConstants.VERSATIA_CORE_NAME).getMessageTemplate("GenericErrorPlayersOnly"));
        return false;
    }

    public static <T> void onAliasesChanged(Collection<String> removedAliases, Collection<String> addedAliases, Map<String, T> aliasesMap, Function<String, T> mapper, Consumer<T> removalAction, Consumer<T> addingAction) throws CommandCore.AliasAlreadyExistsException {
        for(String alias : addedAliases)
            if(aliasesMap.containsKey(alias))
                throw new CommandCore.AliasAlreadyExistsException(alias);
        removedAliases.forEach(accessor -> {
            T element = aliasesMap.remove(accessor);
            removalAction.accept(element);
        });
        addedAliases.forEach(accessor -> {
            T element = mapper.apply(accessor);
            aliasesMap.put(accessor, element);
            addingAction.accept(element);
        });
    }
}
