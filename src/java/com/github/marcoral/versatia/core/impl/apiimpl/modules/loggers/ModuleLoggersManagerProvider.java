package com.github.marcoral.versatia.core.impl.apiimpl.modules.loggers;

import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.modules.VersatiaSubmoduleHandlerProvider;

public class ModuleLoggersManagerProvider implements VersatiaSubmoduleHandlerProvider {
	private final VersatiaModule parent; 
	public ModuleLoggersManagerProvider(VersatiaModule parent) {
		this.parent = parent;
	}
	
	@Override
	public Plugin getRegisterer() {
		return parent.getCorrespondingPlugin();
	}

	@Override
	public VersatiaSubmodule createHandler(VersatiaModule module) {
		return new ModuleLoggersManager(module);
	}
}
