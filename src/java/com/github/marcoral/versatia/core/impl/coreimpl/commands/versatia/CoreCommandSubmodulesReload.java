package com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia;

import java.util.Set;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;

public class CoreCommandSubmodulesReload implements VersatiaGenericCommand {
	@Override
	public String getName() {
		return "reload";
	}
	
	@Override
	public String[] getAliases() {
		return new String[] {"rel"};
	}
	
	@Override
	public String getDescription() {
		return "ReloadDescription";
	}
	
	@Override
	public String getPermission() {
		return VersatiaCoreConstants.Permissions.COMMAND_RELOAD;
	}
	
	@Override
	public String[] getUsageHints() {
		return new String[] {"ReloadUsageHint"};
	}
		
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
			unknownNames.forEach(unknownName -> context.replyToExecutor("ReloadErrorSubmoduleName", unknownName));
		}
		
		Set<String> errorNames = result.getSubmodulesNamesReloadingError();
		if(errorNames.size() > 0) {
			context.replyToExecutor("ReloadError");
			errorNames.forEach(unknownName -> context.replyToExecutor("ReloadErrorSubmoduleName", unknownName));
		}
	}
}
