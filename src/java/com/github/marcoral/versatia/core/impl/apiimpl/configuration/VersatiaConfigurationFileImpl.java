package com.github.marcoral.versatia.core.impl.apiimpl.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;

public class VersatiaConfigurationFileImpl implements VersatiaConfigurationFile {
    private File destination;
    private YamlConfiguration config;
    public VersatiaConfigurationFileImpl(File destination) {
        this.destination = destination;
    }

    @Override
    public boolean exists() {
        return destination.exists();
    }

    @Override
    public VersatiaConfigurationProcessor getProcessor() {
        if(config == null) {
            if(destination.exists())
                config = YamlConfiguration.loadConfiguration(destination);
            else
                config = new YamlConfiguration();
        }
        return new VersatiaConfigurationProcessorImpl(config);
    }
    
    @Override
    public VersatiaConfigurationProcessor getProcessorIgnoreContent() {
        if(config == null)
        	config = new YamlConfiguration();
        return new VersatiaConfigurationProcessorImpl(config);
    }

    @Override
    public void saveData() {
        if(config == null)
            return;
        try {
            if(!destination.exists()) {
                destination.getParentFile().mkdirs();
                destination.createNewFile();
            }
            config.save(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public File getUnderlyingFile() {
		return destination;
	}
}