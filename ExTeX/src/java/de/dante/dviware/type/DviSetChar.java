/*
 * Copyright (C) 2006 The ExTeX Group and individual authors listed below
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

package de.dante.dviware.type;

import java.io.IOException;
import java.io.OutputStream;

import de.dante.dviware.Dvi;

/**
 * This class represents the DVI instruction <tt>set_char</tt>.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.2 $
 */
public class DviSetChar extends AbstractDviCode {

    /**
     * The field <tt>codePoint</tt> contains the code point of the character to
     * set.
     */
    private int codePoint;

    /**
     * Creates a new object.
     *
     * @param codePoint the code point of the character to set
     */
    public DviSetChar(final int codePoint) {

        super();
        this.codePoint = codePoint;
    }

    /**
     * @see de.dante.dviware.type.DviCode#getName()
     */
    public String getName() {

        if (codePoint <= Dvi.SET_CHAR127) {
            return "set_char" + codePoint;
        }
        return "set_char" + variant(codePoint);
    }

    /**
     * @see de.dante.dviware.type.DviCode#write(java.io.OutputStream)
     */
    public int write(final OutputStream stream) throws IOException {

        if (codePoint <= Dvi.SET_CHAR127) {
            stream.write(codePoint);
            return 1;
        }
        return opcode(Dvi.SET1, codePoint, stream);
    }

}
