/*
 * Copyright (C) 2004-2006 The ExTeX Group and individual authors listed below
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package de.dante.extex.unicodeFont.format.xtf;

import java.io.IOException;
import java.text.DateFormat;

import de.dante.extex.font.type.Fixed32;
import de.dante.util.XMLWriterConvertible;
import de.dante.util.file.random.RandomAccessR;
import de.dante.util.xml.XMLStreamWriter;

/**
 * The 'head' table contains global information about the font.
 * It records such facts as the font version number, the creation
 * and modification dates, revision number and basic typographic
 * data that applies to the font as a whole. This includes a
 * specification of the font bounding box, the direction in which
 * the font's glyphs are most likely to be written and other
 * information about the placement of glyphs in the em square.
 *
 * <table BORDER="1">
 *   <tbody>
 *     <tr><td><b>Type</b></td><td><b>Name</b></td><td><b>Description</b></td></tr>
 *   </tbody>
 *   <tr><td>Fixed</td><td>Table version number</td><td>
 *             0x00010000 for version 1.0.</td></tr>
 *   <tr><td>Fixed</td><td>fontRevision</td><td>Set by font manufacturer.</td></tr>
 *   <tr><td>ULONG</td><td>checkSumAdjustment</td><td>
 *              To compute:  set it to 0, sum the entire font as
 *              ULONG, then store 0xB1B0AFBA - sum.</td></tr>
 *   <tr><td>ULONG</td><td>magicNumber</td><td>Set to 0x5F0F3CF5.</td></tr>
 *   <tr><td>USHORT</td><td>flags</td><td>
 *              <p>
 *              Bit 0 - baseline for font at y=0;</p>
 *              <p>
 *              Bit 1 - left sidebearing at x=0;</p>
 *              <p>
 *              Bit 2 - instructions may depend on point size;</p>
 *              <p>
 *              Bit 3 - force ppem to integer values for all
 *              internal scaler math; may use fractional ppem sizes if this bit
 *              is clear;</p>
 *              <p>
 *              Bit 4 - instructions may alter advance width (the
 *              advance widths might not scale linearly);</p>
 *              <p>
 *              Note: All other bits must be zero.</p>
 *          </td></tr>
 *   <tr><td>USHORT</td><td>unitsPerEm</td><td>
 *           Valid range is from 16 to 16384</td></tr>
 *   <tr><td>longDateTime</td><td>created</td><td>
 *           International date (8-byte field).</td></tr>
 *   <tr><td>longDateTime</td><td>modified</td><td>
 *           International date (8-byte field).</td>
 *   <tr><td>FWord</td><td>xMin</td><td>For all glyph bounding boxes.</td></tr>
 *   <tr><td>FWord</td><td>yMin</td><td>For all glyph bounding boxes.</td></tr>
 *   <tr><td>FWord</td><td>xMax</td><td>For all glyph bounding boxes.</td></tr>
 *   <tr><td>FWord</td><td>yMax</td><td>For all glyph bounding boxes.</td></tr>
 *   <tr><td>USHORT</td><td>macStyle</td><td>
 *              Bit 0 bold (if set to 1); Bit 1 italic (if set to
 *              1)<BR>Bits 2-15 reserved (set to 0).</td></tr>
 *   <tr><td>USHORT</td><td>lowestRecPPEM</td><td>
 *                Smallest readable size in pixels.</td></tr>
 *   <tr><td>SHORT</td><td>fontDirectionHint</td><td>
 *              <p>
 *                0   Fully mixed directional glyphs;</p>
 *               <p>
 *                1   Only strongly left to right;</p>
 *               <p>
 *                2   Like 1 but also contains neutrals 1;</p>
 *               <p>
 *               -1   Only strongly right to left;</p>
 *               <p>
 *               -2   Like -1 but also contains neutrals.</p>
 *           </td></tr>
 *   <tr><td>SHORT</td><td>indexToLocFormat</td><td>
 *               0 for short offsets, 1 for long.</td></tr>
 *   <tr><td>SHORT</td><td>glyphDataFormat</td><td>0 for current format.</td></tr>
 * </table>
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.2 $
 */
public class TtfTableHEAD extends AbstractXtfTable
        implements
            XtfTable,
            XMLWriterConvertible {

    /**
     * version.
     */
    private int version;

    /**
     * font revision.
     */
    private Fixed32 fontRevision;

    /**
     * checksumadjustment.
     */
    private int checkSumAdjustment;

    /**
     * magic number.
     */
    private int magicNumber;

    /**
     * flags.
     */
    private short flags;

    /**
     * units per em.
     */
    private short unitsPerEm;

    /**
     * created.
     */
    private long created;

    /**
     * modified.
     */
    private long modified;

    /**
     * xmin.
     */
    private short xMin;

    /**
     * ymin.
     */
    private short yMin;

    /**
     * xmax.
     */
    private short xMax;

    /**
     * ymax.
     */
    private short yMax;

    /**
     * macstyle.
     */
    private short macStyle;

    /**
     * lowestRecPPEM.
     */
    private short lowestRecPPEM;

    /**
     * fontDirectionHint.
     */
    private short fontDirectionHint;

    /**
     * indexToLocFormat.
     */
    private short indexToLocFormat;

    /**
     * glyphDataFormat.
     */
    private short glyphDataFormat;

    /**
     * Create a new object.
     *
     * @param tablemap  the tablemap
     * @param de        entry
     * @param rar       input
     * @throws IOException if an IO-error occurs
     */
    TtfTableHEAD(final XtfTableMap tablemap, final XtfTableDirectory.Entry de,
            final RandomAccessR rar) throws IOException {

        super(tablemap);
        rar.seek(de.getOffset());
        version = rar.readInt();
        fontRevision = new Fixed32(rar.readInt());
        checkSumAdjustment = rar.readInt();
        magicNumber = rar.readInt();
        flags = rar.readShort();
        unitsPerEm = rar.readShort();
        created = rar.readLong();
        modified = rar.readLong();
        xMin = rar.readShort();
        yMin = rar.readShort();
        xMax = rar.readShort();
        yMax = rar.readShort();
        macStyle = rar.readShort();
        lowestRecPPEM = rar.readShort();
        fontDirectionHint = rar.readShort();
        indexToLocFormat = rar.readShort();
        glyphDataFormat = rar.readShort();
    }

    /**
     * Returns the checkSumAdjustment.
     * @return Returns the checkSumAdjustment.
     */
    public int getCheckSumAdjustment() {

        return checkSumAdjustment;
    }

    /**
     * Returns the created.
     * @return Returns the created.
     */
    public long getCreated() {

        return created;
    }

    /**
     * Returns the flags.
     * @return Returns the flags.
     */
    public short getFlags() {

        return flags;
    }

    /**
     * Returns the fontDirectionHint.
     * @return Returns the fontDirectionHint.
     */
    public short getFontDirectionHint() {

        return fontDirectionHint;
    }

    /**
     * Returns the fontRevision.
     * @return Returns the fontRevision.
     */
    public Fixed32 getFontRevision() {

        return fontRevision;
    }

    /**
     * Returns the indexToLocFormat.
     * @return Returns the indexToLocFormat.
     */
    public short getIndexToLocFormat() {

        return indexToLocFormat;
    }

    /**
     * Returns the lowestRecPPEM.
     * @return Returns the lowestRecPPEM.
     */
    public short getLowestRecPPEM() {

        return lowestRecPPEM;
    }

    /**
     * Returns the macStyle.
     * @return Returns the macStyle.
     */
    public short getMacStyle() {

        return macStyle;
    }

    /**
     * Returns the magicNumber.
     * @return Returns the magicNumber.
     */
    public int getMagicNumber() {

        return magicNumber;
    }

    /**
     * Returns the modified.
     * @return Returns the modified.
     */
    public long getModified() {

        return modified;
    }

    /**
     * Returns the unitsPerEm.
     * @return Returns the unitsPerEm.
     */
    public short getUnitsPerEm() {

        return unitsPerEm;
    }

    /**
     * Returns the version.
     * @return Returns the version.
     */
    public int getVersion() {

        return version;
    }

    /**
     * Returns the xMax.
     * @return Returns the xMax.
     */
    public short getXMax() {

        return xMax;
    }

    /**
     * Returns the xMin.
     * @return Returns the xMin.
     */
    public short getXMin() {

        return xMin;
    }

    /**
     * Returns the yMax.
     * @return Returns the yMax.
     */
    public short getYMax() {

        return yMax;
    }

    /**
     * Returns the yMin.
     * @return Returns the yMin.
     */
    public short getYMin() {

        return yMin;
    }

    /**
     * Get the table type, as a table directory value.
     * @return Returns the table type
     */
    public int getType() {

        return XtfReader.HEAD;
    }

    /**
     * @see de.dante.extex.unicodeFont.format.xtf.XtfTable#getShortcur()
     */
    public String getShortcut() {

        return "head";
    }

    /**
     * @see de.dante.util.XMLWriterConvertible#writeXML(
     *      de.dante.util.xml.XMLStreamWriter)
     */
    public void writeXML(final XMLStreamWriter writer) throws IOException {

        writeStartElement(writer);

        DateFormat dformat = DateFormat.getDateInstance();
        writer.writeAttribute("version", XtfReader
                .convertIntToHexString(version));
        writer.writeAttribute("fontrevision", String.valueOf(fontRevision
                .getDoubleValue()));
        writer.writeAttribute("checksumadjustment", XtfReader
                .convertIntToHexString(checkSumAdjustment));
        writer.writeAttribute("magicnumber", XtfReader
                .convertIntToHexString(magicNumber));
        writer.writeAttribute("flags", XtfReader
                .convertIntToBinaryString(flags));
        writer.writeAttribute("unitsperem", String.valueOf(unitsPerEm));
        writer.writeAttribute("created", dformat.format(XtfReader
                .convertDate(created)));
        writer.writeAttribute("modified", dformat.format(XtfReader
                .convertDate(modified)));
        writer.writeAttribute("xmin", String.valueOf(xMin));
        writer.writeAttribute("ymin", String.valueOf(yMin));
        writer.writeAttribute("xmax", String.valueOf(xMax));
        writer.writeAttribute("ymax", String.valueOf(yMax));
        writer.writeAttribute("macstyle", XtfReader
                .convertIntToBinaryString(macStyle));
        writer.writeAttribute("lowestrecppem", String.valueOf(lowestRecPPEM));
        writer.writeAttribute("fontdiretionhint", String
                .valueOf(fontDirectionHint));
        writer.writeAttribute("indextolocformat", String
                .valueOf(indexToLocFormat));
        writer.writeAttribute("glyphdataformat", String
                .valueOf(glyphDataFormat));
        writer.writeEndElement();
    }

    /**
     * Returns the glyphDataFormat.
     * @return Returns the glyphDataFormat.
     */
    public short getGlyphDataFormat() {

        return glyphDataFormat;
    }
}
