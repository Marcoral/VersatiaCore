package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.events.VersatiaModuleReloadedEvent;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.commands.CommandPriority;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaCommandHandler;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandFamilyBuilder;
import com.github.marcoral.versatia.core.api.modules.commands.VersatiaPlayerCommandHandler;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModuleReloadResult;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.commands.ModuleCommandsManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.messages.ModuleMessagesManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules.ModuleSubmodulesManager;

public class VersatiaModuleImpl implements VersatiaModule {
    private final ModuleContext context;
    public VersatiaModuleImpl(Plugin plugin) {
        this.context = new ModuleContext(plugin);
    }

    private void validateNotDisabled() {
        if(!Bukkit.getPluginManager().getPlugin(context.getModuleName()).isEnabled())
            throw new IllegalStateException("Can not invoke on disabled module!");
    }

    private Plugin asPlugin() {
        return Bukkit.getPluginManager().getPlugin(context.getModuleName());
    }

    @Override
    public VersatiaModuleReloadResult reloadEverySubmodule() {
        validateNotDisabled();
        VersatiaModuleReloadResult result = context.getSubmodules().reloadEverySubmodule();
        Bukkit.getPluginManager().callEvent(new VersatiaModuleReloadedEvent(this, result));
        return result;
    }

    @Override
    public VersatiaModuleReloadResult reloadSubmodules(String... submodules) {
        validateNotDisabled();
        VersatiaModuleReloadResult result = context.getSubmodules().reloadSubmodules(submodules);
        Bukkit.getPluginManager().callEvent(new VersatiaModuleReloadedEvent(this, result));
        return result;
    }

    @Override
    public VersatiaCommandBuilder registerGenericCommand(String name, VersatiaCommandHandler handler, CommandPriority priority) {
        return context.getCommands().registerGenericCommand(name, handler, priority, ctx -> {});
    }


    @Override
    public VersatiaCommandBuilder registerPlayerOnlyCommand(String name, VersatiaPlayerCommandHandler handler, CommandPriority priority) {
        return context.getCommands().registerPlayerOnlyCommand(name, handler, priority, ctx -> {});
    }


    @Override
    public VersatiaCommandFamilyBuilder registerGenericCommandsFamily(String commandFamilyLabel, CommandPriority priority) {
        return context.getCommands().registerGenericCommandsFamily(commandFamilyLabel, priority, ctx -> {});
    }

    @Override
    public VersatiaPlayerCommandFamilyBuilder registerPlayerOnlyCommandsFamily(String commandFamilyLabel, CommandPriority priority) {
        return context.getCommands().registerPlayerOnlyCommandsFamily(commandFamilyLabel, priority, ctx -> {});
    }

    @Override
    public void regenerateConfiguration() {
        VersatiaTools.unpackFiles(asPlugin(),false);
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
		public VersatiaMessageEntry getMessageTemplateEntry(String key) {
		return context.getMessages().getTemplate(key);
	}

	public ModuleContext getModuleContext() {
		return context;
	}

    public void shutdown() {
    	context.shutdown();
    }
    
    public class ModuleContext {
        private final String moduleName;
        private final ModuleMessagesManager messagesManager;
        private final ModuleSubmodulesManager submodulesManager;
        private final ModuleCommandsManager commandsManager;

        public ModuleContext(Plugin plugin) {
            this.moduleName = plugin.getName();
            this.messagesManager = new ModuleMessagesManager(new File(plugin.getDataFolder(), "messages"));
            this.submodulesManager = new ModuleSubmodulesManager();
            this.commandsManager = new ModuleCommandsManager(VersatiaModuleImpl.this);
        }

        public String getModuleName() {
            return moduleName;
        }
        
        public ModuleMessagesManager getMessages() {
        	return messagesManager;
        }

        public ModuleCommandsManager getCommands() {
            return commandsManager;
        }
        
        public ModuleSubmodulesManager getSubmodules() {
            return submodulesManager;
        }
        
        private void shutdown() {
        	//SubmodulesManager should always be shut down as first
        	submodulesManager.shutdown();
        	
        	messagesManager.shutdown();
        	commandsManager.shutdown();
        }
    }
}
