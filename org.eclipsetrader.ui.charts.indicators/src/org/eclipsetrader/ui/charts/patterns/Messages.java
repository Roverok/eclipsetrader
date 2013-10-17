package org.eclipsetrader.ui.charts.patterns;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.charts.patterns.messages"; //$NON-NLS-1$
	public static String Bearish;
	public static String Bullish;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
