package com.github.marcoral.versatia.core.impl;

import java.util.regex.Pattern;

public class VersatiaCoreConstants {
    public static class Paths {
        public static final String COLORCODDES = "colorcodes.yml";
    }

    public static class Patterns {
        public static final String MESSAGES_REFERENCE = "$%s$";
        public static final Pattern MESSAGES_REFERENCE_PATTERN = Pattern.compile("\\$(\\w+)\\.(\\w+)\\$");
    }
    
    public static class Permissions {
    	public static final String COMMAND_RELOAD = "versatia.core.reload";
		public static final String COMMAND_REGENERATE = "versatia.core.regenerate";
    }
}