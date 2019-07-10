package com.github.marcoral.versatia.core.impl.apiimpl.colors;

import java.util.EnumMap;
import java.util.Map;

import com.github.marcoral.versatia.core.api.colors.ColorConverter;
import com.github.marcoral.versatia.core.api.colors.VersatiaColor;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.modules.UnloadedModuleAccessSave;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;
import com.github.marcoral.versatia.core.impl.algorithms.TextualNodeDependencyResolver;

public class CoreSubmoduleColors implements VersatiaSubmodule, ColorConverter {
	@Override
	public String getName() {
		return "colorcodes";
	}
	
    private Map<VersatiaColor, String> colorTranslations = new EnumMap<>(VersatiaColor.class);

    private UnloadedModuleAccessSave module;
    public CoreSubmoduleColors(UnloadedModuleAccessSave module) {
        this.module = module;
    }
    
    @Override
    public void unload() {
        colorTranslations.clear();
    }

    @Override
    public void load() {
        VersatiaConfigurationFile config = module.getConfig(VersatiaCoreConstants.Paths.COLORCODDES);
        VersatiaConfigurationProcessor processor = config.getProcessor();
        TextualNodeDependencyResolver<VersatiaColor> resolver = new TextualNodeDependencyResolver<>();
        for (VersatiaColor color : VersatiaColor.values()) {
            String keyString = color.name();
            String value = processor.getStringOrThrow(keyString, String.format("No keycode found for node %s!", keyString));
            resolver.newEntry(color, value, color.getReferenceKey());
        }
        resolver.resolve((color, string) -> colorTranslations.put(color, VersatiaTools.getColoredString(string)));
    }

    @Override
    public String convert(VersatiaColor color) {
        return colorTranslations.get(color);
    }
}