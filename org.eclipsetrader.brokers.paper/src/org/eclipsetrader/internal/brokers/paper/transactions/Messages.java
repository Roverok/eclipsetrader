package org.eclipsetrader.internal.brokers.paper.transactions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.internal.brokers.paper.transactions.messages"; //$NON-NLS-1$
	public static String ExpenseTransaction_Expenses;
	public static String StockTransaction_Description;
	public static String TradeTransaction_Description;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
