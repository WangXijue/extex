/*
 * Copyright (C) 2003-2004 The ExTeX Group and individual authors listed below
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

package de.dante.util.framework.configuration.exception;


/**
 * This exception is thrown when a dynamically loaded class signals an
 * instantiation exception.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.2 $
 */
public class ConfigurationInstantiationException extends ConfigurationException {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new object.
     *
     * @param message the message text
     * @param cause the next Throwable in the list
     */
    public ConfigurationInstantiationException(final String message,
            final Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a new object.
     *
     * @param cause the next Throwable in the list
     */
    public ConfigurationInstantiationException(final Throwable cause) {

        super("", cause);
    }

    /**
     * Getter for the text prefix of this ConfigException.
     * The text is taken from the resource bundle <tt>ConfigurationEception</tt>
     * under the key <tt>ConfigurationInstantiationException.Text</tt>. The
     * argument {0} is replaced by the message of the embedded cause as passed
     * to the constructor.
     *
     * @return the text
     */
    protected String getText() {

        return getLocalizer().format(
                "ConfigurationInstantiationException.Text",
                getCause().getLocalizedMessage());
    }

}