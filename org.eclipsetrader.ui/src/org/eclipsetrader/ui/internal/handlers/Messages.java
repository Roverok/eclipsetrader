package org.eclipsetrader.ui.internal.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.handlers.messages"; //$NON-NLS-1$
	public static String MarketPropertiesHandler_PropertiesFor;
	public static String SecurityPropertiesHandler_PropertiesFor;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
