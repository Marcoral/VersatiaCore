package com.github.marcoral.versatia.core.impl.apiimpl.modules.messages;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageDescriptor;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;

public class VersatiaMessagesImpl extends VersatiaMessages {
	@Override
	protected VersatiaMessageDescriptor createTemplateDescriptorImpl(VersatiaModule module, String messageTemplateKey) {
		if(module.getMessageTemplate(messageTemplateKey) == null)
			return null;
		return new MessageDescriptorImpl(module, messageTemplateKey);
	}
}