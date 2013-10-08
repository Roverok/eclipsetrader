package org.eclipsetrader.kdb.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.kdb.internal.ui.preferences.messages"; //$NON-NLS-1$
	public static String AuthenticationPage_Driver;
	public static String AuthenticationPage_Snapshot;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
