package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import com.github.marcoral.versatia.core.api.events.VersatiaCommandHandlerChangedEvent;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;
import com.github.marcoral.versatia.core.impl.apiimpl.Hacky;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandRoot;
import com.github.marcoral.versatia.core.impl.tools.DummyReflections;
import com.github.marcoral.versatia.core.impl.tools.NMSAvoider;

//It's kind of a driver to server commands - therefore everything is static
public class CommandsManager {
    public enum RegisteringResult {
        FAILED_LOW_PRIORITY, SUCCESS_OVERRIDED_LOWER_PRIORITY, SUCCESS
    }

    public enum UnregisteringResult {
        REMOVED_QUEUED, REMOVED_ACTIVE
    }

    //Made an assumption that these two won't change at the runtime.
    @Hacky private static final SimpleCommandMap commandMap = NMSAvoider.invokeNMSMethodOnObject(Bukkit.getServer(), "getCommandMap");
    
    @SuppressWarnings("unchecked")
	@Hacky private static final Map<String, Command> NMSknownCommands = (Map<String, Command>) DummyReflections.getFieldValue(commandMap, "knownCommands");

    private static final Map<String, PriorityQueue<CommandRoot>> commandsQueue = new HashMap<>();
    private static final Map<String, CommandRoot> activeCommands = new HashMap<>();

    public static RegisteringResult registerCommand(CommandRoot command) {
        CommandRoot existingCommand = activeCommands.get(command.getName());
        RegisteringResult result;
        if(existingCommand == null)
            result = RegisteringResult.SUCCESS;
        else {
            CommandPriority priorityOfExistingCommand = existingCommand.getPriority();
            if(command.getPriority().getLevel() <= priorityOfExistingCommand.getLevel())
                result = RegisteringResult.FAILED_LOW_PRIORITY;
            else
                result = RegisteringResult.SUCCESS_OVERRIDED_LOWER_PRIORITY;
        }

        switch (result) {
            case FAILED_LOW_PRIORITY:
                addCommandToQueue(command);
                return result;
            case SUCCESS_OVERRIDED_LOWER_PRIORITY:
                checkedCommandOverride(existingCommand, command);
                return result;
            case SUCCESS:
                checkedCommandRegister(command);
                return result;
            default:
                throw new RuntimeException("Unknown state");
        }
    }

    private static void addCommandToQueue(CommandRoot command) {
        PriorityQueue<CommandRoot> queue = commandsQueue.computeIfAbsent(command.getName(), accessorC -> new PriorityQueue<>());
        queue.add(command);
    }

    private static void checkedCommandRegister(CommandRoot command) {
        checkedMarkCommandAsActive(command);
    }

    private static void checkedCommandOverride(CommandRoot existingCommand, CommandRoot command) {
        addCommandToQueue(existingCommand);
        checkedMarkCommandAsActive(command);
        Bukkit.getServer().getPluginManager().callEvent(new VersatiaCommandHandlerChangedEvent(command.getName(), existingCommand.getRegisterer(), existingCommand.getPriority(), command.getRegisterer(), command.getPriority()));
    }

    private static void checkedMarkCommandAsActive(CommandRoot command) {
        String accessor = command.getName();
        NMSknownCommands.remove(accessor);
        activeCommands.put(accessor, command);
        commandMap.register(accessor, command);
    }

    public static UnregisteringResult unregisterCommand(CommandRoot command) {
        String accessor = command.getName();
        CommandRoot activeCommand = activeCommands.get(accessor);
        if(activeCommand.equals(command)) {
            checkedCommandUnregister(accessor);
            return UnregisteringResult.REMOVED_ACTIVE;
        } else {
            PriorityQueue<CommandRoot> queue = commandsQueue.get(accessor);
            queue.remove(command);
            if(queue.size() == 0)
                commandsQueue.remove(accessor);
            return UnregisteringResult.REMOVED_QUEUED;
        }
    }

    private static void checkedCommandUnregister(String accessor) {
        activeCommands.remove(accessor);
        NMSknownCommands.remove(accessor);
        registerNextCommandFromQueue(accessor);
    }

    private static void registerNextCommandFromQueue(String accessor) {
        PriorityQueue<CommandRoot> queue = commandsQueue.get(accessor);
        if(queue != null) {
            CommandRoot command = queue.poll();
            if(queue.size() == 0)
                commandsQueue.remove(accessor);
            checkedCommandRegister(command);
        }
    }    
}
