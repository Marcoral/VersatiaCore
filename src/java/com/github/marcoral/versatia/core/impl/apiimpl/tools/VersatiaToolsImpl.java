package com.github.marcoral.versatia.core.impl.apiimpl.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.Initializer;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.apiimpl.configuration.VersatiaConfigurationFileImpl;
import com.github.marcoral.versatia.core.impl.apiimpl.configuration.VersatiaConfigurationProcessorImpl;

public class VersatiaToolsImpl extends VersatiaTools {
    @Override
    protected VersatiaConfigurationFile searchForConfigurationFileImpl(File file) {
        return new VersatiaConfigurationFileImpl(file);
    }
    
	@Override
	protected VersatiaConfigurationProcessor wrapConfigurationToVersatiaProcessorImpl(ConfigurationSection configurationSection) {
		return new VersatiaConfigurationProcessorImpl(configurationSection);
	}

    @Override
    protected void unpackFilesImpl(Plugin module, boolean overrideExisting) {
        File destination = module.getDataFolder();
        try {
            destination.mkdirs();
            String rawPath = module.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            String utfPath = URLDecoder.decode(rawPath, "UTF-8");
            JarFile jar = new JarFile(utfPath);
            Enumeration<JarEntry> enu = jar.entries();
            while (enu.hasMoreElements()) {
                JarEntry entry = enu.nextElement();
                String entryPath = entry.getName();
                if (!entryPath.startsWith("resources"))
                    continue;
                entryPath = entryPath.substring("resources".length());

                File fl = new File(destination, entryPath);
                if (!fl.exists() || overrideExisting) {
                    if (!overrideExisting)
                        fl.getParentFile().mkdirs();
                    if (entry.isDirectory())
                        continue;
                    try (InputStream is = jar.getInputStream(entry); FileOutputStream fo = new FileOutputStream(fl)) {
                        while (is.available() > 0)
                            fo.write(is.read());
                    	Initializer.logIfPossible(logger -> logger.finer("ResourceUnpacked", fl.getPath()));
                    }
                }
            }
            jar.close();
        } catch (IOException e) {
        	Initializer.logIfPossible(logger -> logger.severe("ResourcesUnpackingError", module.getName()));
            e.printStackTrace();
        }
    }
}