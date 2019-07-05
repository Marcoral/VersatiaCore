package com.github.marcoral.versatia.core.impl.coreimpl.commands;

import java.util.Set;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandHandler;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;

public class SubmodulesReloadCommand implements VersatiaCommandHandler {
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		if(context.getArgsCount() == 0)
			return false;
		
		String moduleName = context.getArgument(0);
		VersatiaModule module = VersatiaModules.getModule(moduleName);
		if(module == null) {
			context.replyToExecutor("ErrorNoModuleFound", moduleName);
			return true;
		}
		
		reload(module, moduleName, context, 1);
		return true;
	}
	
	static void reload(VersatiaModule module, String moduleName, VersatiaCommandContext context, int argsOffset) {
		VersatiaModuleReloadResult result;
		if(context.getArgsCount() == argsOffset)
			result = module.reloadEverySubmodule();
		else {
			String[] submodulesNames = new String[context.getArgsCount() - argsOffset];
			for(int i = 0; i < submodulesNames.length; ++i)
				submodulesNames[i] = context.getArgument(i + argsOffset);
			result = module.reloadSubmodules(submodulesNames);
		}

		context.replyToExecutor("ReloadSuccess", moduleName, String.valueOf(result.getReloadedSubmodulesNames().size()));

		Set<String> unknownNames = result.getUnknownSubmodulesNames();
		if(unknownNames.size() > 0) {
			context.replyToExecutor("ReloadErrorFoundUnknownSubmodules");
			unknownNames.forEach(unknownName -> context.replyToExecutor("ReloadErrorNoSubmoduleFound", unknownName));
		}
	}
}
