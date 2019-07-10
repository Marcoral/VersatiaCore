package com.github.marcoral.versatia.core.impl.apiimpl.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageDescriptor;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessages;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;

public class VersatiaConfigurationProcessorImpl implements VersatiaConfigurationProcessor {
    private ConfigurationSection data;
    public VersatiaConfigurationProcessorImpl(ConfigurationSection data) {
        this.data = data;
    }

    @Override
    public boolean getBooleanOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getBoolean(key);
    }

    @Override
    public List<Boolean> getBooleanListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getBooleanList(key);
    }

    @Override
    public List<Byte> getByteListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getByteList(key);
    }

    @Override
    public List<Character> getCharacterListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getCharacterList(key);
    }

    @Override
    public double getDoubleOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getDouble(key);
    }

    @Override
    public List<Double> getDoubleListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getDoubleList(key);
    }

    @Override
    public List<Float> getFloatListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getFloatList(key);
    }

    @Override
    public int getIntOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getInt(key);
    }

    @Override
    public List<Integer> getIntegerListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getIntegerList(key);
    }

    @Override
    public long getLongOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getLong(key);
    }

    @Override
    public List<Long> getLongListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getLongList(key);
    }

    @Override
    public String getStringOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getString(key);
    }

    @Override
    public List<String> getStringListOrThrow(String key, String exceptionMessage) {
        if(!contains(key))
            throw new NullPointerException(exceptionMessage);
        return getStringList(key);
    }

    @Override
    public void moveToSectionOrThrow(String key, String exceptionMessage) {
        if(!isConfigurationSection(key))
            throw new NullPointerException(exceptionMessage);
        ConfigurationSection section = getConfigurationSection(key);
        data = section;
    }

    @Override
    public String getColoredStringOrThrow(String key, String exceptionMessage) {
        String message = getStringOrThrow(key, exceptionMessage);
        return VersatiaTools.getColoredString(message);
    }

    @Override
    public String getVersatiaStringOrThrow(String key, String exceptionMessage) {
        return VersatiaTools.getVersatiaString(getStringOrThrow(key, exceptionMessage));
    }
	
	/* ---------
	 * Delegates
	 ----------- */

    @Override
    public Set<String> getKeys(boolean deep) {
        return data.getKeys(deep);
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return data.getValues(deep);
    }

    @Override
    public boolean contains(String path) {
        return data.contains(path);
    }

    @Override
    public boolean contains(String path, boolean ignoreDefault) {
        return data.contains(path, ignoreDefault);
    }

    @Override
    public boolean isSet(String path) {
        return data.isSet(path);
    }

    @Override
    public String getCurrentPath() {
        return data.getCurrentPath();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public Configuration getRoot() {
        return data.getRoot();
    }

    @Override
    public ConfigurationSection getParent() {
        return data.getParent();
    }

    @Override
    public Object get(String path) {
        return data.get(path);
    }

    @Override
    public Object get(String path, Object def) {
        return data.get(path, def);
    }

    @Override
    public void set(String path, Object value) {
        data.set(path, value);
    }

    @Override
    public ConfigurationSection createSection(String path) {
        return data.createSection(path);
    }

    @Override
    public ConfigurationSection createSection(String path, Map<?, ?> map) {
        return data.createSection(path, map);
    }

    @Override
    public String getString(String path) {
        return data.getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return data.getString(path, def);
    }

    @Override
    public boolean isString(String path) {
        return data.isString(path);
    }

    @Override
    public int getInt(String path) {
        return data.getInt(path);
    }

    @Override
    public int getInt(String path, int def) {
        return data.getInt(path, def);
    }

    @Override
    public boolean isInt(String path) {
        return data.isInt(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return data.getBoolean(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return data.getBoolean(path, def);
    }

    @Override
    public boolean isBoolean(String path) {
        return data.isBoolean(path);
    }

    @Override
    public double getDouble(String path) {
        return data.getDouble(path);
    }

    @Override
    public double getDouble(String path, double def) {
        return data.getDouble(path, def);
    }

    @Override
    public boolean isDouble(String path) {
        return data.isDouble(path);
    }

    @Override
    public long getLong(String path) {
        return data.getLong(path);
    }

    @Override
    public long getLong(String path, long def) {
        return data.getLong(path, def);
    }

    @Override
    public boolean isLong(String path) {
        return data.isLong(path);
    }

    @Override
    public List<?> getList(String path) {
        return data.getList(path);
    }

    @Override
    public List<?> getList(String path, List<?> def) {
        return data.getList(path, def);
    }

    @Override
    public boolean isList(String path) {
        return data.isList(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return data.getStringList(path);
    }

    @Override
    public List<Integer> getIntegerList(String path) {
        return data.getIntegerList(path);
    }

    @Override
    public List<Boolean> getBooleanList(String path) {
        return data.getBooleanList(path);
    }

    @Override
    public List<Double> getDoubleList(String path) {
        return data.getDoubleList(path);
    }

    @Override
    public List<Float> getFloatList(String path) {
        return data.getFloatList(path);
    }

    @Override
    public List<Long> getLongList(String path) {
        return data.getLongList(path);
    }

    @Override
    public List<Byte> getByteList(String path) {
        return data.getByteList(path);
    }

    @Override
    public List<Character> getCharacterList(String path) {
        return data.getCharacterList(path);
    }

    @Override
    public List<Short> getShortList(String path) {
        return data.getShortList(path);
    }

    @Override
    public List<Map<?, ?>> getMapList(String path) {
        return data.getMapList(path);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getObject(String s, Class<T> aClass) {
        Object result = data.get(s, aClass);
        if (result == null)
        	return null;
        else
        	return (T) result;
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass, T t) {
        return data.getObject(s, aClass, t);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz) {
        return data.getSerializable(path, clazz);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz, T def) {
        return data.getSerializable(path, clazz, def);
    }

    @Override
    public Vector getVector(String path) {
        return data.getVector(path);
    }

    @Override
    public Vector getVector(String path, Vector def) {
        return data.getVector(path, def);
    }

    @Override
    public boolean isVector(String path) {
        return data.isVector(path);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String path) {
        return data.getOfflinePlayer(path);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
        return data.getOfflinePlayer(path, def);
    }

    @Override
    public boolean isOfflinePlayer(String path) {
        return data.isOfflinePlayer(path);
    }

    @Override
    public ItemStack getItemStack(String path) {
        return data.getItemStack(path);
    }

    @Override
    public ItemStack getItemStack(String path, ItemStack def) {
        return data.getItemStack(path, def);
    }

    @Override
    public boolean isItemStack(String path) {
        return data.isItemStack(path);
    }

    @Override
    public Color getColor(String path) {
        return data.getColor(path);
    }

    @Override
    public Color getColor(String path, Color def) {
        return data.getColor(path, def);
    }

    @Override
    public boolean isColor(String path) {
        return data.isColor(path);
    }

    @Override
    public VersatiaConfigurationProcessor getConfigurationSection(String path) {
        return new VersatiaConfigurationProcessorImpl(data.getConfigurationSection(path));
    }

    @Override
    public boolean isConfigurationSection(String path) {
        return data.isConfigurationSection(path);
    }

    @Override
    public ConfigurationSection getDefaultSection() {
        return data.getDefaultSection();
    }

    @Override
    public void addDefault(String path, Object value) {
        data.addDefault(path, value);
    }

	@Override
	public VersatiaConfigurationProcessor getConfigurationSectionOrThrow(String key, String exceptionMessage) {
		if(!isConfigurationSection(key))
            throw new NullPointerException(exceptionMessage);
        ConfigurationSection section = getConfigurationSection(key);
        return new VersatiaConfigurationProcessorImpl(section);
	}

	@Override
	public boolean moveToSectionIfPossible(String key) {
		if(!isConfigurationSection(key))
			return false;
		data = getConfigurationSection(key);
		return true;
	}

	@Override
	public VersatiaMessageDescriptor getMessageDescriptor(String key, VersatiaModule defaultModule) {
		String message = getString(key);
        Matcher matcher = VersatiaCoreConstants.Patterns.MESSAGES_REFERENCE_PATTERN.matcher(message);
        if(!matcher.find())
        	return null;
        String referencedModuleName = matcher.group(1);
        String referencedNode = matcher.group(3);
        
        //That means that in fact no referenced module name was found
        if(referencedNode == null)
        	//In that case referencedModuleName is in fact referenced node key
            return VersatiaMessages.createTemplateDescriptor(defaultModule, referencedModuleName);
        else
        	return VersatiaMessages.createTemplateDescriptor(referencedModuleName, referencedNode);
	}

	@Override
	public VersatiaMessageDescriptor getMessageDescriptorOrThrow(String key, VersatiaModule defaultModule, String exceptionMessage) {
        if(!isString(key))
            throw new NullPointerException(exceptionMessage);
		return getMessageDescriptor(key, defaultModule);
	}
}