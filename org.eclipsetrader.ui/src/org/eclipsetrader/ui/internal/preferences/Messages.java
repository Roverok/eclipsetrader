package org.eclipsetrader.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.preferences.messages"; //$NON-NLS-1$
	public static String BarsPreferencePage_Add;
	public static String BarsPreferencePage_BarAggregationSizes;
	public static String BarsPreferencePage_Remove;
	public static String LaunchersPreferences_ServiceLaunchedAtStartup;
	public static String LaunchersPreferences_StartAllServices;
	public static String LaunchersPreferences_StartServicesSelectedBellow;
	public static String PluginsPage_ExpandTree;
	public static String PreferenceInitializer_1Day;
	public static String PreferenceInitializer_1Month;
	public static String PreferenceInitializer_1Year;
	public static String PreferenceInitializer_2Years;
	public static String PreferenceInitializer_3Months;
	public static String PreferenceInitializer_5Days;
	public static String PreferenceInitializer_6Months;
	public static String WatchListPreferencesPage_AlternateRowsBackground;
	public static String WatchListPreferencesPage_EnableHilights;
	public static String WatchListPreferencesPage_FadeToBackground;
	public static String WatchListPreferencesPage_HilightOutline;
	public static String WatchListPreferencesPage_NegativeHighlight;
	public static String WatchListPreferencesPage_PositiveHighlight;
	public static String WorkbenchPreferencePage_ExpandTree;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
