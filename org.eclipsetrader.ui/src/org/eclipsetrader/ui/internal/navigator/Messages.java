package org.eclipsetrader.ui.internal.navigator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.navigator.messages"; //$NON-NLS-1$
	public static String InstrumentTypeGroup_Currencies;
	public static String InstrumentTypeGroup_Stocks;
	public static String InstrumentTypeGroup_WatchLists;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
