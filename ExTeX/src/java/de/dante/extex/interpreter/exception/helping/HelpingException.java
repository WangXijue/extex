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

package de.dante.extex.interpreter.exception.helping;

import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.util.framework.i18n.Localizer;

/**
 * This class provides an Exception with the possibility to provide additional
 * help on the error encountered. Thus it has two levels of information: the
 * first level is the message and the second level is the additional help.
 * <p>
 * Both information strings are mapped via the
 * {@link de.dante.util.framework.i18n.Localizer Localizer} apparatus.
 * The key provided to this Exception is used as a key to find the format in
 * the resource bundle. For the localized message of the exception it is used
 * plain and for the help the string ".help" is appended.
 * </p>
 * <h3>Example</h3>
 * <p>
 * Consider the following lines in the resource (properties) file for the
 * localizer:
 * </p>
 * <pre>
 * abc.def = This is the message
 * abc.def.help = This is the help text. \
 *               It can even span several lines.
 * </pre>
 * Then the following instruction can be used save:
 * <pre>
 *     throw new HelpingException(localizer, "abc.def");
 * </pre>
 * <p>
 * With this exception up to three arguments can be used. The String value of
 * those arguments are inserted into the message string for the placeholders
 * {0}, {1}, and {2}. Consider the following format definition in the resource
 * of the localizer:
 * </p>
 * <pre>
 * ghi = This is the {0} message: {2}
 * </pre>
 * Then the instruction
 * <pre>
 *     new HelpingException(localizer, "ghi", "first", "second", "third");
 * </pre>
 * will produce an exception with the following localized message:
 * <pre>
 * This is the first message: third
 * </pre>
 * </p>
 * <p>
 * Note that some special rules hold for strings in resource bundles:
 * <ul>
 * <li>The character <tt>\</tt> acts as escape character. In the combination
 *  <tt>\n</tt> it produces a newline.</li>
 * <li>If the character <tt>\</tt> is the last character of a line then the
 *  format is continued in the next line. The leading whitespace in the
 *  continuing line is silently removed.</li>
 * <li>The character <tt>'</tt> also has a special meaning. Thi usually means
 *  that you have to double a single quote in your format.</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.5 $
 */
public class HelpingException extends InterpreterException {

    /**
     * The constant <tt>DEFAULT_ARGUMENT</tt> contains the argument if none
     * is given.
     */
    private static final String DEFAULT_ARGUMENT = "?";

    /**
     * The constant <tt>DEFAULT_TAG</tt> contains the tag to be used if none
     * is given.
     */
    private static final String DEFAULT_TAG = "GeneralDetailedException.help";

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 1L;

    /**
     * The field <tt>arg1</tt> contains the first argument.
     */
    private String arg1;

    /**
     * The field <tt>arg2</tt> contains the second argument.
     */
    private String arg2;

    /**
     * The field <tt>arg3</tt> contains the third argument.
     */
    private String arg3;

    /**
     * The field <tt>localizer</tt> contains the localizer.
     */
    private Localizer localizer;

    /**
     * The field <tt>tag</tt> contains the name of the message to show.
     */
    private String tag;

    /**
     * Creates a new object.
     */
    protected HelpingException() {

        super();
        this.tag = DEFAULT_TAG;
        this.localizer = null;
        this.arg1 = DEFAULT_ARGUMENT;
        this.arg2 = DEFAULT_ARGUMENT;
        this.arg3 = DEFAULT_ARGUMENT;
    }

    /**
     * Creates a new object without variable arguments.
     *
     * @param messageTag the message
     * @param theLocalizer the localizer to use
     */
    public HelpingException(final Localizer theLocalizer,
            final String messageTag) {

        super();
        this.tag = messageTag;
        this.localizer = theLocalizer;
        this.arg1 = DEFAULT_ARGUMENT;
        this.arg2 = DEFAULT_ARGUMENT;
        this.arg3 = DEFAULT_ARGUMENT;
    }

    /**
     * Creates a new object with one variable argument.
     *
     * @param messageTag the message
     * @param a1 the first argument
     * @param theLocalizer the localizer to use
     */
    public HelpingException(final Localizer theLocalizer,
            final String messageTag, final String a1) {

        super();
        this.tag = messageTag;
        this.localizer = theLocalizer;
        this.arg1 = a1;
        this.arg2 = DEFAULT_ARGUMENT;
        this.arg3 = DEFAULT_ARGUMENT;
    }

    /**
     * Creates a new object with two variable arguments.
     *
     * @param messageTag the message
     * @param a1 the first argument
     * @param a2 the second argument
     * @param theLocalizer the localizer to use
     */
    public HelpingException(final Localizer theLocalizer,
            final String messageTag, final String a1, final String a2) {

        super();
        this.tag = messageTag;
        this.localizer = theLocalizer;
        this.arg1 = a1;
        this.arg2 = a2;
        this.arg3 = DEFAULT_ARGUMENT;
    }

    /**
     * Creates a new object with three variable arguments.
     *
     * @param messageTag the message
     * @param a1 the first argument
     * @param a2 the second argument
     * @param a3 the third argument
     * @param theLocalizer the localizer to use
     */
    public HelpingException(final Localizer theLocalizer,
            final String messageTag, final String a1, final String a2,
            final String a3) {

        super();
        this.tag = messageTag;
        this.localizer = theLocalizer;
        this.arg1 = a1;
        this.arg2 = a2;
        this.arg3 = a3;
    }

    /**
     * Getter for further help information.
     *
     * @return the help information
     */
    public String getHelp() {

        return localizer.format(tag + ".help", arg1, arg2, arg3);
    }

    /**
     * Getter for further help information.
     *
     * @return the help information
     */
    public String getLocalizedMessage() {

        if (localizer == null) {
            return "???";
        }
        return localizer.format(tag, arg1, arg2, arg3);
    }

}
