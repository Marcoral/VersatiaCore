package com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;

public class CoreCommandCoreSubmodulesReload implements VersatiaGenericCommand {
	@Override
	public String getName() {
		return "reloadcore";
	}
	
	@Override
	public String[] getAliases() {
		return new String[] {"relcore", "relc"};
	}
	
	@Override
	public String getDescription() {
		return "ReloadCoreDescription";
	}
	
	@Override
	public String getPermission() {
		return VersatiaCoreConstants.Permissions.COMMAND_RELOAD;
	}
	
	@Override
	public String[] getUsageHints() {
		return new String[] {"ReloadCoreUsageHint"};
	}
		
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		final String versatiaPluginName = VersatiaConstants.VERSATIA_CORE_NAME;
		VersatiaModule module = VersatiaModules.getModule(versatiaPluginName);
		
		CoreCommandSubmodulesReload.reload(module, versatiaPluginName, context, 0);
		return true;
	}
}
