package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders;

import org.bukkit.command.CommandSender;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandHandler;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.AbstractCommandFamily;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.AbstractIntermediateCommand;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandContextImpl;

public class GenericCommandFamilyBuilder extends PlayerCommandFamilyBuilder implements VersatiaCommandFamilyBuilder {
    public GenericCommandFamilyBuilder(VersatiaModule module, AbstractCommandFamily parent) {
        super(module, parent);
    }

    @Override
    public VersatiaCommandFamilyBuilder withPermission(String permission) {
        return (VersatiaCommandFamilyBuilder) super.withPermission(permission);
    }

    @Override
    public VersatiaCommandFamilyBuilder withAliases(String... aliases) {
        return (VersatiaCommandFamilyBuilder) super.withAliases(aliases);
    }

    @Override
    public VersatiaCommandFamilyBuilder registerCommandsFamily(String name) {
        return registerNewBuilder(name, currentFamily -> new AbstractIntermediateCommandFamily(name, currentFamily), family -> new GenericCommandFamilyBuilder(getModule(), family));
    }

    @Override
    public VersatiaCommandBuilder registerCommand(String name, VersatiaCommandHandler handler) {
        return registerNewBuilder(name,
                family -> new AbstractIntermediateCommand(getModule(), family) {
                    @Override
                    protected boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
                        return handler.invoked(new CommandContextImpl(getModule(), commandSender, args, family.getNestingLevel() + 1));
                    }
                },
                CommandBuilder::new);
    }
}