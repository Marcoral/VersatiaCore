package com.github.marcoral.versatia.core.impl.apiimpl.modules.messages;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.messages.VersatiaMessageEntry;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaModules;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;
import com.github.marcoral.versatia.core.impl.algorithms.TextualNodeDependencyResolver;

public class ModuleMessagesManager implements VersatiaSubmodule {
	private File messagesDirectory;
    private Map<String, MessageEntryImpl> templates = new HashMap<>();
    private BiConsumer<String, String> messagesProcessor;
    public ModuleMessagesManager(File messagesDirectory) {
        this.messagesDirectory = messagesDirectory;
    }

    public void setMessageTempaltesProcessor(BiConsumer<String, String> processor) {
        this.messagesProcessor = processor;
    }
    
    public VersatiaMessageEntry getTemplate(String key) {
		return templates.get(key);
	}
    
    /*
     * Reload stuff
     */

    @Override
    public void reload() {
    	final String VALUE_KEY = "Value";
    	
        templates.clear();
        File[] messagesFiles = messagesDirectory.listFiles();
        if(messagesFiles == null)
            return;
        try {
            TextualNodeDependencyResolver<String> resolver = new TextualNodeDependencyResolver<>();
			Files.walkFileTree(messagesDirectory.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					FileConfiguration config = YamlConfiguration.loadConfiguration(path.toFile());
		            config.getKeys(false).forEach(key -> {
		            	MessageEntryImpl entry = new MessageEntryImpl(key);
		            	String value;
		            	if(!config.isConfigurationSection(key))
		            		value = VersatiaTools.getVersatiaString(config.getString(key));
		            	else {
		            		ConfigurationSection subsection = config.getConfigurationSection(key);
		            		value = VersatiaTools.getVersatiaString(subsection.getString(VALUE_KEY));
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
		} catch (IOException e) {
			throw new RuntimeException("An error occurred when scanning for message templates!");
		}
    }

    private String referenceExternalMessageParts(String base) {
        Matcher matcher = VersatiaCoreConstants.Patterns.MESSAGES_REFERENCE_PATTERN.matcher(base);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            String referencedModuleName = matcher.group(1);
            String referencedNode = matcher.group(2);
            VersatiaModule module = VersatiaModules.getModule(referencedModuleName);
            if(module == null)
                throw new RuntimeException(String.format("Referenced unknown module: %s! Have you added it as a dependency?", referencedModuleName));
            String referencedText = module.getMessageTemplate(referencedNode);
            if(referencedText == null)
                throw new RuntimeException(String.format("No message node have been found: %s", matcher.group(0)));
            matcher.group();
            matcher.appendReplacement(buffer, referencedText);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}