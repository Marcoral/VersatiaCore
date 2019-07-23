package com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaTabCompletionContext;
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
		return "CommandReloadDescription";
	}
	
	@Override
	public String getPermission() {
		return VersatiaCoreConstants.Permissions.COMMAND_RELOAD;
	}
	
	@Override
	public String[] getUsageHints() {
		return new String[] {"CommandReloadUsageHint"};
	}
		
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		if(context.getArgsCount() == 0)
			return false;
		
		String moduleName = context.getArgument(0);
		VersatiaModule module = VersatiaModules.getModule(moduleName);
		if(module == null) {
			context.printModuleNotFoundMessage(moduleName);
			return true;
		}
		
		reload(module, moduleName, context, 1);
		return true;
	}
	
	@Override
	public List<String> tabComplete(VersatiaTabCompletionContext context) {
		switch (context.getArgsCount()) {
			case 0:
				return new ArrayList<>();
			case 1:
				return new ArrayList<>(VersatiaModules.getModulesNames());
			default:
				VersatiaModule module = VersatiaModules.getModule(context.getArgument(0));
				if(module == null)
					return Arrays.asList(new String[] {VersatiaConstants.VERSATIA.getMessageTemplate("TabCompletionErrorNoModule")});
				else {
					Set<String> currentSubmoduleNames = new HashSet<>();
					for(int i = 1; i < context.getArgsCount(); ++i)
						currentSubmoduleNames.add(context.getArgument(i));
					Set<String> completion = new HashSet<>(module.getReloadableNames());
					completion.removeAll(currentSubmoduleNames);
					return new ArrayList<>(completion);
				}
		}
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

		context.replyToExecutor("CommandReloadSuccess", moduleName, String.valueOf(result.getReloadedSubmodulesNames().size()));

		Set<String> unknownNames = result.getUnknownSubmodulesNames();
		if(unknownNames.size() > 0) {
			context.replyToExecutor("CommandReloadErrorFoundUnknownSubmodules");
			unknownNames.forEach(unknownName -> context.replyToExecutor("CommandReloadErrorSubmoduleName", unknownName));
		}
		
		Set<String> errorNames = result.getSubmodulesNamesReloadingError();
		if(errorNames.size() > 0) {
			context.replyToExecutor("CommandReloadError");
			errorNames.forEach(unknownName -> context.replyToExecutor("CommandReloadErrorSubmoduleName", unknownName));
		}
	}
}
