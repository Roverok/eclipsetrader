package org.eclipsetrader.core.internal.trading;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.core.internal.trading.messages"; //$NON-NLS-1$
	public static String TargetPrice_Ask;
	public static String TargetPrice_Bid;
	public static String TargetPrice_Crosses;
	public static String TargetPrice_Last;
	public static String TargetPrice_Price;
	public static String TargetPrice_Reaches;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
