package com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandContext;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaTabCompletionContext;
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
		return "CommandRegenerateCoreDescription";
	}
	
	@Override
	public String getPermission() {
		return VersatiaCoreConstants.Permissions.COMMAND_REGENERATE;
	}
	
	@Override
	public String[] getUsageHints() {
		return new String[] {"CommandRegenerateCoreUsageHint"};
	}
	
	@Override
	public String[] getUsageFlags() {
		return new String[] {"CommandRegenerateCoreUsageFlags"};
	}
	
	
	
	
	@Override
	public boolean invoked(VersatiaCommandContext context) {
		int argsCount = context.getArgsCount();
		if(argsCount > 1)
			return false;
		
		final String versatiaPluginName = VersatiaConstants.VERSATIA_CORE_NAME;
		String passedParameter = argsCount == 1? context.getArgument(0) : null;
		return CoreCommandConfigurationRegenerate.regenerate(VersatiaConstants.VERSATIA, versatiaPluginName, passedParameter, context);
	}
	
	@Override
	public List<String> tabComplete(VersatiaTabCompletionContext context) {
		switch (context.getArgsCount()) {
			case 1:
				return Arrays.asList(new String[] {"-f", "-fr"});
			default:
				return new ArrayList<>();
		}
	}
}