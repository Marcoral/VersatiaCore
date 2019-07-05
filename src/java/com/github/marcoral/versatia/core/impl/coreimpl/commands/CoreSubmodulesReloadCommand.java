package com.github.marcoral.versatia.core.impl.coreimpl.commands;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandHandler;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;

public class CoreSubmodulesReloadCommand implements VersatiaCommandHandler {
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		final String versatiaPluginName = VersatiaConstants.VERSATIA_CORE_NAME;
		VersatiaModule module = VersatiaModules.getModule(versatiaPluginName);
		
		SubmodulesReloadCommand.reload(module, versatiaPluginName, context, 0);
		return true;
	}
}
