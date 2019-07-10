package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import java.io.File;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.events.VersatiaModuleReloadedEvent;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaGenericCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommand;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLogger;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLoggers;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.ModuleCommandsManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.loggers.ModuleLoggersManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.messages.ModuleMessagesManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules.ModuleSubmodulesManager;

public class VersatiaModuleImpl implements VersatiaModule {
	protected final String moduleName;
    protected final ModuleCommandsManager commandsManager;
    protected final ModuleLoggersManager loggersManager;
    protected final ModuleMessagesManager messagesManager;
    protected final ModuleSubmodulesManager submodulesManager;
    
    public VersatiaModuleImpl(Plugin plugin) {
        this.moduleName = plugin.getName();
        this.commandsManager = new ModuleCommandsManager(this);
        this.loggersManager = new ModuleLoggersManager(new File(plugin.getDataFolder(), VersatiaConstants.Path.LOGGERS_DIRECTORY), moduleName, this);
        this.messagesManager = new ModuleMessagesManager(plugin);
        this.submodulesManager = new ModuleSubmodulesManager(plugin.getName());
    }

    public void addDefaultSubmodules() {
        submodulesManager.addSubmodule(messagesManager);
        submodulesManager.addSubmodule(loggersManager);	//Must be added after messages submodule!
    }

    private void validateNotDisabled() {
        if(!Bukkit.getPluginManager().getPlugin(moduleName).isEnabled())
            throw new IllegalStateException("Can not invoke on disabled module!");
    }

    private Plugin asPlugin() {
        return Bukkit.getPluginManager().getPlugin(moduleName);
    }
    
	@Override
	public void addSubmodule(VersatiaSubmodule submodule) {
		submodulesManager.addSubmodule(submodule);
	}

	@Override
	public void groupSubmodules(String groupKey, String... submodulesNames) {
		submodulesManager.groupSubmodules(groupKey, submodulesNames);
	}

    @Override
    public VersatiaModuleReloadResult reloadEverySubmodule() {
        validateNotDisabled();
        VersatiaLoggers.getDefaultLogger().finer("SubmoduleReloadEveryRequest", moduleName);
        VersatiaModuleReloadResult result = submodulesManager.reloadEverySubmodule();
        Bukkit.getPluginManager().callEvent(new VersatiaModuleReloadedEvent(this, result));
        return result;
    }

    @Override
    public VersatiaModuleReloadResult reloadSubmodules(String... submodules) {
        validateNotDisabled();
        VersatiaModuleReloadResult result = submodulesManager.reloadSubmodules(submodules);
        Bukkit.getPluginManager().callEvent(new VersatiaModuleReloadedEvent(this, result));
        return result;
    }

    @Override
    public void registerGenericCommand(VersatiaGenericCommand command, CommandPriority priority) {
        commandsManager.registerGenericCommand(command, priority);
    }


    @Override
    public void registerPlayerOnlyCommand(VersatiaPlayerCommand handler, CommandPriority priority) {
        commandsManager.registerPlayerOnlyCommand(handler, priority);
    }


    @Override
    public VersatiaCommandFamilyBuilder registerGenericCommandsFamily(VersatiaCommand descriptor, CommandPriority priority) {
        return commandsManager.registerGenericCommandsFamily(descriptor, priority);
    }

    @Override
    public VersatiaPlayerCommandFamilyBuilder registerPlayerOnlyCommandsFamily(VersatiaCommand descriptor, CommandPriority priority) {
        return commandsManager.registerPlayerOnlyCommandsFamily(descriptor, priority);
    }
    
	@Override
	public void unregisterRootCommand(String name) {
		commandsManager.unregisterRootCommand(name);	
	}

    @Override
    public void regenerateConfiguration() {
        VersatiaTools.unpackFiles(asPlugin(), false);
    }

    @Override
    public void overwriteConfiguration() {
        VersatiaTools.unpackFiles(asPlugin(), true);
    }

    @Override
    public VersatiaConfigurationFile getConfig(String path) {
        path = asPlugin().getDataFolder() + "/" + path;
        return VersatiaTools.searchForConfigurationFile(path);
    }
    
	@Override
	public VersatiaConfigurationProcessor getConfigProcessor(String path) {
		VersatiaConfigurationFile configFile = getConfig(path);
		if(configFile.exists())
			throw new NullPointerException("Requested processor of non-existing config!");
		return configFile.getProcessor();
	}
    
	@Override
	public VersatiaLogger getLogger(String loggerName) {
		return loggersManager.getLogger(loggerName);
	}
    
	@Override
	public void setMessageTemplatesProcessor(BiConsumer<String, String> processor) {
		messagesManager.setMessageTemplatesProcessor(processor);
	}
    
	@Override
	public VersatiaMessageEntry getMessageTemplateEntry(String key) {
		return messagesManager.getTemplate(key);
	}

    public void shutdown() {
    	//SubmodulesManager should always be shut down as first
    	submodulesManager.shutdown();

    	commandsManager.shutdown();
    }

	public void validate() {
		submodulesManager.validate();
	}
}
