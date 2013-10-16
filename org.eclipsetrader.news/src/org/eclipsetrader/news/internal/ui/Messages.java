package org.eclipsetrader.news.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.news.internal.ui.messages"; //$NON-NLS-1$
	public static String HeadLineViewer_Date;
	public static String HeadLineViewer_MarkAllAsRead;
	public static String HeadLineViewer_MarkAsRead;
	public static String HeadLineViewer_Next;
	public static String HeadLineViewer_Open;
	public static String HeadLineViewer_OpenInNewBrowser;
	public static String HeadLineViewer_Previous;
	public static String HeadLineViewer_Refresh;
	public static String HeadLineViewer_Security;
	public static String HeadLineViewer_Source;
	public static String HeadLineViewer_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
