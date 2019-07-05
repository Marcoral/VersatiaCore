package com.github.marcoral.versatia.core.impl.apiimpl.modules.messages;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;

public class MessageEntryImpl implements VersatiaMessageEntry {
	private final String entryName;
	private final Map<String, Object> properties = new HashMap<>();
	private final Map<String, Object> propertiesUnmodifable = Collections.unmodifiableMap(properties);
	private String templateString;
	
	public MessageEntryImpl(String entryName) {
		this.entryName = entryName;
	}
	
	public void setTemplateString(String templateString) {
		this.templateString = templateString;
	}
	
	public void setMetadataObject(String key, Object value) {
		if(properties.containsKey(key))
			throw new RuntimeException(String.format("There is more than 1 metadata object with key %s in message template: %s!", key, entryName));
		properties.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getMetadataObject(String key) {
		Object property = properties.get(key);
		if(property == null)
			return null;
		return (T) property;
	}
	
	@Override
	public <T> T getMetadataObjectOrThrow(String key) {
		T value = getMetadataObject(key);
		if(value == null)
			throw new RuntimeException(String.format("No metadata object of key %s found in message template: %s!", key, entryName));
		return value;
	}

	@Override
	public String getTemplateString() {
		return templateString;
	}

	@Override
	public Map<String, Object> getMetadataUnmodifable() {
		return propertiesUnmodifable;
	}
}
