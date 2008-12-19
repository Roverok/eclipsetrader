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

package org.eclipsetrader.ui.internal.providers;

import java.text.NumberFormat;
import java.text.ParseException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipsetrader.core.views.Holding;
import org.eclipsetrader.core.views.IDataProvider;
import org.eclipsetrader.core.views.IDataProviderFactory;
import org.eclipsetrader.core.views.IEditableDataProvider;
import org.eclipsetrader.core.views.IHolding;
import org.eclipsetrader.ui.internal.UIActivator;

public class PurchasePriceFactory extends AbstractProviderFactory {
	private NumberFormat formatter = NumberFormat.getInstance();

	public class DataProvider implements IDataProvider, IEditableDataProvider {

		public DataProvider() {
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#init(org.eclipse.core.runtime.IAdaptable)
         */
        public void init(IAdaptable adaptable) {
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#getFactory()
         */
        public IDataProviderFactory getFactory() {
	        return PurchasePriceFactory.this;
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#getValue(org.eclipse.core.runtime.IAdaptable)
         */
        public IAdaptable getValue(IAdaptable adaptable) {
        	IHolding element = (IHolding) adaptable.getAdapter(IHolding.class);
        	if (element != null) {
        		final Double value = element.getPurchasePrice();
        		return new IAdaptable() {
                    @SuppressWarnings("unchecked")
                    public Object getAdapter(Class adapter) {
                    	if (adapter.isAssignableFrom(String.class))
                    		return value != null ? formatter.format(value) : "";
                    	if (adapter.isAssignableFrom(Double.class))
                    		return value;
	                    return null;
                    }

                    @Override
                    public boolean equals(Object obj) {
                    	if (!(obj instanceof IAdaptable))
                    		return false;
                    	Double s = (Double) ((IAdaptable) obj).getAdapter(Double.class);
                    	return s == value || (value != null && value.equals(s));
                    }
        		};
        	}
	        return null;
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IEditableDataProvider#setValue(org.eclipse.core.runtime.IAdaptable, java.lang.Object)
         */
        public void setValue(IAdaptable adaptable, Object value) {
        	Holding element = (Holding) adaptable.getAdapter(Holding.class);
        	if (element != null) {
        		Double l = null;
        		if (value instanceof Number)
        			l = ((Number) value).doubleValue();
        		else if (value != null) {
	                try {
	                    l = formatter.parse(value.toString()).doubleValue();
                    } catch (ParseException e) {
	                    UIActivator.log("Error parsing edited purchase price value", e);
                    }
        		}
        		element.setPurchasePrice(l);
        	}
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#dispose()
         */
        public void dispose() {
        }
	}

	public PurchasePriceFactory() {
		formatter.setGroupingUsed(true);
		formatter.setMinimumIntegerDigits(1);
		formatter.setMinimumFractionDigits(2);
		formatter.setMaximumFractionDigits(4);
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.views.IDataProviderFactory#createProvider()
	 */
	public IDataProvider createProvider() {
		return new DataProvider();
	}

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.views.IDataProviderFactory#getType()
     */
    @SuppressWarnings("unchecked")
    public Class[] getType() {
	    return new Class[] {
	    		Double.class,
	    		String.class,
	    	};
    }
}
