package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import java.io.File;
import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

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
import com.github.marcoral.versatia.core.api.modules.loggers.LoggingPriority;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLogger;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLoggers;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.api.tools.modules.VersatiaSubmoduleHandlerProvider;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.ModuleCommandsManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.loggers.ModuleLoggersManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.loggers.ModuleLoggersManagerProvider;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.messages.ModuleMessagesManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.messages.ModuleMessagesManagerProvider;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules.ModuleSubmodulesManager;

public class VersatiaModuleImpl implements VersatiaModule {
	protected final Plugin plugin;
    protected final ModuleCommandsManager commandsManager;
    protected final ModuleSubmodulesManager submodulesManager;
    
    protected ModuleLoggersManager loggersManager;
    protected ModuleMessagesManager messagesManager;
    
    public VersatiaModuleImpl(Plugin plugin) {
        this.plugin = plugin;
        this.commandsManager = new ModuleCommandsManager(this);
        this.submodulesManager = new ModuleSubmodulesManager(this);
    }

    public void addDefaultSubmodules() {
    	messagesManager = submodulesManager.addServicedSubmodule(ModuleMessagesManagerProvider.class, true);
    	loggersManager = submodulesManager.addServicedSubmodule(ModuleLoggersManagerProvider.class, true);	//Must be added after messages submodule!
    }

    private void validateNotDisabled() {
        if(!plugin.isEnabled())
            throw new IllegalStateException("Can not invoke on disabled module!");
    }
    
	@Override
	public void addSubmodule(VersatiaSubmodule submodule) {
		submodulesManager.addSubmodule(submodule);
	}
	
	@Override
	public <T extends VersatiaSubmodule> T addServicedSubmodule(Class<? extends VersatiaSubmoduleHandlerProvider> handler, boolean hardDepend) {
		return submodulesManager.addServicedSubmodule(handler, hardDepend);
	}

	@Override
	public void groupSubmodules(String groupKey, String... submodulesNames) {
		submodulesManager.groupSubmodules(groupKey, submodulesNames);
	}

    @Override
    public VersatiaModuleReloadResult reloadEverySubmodule() {
        validateNotDisabled();
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
	public Set<String> getReloadableNames() {
		return submodulesManager.getReloadableNames();
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
        VersatiaTools.unpackFiles(plugin, false);
    }

    @Override
    public void overwriteConfiguration() {
        VersatiaTools.unpackFiles(plugin, true);
    }

    @Override
    public VersatiaConfigurationFile getConfig(String path) {
        return VersatiaTools.searchForConfigurationFile(new File(plugin.getDataFolder(), path));
    }
    
	@Override
	public VersatiaConfigurationFile getConfig(String parentPath, String path) {
        return VersatiaTools.searchForConfigurationFile(new File(new File(plugin.getDataFolder(), parentPath), path));
	}
    
	@Override
	public VersatiaConfigurationProcessor getConfigProcessor(String path) {
		VersatiaConfigurationFile configFile = getConfig(path);
		if(!configFile.exists())
			throw new NullPointerException("Requested processor of non-existing config!");
		return configFile.getProcessor();
	}
    
	@Override
	public VersatiaLogger getLogger(String loggerName) {
		return loggersManager.getLogger(loggerName);
	}
	
	@Override
	public void log(LoggingPriority priority, String messageTemplateName, Object... args) {
		VersatiaLoggers.getDefaultLogger().log(priority, VersatiaMessages.createTemplateDescriptor(this, messageTemplateName), args);
	}
    
	@Override
	public void setMessageTemplatesProcessor(BiConsumer<String, String> processor) {
		messagesManager.setMessageTemplatesProcessor(processor);
	}
    
	@Override
	public VersatiaMessageEntry getMessageTemplateEntry(String key) {
		return messagesManager.getTemplate(key);
	}
	
	@Override
	public Plugin getCorrespondingPlugin() {
		return plugin;
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
