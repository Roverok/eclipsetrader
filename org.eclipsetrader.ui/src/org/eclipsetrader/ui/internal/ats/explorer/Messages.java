package org.eclipsetrader.ui.internal.ats.explorer;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.ats.explorer.messages"; //$NON-NLS-1$
	public static String ExplorerViewModel_Instruments;
	public static String ExplorerViewModel_Scripts;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
