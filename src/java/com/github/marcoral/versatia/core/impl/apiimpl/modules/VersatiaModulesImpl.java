package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.VersatiaModuleBuilder;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;

public class VersatiaModulesImpl extends VersatiaModules {
    private ModulesManager manager;
    public VersatiaModulesImpl(Logger versatiaLogger) {
        this.manager = new ModulesManager(versatiaLogger);
    }

    public VersatiaModuleBuilder createBuilderForImpl(JavaPlugin plugin) {
        return new VersatiaModuleBuilderImpl(plugin, manager);
    }

    public void invalidateImpl(JavaPlugin plugin) {
        manager.removeModule(plugin);
    }

    public VersatiaModule getModuleImpl(String pluginName) {
        return manager.getModule(pluginName);
    }
}