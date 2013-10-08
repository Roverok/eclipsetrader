package org.eclipsetrader.ui.internal.application;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.application.messages"; //$NON-NLS-1$
	public static String TraderActionBarAdvisor_Edit;
	public static String TraderActionBarAdvisor_File;
	public static String TraderActionBarAdvisor_Help;
	public static String TraderActionBarAdvisor_New;
	public static String TraderActionBarAdvisor_NewWindow;
	public static String TraderActionBarAdvisor_OpenPerspective;
	public static String TraderActionBarAdvisor_ShowView;
	public static String TraderActionBarAdvisor_Window;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
