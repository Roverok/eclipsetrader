package org.eclipsetrader.ui.internal.charts;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.charts"; //$NON-NLS-1$
	public static String PatternChart_Bearish;
	public static String PatternChart_Bullish;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
