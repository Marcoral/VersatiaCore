package com.github.marcoral.versatia.core.impl.apiimpl.modules.commands;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;

public class GenericCommandFamily extends CommandFamily {
	public GenericCommandFamily(VersatiaModule module, VersatiaCommand descriptor, int nestingLevel) {
		super(new CommandFamilyCore(module, descriptor, nestingLevel));
	}
}
