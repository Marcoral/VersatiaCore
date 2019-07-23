package com.github.marcoral.versatia.core.impl.apiimpl.modules;

import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.impl.apiimpl.modules.loggers.ModuleLoggersManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.messages.ModuleMessagesManager;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules.InitializerSubmoduleProperty;
import com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules.VersatiaCoreSubmoduleDecorator;

public class CoreModuleImpl extends VersatiaModuleImpl {
	private final InitializerSubmoduleProperty loggerProperty;
	public CoreModuleImpl(Plugin plugin, InitializerSubmoduleProperty loggerProperty) {
		super(plugin);
		this.loggerProperty = loggerProperty;
	}
	
	@Override
	public void addDefaultSubmodules() {
    	messagesManager = new ModuleMessagesManager(plugin);
    	loggersManager = new ModuleLoggersManager(this);
    	submodulesManager.addSubmodule(messagesManager);
    	submodulesManager.addSubmodule(new VersatiaCoreSubmoduleDecorator(loggersManager, loggerProperty));
	}
}
