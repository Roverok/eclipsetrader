package org.eclipsetrader.yahoojapanfx.internal.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.yahoojapanfx.internal.ui.wizards.messages"; //$NON-NLS-1$
	public static String DataImportWizard_ImportDataFromKDB;
	public static String ImportDataPage_Aggregation;
	public static String ImportDataPage_AllSecurities;
	public static String ImportDataPage_Days;
	public static String ImportDataPage_Full;
	public static String ImportDataPage_FullIncremental;
	public static String ImportDataPage_Import;
	public static String ImportDataPage_Incremental;
	public static String ImportDataPage_Minutes;
	public static String ImportDataPage_Period;
	public static String ImportDataPage_SecuritiesSelectedBelow;
	public static String ImportDataPage_SelectImportSecurity;
	public static String ImportDataPage_To;
	public static String ImportDataPage_TYpe;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
