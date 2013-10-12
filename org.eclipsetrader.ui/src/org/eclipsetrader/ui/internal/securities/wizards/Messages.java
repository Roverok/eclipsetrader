package org.eclipsetrader.ui.internal.securities.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.securities.wizards.messages"; //$NON-NLS-1$
	public static String CurrencyExchangePage_CreateCurrencyExchange;
	public static String CurrencyExchangePage_CurrencyExchange;
	public static String CurrencyExchangePage_FromCurrency;
	public static String CurrencyExchangePage_SameNamePrompt;
	public static String CurrencyExchangePage_SecurityName;
	public static String CurrencyExchangePage_TargetRepository;
	public static String CurrencyExchangePage_ToCurrency;
	public static String CurrencyExchangeWizard_NewCurrencyExchange;
	public static String IdentifierPage_AssignFeedIdentifier;
	public static String IdentifierPage_Identifier;
	public static String IdentifierPage_IdentifierName;
	public static String IdentifierPage_Properties;
	public static String MarketsPage_AssignMarkets;
	public static String MarketsPage_Markets;
	public static String MarketsPage_Markets2;
	public static String NamePage_CommonStock;
	public static String NamePage_CreateNewCommonStock;
	public static String NamePage_Currency;
	public static String NamePage_SameNamePrompt;
	public static String NamePage_SecurityName;
	public static String NamePage_TargetRepository;
	public static String SecurityWizard_NewCommonStock;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
