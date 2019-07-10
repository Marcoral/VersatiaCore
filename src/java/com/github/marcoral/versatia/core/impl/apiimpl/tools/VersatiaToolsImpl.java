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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import com.github.marcoral.versatia.core.Initializer;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.tools.ExternalDependency;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.apiimpl.configuration.VersatiaConfigurationFileImpl;
import com.github.marcoral.versatia.core.impl.apiimpl.configuration.VersatiaConfigurationProcessorImpl;

public class VersatiaToolsImpl extends VersatiaTools {
	private static abstract class Monade<T, V> {
		private V value;
		
		public final void applyAndSaveResult(T t) throws Exception {
			value = apply(t);
		}
		
		public abstract V apply(T t) throws Exception;
		
		public final V getReturnedValue() {
			return value;
		}
	}
	
    @Override
    public <T> void injectExternalDependencyImpl(Class<? super T> containingClass, T instance, String fieldKey, Object value, boolean fieldSurelyExists) throws NoBridgeFieldException {
        performActionOnBridgeField(containingClass, fieldKey, fieldSurelyExists, new Monade<Field, Void>() {
			@Override
			public Void apply(Field t) throws Exception {
				t.set(instance, value);
				return null;
			}
		});
    }
        
	@Override
	protected <T> T getExternalFieldImpl(Object instance, String fieldKey) throws NoBridgeFieldException {
		Class<?> containingClass = instance.getClass();
		Monade<Field, T> monade = new Monade<Field, T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T apply(Field t) throws Exception {
				return (T) t.get(instance);
			}
		};
		performActionOnBridgeField(containingClass, fieldKey, true, monade);
		return monade.getReturnedValue();
	}
	
	private <V> void performActionOnBridgeField(Class<?> containingClass, String fieldKey, boolean fieldSurelyExists, Monade<Field, V> monade) {
		Field[] fields = containingClass.getDeclaredFields();
        Field destination = null;
        for(Field field : fields) {
            if(field.isAnnotationPresent(ExternalDependency.class) && field.getAnnotation(ExternalDependency.class).value().equals(fieldKey))
                if(destination == null)
                    destination = field;
                else
                    throw new RuntimeException(String.format("At least two fields in the class %s have the same bridge field key: %s!", containingClass, fieldKey));
        }
        if(destination == null)
            if(!fieldSurelyExists)
                return;
            else
                throw new NoBridgeFieldException(fieldKey, containingClass);
        destination.setAccessible(true);
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                modifiersField.setAccessible(true);
                return null;
            });

            modifiersField.setInt(destination, destination.getModifiers() & ~Modifier.FINAL);
            monade.applyAndSaveResult(destination);
            destination.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException("Exception caught during accessing bridge field", e);
        } finally {
            destination.setAccessible(false);
        }
	}

    @Override
    protected VersatiaConfigurationFile searchForConfigurationFileImpl(String filePath) {
        return new VersatiaConfigurationFileImpl(new File(filePath));
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