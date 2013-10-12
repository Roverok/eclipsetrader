package org.eclipsetrader.news.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.news.internal.messages"; //$NON-NLS-1$
	public static String HeadLineCountNotification_News;
	public static String HeadLineCountNotification_NewsHasUnreadedHeadline;
	public static String HeadLineCountNotification_UnexpectedError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
