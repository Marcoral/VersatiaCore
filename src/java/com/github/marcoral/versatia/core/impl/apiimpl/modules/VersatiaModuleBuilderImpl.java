package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.marcoral.versatia.core.api.events.VersatiaModuleLoadedEvent;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.VersatiaModuleBuilder;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;

public class VersatiaModuleBuilderImpl implements VersatiaModuleBuilder {
    private VersatiaModuleImpl module;
    private Plugin bukkitParent;
    private ModulesManager modulesStorage;

    private boolean built = false;

    public VersatiaModuleBuilderImpl(JavaPlugin bukkitParent, ModulesManager modulesStorage) {
        this.module = new VersatiaModuleImpl(bukkitParent);
        this.bukkitParent = bukkitParent;
        this.modulesStorage = modulesStorage;
        addDefaultSubmodules();
    }

    private void addDefaultSubmodules() {
    	VersatiaModuleImpl.ModuleContext context = module.getModuleContext();
        context.getSubmodules().addSubmodule("messages", context.getMessages());
    }

    @Override
    public void addSubmodule(String reloadKey, VersatiaSubmodule submodule) {
        validateNotBuilt();
        module.getModuleContext().getSubmodules().addSubmodule(reloadKey, submodule);
    }

    @Override
    public void groupSubmodules(String groupKey, VersatiaSubmodule... submodules) {
    	module.getModuleContext().getSubmodules().groupSubmodules(groupKey, submodules);
    }

    @Override
    public void setMessageTemplatesProcessor(BiConsumer<String, String> processor) {
    	module.getModuleContext().getMessages().setMessageTempaltesProcessor(processor);
    }

    @Override
    public VersatiaModule getCorrespondingModule() {
        return module;
    }

    @Override
    public void buildAndRun() {
        if(built)
            throw new IllegalStateException("Module was already built! You probably used buildAndRun() method inside of onVersatiaEnable() method. Don't do it, it will be invoked automatically after this method.");
        module.reloadAll();
        modulesStorage.putModule(bukkitParent.getName(), module);
        Bukkit.getPluginManager().callEvent(new VersatiaModuleLoadedEvent(module));
    }

    private void validateNotBuilt() {
        if(built)
            throw new IllegalStateException("You should not invoke this method once builder finished its work.");
    }
}