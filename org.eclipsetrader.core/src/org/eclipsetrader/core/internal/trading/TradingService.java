/*
 * Copyright (c) 2004-2008 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package org.eclipsetrader.core.internal.trading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipsetrader.core.instruments.ISecurity;
import org.eclipsetrader.core.internal.CoreActivator;
import org.eclipsetrader.core.markets.IMarket;
import org.eclipsetrader.core.markets.IMarketService;
import org.eclipsetrader.core.trading.IBroker;
import org.eclipsetrader.core.trading.IOrderChangeListener;
import org.eclipsetrader.core.trading.IOrderMonitor;
import org.eclipsetrader.core.trading.ITradingService;
import org.eclipsetrader.core.trading.ITradingServiceRunnable;
import org.eclipsetrader.core.trading.OrderChangeEvent;
import org.eclipsetrader.core.trading.OrderDelta;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class TradingService implements ITradingService {
	private Map<String, IBroker> brokers = new HashMap<String, IBroker>();
	private List<IOrderMonitor> orders = new ArrayList<IOrderMonitor>();

	private List<OrderDelta> deltas = new ArrayList<OrderDelta>();
	private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

	private IJobManager jobManager;
	private final ILock lock;

	public TradingService() {
		jobManager = Job.getJobManager();
		lock = jobManager.newLock();
	}

	public void startUp() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CoreActivator.BROKERS_EXTENSION_ID);
		if (extensionPoint != null) {
			IConfigurationElement[] configElements = extensionPoint.getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) {
				try {
	                IBroker connector = (IBroker) configElements[j].createExecutableExtension("class");
	                brokers.put(connector.getId(), connector);
                } catch (CoreException e) {
    				Status status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error creating broker instance with id " + configElements[j].getAttribute("id"), e);
    				CoreActivator.log(status);
                }
			}
		}

		for (IBroker connector : brokers.values()) {
			IOrderMonitor[] o = connector.getOrders();
            if (o != null)
            	orders.addAll(Arrays.asList(o));
		}
	}

	public void shutDown() {
		for (IBroker connector : brokers.values())
			connector.disconnect();
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.ITradingService#getBrokers()
	 */
	public IBroker[] getBrokers() {
		Collection<IBroker> c = brokers.values();
		return c.toArray(new IBroker[c.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.ITradingService#getBroker(java.lang.String)
	 */
	public IBroker getBroker(String id) {
		return brokers.get(id);
	}

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.trading.ITradingService#getBrokerForSecurity(org.eclipsetrader.core.instruments.ISecurity)
     */
    public IBroker getBrokerForSecurity(ISecurity security) {
    	IBroker broker = null;

    	try {
    		BundleContext context = CoreActivator.getDefault().getBundle().getBundleContext();
    		ServiceReference serviceReference = context.getServiceReference(IMarketService.class.getName());
    		if (serviceReference != null) {
        		IMarketService service = (IMarketService) context.getService(serviceReference);

        		IMarket market = service.getMarketForSecurity(security);
        		if (market != null)
        			broker = market.getBroker();

        		context.ungetService(serviceReference);
    		}
    	} catch(Exception e) {
    		Status status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error reading market service", e);
    		CoreActivator.getDefault().getLog().log(status);
    	}

    	if (broker == null) {
        	for (IBroker connector : brokers.values()) {
        		if (connector.canTrade(security))
        			return connector;
        	}
    	}

    	return broker;
    }

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.ITradingService#getOrders()
	 */
	public IOrderMonitor[] getOrders() {
		return orders.toArray(new IOrderMonitor[orders.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.ITradingService#addOrderChangeListener(org.eclipsetrader.core.trading.IOrderChangeListener)
	 */
	public void addOrderChangeListener(IOrderChangeListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.trading.ITradingService#removeOrderChangeListener(org.eclipsetrader.core.trading.IOrderChangeListener)
	 */
	public void removeOrderChangeListener(IOrderChangeListener listener) {
		listeners.remove(listener);
	}

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.trading.ITradingService#addOrders(org.eclipsetrader.core.trading.IOrderMonitor[])
     */
    public void addOrders(IOrderMonitor[] order) {
    	for (int i = 0; i < order.length; i++) {
    		if (!orders.contains(order[i])) {
    			orders.add(order[i]);
    			deltas.add(new OrderDelta(OrderDelta.KIND_ADDED, order[i]));
    		}
    	}
    }

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.trading.ITradingService#removeOrders(org.eclipsetrader.core.trading.IOrderMonitor[])
     */
    public void removeOrders(IOrderMonitor[] order) {
    	for (int i = 0; i < order.length; i++) {
    		if (orders.contains(order[i])) {
    			orders.remove(order[i]);
    			deltas.add(new OrderDelta(OrderDelta.KIND_REMOVED, order[i]));
    		}
    	}
    }

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.trading.ITradingService#updateOrders(org.eclipsetrader.core.trading.IOrderMonitor[])
     */
    public void updateOrders(IOrderMonitor[] order) {
    	for (int i = 0; i < order.length; i++) {
    		if (orders.contains(order[i])) {
    			orders.remove(order[i]);
    			deltas.add(new OrderDelta(OrderDelta.KIND_UPDATED, order[i]));
    		}
    	}
    }

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.trading.ITradingService#runInService(org.eclipsetrader.core.trading.ITradingServiceRunnable, org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus runInService(ITradingServiceRunnable runnable, IProgressMonitor monitor) {
    	return runInService(runnable, null, monitor);
    }

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.trading.ITradingService#runInService(org.eclipsetrader.core.trading.ITradingServiceRunnable, org.eclipse.core.runtime.jobs.ISchedulingRule, org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus runInService(ITradingServiceRunnable runnable, ISchedulingRule rule, IProgressMonitor monitor) {
    	IStatus status;
    	if (rule != null)
    		jobManager.beginRule(rule, monitor);
		try {
			lock.acquire();
			deltas = new ArrayList<OrderDelta>();

			try {
    			status = runnable.run(this, monitor);
    		} catch(Throwable e) {
    			status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error running service task", e); //$NON-NLS-1$
    			CoreActivator.log(status);
    		}

    		fireUpdateNotifications();
		} catch (Exception e) {
			status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error running repository task", e); //$NON-NLS-1$
			CoreActivator.log(status);
		} finally {
			lock.release();
	    	if (rule != null)
	    		jobManager.endRule(rule);
		}
		return status;
    }

    protected void fireUpdateNotifications() {
		final OrderChangeEvent event;
    	synchronized(deltas) {
			event = new OrderChangeEvent(deltas.toArray(new OrderDelta[deltas.size()]));
			deltas.clear();
    	}

    	if (event.deltas.length != 0) {
        	Object[] l = listeners.getListeners();
    		for (int i = 0; i < l.length; i++) {
    			final IOrderChangeListener listener = (IOrderChangeListener) l[i];
    			SafeRunner.run(new ISafeRunnable() {
                    public void run() throws Exception {
            			listener.orderChanged(event);
                    }

                    public void handleException(Throwable exception) {
            			Status status = new Status(Status.ERROR, CoreActivator.PLUGIN_ID, 0, "Error running repository listener", exception); //$NON-NLS-1$
            			CoreActivator.log(status);
                    }
    			});
    		}
    	}
    }
}