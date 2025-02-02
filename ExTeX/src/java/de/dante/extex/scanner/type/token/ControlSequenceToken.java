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

package de.dante.extex.scanner.type.token;

import de.dante.extex.interpreter.Namespace;
import de.dante.extex.scanner.type.Catcode;
import de.dante.util.UnicodeChar;

/**
 * This class represents a control sequence token.
 * <p>
 * This class has a protected constructor only. Use the factory
 * {@link de.dante.extex.scanner.type.token.TokenFactory TokenFactory}
 * to get an instance of this class.
 * </p>
 *
 * <p>
 *  Note that in contrast to <logo>TeX</logo> the escape character leading to
 *  this control sequence token is stored in the character code of the abstract
 *  base class.
 * </p>
 *
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.9 $
 */
public class ControlSequenceToken extends AbstractToken implements CodeToken {

    /**
     * The constant <tt>HASH_FACTOR</tt> contains the factor used to construct
     * the hash code.
     */
    private static final int HASH_FACTOR = 17;

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 2006L;

    /**
     * The field <tt>value</tt> contains the string value.
     */
    private String name;

    /**
     * The field <tt>namespace</tt> contains the name space for this token.
     */
    private String namespace;

    /**
     * Creates a new object from the first character of a String.
     * If the string is empty then a space character is used instead.
     *
     * @param esc the escape character
     * @param name the name of the control sequence &ndash; without the leading
     *  escape character token
     * @param namespace the name space
     */
    protected ControlSequenceToken(final UnicodeChar esc, final String name,
            final String namespace) {

        super(esc);
        this.namespace = namespace;
        this.name = name;
    }

    /**
     * Create a new instance of the token where the name space is the default
     * name space and the other attributes are the same as for the current token.
     *
     * @return the new token
     *
     * @see de.dante.extex.scanner.type.token.CodeToken#cloneInDefaultNamespace()
     */
    public CodeToken cloneInDefaultNamespace() {

        if (Namespace.DEFAULT_NAMESPACE.equals(namespace)) {
            return this;
        }
        return new ControlSequenceToken(getChar(), name,
                Namespace.DEFAULT_NAMESPACE);
    }

    /**
     * Create a new instance of the token where the name space is the given one
     * and the other attributes are the same as for the current token.
     *
     * @param theNamespace the name space to use
     *
     * @return the new token
     *
     * @see de.dante.extex.scanner.type.token.CodeToken#cloneInNamespace(
     *      java.lang.String)
     */
    public CodeToken cloneInNamespace(final String theNamespace) {

        if (theNamespace == null || namespace.equals(theNamespace)) {
            return this;
        }
        return new ControlSequenceToken(getChar(), name, theNamespace);
    }

    /**
     * @see de.dante.extex.scanner.type.token.AbstractToken#equals(
     *      de.dante.extex.scanner.type.Catcode, char)
     */
    public boolean equals(final Catcode cc, final char c) {

        return getCatcode() == cc && name.length() == 1 && name.charAt(0) == c;
    }

    /**
     * @see de.dante.extex.scanner.type.token.Token#equals(
     *      de.dante.extex.scanner.type.Catcode, java.lang.String)
     */
    public boolean equals(final Catcode cc, final String s) {

        return getCatcode() == cc && name.equals(s);
    }

    /**
     * @see de.dante.extex.scanner.type.token.AbstractToken#equals(char)
     */
    public boolean equals(final char c) {

        return name.length() == 1 && name.charAt(0) == c;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object other) {

        if (other instanceof ControlSequenceToken) {
            ControlSequenceToken othertoken = (ControlSequenceToken) other;
            return (name.equals(othertoken.getName()) //
            && namespace.equals(othertoken.namespace));
        }
        return false;
    }

    /**
     * @see de.dante.extex.scanner.type.token.Token#getCatcode()
     */
    public Catcode getCatcode() {

        return Catcode.ESCAPE;
    }

    /**
     * @see de.dante.extex.scanner.type.token.CodeToken#getName()
     */
    public String getName() {

        return name;
    }

    /**
     * @see de.dante.extex.scanner.type.token.CodeToken#getNamespace()
     */
    public String getNamespace() {

        return namespace;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        return name.hashCode() + HASH_FACTOR * namespace.hashCode();
    }

    /**
     * Get the string representation of this object for debugging purposes.
     *
     * @return the string representation
     */
    public String toString() {

        UnicodeChar c = getChar();
        return getLocalizer().format("ControlSequenceToken.Text",
                (c != null ? c.toString() : ""), name, namespace);
    }

    /**
     * Print the token into a StringBuffer.
     *
     * @param sb the target string buffer
     *
     * @see de.dante.extex.scanner.type.token.Token#toString(java.lang.StringBuffer)
     */
    public void toString(final StringBuffer sb) {

        sb.append(getLocalizer().format("ControlSequenceToken.Text",
                getChar().toString(), name, namespace));
    }

    /**
     * @see de.dante.extex.scanner.type.token.AbstractToken#toText()
     */
    public String toText() {

        UnicodeChar c = getChar();
        if (c != null && c.getCodePoint() != 0) {
            return c.toString() + name;
        }

        return name;
    }

    /**
     * @see de.dante.extex.scanner.type.token.Token#toText(
     *      de.dante.util.UnicodeChar)
     */
    public String toText(final UnicodeChar esc) {

        if (esc != null) {
            return esc.toString() + name;
        }

        return name;
    }

    /**
     * @see de.dante.extex.scanner.type.token.Token#visit(
     *      de.dante.extex.scanner.type.token.TokenVisitor,
     *      java.lang.Object)
     */
    public Object visit(final TokenVisitor visitor, final Object arg1)
            throws Exception {

        return visitor.visitEscape(this, arg1);
    }

}
