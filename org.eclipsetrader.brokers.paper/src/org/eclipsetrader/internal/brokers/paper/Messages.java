package org.eclipsetrader.internal.brokers.paper;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.internal.brokers.paper.messages"; //$NON-NLS-1$
	public static String AccountPropertyPage_Balance;
	public static String AccountPropertyPage_Currency;
	public static String AccountPropertyPage_ExpensesScheme;
	public static String AccountPropertyPage_Name;
	public static String NewAccountWizard_NewAccount;
	public static String NamePage_Name;
	public static String SettingsPage_Currency;
	public static String SettingsPage_ExpensesScheme;
	public static String SettingsPage_InitialBalance;
	public static String SettingsPage_Settings;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
