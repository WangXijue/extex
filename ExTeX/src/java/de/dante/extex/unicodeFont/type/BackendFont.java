/*
 * Copyright (C) 2005 The ExTeX Group and individual authors listed below
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

package de.dante.extex.unicodeFont.type;

import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.util.UnicodeChar;

/**
 * Interface for the backend font.
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.1 $
 */

public interface BackendFont extends Font {

    /**
     * Returns the postscript name of the font.
     * @return Returns the postscript name of the font.
     */
    String getPostscriptName();

    /**
     * Returns the width of the char.
     *
     * @param uc    The Unicode char.
     * @return Returns the width of the char.
     */
    Dimen getWidth(UnicodeChar uc);
}
