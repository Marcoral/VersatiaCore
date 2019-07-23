package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.marcoral.versatia.core.api.events.VersatiaModuleInvalidatedEvent;
import com.github.marcoral.versatia.core.api.events.VersatiaModuleLoadedEvent;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.VersatiaModuleInitializer;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.api.tools.modules.VersatiaSubmoduleHandlerProvider;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules.ModuleSubmodulesManager;

public class VersatiaModulesImpl extends VersatiaModules {
    private final Logger versatiaLogger;
    
    private final Map<String, VersatiaModuleImpl> modules = new HashMap<>();
    public VersatiaModulesImpl(Logger versatiaLogger) {
        this.versatiaLogger = versatiaLogger;
    }
    
    public void _internal_buildImpl(VersatiaModuleImpl underlyingModule, Consumer<VersatiaModuleInitializer> preInitialization, Consumer<VersatiaModuleInitializer> postInitialization) {
		VersatiaModuleInitializerImpl initializer = new VersatiaModuleInitializerImpl(underlyingModule);
		putModule(underlyingModule);
		underlyingModule.regenerateConfiguration();
		preInitialization.accept(initializer);
		underlyingModule.addDefaultSubmodules();
		postInitialization.accept(initializer);
		underlyingModule.validate();
        Bukkit.getPluginManager().callEvent(new VersatiaModuleLoadedEvent(underlyingModule));
    }

	@Override
	protected void buildImpl(JavaPlugin plugin, Consumer<VersatiaModuleInitializer> initialization) {
		_internal_buildImpl(new VersatiaModuleImpl(plugin), initializer -> {}, initialization);
	}
	
    private void putModule(VersatiaModuleImpl module) {
    	String pluginName = module.getCorrespondingPlugin().getName();
        if(modules.containsKey(pluginName))
            versatiaLogger.warning(String.format("%s's VersatiaModule data was just overriden! Nag its author that he probably forgot to add VersatiaModules.invalidate() in onDisable() method.", pluginName));
        modules.put(pluginName, module);
    }

    @Override
    protected void invalidateImpl(JavaPlugin plugin) {
    	String pluginName = plugin.getName();
        if(!modules.containsKey(pluginName))
            throw new NullPointerException(String.format("%s was not registered as VersatiaModule!", pluginName));
        if(plugin.isEnabled())
            throw new IllegalStateException(String.format("%s can not be invalidated as it is still enabled!", pluginName));
        VersatiaModuleImpl module = modules.remove(pluginName);
        module.shutdown();
        ModuleSubmodulesManager.unregisterEverySubmoduleHandlerProviders(module.getCorrespondingPlugin());
        Bukkit.getPluginManager().callEvent(new VersatiaModuleInvalidatedEvent(module));
    }
    

	@Override
	protected <T extends VersatiaSubmoduleHandlerProvider> void registerSubmoduleHandlerProviderImpl(Class<T> interfaceClass, T provider) {
		ModuleSubmodulesManager.registerSubmoduleHandlerProvider(interfaceClass, provider);
	}

    @Override
    protected VersatiaModule getModuleImpl(String pluginName) {
        return modules.get(pluginName);
    }

	@Override
	protected Stream<? extends VersatiaModule> getModulesStreamImpl() {
		return modules.values().stream();
	}

	@Override
	protected Set<String> getModulesNamesImpl() {
		return modules.keySet();
	}
}