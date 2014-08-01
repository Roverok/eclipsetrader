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

package org.eclipsetrader.ui.charts;

public enum MAType {
    SMA("sma", Messages.MAType_Simple), EMA("ema", Messages.MAType_Exponential), WMA("wma", Messages.MAType_Weighted), DEMA("dema", Messages.MAType_DoubleExponential), TEMA("tema", Messages.MAType_TripleExponential), TRIMA("trima", Messages.MAType_Triangular), KAMA("kama", Messages.MAType_KaufmanAdaptive), MAMA("mama", Messages.MAType_MESAAdaptive), T3("t3", Messages.MAType_TripleExponentialT3);

    private String name;
    private String description;

    private MAType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the TA-Lib moving average type from our type.
     *
     * @param type our type.
     * @return TA-Lib type.
     */
    public static com.tictactec.ta.lib.MAType getTALib_MAType(MAType type) {
        com.tictactec.ta.lib.MAType maType = com.tictactec.ta.lib.MAType.Sma;
        switch (type) {
            case SMA:
                maType = com.tictactec.ta.lib.MAType.Sma;
                break;
            case EMA:
                maType = com.tictactec.ta.lib.MAType.Ema;
                break;
            case WMA:
                maType = com.tictactec.ta.lib.MAType.Wma;
                break;
            case DEMA:
                maType = com.tictactec.ta.lib.MAType.Dema;
                break;
            case TEMA:
                maType = com.tictactec.ta.lib.MAType.Tema;
                break;
            case TRIMA:
                maType = com.tictactec.ta.lib.MAType.Trima;
                break;
            case KAMA:
                maType = com.tictactec.ta.lib.MAType.Kama;
                break;
            case MAMA:
                maType = com.tictactec.ta.lib.MAType.Mama;
                break;
            case T3:
                maType = com.tictactec.ta.lib.MAType.T3;
                break;
        }
        return maType;
    }

    public static MAType getFromName(String name) {
        MAType[] l = values();
        for (int i = 0; i < l.length; i++) {
            if (l[i].getName().equals(name)) {
                return l[i];
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return description;
    }
}
