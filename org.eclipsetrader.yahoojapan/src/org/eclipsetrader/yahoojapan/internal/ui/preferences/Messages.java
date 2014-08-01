package org.eclipsetrader.yahoojapan.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.yahoojapan.internal.ui.preferences.messages"; //$NON-NLS-1$
	public static String AuthenticationPage_Driver;
	public static String AuthenticationPage_Snapshot;
	public static String NewsPreferencesPage_ConsiderNewsAsRecent;
	public static String NewsPreferencesPage_Hours;
	public static String NewsPreferencesPage_Minutes;
	public static String NewsPreferencesPage_Subcriptions;
	public static String NewsPreferencesPage_UpdateEvery;
	public static String NewsPreferencesPage_UpdateSecurityNews;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
