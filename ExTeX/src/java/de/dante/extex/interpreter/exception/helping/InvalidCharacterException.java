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

import de.dante.util.UnicodeChar;
import de.dante.util.framework.i18n.Localizer;
import de.dante.util.framework.i18n.LocalizerFactory;

/**
 * This exception is raised when an unexpected character code is encountered.
 * <p>
 *  The localization format is taken from the Localizer under the key
 *  <tt>UnexpectedEofIn</tt>.
 * </p>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.7 $
 */
public class InvalidCharacterException extends HelpingException {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 2006L;

    /**
     * The field <tt>cc</tt> contains the invalid character.
     */
    private String cc;

    /**
     * Creates a new object.
     *
     * @param uc the invalid character
     */
    public InvalidCharacterException(final UnicodeChar uc) {

        super(LocalizerFactory.getLocalizer(InvalidCharacterException.class),
                "TTP.InvalidChar", uc.toString());
        cc = uc.toString();
    }

    /**
     * Creates a new object.
     *
     * @param uc the invalid character
     */
    public InvalidCharacterException(final String uc) {

        super(LocalizerFactory.getLocalizer(InvalidCharacterException.class),
                "TTP.InvalidChar", uc);
        cc = uc;
    }

    /**
     * Creates a new object.
     *
     * @param localizer the localizer
     * @param messageTag the message tag
     * @param arg the argument
     */
    public InvalidCharacterException(final Localizer localizer,
            final String messageTag, final String arg) {

        super(localizer, messageTag, arg);
    }

    /**
     * Getter for cc.
     *
     * @return the cc
     */
    public String getChar() {

        return this.cc;
    }

}
