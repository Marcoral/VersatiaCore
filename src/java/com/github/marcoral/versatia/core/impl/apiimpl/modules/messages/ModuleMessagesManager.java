package com.github.marcoral.versatia.core.impl.apiimpl.modules.messages;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.Initializer;
import com.github.marcoral.versatia.core.api.VersatiaConstants;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;
import com.github.marcoral.versatia.core.impl.algorithms.TextualNodeDependencyResolver;

public class ModuleMessagesManager implements VersatiaSubmodule {
	@Override
	public String getName() {
		return "messages";
	}

	
	private final File messagesDirectory;
	private final String moduleName;
    private final Map<String, MessageEntryImpl> templates = new HashMap<>();
    private BiConsumer<String, String> messagesProcessor;
    public ModuleMessagesManager(Plugin plugin) {
        this.messagesDirectory = new File(plugin.getDataFolder(), VersatiaConstants.Path.MESSAGES_DIRECTORY);
        this.moduleName = plugin.getName();
    }

    public void setMessageTemplatesProcessor(BiConsumer<String, String> processor) {
        this.messagesProcessor = processor;
        reload();
    }
    
    public VersatiaMessageEntry getTemplate(String key) {
		return templates.get(key);
	}
    
    /*
     * Reload stuff
     */

    @Override
    public void unload() {
        templates.clear();
    }
    
    @Override
    public void load() {
    	final String VALUE_KEY = "Value";
        if(!messagesDirectory.exists())
        	return;
        try {
            TextualNodeDependencyResolver<String> resolver = new TextualNodeDependencyResolver<>();
            Set<String> keys = new HashSet<>();
			Files.walkFileTree(messagesDirectory.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					FileConfiguration configFile = YamlConfiguration.loadConfiguration(path.toFile());
					VersatiaConfigurationProcessor config = VersatiaTools.wrapConfigurationToVersatiaProcessor(configFile);
		            configFile.getKeys(false).forEach(key -> {
		            	boolean unique = keys.add(key);
		                if(!unique)
		                    throw new RuntimeException(String.format("There is more than 1 template which uses key %s!", key));
		            	MessageEntryImpl entry = new MessageEntryImpl(key);
		            	String value;
		            	if(!configFile.isConfigurationSection(key))
		            		value = VersatiaTools.getVersatiaString(configFile.getString(key));
		            	else {
		            		VersatiaConfigurationProcessor subsection = config.getConfigurationSection(key);
		            		value = VersatiaTools.getVersatiaString(subsection.getStringOrThrow(VALUE_KEY, "No \"Value\" key found! It was supposed to contain message template."));
		            		for(String propKey : subsection.getKeys(false))
		            			if(!propKey.equals(VALUE_KEY))
		            				entry.setMetadataObject(propKey, subsection.get(propKey));
		            	}
		                value = referenceExternalMessageParts(value);
		                if(messagesProcessor != null)
		                    messagesProcessor.accept(key, value);
		                resolver.newEntry(key, value, String.format(VersatiaCoreConstants.Patterns.MESSAGES_REFERENCE, key));
		                templates.put(key, entry);
		            });
					return FileVisitResult.CONTINUE;
				}
			});
	        resolver.resolve((key, value) -> templates.get(key).setTemplateString(value));
	        Initializer.logIfPossible(logger -> logger.finest("TotalLoadedMessageTemplates", moduleName, templates.size()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("An error occurred when scanning for message templates!");
		}
    }

    private String referenceExternalMessageParts(String base) {
        Matcher matcher = VersatiaCoreConstants.Patterns.MESSAGES_REFERENCE_PATTERN.matcher(base);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            String referencedModuleName = matcher.group(1);
            String referencedNode = matcher.group(3);
            
            //That means that in fact no referenced module name was found
            if(referencedNode == null)
            	continue;

            VersatiaModule module = VersatiaModules.getModule(referencedModuleName);
            if(module == null)
                throw new RuntimeException(String.format("Referenced unknown module: %s! Have you added it as a dependency?", referencedModuleName));
            String referencedText = module.getMessageTemplate(referencedNode);
            if(referencedText == null)
                throw new RuntimeException(String.format("No message node have been found: %s", matcher.group(0)));
            matcher.appendReplacement(buffer, referencedText);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}