/*
 * Copyright (C) 2005 The ExTeX Group and individual authors listed below
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

package de.dante.extex.font.type.efm.commands;

import java.io.Serializable;

import org.jdom.Attribute;
import org.jdom.Element;

import de.dante.extex.font.type.efm.exception.FontAttributeException;
import de.dante.extex.font.type.vf.VFFont;
import de.dante.extex.interpreter.type.dimen.Dimen;

/**
 * EFM char command.
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.1 $
 */

public class EfmChar implements Serializable, EfmHVW {

    /**
     * the character
     */
    private int character;

    /**
     * the font
     */
    private String font;

    /**
     * the font size
     */
    private Dimen fontsize;

    /**
     * the width of the character
     */
    private Dimen width;

    /**
     * the horiziontal position
     */
    private Dimen h;

    /**
     * the vertical position
     */
    private Dimen v;

    /**
     * Create a new object.
     * @param element       the char element
     * @param designsize    the design size of the masterfont
     * @throws FontAttributeException if a attribute have a wrong value.
     */
    public EfmChar(final Element element, final Dimen designsize)
            throws FontAttributeException {

        try {
            Attribute attr = element.getAttribute("id");
            character = attr.getIntValue();

            attr = element.getAttribute("font");
            font = attr.getValue();

            attr = element.getAttribute("fontsize");
            float f = attr.getFloatValue();
            fontsize = new Dimen((long) (f * Dimen.ONE));

            attr = element.getAttribute("width");
            width = new Dimen((long) (designsize.getValue()
                    * attr.getDoubleValue() * VFFont.UNITS_PER_EM_DEFAULT));

            attr = element.getAttribute("h");
            h = new Dimen(
                    (long) (designsize.getValue() * attr.getDoubleValue() * VFFont.UNITS_PER_EM_DEFAULT));

            attr = element.getAttribute("v");
            v = new Dimen(
                    (long) (designsize.getValue() * attr.getDoubleValue() * VFFont.UNITS_PER_EM_DEFAULT));

        } catch (Exception e) {
            throw new FontAttributeException(e.getMessage());
        }
    }

    /**
     * Returns the character.
     * @return Returns the character.
     */
    public int getCharacter() {

        return character;
    }

    /**
     * Returns the font.
     * @return Returns the font.
     */
    public String getFont() {

        return font;
    }

    /**
     * Returns the h.
     * @return Returns the h.
     */
    public Dimen getH() {

        return h;
    }

    /**
     * Returns the v.
     * @return Returns the v.
     */
    public Dimen getV() {

        return v;
    }

    /**
     * Returns the width.
     * @return Returns the width.
     */
    public Dimen getWidth() {

        return width;
    }

    /**
     * Returns the fontsize.
     * @return Returns the fontsize.
     */
    public Dimen getFontsize() {

        return fontsize;
    }
}
