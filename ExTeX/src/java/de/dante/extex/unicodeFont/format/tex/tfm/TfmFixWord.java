/*
 * Copyright (C) 2004-2006 The ExTeX Group and individual authors listed below
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package de.dante.extex.unicodeFont.format.tex.tfm;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * TFM-FixWord.
 * <p>
 * The dimensions are represented in the same way as in tfm files. Higher
 * 12 bits is the whole part and lower 20 bits is the fractional part.
 * </p>
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.1 $
 */
public class TfmFixWord implements Serializable {

    /**
     * NULL.
     */
    public static final TfmFixWord NULL = null;

    /**
     * ZERO.
     */
    public static final TfmFixWord ZERO = new TfmFixWord(0);

    /**
     * UNITY.
     */
    public static final TfmFixWord UNITY = new TfmFixWord(1);

    /**
     * TEN.
     */
    public static final TfmFixWord TEN = new TfmFixWord(TfmConstants.CONST_10);

    /**
     * POINT-SHIFT.
     */
    public static final int POINTSHIFT = 20;

    /**
     * fixdominator.
     */
    public static final int FIXWORDDENOMINATOR = 0x100000;

    /**
     * tfmconf.
     */
    public static final int TFMCONV = 0x10;

    /**
     * conf.
     */
    public static final int CONV = 0x10000;

    /**
     * Create a new object.
     *
     * @param val the values as int
     */
    public TfmFixWord(final int val) {

        value = val << POINTSHIFT;
    }

    /**
     * Create a new object.
     */
    public TfmFixWord() {

        value = 0;
    }

    /**
     * Create a new object.
     *
     * @param val the values as String
     */
    public TfmFixWord(final String val) {

        try {
            value = Integer.parseInt(val);
        } catch (NumberFormatException e) {
            // use default
            value = 0;
        }
    }

    /**
     * Create new object.
     *
     * @param num   the num
     * @param den   the den
     */
    public TfmFixWord(final int num, final int den) {

        value = ((long) num << POINTSHIFT) / den;
    }

    /**
     * the internal value.
     */
    private long value;

    /**
     * Return the internal value.
     *
     * @return the internal value
     */
    public long getValue() {

        return value;
    }

    /**
     * less than.
     *
     * @param num the value to compare
     * @return <code>true</code>, if the internal values is lesser,
     * otherwise <code>false</code>
     */
    public boolean lessThan(final int num) {

        return (value < (num << POINTSHIFT));
    }

    /**
     * more than.
     *
     * @param num the value to compare
     * @return <code>true</code>, if the internal values are more,
     * otherwise <code>false</code>
     */
    public boolean moreThan(final int num) {

        return (value > (num << POINTSHIFT));
    }

    /**
     * Return the value as String in units.
     * <p>
     * It devide the value by 1000.
     * </p>
     *
     * @return the value as String in units
     */
    public String toStringUnits() {

        if (value > 0) {
            return String
                    .valueOf((value * TfmConstants.CONST_1000) >>> POINTSHIFT);
        }
        return String
                .valueOf(-((-value * TfmConstants.CONST_1000) >>> POINTSHIFT));
    }

    /**
     * FRACTIONDIGITS.
     */
    public static final int FRACTIONDIGITS = 6;

    /**
     * Returns the value as Sting in untis with comma (0.00000..).
     * @return Returns the value as Sting in untis with comma.
     */
    public String toStringComma() {

        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMaximumFractionDigits(FRACTIONDIGITS);
        nf.setGroupingUsed(false);
        return nf.format((double) value * TfmConstants.CONST_1000
                / FIXWORDDENOMINATOR);
    }

    /**
     * Returns the value as double in untis.
     * @return Returns the value as double in untis.
     */
    public double toDouble() {

        // mgn check!
        return (double) value /*  * TFMConstants.CONST_1000*/
                / FIXWORDDENOMINATOR;
    }

    /**
     * Return the values as String.
     *
     * @return the values as String
     */
    public String toString() {

        StringBuffer buf = new StringBuffer();
        long v = value;
        int unity = 1 << POINTSHIFT;
        int mask = unity - 1;
        if (v < 0) {
            buf.append('-');
            v = -v;
        }
        buf.append(v >>> POINTSHIFT);
        buf.append('.');
        v = TfmConstants.CONST_10 * (v & mask) + TfmConstants.CONST_5;
        int delta = TfmConstants.CONST_10;
        do {
            if (delta > unity) {
                v += unity / 2 - delta / 2;
            }
            buf.append(Character.forDigit((int) (v >>> POINTSHIFT),
                    TfmConstants.CONST_10));
            v = TfmConstants.CONST_10 * (v & mask);
        } while (v > (delta *= TfmConstants.CONST_10));
        return buf.toString();
    }

    /**
     * Set the value.
     * @param v The value to set.
     */
    public void setValue(final long v) {

        value = v;
    }
}