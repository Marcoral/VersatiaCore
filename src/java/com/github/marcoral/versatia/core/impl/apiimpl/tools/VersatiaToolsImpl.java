package com.github.marcoral.versatia.core.impl.apiimpl.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.tools.ExternalDependency;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.apiimpl.UsedExplicitly;
import com.github.marcoral.versatia.core.impl.apiimpl.configuration.VersatiaConfigurationFileImpl;

public class VersatiaToolsImpl extends VersatiaTools {
    @Override
    @UsedExplicitly
    public <T> void injectExternalDependencyImpl(Class<? super T> clazz, T instance, String keyOfExternal, Object value, boolean fieldSurelyExists) throws RuntimeException {
        Field[] fields = clazz.getDeclaredFields();
        Field destination = null;
        for(Field field : fields) {
            if(field.isAnnotationPresent(ExternalDependency.class) && field.getAnnotation(ExternalDependency.class).value().equals(keyOfExternal))
                if(destination == null)
                    destination = field;
                else
                    throw new RuntimeException(String.format("At least two fields in the class %s have the same external key: %s!", clazz, keyOfExternal));
        }
        if(destination == null)
            if(!fieldSurelyExists)
                return;
            else
                throw new RuntimeException(String.format("No external field with key %s in class %s have been found!", keyOfExternal, clazz));
        destination.setAccessible(true);
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                modifiersField.setAccessible(true);
                return null;
            });

            modifiersField.setInt(destination, destination.getModifiers() & ~Modifier.FINAL);
            destination.set(instance, value);
            destination.setAccessible(false);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException("Exception caught during injecting external field", e);
        } finally {
            destination.setAccessible(false);
        }
    }

    @Override
    protected VersatiaConfigurationFile searchForConfigurationFileImpl(String filePath) {
        return new VersatiaConfigurationFileImpl(new File(filePath));
    }

    @Override
    protected void unpackFilesImpl(Plugin module, boolean overrideExisting) {
        File destination = module.getDataFolder();
        destination.mkdirs();
        try {
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
                    }
                }
            }
            jar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}