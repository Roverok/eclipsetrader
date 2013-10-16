/*
 * Copyright (c) 2004-2011 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 *     Naofumi Fukue - Yahoo! JAPAN Connector
 */

package org.eclipsetrader.yahoojapan.internal.core;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipsetrader.core.feed.IFeedIdentifier;
import org.eclipsetrader.core.feed.IFeedProperties;
import org.eclipsetrader.core.instruments.ISecurity;
import org.eclipsetrader.yahoojapan.internal.YahooJapanActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Util {

    public static final String snapshotFeedHost = "info.finance.yahoo.co.jp"; //$NON-NLS-1$
    public static final String historyFeedHost = "info.finance.yahoo.co.jp"; //$NON-NLS-1$

    private Util() {
    }

    public static String getSymbol(IFeedIdentifier identifier) {
        String symbol = identifier.getSymbol();

        IFeedProperties properties = (IFeedProperties) identifier.getAdapter(IFeedProperties.class);
        if (properties != null) {
            if (properties.getProperty("org.eclipsetrader.yahoojapan.symbol") != null) {
                symbol = properties.getProperty("org.eclipsetrader.yahoojapan.symbol");
            }
        }

        return symbol;
    }

    /**
     * Builds the http method for live prices snapshot download.
     *
     * @return the method.
     */
    public static HttpMethod getSnapshotFeedMethod(String[] symbols, int page) {
        GetMethod method = new GetMethod("http://" + snapshotFeedHost + "/search/");
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < symbols.length; i++) {
            if (i != 0) {
                s.append(" "); //$NON-NLS-1$
            }
            s.append(symbols[i]);
        }
        method.setQueryString(new NameValuePair[] {
                new NameValuePair("query", s.toString()),
                new NameValuePair("ei", "UTF-8"),
                new NameValuePair("view", "l2"),
                new NameValuePair("p", String.valueOf(page)),
        });
        return method;
    }

    /**
     * Builds the http method for historycal prices download.
     *
     * @return the method.
     */
    public static HttpMethod getHistoryFeedMethod(IFeedIdentifier identifier, Date from, Date to) throws URIException {
        String symbol = getSymbol(identifier);

        Calendar fromDate = Calendar.getInstance();
        fromDate.setTime(from);

        Calendar toDate = Calendar.getInstance();
        toDate.setTime(to);

        String prefix = "/history/";
        String suffix = "";
        URI uri = new URI("http", "info.finance.yahoo.co.jp", prefix + suffix, "");

        GetMethod method = new GetMethod();
        method.setURI(uri);
        method.setQueryString(new NameValuePair[] {
                new NameValuePair("code", symbol),
                new NameValuePair("sy", String.valueOf(fromDate.get(Calendar.YEAR))),
                new NameValuePair("sm", String.valueOf(fromDate.get(Calendar.MONTH))),
                new NameValuePair("sd", String.valueOf(fromDate.get(Calendar.DAY_OF_MONTH))),
                new NameValuePair("ey", String.valueOf(toDate.get(Calendar.YEAR))),
                new NameValuePair("em", String.valueOf(toDate.get(Calendar.MONTH))),
                new NameValuePair("ed", String.valueOf(toDate.get(Calendar.DAY_OF_MONTH))),
                new NameValuePair("tm", "d"),
//                new NameValuePair("p", "1"),
        });
        method.setFollowRedirects(true);
        try {
            System.out.println(method.getURI().toString());
        } catch (URIException e) {
            e.printStackTrace();
        }

        return method;
    }

    public static HttpMethod get1YearHistoryFeedMethod(IFeedIdentifier identifier, int year, int page) throws URIException {
        String symbol = getSymbol(identifier);

        String prefix = "/history/";
        String suffix = "";
        URI uri = new URI("http", "info.finance.yahoo.co.jp", prefix + suffix, "");

        GetMethod method = new GetMethod();
        method.setURI(uri);
        method.setQueryString(new NameValuePair[] {
                new NameValuePair("code", symbol),
                new NameValuePair("sy", String.valueOf(year)),
                new NameValuePair("sm", "1"),
                new NameValuePair("sd", "1"),
                new NameValuePair("ey", String.valueOf(year)),
                new NameValuePair("em", "12"),
                new NameValuePair("ed", "31"),
                new NameValuePair("tm", "d"),
                new NameValuePair("p", String.valueOf(page)),
        });
        method.setFollowRedirects(true);

        return method;
    }

    public static HttpMethod getDividendsHistoryMethod(IFeedIdentifier identifier, Date from, Date to, int page) {
        String symbol = getSymbol(identifier);

        Calendar fromDate = Calendar.getInstance();
        fromDate.setTime(from);

        Calendar toDate = Calendar.getInstance();
        toDate.setTime(to);

        GetMethod method = new GetMethod("http://" + historyFeedHost + "/stocks/history/");
        method.setQueryString(new NameValuePair[] {
                new NameValuePair("code", symbol),
                new NameValuePair("sy", String.valueOf(toDate.get(Calendar.YEAR))),
                new NameValuePair("sm", String.valueOf(toDate.get(Calendar.MONTH))),
                new NameValuePair("sd", String.valueOf(toDate.get(Calendar.DAY_OF_MONTH))),
                new NameValuePair("ey", String.valueOf(fromDate.get(Calendar.YEAR))),
                new NameValuePair("em", String.valueOf(fromDate.get(Calendar.MONTH))),
                new NameValuePair("ed", String.valueOf(fromDate.get(Calendar.DAY_OF_MONTH))),
                new NameValuePair("tm", "d"),
                new NameValuePair("p", String.valueOf(page)),
        });
        method.setFollowRedirects(true);

        return method;
    }

    public static URL getRSSNewsFeedForSecurity(ISecurity security) throws MalformedURLException, URIException, NullPointerException {
        IFeedIdentifier identifier = (IFeedIdentifier) security.getAdapter(IFeedIdentifier.class);
        if (identifier == null) {
            return null;
        }

        String symbol = getSymbol(identifier);

        URI feedUrl = new URI("http://finance.yahoo.com/rss/headline?s=" + symbol, false);

        return new URL(feedUrl.toString());
    }

    public static void setupProxy(HttpClient client, String host) throws URISyntaxException {
        if (YahooJapanActivator.getDefault() == null) {
            return;
        }
        BundleContext context = YahooJapanActivator.getDefault().getBundle().getBundleContext();
        ServiceReference reference = context.getServiceReference(IProxyService.class.getName());
        if (reference != null) {
            IProxyService proxyService = (IProxyService) context.getService(reference);
            IProxyData[] proxyData = proxyService.select(new java.net.URI(IProxyData.HTTP_PROXY_TYPE, "//" + host, null));
            if (proxyData != null && proxyData.length != 0) {
                if (proxyData[0].getHost() != null) {
                    client.getHostConfiguration().setProxy(proxyData[0].getHost(), proxyData[0].getPort());
                }
                if (proxyData[0].isRequiresAuthentication()) {
                    client.getState().setProxyCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyData[0].getUserId(), proxyData[0].getPassword()));
                }
            }
            context.ungetService(reference);
        }
    }
}
