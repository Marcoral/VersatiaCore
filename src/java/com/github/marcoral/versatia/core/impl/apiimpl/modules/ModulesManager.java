package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;

public class ModulesManager {
    private Logger versatiaLogger;
    private Map<String, VersatiaModuleImpl> modules = new HashMap<>();

    public ModulesManager(Logger versatiaLogger) {
        this.versatiaLogger = versatiaLogger;
    }

    public VersatiaModule getModule(String pluginName) {
        return modules.get(pluginName);
    }

    public void removeModule(JavaPlugin plugin) {
        String pluginName = plugin.getName();
        if(!modules.containsKey(pluginName))
            throw new NullPointerException(String.format("%s was not registered as VersatiaModule!", pluginName));
        if(plugin.isEnabled())
            throw new IllegalStateException(String.format("%s can not be invalidated as it is still enabled!", pluginName));
        modules.remove(pluginName).shutdown();
    }

    public void putModule(String pluginName, VersatiaModuleImpl module) {
        if(modules.containsKey(pluginName))
            versatiaLogger.warning(String.format("%s's VersatiaModule data was just overriden! Nag its author that he probably forgot to add VersatiaModules.invalidate() in onDisable() method.", pluginName));
        modules.put(pluginName, module);
    }
}