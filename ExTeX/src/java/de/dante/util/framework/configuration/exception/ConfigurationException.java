/*
 * Copyright (C) 2003-2006 The ExTeX Group and individual authors listed below
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

import de.dante.util.framework.i18n.Localizer;
import de.dante.util.framework.i18n.LocalizerFactory;

/**
 * This exception is thrown when a problem in the configuration has been
 * detected.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.3 $
 */
public abstract class ConfigurationException extends Exception {

    /**
     * The field <tt>localizer</tt> contains the localizer.
     */
    private Localizer localizer = null;

    /**
     * The field <tt>source</tt> contains the location for this exception.
     */
    private String source = null;

    /**
     * Creates a new object.
     *
     * @param message the message string
     */
    public ConfigurationException(final String message) {

        super(message);
    }

    /**
     * Creates a new object.
     *
     * @param message the message string
     * @param theSource the name of the file causing this error
     */
    public ConfigurationException(final String message, final String theSource) {

        super(message);
        this.source = theSource;
    }

    /**
     * Creates a new object.
     *
     * @param message message the message string
     * @param theCause the next Throwable in the list
     */
    public ConfigurationException(final String message, final Throwable theCause) {

        super(message, theCause);
    }

    /**
     * Getter for localizer.
     *
     * @return the localizer.
     */
    protected Localizer getLocalizer() {

        if (this.localizer == null) {
            this.localizer = LocalizerFactory
                    .getLocalizer(ConfigurationException.class);
        }
        return this.localizer;
    }

    /**
     * Getter for the localized message of this Exception.
     * The text is taken from the resource bundle <tt>ConfigurationEception</tt>
     * The key depends on the further information present:
     *
     * <p>
     * <tt>ConfigurationException.FormatCauseMessageLocation</tt>
     * is used when a cause, a message, and a location are present.
     * The arguments {1}, {2}, and {3} are set respectively.
     * </p>
     * <p>
     * <tt>ConfigurationException.FormatCauseLocation</tt>
     * is used when a cause and a location are present.
     * The arguments {1} and {2} are set respectively.
     * </p>
     * <p>
     * <tt>ConfigurationException.FormatCauseMessage</tt>
     * is used when a cause and a message are present.
     * The arguments {1} and {2} are set respectively.
     * </p>
     * <p>
     * <tt>ConfigurationException.FormatCause</tt>
     * is used when a cause is present.
     * The argument {1} is set respectively.
     * </p>
     * <p>
     * <tt>ConfigurationException.FormatMessageLocation</tt>
     * is used when a message and a location are present.
     * The arguments {1} and {2} are set respectively.
     * </p>
     * <p>
     * <tt>ConfigurationException.FormatMessage</tt>
     * is used when a message is present.
     * The argument {1} is set respectively.
     * </p>
     * <p>
     * <tt>ConfigurationException.FormatLocation</tt>
     * is used when a location is present.
     * The argument {1} is set respectively.
     * </p>
     * <p>
     * The arguments {0} is always replaced by the text from
     * {@link #getText() getText()}.
     * </p>
     *
     * @return the message
     */
    public String getLocalizedMessage() {

        String message = getMessage();

        if (getCause() != null) {
            for (Throwable t = getCause(); t != null; t = t.getCause()) {
                String msg = t.getLocalizedMessage();

                if (msg != null) {
                    if (source != null) {
                        if (message != null) {
                            return getLocalizer()
                                    .format(
                                            "ConfigurationException.FormatCauseMessageLocation",
                                            getText(), msg, message, source);
                        } else {
                            return getLocalizer()
                                    .format(
                                            "ConfigurationException.FormatCauseLocation",
                                            getText(), msg, source);
                        }
                    } else if (message != null) {
                        return getLocalizer().format(
                                "ConfigurationException.FormatCauseMessage",
                                getText(), msg, message);
                    } else {
                        return getLocalizer().format(
                                "ConfigurationException.FormatCause",
                                getText(), msg);
                    }
                }
            }
        }

        if (source != null) {
            if (message != null) {
                return getLocalizer().format(
                        "ConfigurationException.FormatMessageLocation",
                        getText(), message, source);
            } else {
                return getLocalizer().format(
                        "ConfigurationException.FormatLocation", getText(),
                        source);
            }
        } else if (message != null) {
            return getLocalizer().format(
                    "ConfigurationException.FormatMessage", getText(), message);
        } else {
            return getLocalizer().format("ConfigurationException.Format",
                    getText());
        }
    }

    /**
     * Getter for the text prefix of this ConfigurationException.
     * The text is taken from the resource bundle <tt>ConfigurationEception</tt>
     * under the key <tt>ConfigurationException.Text</tt>.
     *
     * @return the text
     */
    protected String getText() {

        return getLocalizer().format("ConfigurationException.Text");
    }

}
