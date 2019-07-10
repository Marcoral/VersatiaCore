package com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules;

import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;

public class VersatiaCoreSubmoduleDecorator implements VersatiaSubmodule {
	private final VersatiaSubmodule base;
	private final InitializerSubmoduleProperty property;
	public VersatiaCoreSubmoduleDecorator(VersatiaSubmodule base, InitializerSubmoduleProperty property) {
		this.base = base;
		this.property = property;
	}
	
	@Override
	public final void unload() {
		property.markSetUp(false);
		base.unload();
	}

	@Override
	public final void load() {
		base.load();
		property.markSetUp(true);
	}
	
	@Override
	public final void shutdown() {
		property.markSetUp(false);
		base.shutdown();
	}

	@Override
	public String getName() {
		return base.getName();
	}
}
