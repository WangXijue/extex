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

package de.dante.extex.interpreter.exception.helping;

import de.dante.extex.scanner.type.token.Token;
import de.dante.util.framework.i18n.LocalizerFactory;

/**
 * This exception is raised when an unused prefix flag has been encountered.
 * <p>
 *  The localization format is taken from the Localizer under the key
 *  <tt>UnusedPrefix</tt>.
 * </p>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.3 $
 */
public class UnusedPrefixException extends HelpingException {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 2006L;

    /**
     * Creates a new object.
     *
     * @param flag the name of the flag which has been used
     * @param token the command on which this has happened
     */
    public UnusedPrefixException(final String flag, final Token token) {

        super(LocalizerFactory.getLocalizer(UnusedPrefixException.class),
                "UnusedPrefix", flag, token.toString());
    }

}
