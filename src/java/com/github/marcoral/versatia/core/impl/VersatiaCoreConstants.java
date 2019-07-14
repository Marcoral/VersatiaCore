package com.github.marcoral.versatia.core.impl;

import java.util.regex.Pattern;

public class VersatiaCoreConstants {
	public static class Names {
		public static final String PRIMARY_LOGGER = "PrimaryLogger";
	}
	
    public static class Paths {
        public static final String COLORCODDES = "colorcodes.yml";
    }

    public static class Patterns {
        public static final String MESSAGES_REFERENCE = "$%s$";
        
        //If third group is null, then the first one stands for node name
        //Otherwise, first group stands for module name and third one for node name
        public static final Pattern MESSAGES_REFERENCE_PATTERN = Pattern.compile("\\$(\\w+)(\\.(\\w+))?\\$");
    }
    
    public static class Permissions {
    	public static final String COMMAND_RELOAD = "versatia.core.reload";
		public static final String COMMAND_REGENERATE = "versatia.core.regenerate";
    }
}