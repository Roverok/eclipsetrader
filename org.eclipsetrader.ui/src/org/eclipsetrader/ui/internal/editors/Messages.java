package org.eclipsetrader.ui.internal.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.editors.messages"; //$NON-NLS-1$
	public static String NamePage_CreateNewScript;
	public static String NamePage_Name;
	public static String NamePage_Script;
	public static String NamePage_TargetRepository;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
