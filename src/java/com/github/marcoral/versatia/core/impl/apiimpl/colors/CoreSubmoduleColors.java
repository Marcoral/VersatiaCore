package com.github.marcoral.versatia.core.impl.apiimpl.colors;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import com.github.marcoral.versatia.core.api.colors.ColorConverter;
import com.github.marcoral.versatia.core.api.colors.VersatiaColor;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationFile;
import com.github.marcoral.versatia.core.api.configuration.VersatiaConfigurationProcessor;
import com.github.marcoral.versatia.core.api.modules.VersatiaModule;
import com.github.marcoral.versatia.core.api.modules.submodules.VersatiaSubmodule;
import com.github.marcoral.versatia.core.api.tools.VersatiaTools;
import com.github.marcoral.versatia.core.impl.VersatiaCoreConstants;
import com.github.marcoral.versatia.core.impl.algorithms.TextualNodeDependencyResolver;

public class CoreSubmoduleColors implements VersatiaSubmodule, ColorConverter {
    private Map<VersatiaColor, String> colorTranslations = new EnumMap<>(VersatiaColor.class);

    private Logger versatiaLogger;
    private VersatiaModule versatia;
    public CoreSubmoduleColors(Logger versatiaLogger, VersatiaModule versatia) {
        this.versatiaLogger = versatiaLogger;
        this.versatia = versatia;
    }

    @Override
    public void reload() {
        colorTranslations.clear();
        VersatiaConfigurationFile config = versatia.getConfig(VersatiaCoreConstants.Paths.COLORCODDES);
        if(!config.exists()) {
            versatiaLogger.warning(String.format("File %s does not exist! Regenerating configuration...", VersatiaCoreConstants.Paths.COLORCODDES));
            versatia.regenerateConfiguration();
            config = versatia.getConfig(VersatiaCoreConstants.Paths.COLORCODDES);
        }
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