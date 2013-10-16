/*
 * Copyright (c) 2004-2013 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 *     Naofumi Fukue - Yahoo! JAPAN Connector
 */

package org.eclipsetrader.yahoojapan.internal.core.connector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipsetrader.core.feed.IConnectorListener;
import org.eclipsetrader.core.feed.IFeedConnector;
import org.eclipsetrader.core.feed.IFeedIdentifier;
import org.eclipsetrader.core.feed.IFeedSubscription;
import org.eclipsetrader.yahoojapan.internal.YahooJapanActivator;
import org.eclipsetrader.yahoojapan.internal.core.Util;
import org.eclipsetrader.yahoojapan.internal.core.repository.IdentifierType;
import org.eclipsetrader.yahoojapan.internal.core.repository.IdentifiersList;
import org.eclipsetrader.yahoojapan.internal.core.repository.PriceDataType;

public class SnapshotConnector implements Runnable, IFeedConnector, IExecutableExtension, PropertyChangeListener {

    private static final int I_CODE = 0;
    private static final int I_LAST = 1;
    private static final int I_DATE = 2;
    private static final int I_TIME = 3;
    //private static final int I_CHANGE = 4;
    private static final int I_OPEN = 5;
    private static final int I_HIGH = 6;
    private static final int I_LOW = 7;
    private static final int I_VOLUME = 8;
    private static final int I_BID = 9;
    private static final int I_ASK = 10;
    private static final int I_CLOSE = 11;
    //private static final int I_BID_SIZE = 12;
    //private static final int I_ASK_SIZE = 13;
    private static SnapshotConnector instance;
    private String id;
    private String name;

    protected Map<String, FeedSubscription> symbolSubscriptions;
    private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

    protected TimeZone timeZone;
    private SimpleDateFormat dateTimeParser;
    private SimpleDateFormat dateParser;
    private SimpleDateFormat timeParser;
    private NumberFormat numberFormat;

    protected Thread thread;
    private boolean stopping = false;
    private boolean subscriptionsChanged = false;

    public SnapshotConnector() {
        symbolSubscriptions = new HashMap<String, FeedSubscription>();

        timeZone = TimeZone.getTimeZone("Asia/Tokyo");

        dateTimeParser = new SimpleDateFormat("M/d H:m"); //$NON-NLS-1$
        dateTimeParser.setTimeZone(timeZone);

        dateParser = new SimpleDateFormat("M/d"); //$NON-NLS-1$
        dateParser.setTimeZone(timeZone);

        timeParser = new SimpleDateFormat("H:m"); //$NON-NLS-1$
        timeParser.setTimeZone(timeZone);

        numberFormat = NumberFormat.getInstance(Locale.JAPAN);
    }

    public synchronized static SnapshotConnector getInstance() {
        if (instance == null) {
            instance = new SnapshotConnector();
        }
        return instance;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
     */
    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
        id = config.getAttribute("id");
        name = config.getAttribute("name");
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IFeedConnector#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IFeedConnector#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IFeedConnector#subscribe(org.eclipsetrader.core.feed.IFeedIdentifier)
     */
    @Override
    public IFeedSubscription subscribe(IFeedIdentifier identifier) {
        synchronized (symbolSubscriptions) {
            IdentifierType identifierType = IdentifiersList.getInstance().getIdentifierFor(identifier);
            FeedSubscription subscription = symbolSubscriptions.get(identifierType.getSymbol());
            if (subscription == null) {
                subscription = new FeedSubscription(this, identifierType);

                PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport) identifier.getAdapter(PropertyChangeSupport.class);
                if (propertyChangeSupport != null) {
                    propertyChangeSupport.addPropertyChangeListener(this);
                }

                symbolSubscriptions.put(identifierType.getSymbol(), subscription);
                setSubscriptionsChanged(true);
            }
            subscription.incrementInstanceCount();
            return subscription;
        }
    }

    protected void disposeSubscription(FeedSubscription subscription) {
        synchronized (symbolSubscriptions) {
            if (subscription.decrementInstanceCount() <= 0) {
                IdentifierType identifierType = subscription.getIdentifierType();

                if (subscription.getIdentifier() != null) {
                    PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport) subscription.getIdentifier().getAdapter(PropertyChangeSupport.class);
                    if (propertyChangeSupport != null) {
                        propertyChangeSupport.removePropertyChangeListener(this);
                    }
                }

                symbolSubscriptions.remove(identifierType.getSymbol());
                setSubscriptionsChanged(true);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IFeedConnector#connect()
     */
    @Override
    public void connect() {
        if (thread == null || !thread.isAlive()) {
            stopping = false;
            thread = new Thread(this, name + " - Data Reader");
            thread.start();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IFeedConnector#disconnect()
     */
    @Override
    public void disconnect() {
        stopping = true;

        if (thread != null) {
            try {
                synchronized (thread) {
                    thread.notify();
                }
                thread.join(30 * 1000);
            } catch (InterruptedException e) {
                Status status = new Status(IStatus.ERROR, YahooJapanActivator.PLUGIN_ID, 0, "Error stopping thread", e);
                YahooJapanActivator.log(status);
            }
            thread = null;
        }
    }

    public boolean isStopping() {
        return stopping;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            Util.setupProxy(client, Util.snapshotFeedHost);

            synchronized (thread) {
                while (!isStopping()) {
                    synchronized (symbolSubscriptions) {
                        if (symbolSubscriptions.size() != 0) {
                            String[] symbols = symbolSubscriptions.keySet().toArray(new String[symbolSubscriptions.size()]);
                            if (symbolSubscriptions.size() == 1) {
                                fetchLatestSnapshot1(client, symbols, false);
                            } else {
                                fetchLatestSnapshot(client, symbols, false);
                            }
                            setSubscriptionsChanged(false);
                        }
                    }

                    try {
                        thread.wait(5000);
                    } catch (InterruptedException e) {
                        // Ignore exception, not important at this time
                    }
                }
            }
        } catch (Exception e) {
            Status status = new Status(IStatus.ERROR, YahooJapanActivator.PLUGIN_ID, 0, "Error reading data", e);
            YahooJapanActivator.log(status);
        }
    }

    protected void fetchLatestSnapshot(HttpClient client, String[] symbols, boolean isStaleUpdate) {
        HttpMethod method = null;
        BufferedReader in = null;
        String line = ""; //$NON-NLS-1$

        try {
        	for (int page = 1; page < 11; page++) {
                StringBuilder work = new StringBuilder();
                boolean isNextPage = false;

                method = Util.getSnapshotFeedMethod(symbols, page);
                method.setFollowRedirects(true);

                client.executeMethod(method);

                in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                while ((line = in.readLine()) != null) {
                    if (line.indexOf("<!-- .pfListView -->") >= 0) {
                        break;
                    }
                }
                while ((line = in.readLine()) != null) {
                    if (line.indexOf("<!--ページング-->") >= 0) {
                    	isNextPage = (line.indexOf("次へ</a>") >= 0);
                        break;
                    }
                	if (line.startsWith("<a href=\"http://stocks.finance.yahoo.co.jp/stocks/detail/?code=")) {
                		//CODE
//                		work.append(line.substring(63, 67));
//                		work.append(",");
        				work.append(line.substring(line.indexOf("<strong>") + 8, line.indexOf("</strong>")));
                		work.append(",");
                        //LAST, DATE
                        while ((line = in.readLine()) != null) {
                			if (line.equals("<td nowrap>")) {
                				break;
                			}
                		}
                        if ((line = in.readLine()) != null) {
                			if (line.indexOf("<td>") >= 0) {
                				work.append(line.substring(line.indexOf("<strong>") + 8, line.indexOf("</strong>")).replaceAll(",", ""));
                				work.append(",");
                				String date = line.substring(0, line.indexOf("</td>"));
                				if (date.indexOf("/") >= 0) {
                					work.append(date);
                    				work.append(",00:00,,");
                				} else {
                		            Calendar c = Calendar.getInstance();
                		            work.append(String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
                    				work.append(",");
                    				work.append(date);
                    				work.append(",,");
                				}
                			}
                		}
                        //OPEN
                        while ((line = in.readLine()) != null) {
                			if (line.equals("<td nowrap>")) {
                				break;
                			}
                		}
                        if ((line = in.readLine()) != null) {
                			if (line.indexOf("</td>") >= 0) {
                				work.append(line.substring(0, line.indexOf("</td>")).replaceAll(",", ""));
                			}
                		}
        				work.append(",");
                        //HIGH
                        while ((line = in.readLine()) != null) {
                			if (line.equals("<td nowrap>")) {
                				break;
                			}
                		}
                        if ((line = in.readLine()) != null) {
                			if (line.indexOf("</td>") >= 0) {
                				work.append(line.substring(0, line.indexOf("</td>")).replaceAll(",", ""));
                			}
                		}
        				work.append(",");
                        //LOW
                        while ((line = in.readLine()) != null) {
                			if (line.equals("<td nowrap>")) {
                				break;
                			}
                		}
                        if ((line = in.readLine()) != null) {
                			if (line.indexOf("</td>") >= 0) {
                				work.append(line.substring(0, line.indexOf("</td>")).replaceAll(",", ""));
                			}
                		}
        				work.append(",,,,,,,@");
                        processSnapshotData(work.toString(), isStaleUpdate);
                        work = new StringBuilder();
                	}
                }

                FeedSubscription[] subscriptions;
                synchronized (symbolSubscriptions) {
                    Collection<FeedSubscription> c = symbolSubscriptions.values();
                    subscriptions = c.toArray(new FeedSubscription[c.size()]);
                }
                for (int i = 0; i < subscriptions.length; i++) {
                    subscriptions[i].fireNotification();
                }

                if (!isNextPage) {
                    break;
                }
        	}

        } catch (Exception e) {
            Status status = new Status(IStatus.ERROR, YahooJapanActivator.PLUGIN_ID, 0, "Error reading data", e);
            YahooJapanActivator.log(status);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (method != null) {
                    method.releaseConnection();
                }
            } catch (Exception e) {
                Status status = new Status(IStatus.WARNING, YahooJapanActivator.PLUGIN_ID, 0, "Connection wasn't closed cleanly", e);
                YahooJapanActivator.log(status);
            }
        }
    }

    protected void fetchLatestSnapshot1(HttpClient client, String[] symbols, boolean isStaleUpdate) {
        HttpMethod method = null;
        BufferedReader in = null;
        String line = ""; //$NON-NLS-1$

        try {
            StringBuilder work = new StringBuilder();

            method = Util.getSnapshotFeedMethod(symbols, 1);
            method.setFollowRedirects(true);

            client.executeMethod(method);

            in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            while ((line = in.readLine()) != null) {
                if (line.indexOf("<!-- NEW STOCKS DETAIL -->") >= 0) {
                    break;
                }
            }
    		//CODE
            while ((line = in.readLine()) != null) {
                if (line.indexOf("<dl class=\"stocksInfo\">") >= 0) {
                    break;
                }
            }
            if ((line = in.readLine()) != null) {
    			if (line.length() > 8) {
    				work.append(line.substring(4, 8));
    			}
    		}
    		work.append(",");
            //DATE
    		String date = "";
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<dd class=\"yjSb real\">") >= 0) {
    				break;
    			}
    		}
			if (line.length() > 33) {
				date = line.substring(28, line.indexOf("</span>"));
    		}
            //LAST
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<td class=\"stoksPrice\">") >= 0) {
    				break;
    			}
    		}
			if (line.length() > 23) {
				work.append(line.substring(line.indexOf("<td class=\"stoksPrice\">") + 23, line.indexOf("</td>")).replaceAll(",", ""));
    		}
			work.append(",");
			if (date.indexOf("/") >= 0) {
				work.append(date);
				work.append(",00:00,,");
			} else {
	            Calendar c = Calendar.getInstance();
	            work.append(String.valueOf(c.get(Calendar.MONTH) + 1) + "/" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
				work.append(",");
				work.append(date);
				work.append(",,");
			}
			//YESTERDAY LAST
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<div class=\"innerDate\">") >= 0) {
    				break;
    			}
    		}
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<strong>") >= 0) {
    				break;
    			}
    		}
            //OPEN
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<strong>") >= 0) {
    				break;
    			}
    		}
			if (line.length() > 16) {
				work.append(line.substring(line.indexOf("<strong>") + 8, line.indexOf("</strong>")).replaceAll(",", ""));
    		}
			work.append(",");
            //HIGH
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<strong>") >= 0) {
    				break;
    			}
    		}
			if (line.length() > 16) {
				work.append(line.substring(line.indexOf("<strong>") + 8, line.indexOf("</strong>")).replaceAll(",", ""));
    		}
			work.append(",");
            //LOW
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<strong>") >= 0) {
    				break;
    			}
    		}
			if (line.length() > 16) {
				work.append(line.substring(line.indexOf("<strong>") + 8, line.indexOf("</strong>")).replaceAll(",", ""));
    		}
			work.append(",");
            //VOLUME
            while ((line = in.readLine()) != null) {
    			if (line.indexOf("<strong>") >= 0) {
    				break;
    			}
    		}
			if (line.length() > 16) {
				work.append(line.substring(line.indexOf("<strong>") + 8, line.indexOf("</strong>")).replaceAll(",", ""));
    		}
			work.append(",,,,,,@");

			processSnapshotData(work.toString(), isStaleUpdate);

            FeedSubscription[] subscriptions;
            synchronized (symbolSubscriptions) {
                Collection<FeedSubscription> c = symbolSubscriptions.values();
                subscriptions = c.toArray(new FeedSubscription[c.size()]);
            }
            for (int i = 0; i < subscriptions.length; i++) {
                subscriptions[i].fireNotification();
            }

        } catch (Exception e) {
            Status status = new Status(IStatus.ERROR, YahooJapanActivator.PLUGIN_ID, 0, "Error reading data", e);
            YahooJapanActivator.log(status);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (method != null) {
                    method.releaseConnection();
                }
            } catch (Exception e) {
                Status status = new Status(IStatus.WARNING, YahooJapanActivator.PLUGIN_ID, 0, "Connection wasn't closed cleanly", e);
                YahooJapanActivator.log(status);
            }
        }
    }

    void processSnapshotData(String line, boolean isStaleUpdate) {
        String[] elements;
        if (line.indexOf(";") != -1) {
            elements = line.split(";"); //$NON-NLS-1$
        }
        else {
            elements = line.split(","); //$NON-NLS-1$
        }

        String symbol = stripQuotes(elements[I_CODE]);
        FeedSubscription subscription = symbolSubscriptions.get(symbol);
        if (subscription != null) {
            IdentifierType identifierType = subscription.getIdentifierType();
            PriceDataType priceData = identifierType.getPriceData();

            priceData.setTime(getDateValue(elements[I_DATE], elements[I_TIME]));
            priceData.setLast(getDoubleValue(elements[I_LAST]));
            priceData.setVolume(getLongValue(elements[I_VOLUME]));
            subscription.setTrade(priceData.getTime(), priceData.getLast(), null, priceData.getVolume());

            priceData.setBid(getDoubleValue(elements[I_BID]));
            if (!isStaleUpdate) {
                priceData.setBidSize(null); // getLongValue(elements[I_BID_SIZE]));
            }
            priceData.setAsk(getDoubleValue(elements[I_ASK]));
            if (!isStaleUpdate) {
                priceData.setAskSize(null); // getLongValue(elements[I_ASK_SIZE]));
            }
            subscription.setQuote(priceData.getBid(), priceData.getAsk(), priceData.getBidSize(), priceData.getAskSize());

            priceData.setOpen(getDoubleValue(elements[I_OPEN]));
            priceData.setHigh(getDoubleValue(elements[I_HIGH]));
            priceData.setLow(getDoubleValue(elements[I_LOW]));
            if (priceData.getOpen() != null && priceData.getOpen() != 0.0 && priceData.getHigh() != null && priceData.getHigh() != 0.0 && priceData.getLow() != null && priceData.getLow() != 0.0) {
                subscription.setTodayOHL(priceData.getOpen(), priceData.getHigh(), priceData.getLow());
            }

            priceData.setClose(getDoubleValue(elements[I_CLOSE]));
            subscription.setLastClose(priceData.getClose(), null);
        }
    }

    protected Date getDateValue(String dateValue, String timeValue) {
        String date = stripQuotes(dateValue);
        String time = stripQuotes(timeValue);

        if (date.indexOf("N/A") != -1 && time.indexOf("N/A") != -1) {
            return null;
        }

        try {
            if (date.indexOf("N/A") != -1) {
                date = dateParser.format(Calendar.getInstance(timeZone).getTime());
            }
            if (time.indexOf("N/A") != -1) {
                time = timeParser.format(Calendar.getInstance(timeZone).getTime());
            }

            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            c.setTime(dateTimeParser.parse(date + " " + time)); //$NON-NLS-1$
            c.set(Calendar.YEAR, y);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.setTimeZone(TimeZone.getDefault());
            if (c.get(Calendar.YEAR) < 70) {
                c.add(Calendar.YEAR, 2000);
            }

            return c.getTime();
        } catch (ParseException e) {
            Status status = new Status(IStatus.ERROR, YahooJapanActivator.PLUGIN_ID, 0, "Error parsing date/time values", e);
            YahooJapanActivator.log(status);
        }

        return null;
    }

    protected Double getDoubleValue(String value) {
        try {
            if (!value.equals("") && !value.equalsIgnoreCase("N/A")) {
                return numberFormat.parse(value).doubleValue();
            }
        } catch (ParseException e) {
            Status status = new Status(IStatus.ERROR, YahooJapanActivator.PLUGIN_ID, 0, "Error parsing number", e);
            YahooJapanActivator.log(status);
        }
        return null;
    }

    protected Long getLongValue(String value) {
        try {
            if (!value.equals("") && !value.equalsIgnoreCase("N/A")) {
                return numberFormat.parse(value).longValue();
            }
        } catch (ParseException e) {
            Status status = new Status(IStatus.ERROR, YahooJapanActivator.PLUGIN_ID, 0, "Error parsing number", e);
            YahooJapanActivator.log(status);
        }
        return null;
    }

    protected String stripQuotes(String s) {
        if (s.startsWith("\"")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    protected boolean isSubscriptionsChanged() {
        return subscriptionsChanged;
    }

    protected void setSubscriptionsChanged(boolean subscriptionsChanged) {
        this.subscriptionsChanged = subscriptionsChanged;
    }

    Map<String, FeedSubscription> getSymbolSubscriptions() {
        return symbolSubscriptions;
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof IFeedIdentifier) {
            IFeedIdentifier identifier = (IFeedIdentifier) evt.getSource();
            synchronized (symbolSubscriptions) {
                for (FeedSubscription subscription : symbolSubscriptions.values()) {
                    if (subscription.getIdentifier() == identifier) {
                        symbolSubscriptions.remove(subscription.getIdentifierType().getSymbol());
                        IdentifierType identifierType = IdentifiersList.getInstance().getIdentifierFor(identifier);
                        subscription.setIdentifierType(identifierType);
                        symbolSubscriptions.put(identifierType.getSymbol(), subscription);
                        setSubscriptionsChanged(true);
                        break;
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IFeedConnector#addConnectorListener(org.eclipsetrader.core.feed.IConnectorListener)
     */
    @Override
    public void addConnectorListener(IConnectorListener listener) {
        listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IFeedConnector#removeConnectorListener(org.eclipsetrader.core.feed.IConnectorListener)
     */
    @Override
    public void removeConnectorListener(IConnectorListener listener) {
        listeners.remove(listener);
    }
}
