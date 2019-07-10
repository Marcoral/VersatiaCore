package com.github.marcoral.versatia.core.impl.apiimpl.modules.messages;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageDescriptor;

public class MessageDescriptorImpl implements VersatiaMessageDescriptor {
	private final VersatiaModule module;
	private final String messageTemplateKey;
	public MessageDescriptorImpl(VersatiaModule module, String messageTemplateKey) {
		this.module = module;
		this.messageTemplateKey = messageTemplateKey;
	}

	@Override
	public String supplyMessageTemplate() {
		return module.getMessageTemplate(messageTemplateKey);
	}
}