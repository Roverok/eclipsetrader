package org.eclipsetrader.ui.internal.ats.monitor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.ats.monitor.messages"; //$NON-NLS-1$
	public static String ColumnsProperties_Columns;
	public static String GeneralProperties_Account;
	public static String GeneralProperties_Broker;
	public static String GeneralProperties_General;
	public static String GeneralProperties_InitialBackfillSize;
	public static String GeneralProperties_StartAutomatically;
	public static String SettingsAction_Settings;
	public static String SettingsAction_SettingsFor;
	public static String TradingSystemPropertiesHandler_PropertiesFor;
	public static String TradingSystemsViewPart_Ask;
	public static String TradingSystemsViewPart_Autostart;
	public static String TradingSystemsViewPart_Bid;
	public static String TradingSystemsViewPart_DateTime;
	public static String TradingSystemsViewPart_ExpandAll;
	public static String TradingSystemsViewPart_Gain;
	public static String TradingSystemsViewPart_Last;
	public static String TradingSystemsViewPart_Position;
	public static String TradingSystemsViewPart_Started;
	public static String TradingSystemsViewPart_Starting;
	public static String TradingSystemsViewPart_Stopped;
	public static String TradingSystemsViewPart_Stopping;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
