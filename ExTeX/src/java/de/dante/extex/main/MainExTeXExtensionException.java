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

package de.dante.extex.main;

import de.dante.extex.i18n.Messages;

/**
 * This exception is thrown when the main program use a ExTeX-extension witch is not avaiable.
 *
 * @author <a href="mailto:mgn@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.5 $
 */
public class MainExTeXExtensionException extends MainException {

    /**
     * number
     */
    private static final int NUMBER = -16;

    /**
     * Creates a new object.
     *
     */
    public MainExTeXExtensionException() {

        super(NUMBER, "ExTeXExtensionException");
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {

        return Messages.format("MainExTeXExtensionException.Message", super
                .getMessage());
    }
}
