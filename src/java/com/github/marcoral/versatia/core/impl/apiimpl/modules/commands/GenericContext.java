package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import org.bukkit.command.CommandSender;

class GenericContext {
    protected final CommandSender executor;
    protected final String[] args;
    protected final int argsOffset;
    public GenericContext(CommandSender executor, String[] args, int argsOffset) {
        this.executor = executor;
        this.args = args;
        this.argsOffset = argsOffset;
    }

    public int getArgsCount() {
        return args.length - argsOffset;
    }

    public int getFamilyNameAccessorsUsedCount() {
        return argsOffset;
    }

    public String getArgument(int index) {
        if(index < 0)
            throw new ArrayIndexOutOfBoundsException();
        return args[index + argsOffset];
    }

    public String getFamilyNameAccessorUsed(int index) {
        if(index >= argsOffset || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        return args[index];
    }

    public CommandSender getExecutor() {
        return executor;
    }
}
