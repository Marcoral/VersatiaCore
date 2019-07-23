package com.github.marcoral.versatia.core.impl.apiimpl.modules.messages;

import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.modules.VersatiaSubmoduleHandlerProvider;

public class ModuleMessagesManagerProvider implements VersatiaSubmoduleHandlerProvider {
	private final Plugin registerer; 
	public ModuleMessagesManagerProvider(Plugin registerer) {
		this.registerer = registerer;
	}
	
	@Override
	public Plugin getRegisterer() {
		return registerer;
	}

	@Override
	public VersatiaSubmodule createHandler(VersatiaModule module) {
		return new ModuleMessagesManager(module.getCorrespondingPlugin());
	}
}
