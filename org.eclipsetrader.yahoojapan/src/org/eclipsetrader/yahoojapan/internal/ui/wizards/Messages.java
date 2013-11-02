package org.eclipsetrader.yahoojapan.internal.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.yahoojapan.internal.ui.wizards.messages"; //$NON-NLS-1$
	public static String DataImportJob_ImportData;
	public static String DataImportJob_MissingDataFor;
	public static String DataImportWizard_ImportFromYahooJAPAN;
	public static String ImportDataPage_Aggregation;
	public static String ImportDataPage_AllSecurity;
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
	public static String ImportDataPage_Type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
