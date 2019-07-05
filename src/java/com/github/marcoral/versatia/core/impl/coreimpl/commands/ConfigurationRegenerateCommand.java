package com.github.marcoral.versatia.core.impl.coreimpl.commands;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandHandler;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;

public class ConfigurationRegenerateCommand implements VersatiaCommandHandler {
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		int argsCount = context.getArgsCount();
		if(argsCount == 0 || argsCount > 2)
			return false;
		
		String moduleName = context.getArgument(0);
		VersatiaModule module = VersatiaModules.getModule(moduleName);
		if(module == null) {
			context.replyToExecutor("ErrorNoModuleFound", moduleName);
			return true;
		}
		
		String passedParameter = argsCount == 2? context.getArgument(1) : null;
		return regenerate(module, moduleName, passedParameter, context);
	}
	
	static boolean regenerate(VersatiaModule module, String moduleName, String flags, VersatiaCommandContext context) {
		if(flags == null) {
			module.regenerateConfiguration();
			context.replyToExecutor("RegenerationSuccess", moduleName);
		} else if(flags.equalsIgnoreCase("-f")) {
			module.overwriteConfiguration();
			context.replyToExecutor("OverwrittingSuccess", moduleName);
		} else if(flags.equalsIgnoreCase("-fr")) {
			module.overwriteConfiguration();
			context.replyToExecutor("OverwrittingSuccess", moduleName);
			module.reloadAll();
		} else
			return false;
		return true;
	}
}