package com.github.marcoral.versatia.core.impl.apiimpl.modules.submodules;

public class InitializerSubmoduleProperty {
	private boolean isSetUp;
	
	public void markSetUp(boolean isSetUp) {
		this.isSetUp = isSetUp;
	}
	
	public boolean isSetUp() {
		return this.isSetUp;
	}
}
