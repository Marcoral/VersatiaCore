package com.github.marcoral.versatia.core.impl.apiimpl.modules.loggers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.marcoral.versatia.core.Initializer;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.loggers.LoggingPriority;
import com.github.marcoral.versatia.core.api.modules.loggers.VersatiaLogger;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageDescriptor;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;

public class ModuleLoggersManager implements VersatiaSubmodule {
	@Override
	public String getName() {
		return "loggers";
	}
	
	
	private final File loggersDirectory;
	private final String moduleName;
	private final VersatiaModule parent;
	
    private final Map<String, VersatiaLogger> loggers = new HashMap<>();
    public ModuleLoggersManager(File loggersDirectory, String moduleName, VersatiaModule parent) {
        this.loggersDirectory = loggersDirectory;
        this.moduleName = moduleName;
        this.parent = parent;
    }
    
    public VersatiaLogger getLogger(String name) {
		return loggers.get(name);
	}
    
    /*
     * Reload stuff
     */
    
    @Override
    public void unload() {
        loggers.clear();
    }
	
	@Override
	public void load() {    	
        try {
			Files.walkFileTree(loggersDirectory.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					FileConfiguration configFile = YamlConfiguration.loadConfiguration(path.toFile());
            		VersatiaConfigurationProcessor config = VersatiaTools.wrapConfigurationToVersatiaProcessor(configFile);
					configFile.getKeys(false).forEach(loggerName -> {
						VersatiaLogger newLogger;

		            	if(!configFile.isConfigurationSection(loggerName)) {
		            		LoggingPriority threshold = getPriorityThreshold(config, loggerName, loggerName);
		            		newLogger = new LoggerImpl(threshold, parent);
		            	} else {
		            		VersatiaConfigurationProcessor subsection = config.getConfigurationSection(loggerName);
		            		LoggingPriority threshold = getPriorityThreshold(subsection, loggerName, "PriorityThreshold");
		            		VersatiaMessageDescriptor[] descriptors = getMessageDescriptors(subsection);
		            		newLogger = new LoggerImpl(threshold, descriptors, parent);
		            	}
						
		            	Initializer.logIfPossible(logger -> logger.finest("LoggerRegistered", moduleName, loggerName));
						loggers.put(loggerName, newLogger);
					});
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("An error occurred when scanning for message templates!");
		}
	}
	
	private LoggingPriority getPriorityThreshold(VersatiaConfigurationProcessor section, String loggerName, String key) {
		try {
			String priorityString = section.getStringOrThrow(key, "No \"PriorityThreshold\" key found! It was supposed to contain minimal priority of messages which should be logged.");
			LoggingPriority priorityThreshold = LoggingPriority.valueOf(priorityString); 
			return priorityThreshold;
		} catch(IllegalArgumentException e) {
			String allowedValues = Arrays.toString(LoggingPriority.values());
			throw new RuntimeException(String.format("Invalid priority for logger %s have been found! Allowed values are: %s.", loggerName, allowedValues));
		}
	}
	
	private VersatiaMessageDescriptor[] getMessageDescriptors(VersatiaConfigurationProcessor config) {		
		LoggingPriority[] priorities = LoggingPriority.values();
		VersatiaMessageDescriptor[] descriptors = new VersatiaMessageDescriptor[priorities.length];
		for(LoggingPriority priority : priorities) {
			String key = String.format("Prefix%s", priority.getConfigName());
			if(config.contains(key))
				descriptors[priority.ordinal()] = config.getMessageDescriptor(key, parent);
		}
		
		config.ifMessageDescriptorPresent("GenericPrefix", parent, prefix -> {
			for(int i = 0; i < descriptors.length; ++i)
				if(descriptors[i] == null)
					descriptors[i] = prefix;
		});
		
		return descriptors;
	}
}