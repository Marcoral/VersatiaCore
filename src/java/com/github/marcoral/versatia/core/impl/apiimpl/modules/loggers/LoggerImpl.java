package com.github.marcoral.versatia.core.impl.apiimpl.modules.loggers;

import java.text.MessageFormat;

import org.bukkit.Bukkit;

import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.loggers.LoggingPriority;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLogger;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageDescriptor;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;

public class LoggerImpl implements VersatiaLogger {
	private final VersatiaModule owner;
	
	private LoggingPriority priorityThreshold;
	private VersatiaMessageDescriptor[] prefixMessageDescriptors;
	
	public LoggerImpl(LoggingPriority priorityThreshold, VersatiaModule owner) {
		this(priorityThreshold, new VersatiaMessageDescriptor[LoggingPriority.values().length], owner);
	}
	
	public LoggerImpl(LoggingPriority priorityThreshold, VersatiaMessageDescriptor[] prefixMessageDescriptors, VersatiaModule owner) {
		setPriorityThreshold(priorityThreshold);
		this.owner = owner;
		int expectedDescriptorsCount = LoggingPriority.values().length;
		int receivedDescriptorsCount = prefixMessageDescriptors.length; 
		if(receivedDescriptorsCount != expectedDescriptorsCount)
			throw new IllegalArgumentException(String.format("Illegal amount of prefixMessageDescriptors! Expected %d but received %d!", expectedDescriptorsCount, receivedDescriptorsCount));
		this.prefixMessageDescriptors = prefixMessageDescriptors;
		fixInitiallyNullDescriptors();
	}
	
	private void fixInitiallyNullDescriptors() {
		for(int i = 0; i < prefixMessageDescriptors.length; ++i)
			if(prefixMessageDescriptors[i] == null)
				prefixMessageDescriptors[i] = VersatiaMessages.createTemplateDescriptor(VersatiaConstants.VERSATIA_CORE_NAME, "LoggersPrefix" + LoggingPriority.values()[i].getConfigName());
	}
	
	@Override
	public void setPriorityThreshold(LoggingPriority priorityThreshold) {
		if(priorityThreshold == null)
			priorityThreshold = LoggingPriority.FINEST;
		this.priorityThreshold = priorityThreshold;
	}
	
	@Override
	public void setPrefix(String messageTemplate) {
		for(int i = 0; i < prefixMessageDescriptors.length; ++i)
			this.prefixMessageDescriptors[i] = VersatiaMessages.createTemplateDescriptor(owner, messageTemplate);
	}
	
	@Override
	public void setPrefix(LoggingPriority priority, String messageTemplate) {
		this.prefixMessageDescriptors[priority.ordinal()] = VersatiaMessages.createTemplateDescriptor(owner, messageTemplate);
	}
	
	@Override
	public void setPrefix(VersatiaMessageDescriptor messageDescriptor) {
		for(int i = 0; i < prefixMessageDescriptors.length; ++i)
			this.prefixMessageDescriptors[i] = messageDescriptor;
	}

	@Override
	public void setPrefix(LoggingPriority priority, VersatiaMessageDescriptor messageDescriptor) {
		this.prefixMessageDescriptors[priority.ordinal()] = messageDescriptor;
	}
	
	@Override
	public void log(LoggingPriority priority, String messageTemplate, Object... args) {
		String messageToLog = owner.getMessageTemplate(messageTemplate);
		if(priority.getLevel() >= priorityThreshold.getLevel())
			Bukkit.getConsoleSender().sendMessage(prefixMessageDescriptors[priority.ordinal()].supplyMessageTemplate() + MessageFormat.format(messageToLog, (Object[]) args));
	}

	@Override
	public void log(LoggingPriority priority, VersatiaMessageDescriptor messageDescriptor, Object... args) {
		String messageToLog = messageDescriptor.supplyMessageTemplate();
		if(priority.getLevel() >= priorityThreshold.getLevel())
			Bukkit.getConsoleSender().sendMessage(prefixMessageDescriptors[priority.ordinal()].supplyMessageTemplate() + MessageFormat.format(messageToLog, (Object[]) args));
	}
}
