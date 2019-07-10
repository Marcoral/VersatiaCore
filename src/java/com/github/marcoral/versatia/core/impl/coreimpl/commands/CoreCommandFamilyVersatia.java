package com.github.marcoral.versatia.core.impl.coreimpl.commands;

import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;

public class CoreCommandFamilyVersatia implements VersatiaCommand {
	@Override
	public String getName() {
		return "versatia";
	}
	
	@Override
	public String[] getAliases() {
		return new String[] {"vers"};
	}
}
