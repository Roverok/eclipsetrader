package org.eclipsetrader.ui.internal.securities;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.securities.messages"; //$NON-NLS-1$
	public static String FeedPropertiesControl_Default;
	public static String FeedPropertiesControl_Property;
	public static String FeedPropertiesControl_Value;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
