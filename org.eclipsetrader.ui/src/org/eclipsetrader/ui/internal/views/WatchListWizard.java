/*
 * Copyright (c) 2004-2011 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package org.eclipsetrader.ui.internal.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipsetrader.core.internal.CoreActivator;
import org.eclipsetrader.core.repositories.IRepository;
import org.eclipsetrader.core.repositories.IRepositoryRunnable;
import org.eclipsetrader.core.repositories.IRepositoryService;
import org.eclipsetrader.core.repositories.IStoreObject;
import org.eclipsetrader.core.views.Column;
import org.eclipsetrader.core.views.IColumn;
import org.eclipsetrader.core.views.IWatchList;
import org.eclipsetrader.core.views.WatchList;
import org.eclipsetrader.ui.internal.UIActivator;

public class WatchListWizard extends Wizard implements INewWizard {

    private Image image;
    private NamePage namePage;
    private ColumnsPage columnsPage;
    private IWorkbench workbench;

    public WatchListWizard() {
        ImageDescriptor descriptor = ImageDescriptor.createFromURL(UIActivator.getDefault().getBundle().getResource("icons/wizban/newfile_wiz.gif")); //$NON-NLS-1$
        image = descriptor.createImage();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#dispose()
     */
    @Override
    public void dispose() {
        if (image != null) {
            image.dispose();
        }
        super.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
     */
    @Override
    public String getWindowTitle() {
        return Messages.WatchListWizard_NewWatchList;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#getDefaultPageImage()
     */
    @Override
    public Image getDefaultPageImage() {
        return image;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(namePage = new NamePage());
        addPage(columnsPage = new ColumnsPage());

        CoreActivator activator = CoreActivator.getDefault();
        IColumn[] defaultColumns = new IColumn[] {
                new Column(Messages.WatchListWizard_Symbol, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.FeedIdentifier")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_Name, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.SecurityName")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_Last, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.LastTrade")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_Change, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.Change")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_BidSize, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.BidSize")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_Bid, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.BidPrice")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_Ask, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.AskPrice")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_AskSize, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.AskSize")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_Volume, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.TradeVolume")), //$NON-NLS-2$
                new Column(Messages.WatchListWizard_DateTime, activator.getDataProviderFactory("org.eclipsetrader.ui.providers.LastTradeDateTime")), //$NON-NLS-2$
        };
        columnsPage.setDefaultColumns(defaultColumns);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        final IRepository repository = namePage.getRepository();
        final IWatchList resource = new WatchList(namePage.getResourceName(), columnsPage.getColumns());

        final IRepositoryService service = UIActivator.getDefault().getRepositoryService();
        service.runInService(new IRepositoryRunnable() {

            @Override
            public IStatus run(IProgressMonitor monitor) throws Exception {
                service.moveAdaptable(new IAdaptable[] {
                    resource
                }, repository);
                return Status.OK_STATUS;
            }
        }, null);

        IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
        try {
            IStoreObject storeObject = (IStoreObject) resource.getAdapter(IStoreObject.class);

            IDialogSettings dialogSettings = UIActivator.getDefault().getDialogSettingsForView(storeObject.getStore().toURI());
            IDialogSettings columnsSection = dialogSettings.getSection("columns"); //$NON-NLS-1$
            if (columnsSection == null) {
                columnsSection = dialogSettings.addNewSection("columns"); //$NON-NLS-1$
            }
            columnsSection.put(Messages.WatchListWizard_Symbol, 80);
            columnsSection.put(Messages.WatchListWizard_Name, 190);
            columnsSection.put(Messages.WatchListWizard_Last, 60);
            columnsSection.put(Messages.WatchListWizard_Change, 100);
            columnsSection.put(Messages.WatchListWizard_BidSize, 80);
            columnsSection.put(Messages.WatchListWizard_Bid, 60);
            columnsSection.put(Messages.WatchListWizard_Ask, 60);
            columnsSection.put(Messages.WatchListWizard_AskSize, 80);
            columnsSection.put(Messages.WatchListWizard_Volume, 80);
            columnsSection.put(Messages.WatchListWizard_DateTime, 150);

            page.showView(WatchListView.VIEW_ID, dialogSettings.getName(), IWorkbenchPage.VIEW_ACTIVATE);
        } catch (PartInitException e) {
            Status status = new Status(IStatus.ERROR, UIActivator.PLUGIN_ID, 0, "Error opening watchlist view", e); //$NON-NLS-1$
            UIActivator.getDefault().getLog().log(status);
        }

        return true;
    }
}
