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

package org.eclipsetrader.yahoojapanfx.internal.core.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipsetrader.core.feed.IBackfillConnector;
import org.eclipsetrader.core.feed.IDividend;
import org.eclipsetrader.core.feed.IFeedIdentifier;
import org.eclipsetrader.core.feed.IOHLC;
import org.eclipsetrader.core.feed.ISplit;
import org.eclipsetrader.core.feed.OHLC;
import org.eclipsetrader.core.feed.TimeSpan;
import org.eclipsetrader.core.feed.TimeSpan.Units;
import org.eclipsetrader.yahoojapanfx.internal.Activator;
import org.eclipsetrader.yahoojapanfx.internal.core.Util;
import org.json.JSONArray;

public class BackfillConnector implements IBackfillConnector, IExecutableExtension {

    private String id;
    private String name;

    private SimpleDateFormat df;
    private SimpleDateFormat tf;
//    private NumberFormat pf;
//    private NumberFormat nf;

    public BackfillConnector() {
        df = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
        tf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
//        pf = NumberFormat.getInstance(Locale.JAPAN);
//        nf = NumberFormat.getInstance(Locale.JAPAN);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
     */
    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
        id = config.getAttribute("id"); //$NON-NLS-1$
        name = config.getAttribute("name"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IBackfillConnector#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IBackfillConnector#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IBackfillConnector#canBackfill(org.eclipsetrader.core.feed.IFeedIdentifier, org.eclipsetrader.core.feed.TimeSpan)
     */
    @Override
    public boolean canBackfill(IFeedIdentifier identifier, TimeSpan timeSpan) {
        if (timeSpan.getUnits() == Units.Days && timeSpan.getLength() == 1) {
            return true;
        }
        if (timeSpan.getUnits() == Units.Minutes && (timeSpan.getLength() == 1 || timeSpan.getLength() == 5 || timeSpan.getLength() == 15 || timeSpan.getLength() == 60)) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IBackfillConnector#backfillHistory(org.eclipsetrader.core.feed.IFeedIdentifier, java.util.Date, java.util.Date, org.eclipsetrader.core.feed.TimeSpan)
     */
    @Override
    public IOHLC[] backfillHistory(IFeedIdentifier identifier, Date from, Date to, TimeSpan timeSpan) {
        List<OHLC> list = new ArrayList<OHLC>();

        String period = "";
        if (timeSpan.getUnits() == TimeSpan.Units.Days) {
            period = "1d";
        } else if (timeSpan.getUnits() == TimeSpan.Units.Minutes) {
            period = String.valueOf(timeSpan.getLength()) + "m";
        }

        HttpClient client = new HttpClient();
        try {
            HttpMethod method = Util.getPrepareHistoryFeedMethod(identifier);
            Util.setupProxy(client, method.getURI().getHost());

            client.executeMethod(method);

            BufferedReader in;
            if ((method.getResponseHeader("Content-Encoding") != null) && (method.getResponseHeader("Content-Encoding").getValue().equals("gzip"))) {
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(method.getResponseBodyAsStream())));
            } else {
                in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            }
            String inputLine;
            String c = "";
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.indexOf("YJFIN.c =") >= 0) {
                    c = inputLine.substring(inputLine.indexOf("YJFIN.c =") + 11, inputLine.indexOf("\";"));
                }
            }
            in.close();

            method = Util.getHistoryFeedMethod(identifier, period, c);

            client.executeMethod(method);

            if ((method.getResponseHeader("Content-Encoding") != null) && (method.getResponseHeader("Content-Encoding").getValue().equals("gzip"))) {
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(method.getResponseBodyAsStream())));
            } else {
                in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            }
            if (timeSpan.getUnits() == TimeSpan.Units.Days) {
                readHistoryStream(in, list);
            } else {
                readIntradayStream(in, list);
            }
            in.close();
        } catch (Exception e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Error reading data", e); //$NON-NLS-1$
            Activator.log(status);
        }

        Collections.sort(list, new Comparator<OHLC>() {

            @Override
            public int compare(OHLC o1, OHLC o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        for (Iterator<OHLC> iter = list.iterator(); iter.hasNext();) {
            OHLC ohlc = iter.next();
            if (ohlc.getDate().before(from) || ohlc.getDate().after(to)) {
                iter.remove();
            }
        }

        return list.toArray(new IOHLC[list.size()]);
    }

    void readHistoryStream(BufferedReader in, List<OHLC> list) throws IOException {
    	StringBuilder sb = new StringBuilder(1000);
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
        	sb.append(inputLine);
        }

        try {
            parseResponseLine(sb.toString(), list);
        } catch (ParseException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Error parsing data: " + inputLine, e);
            Activator.log(status);
        }
    }

    void readIntradayStream(BufferedReader in, List<OHLC> list) throws IOException {
    	StringBuilder sb = new StringBuilder(1000);
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
        	sb.append(inputLine);
        }

        try {
            parse1DayResponseLine(sb.toString(), list);
        } catch (ParseException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Error parsing data: " + inputLine, e);
            Activator.log(status);
        }
    }

    protected void parseResponseLine(String inputLine, List<OHLC> list) throws ParseException {
        JSONArray arr = new JSONArray(inputLine);
        for (int i = 0; i < arr.length(); i++) {
        	JSONArray a = arr.getJSONArray(i);
            Calendar day = Calendar.getInstance();
            try {
                day.setTime(df.parse(a.getString(0)));
            } catch (ParseException e) {
                throw e;
            }
            day.set(Calendar.HOUR, 0);
            day.set(Calendar.MINUTE, 0);
            day.set(Calendar.SECOND, 0);
            day.set(Calendar.MILLISECOND, 0);

            double close = a.getDouble(4);
            double high = a.getDouble(1);
            double low = a.getDouble(2);
            double open = a.getDouble(3);
            long volume = 0l;

            OHLC bar = new OHLC(day.getTime(), open, high, low, close, volume);
            list.add(bar);
        }
    }

    protected void parse1DayResponseLine(String inputLine, List<OHLC> list) throws ParseException {
        JSONArray arr = new JSONArray(inputLine);
        for (int i = 0; i < arr.length(); i++) {
        	JSONArray a = arr.getJSONArray(i);
            Calendar day = Calendar.getInstance();
            try {
                day.setTime(tf.parse(a.getString(0)));
            } catch (ParseException e) {
                throw e;
            }
            day.set(Calendar.MILLISECOND, 0);

            double close = a.getDouble(4);
            double high = a.getDouble(1);
            double low = a.getDouble(2);
            double open = a.getDouble(3);
            long volume = 0l;

            OHLC bar = new OHLC(day.getTime(), open, high, low, close, volume);
            list.add(bar);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IBackfillConnector#backfillDividends(org.eclipsetrader.core.feed.IFeedIdentifier, java.util.Date, java.util.Date)
     */
    @Override
    public IDividend[] backfillDividends(IFeedIdentifier identifier, Date from, Date to) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipsetrader.core.feed.IBackfillConnector#backfillSplits(org.eclipsetrader.core.feed.IFeedIdentifier, java.util.Date, java.util.Date)
     */
    @Override
    public ISplit[] backfillSplits(IFeedIdentifier identifier, Date from, Date to) {
        return null;
    }
}
