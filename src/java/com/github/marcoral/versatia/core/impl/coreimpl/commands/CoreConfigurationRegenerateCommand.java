package com.github.marcoral.versatia.core.impl.coreimpl.commands;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandHandler;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;

public class CoreConfigurationRegenerateCommand implements VersatiaCommandHandler {
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		int argsCount = context.getArgsCount();
		if(argsCount > 1)
			return false;
		
		final String versatiaPluginName = VersatiaConstants.VERSATIA_CORE_NAME;
		VersatiaModule module = VersatiaModules.getModule(versatiaPluginName);
		String passedParameter = argsCount == 1? context.getArgument(0) : null;
		return ConfigurationRegenerateCommand.regenerate(module, versatiaPluginName, passedParameter, context);
	}
}