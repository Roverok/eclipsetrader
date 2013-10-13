package org.eclipsetrader.ui.internal.ats;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.ats.messages"; //$NON-NLS-1$
	public static String ReportViewPart_Amount;
	public static String ReportViewPart_AvgPrice;
	public static String ReportViewPart_DateTime;
	public static String ReportViewPart_Instrument;
	public static String ReportViewPart_Message;
	public static String ReportViewPart_Performance;
	public static String ReportViewPart_Qty;
	public static String ReportViewPart_Report1;
	public static String ReportViewPart_Report2;
	public static String ReportViewPart_Side;
	public static String ReportViewPart_Trades;
	public static String RunSimulationHandler_Simulation;
	public static String SimulationParametersDialog_Begin;
	public static String SimulationParametersDialog_End;
	public static String SimulationParametersDialog_RunSimulation;
	public static String SimulationParametersDialog_Today;
	public static String TimeSpanDialog_TimeSpan;
	public static String TimeSpanDialog_Value;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
