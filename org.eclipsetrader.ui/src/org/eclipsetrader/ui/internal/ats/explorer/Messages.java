package org.eclipsetrader.ui.internal.ats.explorer;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipsetrader.ui.internal.ats.explorer.messages"; //$NON-NLS-1$
	public static String ExplorerViewModel_Instruments;
	public static String ExplorerViewModel_Scripts;
	public static String ExplorerViewPart_CollapseAll;
	public static String ExplorerViewPart_ConfirmDelete;
	public static String ExplorerViewPart_ConfirmRemove;
	public static String ExplorerViewPart_Delete;
	public static String ExplorerViewPart_ExpandAll;
	public static String ExplorerViewPart_Remove;
	public static String MainScriptItem_Main;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
