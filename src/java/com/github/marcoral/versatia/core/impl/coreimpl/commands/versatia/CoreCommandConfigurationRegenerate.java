package com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaTabCompletionContext;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;

public class CoreCommandConfigurationRegenerate implements VersatiaGenericCommand {
	@Override
	public String getName() {
		return "regenerate";
	}
	
	@Override
	public String[] getAliases() {
		return new String[] {"regen"};
	}
	
	@Override
	public String getDescription() {
		return "CommandRegenerateDescription";
	}
	
	@Override
	public String getPermission() {
		return VersatiaCoreConstants.Permissions.COMMAND_REGENERATE;
	}
	
	@Override
	public String[] getUsageHints() {
		return new String[] {"CommandRegenerateUsageHint"};
	}
	
	@Override
	public String[] getUsageFlags() {
		return new String[] {"CommandRegenerateUsageFlags"};
	}
	
	
	
	
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		int argsCount = context.getArgsCount();
		if(argsCount == 0 || argsCount > 2)
			return false;
		
		String moduleName = context.getArgument(0);
		VersatiaModule module = VersatiaModules.getModule(moduleName);
		if(module == null) {
			context.printModuleNotFoundMessage(moduleName);
			return true;
		}
		
		String passedParameter = argsCount == 2? context.getArgument(1) : null;
		return regenerate(module, moduleName, passedParameter, context);
	}

	@Override
	public List<String> tabComplete(VersatiaTabCompletionContext context) {
		switch (context.getArgsCount()) {
			case 1:
				return new ArrayList<>(VersatiaModules.getModulesNames());
			case 2:
				return Arrays.asList(new String[] {"-f", "-fr"});
			default:
				return new ArrayList<>();
		}
	}
	
	static boolean regenerate(VersatiaModule module, String moduleName, String flags, VersatiaCommandContext context) {
		if(flags == null) {
			module.regenerateConfiguration();
			context.replyToExecutor("CommandRegenerationSuccess", moduleName);
		} else if(flags.equalsIgnoreCase("-f")) {
			module.overwriteConfiguration();
			context.replyToExecutor("CommandOverwrittingSuccess", moduleName);
		} else if(flags.equalsIgnoreCase("-fr")) {
			module.overwriteConfiguration();
			context.replyToExecutor("CommandOverwrittingSuccess", moduleName);
			module.reloadEverySubmodule();
		} else
			return false;
		return true;
	}
}