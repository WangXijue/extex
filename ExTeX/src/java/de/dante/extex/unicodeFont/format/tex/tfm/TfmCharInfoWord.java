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

import java.io.IOException;
import java.io.Serializable;

import org.jdom.Element;

import de.dante.extex.unicodeFont.format.pl.PlFormat;
import de.dante.extex.unicodeFont.format.pl.PlWriter;
import de.dante.util.XMLWriterConvertible;
import de.dante.util.file.random.RandomAccessR;
import de.dante.util.xml.XMLStreamWriter;

/**
 * Class for TFM char info word.
 *
 * <p>Each char_info_word contains six fields packed
 *    into four bytes as follows.</p>
 *
 * <table border="1">
 *   <thead>
 *     <tr><td>byte</td><td>description</td></tr>
 *   </thead>
 *   <tr><td>first  </td><td>width_index (8 bits)</td></tr>
 *   <tr><td>second </td><td>height_index (4 bits) times 16,
 *                           plus depth_index (4 bits)</td></tr>
 *   <tr><td>third  </td><td>italic_index (6 bits) times 4,
 *                           plus tag (2 bits)</td></tr>
 *   <tr><td>fourth </td><td>remainder (8 bits)</td></tr>
 * </table>
 *
 * <p>
 * The tag field has four values that explain how to
 * interpret the remainder field.
 * </p>
 *
 * <table border="1">
 *   <thead>
 *     <tr><td>tag</td><td>description</td></tr>
 *   </thead>
 *   <tr><td>0  </td><td>no_tag: means that remainder is unused.</td></tr>
 *   <tr><td>1  </td><td>lig_tag: means that this character has a
 *                       ligature/kerning program starting at
 *                       lig_kern[remainder].</td></tr>
 *   <tr><td>2  </td><td>list_tag: means that this character is
 *                       part of a chain of characters of ascending sizes,
 *                       and not the largest in the chain.
 *                       The remainder field gives the character code of
 *                       the next larger character.</td></tr>
 *   <tr><td>3  </td><td>ext_tag: means that this character code
 *                       represents an extensible character, i.e.,
 *                       a character that is built up of smaller pieces
 *                       so that it can be made arbitrarily large.
 *                       The pieces are specified in exten[remainder].</td></tr>
 * </table>
 *
 * <p>
 * Information from:
 * The DVI Driver Standard, Level 0
 * The TUG DVI Driver Standards Committee
 * </p>
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.1 $
 */

public class TfmCharInfoWord
        implements
            XMLWriterConvertible,
            PlFormat,
            Serializable {

    /**
     * no_tag: vanilla character.
     */
    public static final Tag NO_TAG = new Tag();

    /**
     * no_tag: 0.
     */
    private static final int TAG0 = 0;

    /**
     * lig_tag: character has a ligature/kerning program.
     */
    public static final Tag LIG_TAG = new Tag();

    /**
     * no_tag: 1.
     */
    private static final int TAG1 = 1;

    /**
     * list_tag: character has a successor in a charlist.
     */
    public static final Tag LIST_TAG = new Tag();

    /**
     * no_tag: 2.
     */
    private static final int TAG2 = 2;

    /**
     * ext_tag: character is extensible.
     */
    public static final Tag EXT_TAG = new Tag();

    /**
     * no_tag: 3.
     */
    private static final int TAG3 = 3;

    /**
     * the width index.
     */
    private short widthindex;

    /**
     * the height index.
     */
    private short heightindex;

    /**
     * the depth index.
     */
    private short depthindex;

    /**
     * the italic index.
     */
    private short italicindex;

    /**
     * the tag (as number).
     */
    private short tag;

    /**
     * the tag.
     */
    private Tag tagT;

    /**
     * the remainder.
     */
    private short remainder;

    /**
     * the char id.
     */
    private int charid;

    /**
     * smallest character code in the font.
     */
    private short bc;

    /**
     * Create a new object.
     * @param rar   the input
     * @param id    the id
     * @throws IOException if an IO-error occurs.
     */
    public TfmCharInfoWord(final RandomAccessR rar, final int id)
            throws IOException {

        charid = id;
        widthindex = (short) rar.readByteAsInt();
        short heightdepthindex = (short) rar.readByteAsInt();
        heightindex = (short) (heightdepthindex >> TfmConstants.CONST_4 & TfmConstants.CONST_X0F);
        depthindex = (short) (heightdepthindex & TfmConstants.CONST_X0F);
        short italicindextag = (short) rar.readByteAsInt();
        italicindex = (short) (italicindextag >> 2 & TfmConstants.CONST_X3F);
        tag = (short) (italicindextag & TfmConstants.CONST_X03);
        remainder = (short) rar.readByteAsInt();

        switch (tag) {
            case TAG0 :
                tagT = NO_TAG;
                break;
            case TAG1 :
                tagT = LIG_TAG;
                break;
            case TAG2 :
                tagT = LIST_TAG;
                break;
            case TAG3 :
                tagT = EXT_TAG;
                break;
            default :
                // not defined: use no_tag
                tagT = NO_TAG;
        }
    }

    /**
     * Symbolic constant for nonexistent character code.
     */
    public static final short NOCHARCODE = -1;

    /**
     * Symbolic constant for index which is not valid.
     */
    public static final int NOINDEX = -1;

    /**
     * Character width.
     */
    private TfmFixWord width = TfmFixWord.ZERO;

    /**
     * Character height.
     */
    private TfmFixWord height = TfmFixWord.ZERO;

    /**
     * Character depth.
     */
    private TfmFixWord depth = TfmFixWord.ZERO;

    /**
     * Character italic correction.
     */
    private TfmFixWord italic = TfmFixWord.ZERO;

    /**
     * Index to newly created ligKernTable which is set
     * during translation of the original raw lig/kern table
     * in the tfm file.
     */
    private int ligkernstart = NOINDEX;

    /**
     * Next larger character code.
     */
    private short nextchar = NOINDEX;

    /**
     * top part chracter code.
     */
    private short top = NOCHARCODE;

    /**
     * middle part chracter code.
     */
    private short mid = NOCHARCODE;

    /**
     * bottom part chracter code.
     */
    private short bot = NOCHARCODE;

    /**
     * repeatable part chracter code.
     */
    private short rep = NOCHARCODE;

    /**
     * Tag (type-safe class).
     */
    private static final class Tag implements Serializable {

        /**
         * Creates a new object.
         */
        public Tag() {

            super();
        }
    }

    /**
     * Returns the charid.
     * @return Returns the charid.
     */
    public int getCharid() {

        return charid;
    }

    /**
     * Returns the depthindex.
     * @return Returns the depthindex.
     */
    public short getDepthindex() {

        return depthindex;
    }

    /**
     * Returns the heightindex.
     * @return Returns the heightindex.
     */
    public short getHeightindex() {

        return heightindex;
    }

    /**
     * Returns the italicindex.
     * @return Returns the italicindex.
     */
    public short getItalicindex() {

        return italicindex;
    }

    /**
     * Returns the remainder.
     * @return Returns the remainder.
     */
    public short getRemainder() {

        return remainder;
    }

    /**
     * Returns the tag as number.
     * @return Returns the tag as number.
     */
    public short getTagNumber() {

        return tag;
    }

    /**
     * Returns the tag.
     * @return Returns the tag.
     */
    public Tag getTag() {

        return tagT;
    }

    /**
     * Returns the widthindex.
     * @return Returns the widthindex.
     */
    public short getWidthindex() {

        return widthindex;
    }

    /**
     * Returns the ligkernstart.
     * @return Returns the ligkernstart.
     */
    public int getLigkernstart() {

        return ligkernstart;
    }

    /**
     * Set the ligkernstart.
     * @param ligkerns  The ligkernstart to set.
     */
    public void setLigkernstart(final int ligkerns) {

        ligkernstart = ligkerns;
    }

    /**
     * Returns the bot.
     * @return Returns the bot.
     */
    public short getBot() {

        return bot;
    }

    /**
     * Set the bot.
     * @param abot The bot to set.
     */
    public void setBot(final short abot) {

        bot = abot;
    }

    /**
     * Returns the depth.
     * @return Returns the depth.
     */
    public TfmFixWord getDepth() {

        return depth;
    }

    /**
     * Det the depth.
     * @param adepth The depth to set.
     */
    public void setDepth(final TfmFixWord adepth) {

        depth = adepth;
    }

    /**
     * Returns the height.
     * @return Returns the height.
     */
    public TfmFixWord getHeight() {

        return height;
    }

    /**
     * Set the height.
     * @param aheight The height to set.
     */
    public void setHeight(final TfmFixWord aheight) {

        height = aheight;
    }

    /**
     * Returns the italic.
     * @return Returns the italic.
     */
    public TfmFixWord getItalic() {

        return italic;
    }

    /**
     * Set the italic.
     * @param aitalic The italic to set.
     */
    public void setItalic(final TfmFixWord aitalic) {

        italic = aitalic;
    }

    /**
     * Returns the mid.
     * @return Returns the mid.
     */
    public short getMid() {

        return mid;
    }

    /**
     * Set the mid.
     * @param amid The mid to set.
     */
    public void setMid(final short amid) {

        mid = amid;
    }

    /**
     * Returns the nextchar.
     * @return Returns the nextchar.
     */
    public short getNextchar() {

        return nextchar;
    }

    /**
     * Set the nextchar.
     * @param anextchar The nextchar to set.
     */
    public void setNextchar(final short anextchar) {

        nextchar = anextchar;
    }

    /**
     * Returns the rep.
     * @return Returns the rep.
     */
    public short getRep() {

        return rep;
    }

    /**
     * Set the rep.
     * @param arep The rep to set.
     */
    public void setRep(final short arep) {

        rep = arep;
    }

    /**
     * Returns the top.
     * @return Returns the top.
     */
    public short getTop() {

        return top;
    }

    /**
     * Set the top.
     * @param atop The top to set.
     */
    public void setTop(final short atop) {

        top = atop;
    }

    /**
     * Returns the width.
     * @return Returns the width.
     */
    public TfmFixWord getWidth() {

        return width;
    }

    /**
     * Set the width.
     * @param awidth The width to set.
     */
    public void setWidth(final TfmFixWord awidth) {

        width = awidth;
    }

    /**
     * Test, if the character exists in the font.
     * (a character exists, if it have a width)
     *
     * @return Returns <code>true</code> if the character exists.
     */
    public boolean exists() {

        return widthindex != 0;
    }

    /**
     * Resets the tag field to NOTAG (zero) value.
     */
    public void resetTag() {

        tag = TAG0;
        tagT = NO_TAG;
    }

    /**
     * Lig/kern programs in the final format.
     */
    private TfmLigKern[] ligKernTable;

    /**
     * Set the ligKernTable.
     * @param lk    The ligKernTable to set.
     */
    public void setLigKernTable(final TfmLigKern[] lk) {

        ligKernTable = lk;
    }

    /**
     * the glyphname.
     */
    private String glyphname;

    /**
     * Returns the glyphname.
     * @return Returns the glyphname.
     */
    public String getGlyphname() {

        return glyphname;
    }

    /**
     * Set the glyphname.
     * @param gn The glyphname to set.
     */
    public void setGlyphname(final String gn) {

        glyphname = gn;
    }

    /**
     * Set bc.
     * @param abc The bc to set.
     */
    public void setBc(final short abc) {

        bc = abc;
    }

    /**
     * @see de.dante.extex.font.type.PlFormat#toPL(de.dante.extex.font.type.PlWriter)
     */
    public void toPL(final PlWriter out) throws IOException {

        out.addFixWord(width, "CHARWD");
        out.addFixWord(height, "CHARHT");
        out.addFixWord(depth, "CHARDP");
        out.addFixWord(italic, "CHARIC");

        if (foundEntry()) {
            out.plopen("COMMENT");
            if (glyphname != null) {
                out.plopen("NAME").addStr(glyphname).plclose();
            }
            if (getTop() != NOCHARCODE) {
                out.plopen("TOP").addDec(getTop()).plclose();
            }
            if (getMid() != NOCHARCODE) {
                out.plopen("MID").addDec(getMid()).plclose();
            }
            if (getBot() != NOCHARCODE) {
                out.plopen("BOT").addDec(getBot()).plclose();
            }
            if (getRep() != NOCHARCODE) {
                out.plopen("REP").addDec(getRep()).plclose();
            }
            // ligature
            int ligstart = getLigkernstart();
            if (ligstart != NOINDEX && ligKernTable != null) {

                for (int k = ligstart; k != NOINDEX; k = ligKernTable[k]
                        .nextIndex(k)) {
                    TfmLigKern lk = ligKernTable[k];

                    if (lk instanceof TfmLigature) {
                        TfmLigature lig = (TfmLigature) lk;

                        out.plopen("LIG").addChar(lig.getNextChar()).addChar(
                                lig.getAddingChar()).plclose();
                    } else if (lk instanceof TfmKerning) {
                        TfmKerning kern = (TfmKerning) lk;

                        out.plopen("KRN").addChar(kern.getNextChar()).addReal(
                                kern.getKern()).plclose();
                    }
                }
            }
            out.plclose();
        }
    }

    /**
     * Add glyph to the element.
     * @param glyph   the element
     */
    public void addGlyph(final Element glyph) {

        glyph.setAttribute("width", getWidth().toStringComma());
        glyph.setAttribute("height", getHeight().toStringComma());
        glyph.setAttribute("depth", getDepth().toStringComma());
        glyph.setAttribute("italic", getItalic().toStringComma());
        glyph.setAttribute("width-fw", String.valueOf(getWidth().getValue()));
        glyph.setAttribute("height-fw", String.valueOf(getHeight().getValue()));
        glyph.setAttribute("depth-fw", String.valueOf(getDepth().getValue()));
        glyph.setAttribute("italic-fw", String.valueOf(getItalic().getValue()));

        // ligature
        int ligstart = getLigkernstart();
        if (ligstart != TfmCharInfoWord.NOINDEX) {

            for (int k = ligstart; k != TfmCharInfoWord.NOINDEX; k = ligKernTable[k]
                    .nextIndex(k)) {
                TfmLigKern lk = ligKernTable[k];

                if (lk instanceof TfmLigature) {
                    TfmLigature lig = (TfmLigature) lk;

                    Element ligature = new Element("ligature");

                    ligature.setAttribute("letter-id", String.valueOf(lig
                            .getNextChar()));
                    String sl = Character.toString((char) lig.getNextChar());
                    if (sl != null && sl.trim().length() > 0) {
                        ligature.setAttribute("letter", sl.trim());
                    }

                    ligature.setAttribute("lig-id", String.valueOf(lig
                            .getAddingChar()));
                    String slig = Character
                            .toString((char) lig.getAddingChar());
                    if (slig != null && slig.trim().length() > 0) {
                        ligature.setAttribute("lig", slig.trim());
                    }
                    glyph.addContent(ligature);
                } else if (lk instanceof TfmKerning) {
                    TfmKerning kern = (TfmKerning) lk;

                    Element kerning = new Element("kerning");

                    kerning.setAttribute("glyph-id", String.valueOf(kern
                            .getNextChar()));
                    String sk = Character.toString((char) kern.getNextChar());
                    if (sk != null && sk.trim().length() > 0) {
                        kerning.setAttribute("char", sk.trim());
                    }
                    kerning
                            .setAttribute("size", kern.getKern()
                                    .toStringComma());

                    glyph.addContent(kerning);
                }
            }
        }
    }

    /**
     * Check, if char has a entry (glyphname, top, mid, bot, rep, ligature or kern.
     * @return Returns true, if the char has an entry.
     */
    private boolean foundEntry() {

        boolean found = false;
        if (glyphname != null) {
            found = true;
        } else if (getTop() != NOCHARCODE) {
            found = true;
        } else if (getMid() != NOCHARCODE) {
            found = true;
        } else if (getBot() != NOCHARCODE) {
            found = true;
        } else if (getRep() != NOCHARCODE) {
            found = true;
        } else {
            int ligstart = getLigkernstart();
            if (ligstart != TfmCharInfoWord.NOINDEX && ligKernTable != null) {

                for (int k = ligstart; k != TfmCharInfoWord.NOINDEX; k = ligKernTable[k]
                        .nextIndex(k)) {
                    TfmLigKern lk = ligKernTable[k];

                    if (lk instanceof TfmLigature || lk instanceof TfmKerning) {
                        found = true;
                        break;
                    }
                }
            }
        }
        return found;
    }

    /**
     * Returns the bc.
     * @return Returns the bc.
     */
    public short getBc() {

        return bc;
    }

    /**
     * @see de.dante.util.XMLWriterConvertible#writeXML(de.dante.util.xml.XMLStreamWriter)
     */
    public void writeXML(final XMLStreamWriter writer) throws IOException {

        writer.writeStartElement("char");
        writer.writeAttribute("id", String.valueOf(charid));
        writer.writeAttribute("glyph-number", String.valueOf(charid + bc));
        String c = Character.toString((char) (charid + bc));
        if (c != null && c.trim().length() > 0) {
            writer.writeAttribute("char", c);
        }
        if (glyphname != null) {
            writer.writeAttribute("glyph-name", glyphname.replaceAll("/", ""));
        }
        writer.writeAttribute("heightindex", String.valueOf(heightindex));
        writer.writeAttribute("depthindex", String.valueOf(depthindex));
        writer.writeAttribute("widthindex", String.valueOf(widthindex));
        writer.writeAttribute("italicindex", String.valueOf(italicindex));
        writer.writeAttribute("tagnr", String.valueOf(tag));
        String s;
        switch (tag) {
            case TAG0 :
                s = "NO_TAG";
                break;
            case TAG1 :
                s = "LIG_TAG";
                break;
            case TAG2 :
                s = "LIST_TAG";
                break;
            default :
                s = "EXT_TAG";
        }
        writer.writeAttribute("tag", s);
        writer.writeAttribute("remainder", String.valueOf(remainder));
        writer
                .writeAttribute("width_fw", String.valueOf(getWidth()
                        .getValue()));
        writer.writeAttribute("width", getWidth().toStringComma());
        writer.writeAttribute("height_fw", String.valueOf(getHeight()
                .getValue()));
        writer.writeAttribute("height", getHeight().toStringComma());
        writer
                .writeAttribute("depth_fw", String.valueOf(getDepth()
                        .getValue()));
        writer.writeAttribute("depth", getDepth().toStringComma());
        writer.writeAttribute("italic_fw", String.valueOf(getItalic()
                .getValue()));
        writer.writeAttribute("italic", getItalic().toStringComma());
        if (getTop() != NOCHARCODE) {
            writer.writeAttribute("top", String.valueOf(getTop()));
        }
        if (getMid() != NOCHARCODE) {
            writer.writeAttribute("mid", String.valueOf(getMid()));
        }
        if (getBot() != NOCHARCODE) {
            writer.writeAttribute("bot", String.valueOf(getBot()));
        }
        if (getRep() != NOCHARCODE) {
            writer.writeAttribute("rep", String.valueOf(getRep()));
        }
        if (getLigkernstart() != NOINDEX) {
            writer.writeAttribute("ligkernstart", String
                    .valueOf(getLigkernstart()));
        }
        if (getNextchar() != NOINDEX) {
            writer.writeAttribute("nextchar", String.valueOf(getNextchar()));
        }

        // ligature
        int ligstart = getLigkernstart();
        if (ligstart != NOINDEX && ligKernTable != null) {

            for (int k = ligstart; k != NOINDEX; k = ligKernTable[k]
                    .nextIndex(k)) {
                TfmLigKern lk = ligKernTable[k];

                if (lk instanceof TfmLigature) {
                    TfmLigature lig = (TfmLigature) lk;

                    writer.writeStartElement("ligature");
                    writer.writeAttribute("letter-id", String.valueOf(lig
                            .getNextChar()));
                    String sl = Character.toString((char) lig.getNextChar());
                    if (sl != null && sl.trim().length() > 0) {
                        writer.writeAttribute("letter", sl.trim());
                    }

                    writer.writeAttribute("lig-id", String.valueOf(lig
                            .getAddingChar()));
                    String slig = Character
                            .toString((char) lig.getAddingChar());
                    if (slig != null && slig.trim().length() > 0) {
                        writer.writeAttribute("lig", slig.trim());
                    }
                    writer.writeEndElement();
                } else if (lk instanceof TfmKerning) {
                    TfmKerning kern = (TfmKerning) lk;

                    writer.writeStartElement("kerning");
                    writer.writeAttribute("id", String.valueOf(kern
                            .getNextChar()));
                    String sk = Character.toString((char) kern.getNextChar());
                    if (sk != null && sk.trim().length() > 0) {
                        writer.writeAttribute("char", sk.trim());
                    }
                    writer.writeAttribute("size_fw", String.valueOf(kern
                            .getKern().getValue()));
                    writer.writeAttribute("size", kern.getKern()
                            .toStringComma());
                    writer.writeEndElement();
                }
            }
        }
        writer.writeEndElement();
    }
}
