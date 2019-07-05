package com.github.marcoral.versatia.core;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.colors.VersatiaColor;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.VersatiaModuleBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;
import com.github.marcoral.versatia.core.impl.apiimpl.colors.CoreSubmoduleColors;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.VersatiaModulesImpl;
import com.github.marcoral.versatia.core.impl.apiimpl.tools.VersatiaToolsImpl;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.ConfigurationRegenerateCommand;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.CoreConfigurationRegenerateCommand;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.CoreSubmodulesReloadCommand;
import com.github.marcoral.versatia.core.impl.coreimpl.commands.SubmodulesReloadCommand;

public class Initializer extends JavaPlugin {
    @Override
    public void onEnable() {
        setupAPI();
        registerAsModule();
    }

    private void setupAPI() {
        //Setup VersatiaTools (it also provides a tool to inject another fields)
        VersatiaToolsImpl tools = new VersatiaToolsImpl();
        tools.injectExternalDependencyImpl(VersatiaConstants.class, null, "pluginName", getName(), true);

        tools.injectExternalDependencyImpl(VersatiaTools.class, null, "INSTANCE", tools, true);

        //Setup VersatiaModules
        tools.injectExternalDependencyImpl(VersatiaModules.class, null, "INSTANCE", new VersatiaModulesImpl(getLogger()),true);
    }

    private void registerAsModule() {
        VersatiaModuleBuilder builder = VersatiaModules.createBuilderFor(this);
        registerMessagesColors(builder);
        registerCommands(builder);
        builder.buildAndRun();
    }

    private void registerMessagesColors(VersatiaModuleBuilder builder) {
        CoreSubmoduleColors messagesSubmodule = new CoreSubmoduleColors(getLogger(), builder.getCorrespondingModule());
        builder.addSubmodule("colorcodes", messagesSubmodule);
        VersatiaTools.injectExternalDependency(VersatiaColor.class, null, "Converter", messagesSubmodule, true);
    }
    
    private void registerCommands(VersatiaModuleBuilder builder) {
    	VersatiaModule module = builder.getCorrespondingModule();
    	VersatiaCommandFamilyBuilder versatia = module.registerGenericCommandsFamily("versatia", CommandPriority.HIGHEST).withAliases("vers");
    	versatia.registerCommand("reload", new SubmodulesReloadCommand())
    		.withAliases("rel")
    		.withDescription("ReloadDescription")
    		.withPermission(VersatiaCoreConstants.Permissions.COMMAND_RELOAD)
    		.withUsageHint("ReloadUsageHint");
    	versatia.registerCommand("reloadcore", new CoreSubmodulesReloadCommand())
			.withAliases("relcore", "relc")
			.withDescription("ReloadCoreDescription")
			.withPermission(VersatiaCoreConstants.Permissions.COMMAND_RELOAD)
			.withUsageHint("ReloadCoreUsageHint");
    	versatia.registerCommand("regenerate", new ConfigurationRegenerateCommand())
    		.withAliases("regen")
    		.withDescription("RegenerateDescription")
    		.withPermission(VersatiaCoreConstants.Permissions.COMMAND_REGENERATE)
    		.withUsageHint("RegenerateUsageHint")
    		.withUsageFlags("RegenerateUsageFlags");
    	versatia.registerCommand("regeneratecore", new CoreConfigurationRegenerateCommand())
    		.withAliases("regencore", "regenc")
    		.withDescription("RegenerateCoreDescription")
    		.withPermission(VersatiaCoreConstants.Permissions.COMMAND_REGENERATE)
    		.withUsageHint("RegenerateCoreUsageHint")
    		.withUsageFlags("RegenerateCoreUsageFlags");
    }

    @Override
    public void onDisable() {
        VersatiaModules.invalidate(this);
    }
}