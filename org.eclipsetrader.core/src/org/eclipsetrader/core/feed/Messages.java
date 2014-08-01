package org.eclipsetrader.core.feed;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.core.feed.messages"; //$NON-NLS-1$
	public static String TimeSpan_Days;
	public static String TimeSpan_Minutes;
	public static String TimeSpan_Months;
	public static String TimeSpan_Years;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
