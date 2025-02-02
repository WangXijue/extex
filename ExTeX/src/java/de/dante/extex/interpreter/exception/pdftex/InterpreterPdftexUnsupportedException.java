/*
 * Copyright (C) 2005-2006 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.exception.pdftex;

import de.dante.extex.interpreter.exception.InterpreterException;


/**
 * This exception is raised when a <logo>pdfTeX</logo> primitive is used
 * without being in PDF mode.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.2 $
 */
public class InterpreterPdftexUnsupportedException extends InterpreterException {

    /**
     * The field <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 2005L;

    /**
     * Creates a new object.
     *
     */
    public InterpreterPdftexUnsupportedException() {

        super();
    }

    /**
     * Creates a new object.
     *
     * @param primitive the name of the primitive in action
     */
    public InterpreterPdftexUnsupportedException(final String primitive) {

        super(primitive);
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {

        return getLocalizer().format("Text", super.getMessage());
    }

}
