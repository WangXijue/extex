/*
 * Copyright (C) 2004 The ExTeX Group and individual authors listed below
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

package de.dante.extex.font.type.efm;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import de.dante.extex.font.FontFile;
import de.dante.extex.font.Glyph;
import de.dante.extex.font.GlyphImpl;
import de.dante.extex.font.Kerning;
import de.dante.extex.font.Ligature;
import de.dante.extex.font.type.BoundingBox;
import de.dante.extex.font.type.ModifiableFount;
import de.dante.extex.i18n.HelpingException;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.glue.Glue;
import de.dante.util.GeneralException;
import de.dante.util.UnicodeChar;
import de.dante.util.configuration.ConfigurationException;
import de.dante.util.resource.ResourceFinder;

/**
 * Abstract class for a efm-font.
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.7 $
 */
public abstract class EFMFount implements ModifiableFount, Serializable {

    /**
     * The fontname
     */
    private String name;

    /**
     * the external fontfile
     */
    private FontFile externalfile;

    /**
     * The glue for letterspace
     */
    private Glue letterspaced;

    /**
     * ligature on/off
     */
    private boolean ligatures;

    /**
     * kerning on/off
     */
    private boolean kerning;

    /**
     * the em-size for the font
     */
    private Dimen emsize;

    /**
     * Creates a new object.
     * @param   doc         the efm-document
     * @param   fontname    the fontname
     * @param   size        the emsize of the font
     * @param   ls          the letterspaced
     * @param   lig         ligature on/off
     * @param   kern        kerning on/off
     * @param   filefinder  the fileFinder-object
     * @throws GeneralException ...
     * @throws ConfigurationException ...
     */
    public EFMFount(final Document doc, final String fontname,
            final Dimen size, final Glue ls, final Boolean lig,
            final Boolean kern, final ResourceFinder filefinder)
            throws GeneralException, ConfigurationException {

        super();
        if (fontname != null) {
            name = fontname;
        }
        emsize = new Dimen(size);
        em = new Dimen(size);
        letterspaced = ls;
        ligatures = lig.booleanValue();
        kerning = kern.booleanValue();

        loadFont(doc, filefinder);
    }

    /**
     * Attribut empr
     */
    private static final String ATTREMPR = "empr";

    /**
     * Attribut default-size
     */
    private static final String ATTRDEFAULTSIZE = "default-size";

    /**
     * load the Font
     * @param   doc         the efm-document
     * @param   fileFinder  the fileFinder
     * @throws GeneralException if a error is thrown.
     * @throws ConfigurationException ...
     */
    private void loadFont(final Document doc, final ResourceFinder fileFinder)
            throws GeneralException, ConfigurationException {

        try {

            // get fontgroup
            Element fontgroup = doc.getRootElement();

            if (fontgroup == null) {
                throw new HelpingException("EFM.nofontgroup");
            }

            // empr
            Attribute attr = fontgroup.getAttribute(ATTREMPR);

            if (attr != null) {
                try {
                    empr = attr.getFloatValue();
                } catch (Exception e) {
                    empr = DEFAULTEMPR;
                }
            }
            getSize(fontgroup);

            // calculate em
            em = new Dimen((long) (emsize.getValue() * empr / PROZ100));

            // get unitsperem
            attr = fontgroup.getAttribute("units-per-em");
            if (attr != null) {
                unitsperem = attr.getIntValue();
            }

            // get ex
            attr = fontgroup.getAttribute("xheight");
            if (attr != null) {
                ex = attr.getIntValue();
            }

            getFontDimenValues(fontgroup);

            // get glyph-list
            Element font = scanForElement(fontgroup, "font");
            if (font != null) {

                attr = font.getAttribute("checksum");
                if (attr != null) {
                    chechSum = attr.getIntValue();
                }
                // boundingbox
                // TODO boundingbox incomplete
                boundingBox = null;

                // exernal fontfile
                String efile = font.getAttributeValue("filename");

                if (efile != null) {
                    if (efile.endsWith(".ttf")) {
                        efile = efile.replaceAll(".ttf", "");
                        //  externalfile = getFontFile(fileFinder.findFile(efile,"ttf"));
                    } else if (efile.endsWith(".pfb")) {
                        efile = efile.replaceAll(".pfb", "");
                        //  externalfile = getFontFile(fileFinder.findFile(efile,"pfb"));
                    } else {
                        throw new HelpingException("EFM.wrongfileextension",
                                efile);
                    }
                }

                List glyphlist = font.getChildren("glyph");
                Element e;

                for (int i = 0; i < glyphlist.size(); i++) {
                    e = (Element) glyphlist.get(i);
                    String key = e.getAttributeValue("ID");
                    if (key != null) {
                        Glyph gv = new GlyphImpl();
                        gv.setNumber(e.getAttributeValue("glyph-number"));
                        gv.setName(e.getAttributeValue("glyph-name"));
                        gv.setExternalFile(externalfile);
                        gv.setWidth(e.getAttributeValue("width"), em,
                                unitsperem);
                        gv.setDepth(e.getAttributeValue("depth"), em,
                                unitsperem);
                        gv.setHeight(e.getAttributeValue("height"), em,
                                unitsperem);
                        gv.setItalicCorrection(e.getAttributeValue("italic"),
                                em, unitsperem);

                        // kerning
                        if (kerning) {
                            List kerninglist = e.getChildren("kerning");
                            for (int k = 0; k < kerninglist.size(); k++) {
                                Element ekerning = (Element) kerninglist.get(k);
                                Kerning kv = new Kerning();
                                kv
                                        .setId(ekerning
                                                .getAttributeValue("glyph-id"));
                                kv.setName(ekerning
                                        .getAttributeValue("glyph-name"));
                                kv.setSize(ekerning.getAttributeValue("size"),
                                        em, unitsperem);
                                gv.addKerning(kv);
                            }
                        }

                        // ligature
                        if (ligatures) {
                            List ligaturelist = e.getChildren("ligature");
                            for (int k = 0; k < ligaturelist.size(); k++) {
                                Element ligature = (Element) ligaturelist
                                        .get(k);
                                Ligature lv = new Ligature();
                                lv.setLetter(ligature
                                        .getAttributeValue("letter"));
                                lv.setLetterid(ligature
                                        .getAttributeValue("letter-id"));
                                lv.setLig(ligature.getAttributeValue("lig"));
                                lv.setLigid(ligature
                                        .getAttributeValue("lig-id"));
                                gv.addLigature(lv);
                            }
                        }
                        glyphmap.put(key, gv);
                        glyphname.put(gv.getName(), key);
                    }
                }
            }

        } catch (JDOMException e) {
            throw new HelpingException("EFM.jdomerror", e.getMessage());
        }
    }

    /**
     * Gets the font dimen values
     * @param fontgroup the fountgroup element
     */
    private void getFontDimenValues(final Element fontgroup) {

        Attribute attr;
        // fontdimen-key-values
        Element efontdimen = fontgroup.getChild("fontdimen");
        if (efontdimen != null) {
            List list = efontdimen.getAttributes();
            for (int i = 0; i < list.size(); i++) {
                attr = (Attribute) list.get(i);
                String key = attr.getName();
                String val = attr.getValue();
                if (val != null && val.trim().length() > 0) {
                    fontdimen.put(key, val);
                }
            }
        }
    }

    /**
     * Calculate the size for a font
     * @param fontgroup the fontgroup element
     */
    private void getSize(final Element fontgroup) {

        // use design-size if emsize equlas sero
        if (emsize.getValue() == 0) {

            // default-size
            Attribute defaultsize = fontgroup.getAttribute(ATTRDEFAULTSIZE);

            if (defaultsize != null) {
                try {
                    float f = defaultsize.getFloatValue();
                    if (f > 0) {
                        emsize = new Dimen(DEFAULTSIZE_IN_PT * Dimen.ONE);
                    }
                } catch (Exception e) {
                    emsize = new Dimen(DEFAULTSIZE_IN_PT * Dimen.ONE);
                }
            } else {
                emsize = new Dimen(DEFAULTSIZE_IN_PT * Dimen.ONE);
            }
        }
    }

    /**
     * Return the FontFile
     * @param file  the file
     * @return  the FontFile
     */
    protected abstract FontFile getFontFile(final File file);

    /**
     * initsize for the hashmap
     */
    private static final int HASHMAPINITSIZE = 256;

    /**
     * The hash for the glyphs (ID)
     */
    private HashMap glyphmap = new HashMap(HASHMAPINITSIZE);

    /**
     * The hash for the glyphs (name -> ID)
     */
    private HashMap glyphname = new HashMap(HASHMAPINITSIZE);

    /**
     * Return the with of a space.
     *
     * @see de.dante.extex.font.type.Fount#getSpace()
     */
    public Glue getSpace() {

        // use em-size for 'space'
        Glue rt = new Glue(em);

        // glyph 'space' exists?
        String key = (String) glyphname.get("space");
        if (key != null) {
            try {
                Glyph rglyph = getGlyph(new UnicodeChar(Integer.parseInt(key)));
                rt = new Glue(rglyph.getWidth());
            } catch (Exception e) {
                // do nothing, use default
                rt = new Glue(em);
            }
        }
        // TODO use key 'SPACE' from getFontDimen()
        return rt;
    }

    /**
     * @see de.dante.extex.font.type.Fount#getEm()
     */
    public Dimen getEm() {

        return em;
    }

    /**
     * ex: the height of 'x'
     */
    private int ex = 0;

    /**
     * the em-procent-size
     */
    private float empr = DEFAULTEMPR;

    /**
     * Default for em-prozent-size
     */
    private static final float DEFAULTEMPR = 100.0f;

    /**
     * Default for default-size (10pt)
     */
    private static final int DEFAULTSIZE_IN_PT = 10;

    /**
     * 100 %
     */
    private static final int PROZ100 = 100;

    /**
     * em: the width
     */
    private Dimen em;

    /**
     * Default unitsperem
     */
    private static final int DEFAULTUNITSPEREM = 1000;

    /**
     * units per em
     */
    private int unitsperem = DEFAULTUNITSPEREM;

    /**
     * @see de.dante.extex.font.type.Fount#getEx()
     */
    public Dimen getEx() {

        return new Dimen(ex * em.getValue() / unitsperem);
    }

    /**
     * hash for fontdimen-keys
     */
    private HashMap fontdimen = new HashMap();

    /**
     * Return the <code>Dimen</code>-value for a key-entry.
     * If no key exists, ZERO-<code>Dimen</code> is returned.
     *
     * @see de.dante.extex.font.type.Fount#getFontDimen(String)
     */
    public Dimen getFontDimen(final String key) {

        String val = (String) fontdimen.get(key);
        Dimen rt = new Dimen(0);
        try {
            float f = Float.parseFloat(val);
            rt = new Dimen((long) (f * em.getValue() / unitsperem));
        } catch (Exception e) {
            // do nothing, use default
            rt = new Dimen(0);
        }
        return rt;
    }

    /**
     * Set the <code>Dimen</code>-value for a key-entry.
     *
     * @see de.dante.extex.font.type.ModifiableFount#setFontDimen(String, Dimen)
     */
    public void setFontDimen(final String key, final Dimen value) {

        double d = value.getValue() / (double) em.getValue() * unitsperem;
        fontdimen.put(key, String.valueOf(d));
    }

    /**
     * @see de.dante.extex.font.type.Fount#getFontName()
     */
    public String getFontName() {

        return name;
    }

    /**
     * Return String for this class.
     * @return the String for this class
     */
    public abstract String toString();

    /**
     * @see de.dante.extex.font.type.Fount#getGlyph(de.dante.util.UnicodeChar)
     */
    public Glyph getGlyph(final UnicodeChar c) {

        Glyph g = (Glyph) glyphmap.get(String.valueOf(c.getCodePoint()));
        if (g == null) {
            g = new GlyphImpl();
            g.setWidth(getEm());
            g.setHeight(getEx());
            // TODO incomplete: use glyph-symbol
        }
        return g;
    }

    /**
     * Return the Element in the tree or <code>null</code>, if not found.
     * @param e     the Element
     * @param name  the elementname
     * @return the element or <code>null</code>, if not found
     */
    public static Element scanForElement(final Element e, final String name) {

        if (e.getName().equals(name)) {
            return e;
        }
        Element element = e.getChild(name);
        if (element != null) {
            return element;
        }
        List liste = e.getChildren();
        for (int i = 0; i < liste.size(); i++) {
            element = scanForElement((Element) liste.get(i), name);
            if (element != null) {
                return element;
            }
        }

        return null;
    }

    /**
     * @return Returns the emsize.
     */
    protected Dimen getEmsize() {

        return emsize;
    }

    /**
     * @return Returns the unitsperem.
     */
    protected int getUnitsperem() {

        return unitsperem;
    }

    /**
     * @return Returns the empr.
     */
    protected float getEmpr() {

        return empr;
    }

    /**
     * Return the size of the glyphmap
     * @return  the size
     */
    protected int getGylphMapSize() {

        return glyphmap.size();
    }

    /**
     * @see de.dante.extex.font.type.Fount#getLetterSpacing()
     */
    public Glue getLetterSpacing() {

        return letterspaced;
    }

    /**
     * property-map
     */
    private Map property = new HashMap();

    /**
     * @see de.dante.extex.font.type.Fount#getProperty(java.lang.String)
     */
    public String getProperty(final String key) {

        return (String) property.get(key);
    }

    /**
     * @see de.dante.extex.font.type.ModifiableFount#setProperty(
     *      java.lang.String,
     *      java.lang.String)
     */
    public void setProperty(final String key, final String value) {

        property.put(key, value);
    }

    /**
     * the checksum
     */
    private int chechSum = -1;

    /**
     * @see de.dante.extex.font.type.Fount#getCheckSum()
     */
    public int getCheckSum() {

        return chechSum;
    }

    /**
     * the BoundingBox
     */
    private BoundingBox boundingBox = null;

    /**
     * @see de.dante.extex.font.type.Fount#getBoundingBox()
     */
    public BoundingBox getBoundingBox() {

        return boundingBox;
    }

}