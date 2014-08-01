/*
 * Copyright (c) 2004-2011 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 *     Naofumi Fukue - Yahoo! JAPAN Fx Connector
 */

package org.eclipsetrader.yahoojapanfx.internal.core;

import java.net.URISyntaxException;
import java.util.Calendar;

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
import org.eclipsetrader.yahoojapanfx.internal.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Util {

    public static final String snapshotFeedHost = "fx.yahoo.co.jp"; //$NON-NLS-1$
    public static final String historyFeedHost = "fx.yahoo.co.jp"; //$NON-NLS-1$
    public static final String ENCODING = "Shift_JIS"; //$NON-NLS-1$

    private Util() {
    }

    public static String getSymbol(IFeedIdentifier identifier) {
        String symbol = identifier.getSymbol();

        IFeedProperties properties = (IFeedProperties) identifier.getAdapter(IFeedProperties.class);
        if (properties != null) {
            if (properties.getProperty("org.eclipsetrader.yahoojapanfx.symbol") != null) {
                symbol = properties.getProperty("org.eclipsetrader.yahoojapanfx.symbol");
            }
        }

        return symbol;
    }

    /**
     * Builds the http method for live prices snapshot download.
     *
     * @return the method.
     */
    public static HttpMethod getSnapshotFeedMethod(String host) {
    	Calendar c = Calendar.getInstance();
//    	long t = c.getTimeInMillis();
        StringBuilder s = new StringBuilder();
        s.append("/all_rate_data.js"); //$NON-NLS-1$
//        s.append("?_=" + String.valueOf(t)); //$NON-NLS-1$

        GetMethod method = new GetMethod("http://" + host + s.toString());
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:24.0) Gecko/20100101 Firefox/24.0");
        method.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        method.setRequestHeader("Accept-Language", "ja,en-us;q=0.7,en;q=0.3");
        method.setRequestHeader("Accept-Encoding", "gzip, deflate");
        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        method.setRequestHeader("Referer", "http://fx.yahoo.co.jp/chart/rate.html");

        return method;
    }

    /**
     * Builds the http method for historycal prices download.
     *
     * @return the method.
     */
    public static HttpMethod getPrepareHistoryFeedMethod(IFeedIdentifier identifier) throws URIException {
        String symbol = getSymbol(identifier).toLowerCase();

        String prefix = "/chart/";
        String suffix = symbol.substring(0, 3) + "_" + symbol.substring(3, 6) + ".html";
        URI uri = new URI("http", historyFeedHost, prefix + suffix, "");

        GetMethod method = new GetMethod();
        method.setURI(uri);
        setCommonRequestHeaders(method);
        method.setFollowRedirects(true);
//        try {
//            System.out.println(method.getURI().toString());
//        } catch (URIException e) {
//            e.printStackTrace();
//        }

        return method;
    }

    /**
     * Builds the http method for historycal prices download.
     *
     * @return the method.
     */
    public static HttpMethod getHistoryFeedMethod(IFeedIdentifier identifier, String period, String c) throws URIException {
        String symbol = getSymbol(identifier).toLowerCase();

        String prefix = "/chart/history.json";
        String suffix = "?period=" + period + "&position=" + symbol + "&c=" + c;
        URI uri = new URI("http", historyFeedHost, prefix + suffix, "");

        GetMethod method = new GetMethod();
        method.setURI(uri);
        setCommonRequestHeaders(method);
        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        method.setRequestHeader("Referer", "http://fx.yahoo.co.jp/chart/" + symbol.substring(0, 3) + "_" + symbol.substring(3, 6) + ".html");
        method.setFollowRedirects(true);
//        try {
//            System.out.println(method.getURI().toString());
//        } catch (URIException e) {
//            e.printStackTrace();
//        }

        return method;
    }

    public static void setupProxy(HttpClient client, String host) throws URISyntaxException {
        if (Activator.getDefault() == null) {
            return;
        }
        BundleContext context = Activator.getDefault().getBundle().getBundleContext();
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

    private static void setCommonRequestHeaders(HttpMethod method) {
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:24.0) Gecko/20100101 Firefox/24.0");
        method.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        method.setRequestHeader("Accept-Language", "ja,en-us;q=0.7,en;q=0.3");
        method.setRequestHeader("Accept-Encoding", "gzip, deflate");
    }
}
