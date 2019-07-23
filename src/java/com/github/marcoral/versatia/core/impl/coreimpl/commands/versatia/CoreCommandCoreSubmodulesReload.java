package com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia;

import java.util.ArrayList;
import java.util.List;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaTabCompletionContext;
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
		return "CommandReloadCoreDescription";
	}
	
	@Override
	public String getPermission() {
		return VersatiaCoreConstants.Permissions.COMMAND_RELOAD;
	}
	
	@Override
	public String[] getUsageHints() {
		return new String[] {"CommandReloadCoreUsageHint"};
	}
		
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		final String versatiaPluginName = VersatiaConstants.VERSATIA_CORE_NAME;
		VersatiaModule module = VersatiaModules.getModule(versatiaPluginName);
		
		CoreCommandSubmodulesReload.reload(module, versatiaPluginName, context, 0);
		return true;
	}
	
	@Override
	public List<String> tabComplete(VersatiaTabCompletionContext context) {
		switch (context.getArgsCount()) {
			case 1:
				return new ArrayList<>(VersatiaConstants.VERSATIA.getReloadableNames());
			default:
				return new ArrayList<>();
		}
	}
}