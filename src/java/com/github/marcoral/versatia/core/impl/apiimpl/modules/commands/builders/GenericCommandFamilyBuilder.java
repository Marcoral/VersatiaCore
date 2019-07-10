package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.builders;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.CommandFamily;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.GenericCommandCore;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.GenericCommandFamily;

public class GenericCommandFamilyBuilder extends PlayerCommandFamilyBuilder implements VersatiaCommandFamilyBuilder {
	public GenericCommandFamilyBuilder(VersatiaModule module, CommandFamily family) {
		super(module, family);
	}

	@Override
	public VersatiaCommandFamilyBuilder registerCommandsFamily(VersatiaCommand commandDescriptor) {
    	CommandFamily newFamily = new GenericCommandFamily(module, commandDescriptor, family.getNestingLevel() + 1);
    	family.addChild(newFamily.getCore());
    	return new GenericCommandFamilyBuilder(module, newFamily);
	}

	@Override
	public void registerCommand(VersatiaGenericCommand command) {
		family.addChild(new GenericCommandCore(module, command, family.getNestingLevel() + 1));		
	}
}