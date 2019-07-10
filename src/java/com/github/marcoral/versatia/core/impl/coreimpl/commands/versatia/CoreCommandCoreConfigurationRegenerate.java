package com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;

public class CoreCommandCoreConfigurationRegenerate implements VersatiaGenericCommand {
	@Override
	public String getName() {
		return "regeneratecore";
	}
	
	@Override
	public String[] getAliases() {
		return new String[] {"regencore", "regenc"};
	}
	
	@Override
	public String getDescription() {
		return "RegenerateCoreDescription";
	}
	
	@Override
	public String getPermission() {
		return VersatiaCoreConstants.Permissions.COMMAND_REGENERATE;
	}
	
	@Override
	public String[] getUsageHints() {
		return new String[] {"RegenerateCoreUsageHint"};
	}
	
	@Override
	public String[] getUsageFlags() {
		return new String[] {"RegenerateCoreUsageFlags"};
	}
	
	
	
	
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		int argsCount = context.getArgsCount();
		if(argsCount > 1)
			return false;
		
		final String versatiaPluginName = VersatiaConstants.VERSATIA_CORE_NAME;
		VersatiaModule module = VersatiaModules.getModule(versatiaPluginName);
		String passedParameter = argsCount == 1? context.getArgument(0) : null;
		return CoreCommandConfigurationRegenerate.regenerate(module, versatiaPluginName, passedParameter, context);
	}
}