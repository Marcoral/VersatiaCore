package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandFamilyBuilder;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandFamily;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.PlayerCommandCore;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.PlayerCommandFamily;

public class PlayerCommandFamilyBuilder implements VersatiaPlayerCommandFamilyBuilder {
    protected final VersatiaModule module;
    protected final CommandFamily family;
	public PlayerCommandFamilyBuilder(VersatiaModule module, CommandFamily family) {
        this.module = module;
        this.family = family;
    }

	protected VersatiaModule getModule() {
		return module;
	}

    @Override
    public VersatiaPlayerCommandFamilyBuilder registerPlayerOnlyCommandsFamily(VersatiaCommand descriptor) {
    	CommandFamily newFamily = new PlayerCommandFamily(module, descriptor, family.getNestingLevel() + 1);
    	family.addChild(newFamily.getCore());
    	return new PlayerCommandFamilyBuilder(module, newFamily);
    }

    @Override
    public void registerPlayerOnlyCommand(VersatiaPlayerCommand command) {
		family.addChild(new PlayerCommandCore(module, command, family.getNestingLevel() + 1));
    }
}