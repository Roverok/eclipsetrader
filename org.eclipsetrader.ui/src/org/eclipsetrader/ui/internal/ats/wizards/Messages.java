package org.eclipsetrader.ui.internal.ats.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.ats.wizards.messages"; //$NON-NLS-1$
	public static String BarsPage_Add;
	public static String BarsPage_Bars;
	public static String BarsPage_Remove;
	public static String BarsPage_SetBarTimespan;
	public static String BarsProperties_Add;
	public static String BarsProperties_Bars;
	public static String BarsProperties_Remove;
	public static String GeneralProperties_General;
	public static String GeneralProperties_Name;
	public static String GeneralProperties_StrategyMustHaveName;
	public static String GeneralProperties_TargetRepository;
	public static String InstrumentsPage_Instruments;
	public static String InstrumentsPage_SetInstruments;
	public static String InstrumentsProperties_Instruments;
	public static String NamePage_CreateNewScriptStrategy;
	public static String NamePage_Name;
	public static String NamePage_ScriptStrategy;
	public static String NamePage_TargetRepository;
	public static String ScriptStrategyWizard_NewScriptStrategy;
	public static String StrategyPropertiesHandler_PropertiesFor;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
