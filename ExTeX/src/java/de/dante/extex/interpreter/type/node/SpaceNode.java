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

package de.dante.extex.interpreter.type.node;

import de.dante.extex.interpreter.type.glue.Glue;
import de.dante.extex.typesetter.Node;
import de.dante.extex.typesetter.NodeVisitor;
import de.dante.util.GeneralException;

/**
 * A space noad represents a simple space character.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.16 $
 */
public class SpaceNode extends GlueNode implements Node {

    /**
     * The field <tt>DEVELOP</tt> contains the indicator that part of the code
     * is used in the development version.
     */
    private static final boolean DEVELOP = true;

    /**
     * The field <tt>size</tt> contains the width of the space to insert.
     */
    private Glue size;

    /**
     * Creates a new object.
     *
     * @param theWidth the size of the space
     */
    public SpaceNode(final Glue theWidth) {

        super(theWidth);
        this.size = theWidth;
    }

    /**
     * Getter for size.
     *
     * @return the size.
     *
     * @deprecated the glue should not be exposed
     */
    public Glue getGlueWidth() {

        return this.size;
    }

    /**
     * This method returns the printable representation.
     * This is meant to produce a exaustive form as it is used in tracing
     * output to the log file.
     *
     * @return the printable representation
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return " ";
    }

    /**
     * @see de.dante.extex.typesetter.Node#toString(java.lang.StringBuffer,
     *      java.lang.String)
     */
    public void toString(final StringBuffer sb, final String prefix) {

        sb.append("\\space "); //TODO gene: I18N???
        sb.append(this.size.toString());
        if (DEVELOP && !getWidth().eq(size.getLength())) {
            sb.append(" [");
            sb.append(getWidth().toString());
            sb.append(']');
        }
    }

    /**
     * @see de.dante.extex.typesetter.Node#toText(java.lang.StringBuffer,
     *      java.lang.String)
     */
    public void toText(final StringBuffer sb, final String prefix) {

        sb.append(" ");
    }

    /**
     * @see de.dante.extex.typesetter.Node#visit(
     *      de.dante.extex.typesetter.NodeVisitor,
     *      java.lang.Object)
     */
    public Object visit(final NodeVisitor visitor, final Object value)
            throws GeneralException {

        return visitor.visitSpace(this, value);
    }

    /**
     * @see de.dante.extex.typesetter.Node#visit(de.dante.extex.typesetter.NodeVisitor,
     *      java.lang.Object, java.lang.Object)
     */
    public Object visit(final NodeVisitor visitor, final Object value,
            final Object value2) throws GeneralException {

        return visitor.visitSpace(value, value2);
    }

}