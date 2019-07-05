package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders;

import java.util.Collection;
import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandHandler;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.AbstractCommandFamily;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.AbstractIntermediateCommand;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandCore;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandTools;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.PlayerCommandContextImpl;

public class PlayerCommandFamilyBuilder extends CommandBuilder implements VersatiaPlayerCommandFamilyBuilder {
    private final VersatiaModule module;
	public PlayerCommandFamilyBuilder(VersatiaModule module, AbstractCommandFamily parent) {
        super(parent);
        this.module = module;
    }

	protected VersatiaModule getModule() {
		return module;
	}

    @Override
    public VersatiaPlayerCommandFamilyBuilder withPermission(String permission) {
        return (VersatiaPlayerCommandFamilyBuilder) super.withPermission(permission);
    }

    @Override
    public VersatiaPlayerCommandFamilyBuilder withAliases(String... aliases) {
        return (VersatiaPlayerCommandFamilyBuilder) super.withAliases(aliases);
    }

    @Override
    public VersatiaPlayerCommandFamilyBuilder registerPlayerOnlyCommandsFamily(String name) {
        return registerNewBuilder(name, currentFamily -> new AbstractIntermediateCommandFamily(name, currentFamily) {
            @Override
            protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
                return CommandTools.handleConditionCheckExecutorIsPlayer(commandSender) && super.handleConditionsCheck(commandSender, accessor, args);
            }
        }, family -> new PlayerCommandFamilyBuilder(module, family));
    }

    @Override
    public VersatiaCommandBuilder registerPlayerOnlyCommand(String name, VersatiaPlayerCommandHandler handler) {
        return registerNewBuilder(name,
                family -> new AbstractIntermediateCommand(module, family) {
                    @Override
                    protected boolean handleConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
                        return CommandTools.handleConditionCheckExecutorIsPlayer(commandSender) && super.handleConditionsCheck(commandSender, accessor, args);
                    }

                    @Override
                    protected boolean passedConditionsCheck(CommandSender commandSender, String accessor, String[] args) {
                        return handler.invoked(new PlayerCommandContextImpl(module, (Player) commandSender, args, family.getNestingLevel() + 1));
                    }
                },
                CommandBuilder::new);
    }

    protected <T extends CommandCore, B> B registerNewBuilder(String name, Function<AbstractCommandFamily, T> handlerSupplier, Function<T, B> builderCreator) {
        AbstractCommandFamily currentFamily = getObjectBeingBuilt();
        try {
            T handler = currentFamily.addChild(name, () -> handlerSupplier.apply(currentFamily));
            return builderCreator.apply(handler);
        } catch (CommandCore.AliasAlreadyExistsException e) {
            CommandTools.throwElementAlreadyExistsException(name);
            return null;
        }
    }

    @Override
    protected AbstractCommandFamily getObjectBeingBuilt() {
        return (AbstractCommandFamily) super.getObjectBeingBuilt();
    }

    protected class AbstractIntermediateCommandFamily extends AbstractCommandFamily {
        private final String name;
        private final AbstractCommandFamily parent;
        public AbstractIntermediateCommandFamily(String name, AbstractCommandFamily parent) {
            super(module, parent.getNestingLevel() + 1);
            this.name = name;
            this.parent = parent;
        }

		@Override
		protected void onAliasesChanged(Collection<String> removedAliases, Collection<String> addedAliases)
				throws AliasAlreadyExistsException {
			parent.modifyChildren(removedAliases, addedAliases, () -> new AbstractIntermediateCommandFamily(name, parent));
		}
    }
}