package com.github.marcoral.versatia.core;

import java.util.function.Consumer;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.colors.VersatiaColor;
import com.github.marcoral.versatia.core.api.modules.VersatiaModuleInitializer;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLogger;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLoggers;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.apiimpl.colors.CoreSubmoduleColors;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.CoreModuleImpl;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.VersatiaModuleImpl;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.VersatiaModulesImpl;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.messages.VersatiaMessagesImpl;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules.InitializerSubmoduleProperty;
import com.github.marcoral.versatia.core.impl.apiimpl.tools.VersatiaToolsImpl;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.CoreCommandFamilyVersatia;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia.CoreCommandConfigurationRegenerate;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia.CoreCommandCoreConfigurationRegenerate;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia.CoreCommandCoreSubmodulesReload;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.versatia.CoreCommandSubmodulesReload;

//Uwaga: Niniejszy kod został napisany przez Polskiego pojeba, informatyka czyt. Marcin Polak
//Z zainteresowania pasjonat informatyki, ale w rzeczywistości to po prostu jebany ochlapus, kroczek nie ma nawet podskoku do tego Pana
//Bardzo często coś klepie, ale zazwyczaj to po prostu nie wychodzi, MaRPG - spalone swoja droga... dygu dyg! - strzeżcie się olbrzymy nadchodzę!
//Jeśli ma zamiar przeczytać to ktoś oprócz Marcina z historii commitów - to tylko żarty spokojnie, mogę tego pana polecić
//~Paweł Dębiński (pseudonim luxdevpl)

//Uwaga: Niniejszy kod został napisany przez Polskiego pojeba, informatyka czyt. Marcin Polak
//Z zainteresowania pasjonat informatyki, ale w rzeczywistości to po prostu jebany ochlapus, kroczek nie ma nawet podskoku do tego Pana
//Bardzo często coś klepie, ale zazwyczaj to po prostu nie wychodzi, MaRPG - spalone swoja droga... dygu dyg! - strzeżcie się olbrzymy nadchodzę!
//Jeśli ma zamiar przeczytać to ktoś oprócz Marcina z historii commitów - to tylko żarty spokojnie, mogę tego pana polecić
//~Paweł Dębiński (pseudonim luxdevpl)

public class Initializer extends JavaPlugin {
	private final InitializerSubmoduleProperty loggerProperty = new InitializerSubmoduleProperty();
	
	private static Initializer INSTANCE;
	public static void logIfPossible(Consumer<VersatiaLogger> action) {
		if(INSTANCE.loggerProperty.isSetUp())
			action.accept(VersatiaLoggers.getDefaultLogger());
	}
	
	@Override
	public void onEnable() {
		Initializer.INSTANCE = this;
		VersatiaModulesImpl modulesManager = new VersatiaModulesImpl(getLogger());
		CoreModuleImpl module = new CoreModuleImpl(this, loggerProperty);
		setupAPI(module, modulesManager);
		registerAsModule(module, modulesManager);
	}

	private void setupAPI(VersatiaModuleImpl module, VersatiaModulesImpl modulesManager) {
		// Setup VersatiaTools (it also provides a tool to inject another fields)
		VersatiaToolsImpl tools = new VersatiaToolsImpl();
		tools.injectExternalDependencyImpl(VersatiaConstants.class, null, "pluginName", getName(), true);
		tools.injectExternalDependencyImpl(VersatiaConstants.class, null, "versatiaModule", module, true);

		tools.injectExternalDependencyImpl(VersatiaTools.class, null, "INSTANCE", tools, true);
		tools.injectExternalDependencyImpl(VersatiaMessages.class, null, "INSTANCE", new VersatiaMessagesImpl(), true);

		// Setup VersatiaModules
		tools.injectExternalDependencyImpl(VersatiaModules.class, null, "INSTANCE", modulesManager, true);
	}

	private void registerAsModule(VersatiaModuleImpl module, VersatiaModulesImpl modulesManager) {
		modulesManager._internal_buildImpl(module, getName(),
			initializer -> {
				registerMessagesColors(initializer);
			},
			initializer -> {
				registerCommands(initializer);
			});
	}

	private void registerMessagesColors(VersatiaModuleInitializer initializer) {
		CoreSubmoduleColors colorsSubmodule = new CoreSubmoduleColors(initializer);
		initializer.addSubmodule(colorsSubmodule);
		VersatiaTools.injectExternalDependency(VersatiaColor.class, null, "Converter", colorsSubmodule, true);
	}

	private void registerCommands(VersatiaModuleInitializer initializer) {
		VersatiaCommandFamilyBuilder versatia = initializer.registerGenericCommandsFamily(new CoreCommandFamilyVersatia(), CommandPriority.HIGHEST);
		versatia.registerCommand(new CoreCommandSubmodulesReload());
		versatia.registerCommand(new CoreCommandCoreSubmodulesReload());
		versatia.registerCommand(new CoreCommandConfigurationRegenerate());
		versatia.registerCommand(new CoreCommandCoreConfigurationRegenerate());
	}

	@Override
	public void onDisable() {
		VersatiaModules.invalidate(this);
	}
}
