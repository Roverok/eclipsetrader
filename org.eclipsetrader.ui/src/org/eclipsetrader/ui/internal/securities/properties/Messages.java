package org.eclipsetrader.ui.internal.securities.properties;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.securities.properties.messages"; //$NON-NLS-1$
	public static String ConnectorOverrideProperties_Default;
	public static String ConnectorOverrideProperties_ErrorReadingFeedService;
	public static String ConnectorOverrideProperties_HistoryBackfill;
	public static String ConnectorOverrideProperties_IntradayBackfill;
	public static String ConnectorOverrideProperties_LiveFeed;
	public static String ConnectorOverrideProperties_None;
	public static String ConnectorOverrideProperties_Overrides;
	public static String CurrencyGeneralProperties_FromCurrency;
	public static String CurrencyGeneralProperties_General;
	public static String CurrencyGeneralProperties_SameNamePrompt;
	public static String CurrencyGeneralProperties_SecurityMustHaveName;
	public static String CurrencyGeneralProperties_SecurityName;
	public static String CurrencyGeneralProperties_TargetRepository;
	public static String CurrencyGeneralProperties_ToCurrency;
	public static String DividendsProperties_Add;
	public static String DividendsProperties_Amount;
	public static String DividendsProperties_Dividents;
	public static String DividendsProperties_ExDate;
	public static String DividendsProperties_Remove;
	public static String GeneralProperties_Currency;
	public static String GeneralProperties_General;
	public static String GeneralProperties_SameNamePrompt;
	public static String GeneralProperties_SecurityMustHaveName;
	public static String GeneralProperties_SecurityName;
	public static String GeneralProperties_TargetRepository;
	public static String IdentifierProperties_Identifier;
	public static String IdentifierProperties_IdentifierName;
	public static String IdentifierProperties_Properties;
	public static String MarketsProperties_Markets;
	public static String MarketsProperties_Markets2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
