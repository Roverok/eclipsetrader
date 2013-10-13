package org.eclipsetrader.internal.brokers.paper.schemes;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.internal.brokers.paper.schemes.messages"; //$NON-NLS-1$
	public static String LimitedProportional1Scheme_LimitedProportionalScheme;
	public static String LimitedProportional2Scheme_LimitedProportional2Scheme;
	public static String NoExpensesScheme_None;
	public static String SimpleFixedScheme_SimpleFixedScheme;
	public static String TwoLevelsPerShareScheme_TwoLevelsPerShareScheme;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
