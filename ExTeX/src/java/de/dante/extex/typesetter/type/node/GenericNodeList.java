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

package de.dante.extex.typesetter.type.node;

import java.util.ArrayList;
import java.util.List;

import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.ImpossibleException;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.dimen.FixedDimen;
import de.dante.extex.interpreter.type.glue.FixedGlue;
import de.dante.extex.typesetter.Typesetter;
import de.dante.extex.typesetter.type.Node;
import de.dante.extex.typesetter.type.NodeIterator;
import de.dante.extex.typesetter.type.NodeList;
import de.dante.extex.typesetter.type.NodeVisitor;
import de.dante.util.exception.GeneralException;

/**
 * Abstract base class for all <code>NodeList</code>s.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.5 $
 */
public class GenericNodeList extends AbstractNode implements NodeList {

    /**
     * The field <tt>serialVersionUID</tt> contains the version number for
     * serialization.
     */
    private static final long serialVersionUID = 20060417L;

    /**
     * The field <tt>list</tt> is the container for the elements of this node
     * list.
     */
    private ArrayList list = new ArrayList();

    /**
     * The field <tt>move</tt> contains the offset of the reference point in
     * vertical direction.
     */
    private Dimen move = new Dimen(0);

    /**
     * The field <tt>shift</tt> contains the offset of the reference point in
     * horizontal direction.
     */
    private Dimen shift = new Dimen(0);

    /**
     * The field <tt>targetDepth</tt> contains the requested depth of the node
     * list.
     */
    private Dimen targetDepth = null;

    /**
     * The field <tt>targetHeight</tt> contains the requested height of the node
     * list.
     */
    private Dimen targetHeight = null;

    /**
     * The field <tt>targetWidth</tt> contains the requested width of the node
     * list.
     */
    private Dimen targetWidth = null;

    /**
     * Creates a new object.
     */
    public GenericNodeList() {

        super();
    }

    /**
     * Creates a new object.
     * The list is filled with the node given.
     *
     * @param node the node to add initially
     */
    public GenericNodeList(final Node node) {

        super();
        add(node);
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#add(int,
     *      de.dante.extex.typesetter.type.Node)
     */
    public void add(final int index, final Node node) {

        list.add(index, node);
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#add(
     *      de.dante.extex.typesetter.type.Node)
     */
    public void add(final Node node) {

        if (node != null) {
            list.add(node);
        }
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#addSkip(
     *      de.dante.extex.interpreter.type.glue.FixedGlue)
     */
    public void addSkip(final FixedGlue glue) {

        throw new UnsupportedOperationException(getClass().getName()
                + "#addSkip()");
    }

    /**
     * @see de.dante.extex.typesetter.type.Node#atShipping(
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.typesetter.Typesetter,
     *      de.dante.extex.typesetter.type.NodeVisitor,
     *      boolean)
     */
    public Node atShipping(final Context context, final Typesetter typesetter,
            final NodeVisitor visitor, final boolean inHMode)
            throws GeneralException {

        Node node, n;
        int size = list.size();

        for (int i = 0; i < size; i++) {
            node = (Node) list.get(i);
            n = node.atShipping(context, typesetter, visitor, inHMode);

            if (n == null) {
                list.remove(i--);
                size--;
            } else if (n != this) {
                list.remove(i);
                list.add(i, n);
            }
        }

        return (Node) this.visit(visitor, inHMode
                ? Boolean.TRUE
                : Boolean.FALSE);
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#clear()
     */
    public void clear() {

        list.clear();
        setWidth(Dimen.ZERO_PT);
        setHeight(Dimen.ZERO_PT);
        setDepth(Dimen.ZERO_PT);
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#copy()
     */
    public NodeList copy() {

        try {
            GenericNodeList clone = (GenericNodeList) this.clone();
            clone.list = (ArrayList) list.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new ImpossibleException(e);
        }
    }

    /**
     * @see de.dante.extex.typesetter.type.Node#countChars()
     */
    public int countChars() {

        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            count += ((Node) list.get(i)).countChars();
        }
        return count;
    }

    /**
     * Test whether the node list is empty.
     *
     * @return <code>true</code>, if the <code>NodeList</code> is empty,
     * otherwise <code>false</code>.
     */
    public boolean empty() {

        return (list.size() == 0);
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#get(int)
     */
    public Node get(final int index) {

        return (Node) list.get(index);
    }

    /**
     * @see de.dante.extex.typesetter.type.Node#getChars()
     */
    public CharNode[] getChars() {

        int len = countChars();
        if (len == 0) {
            return NO_CHARS;
        }
        CharNode[] chars = new CharNode[len];
        int idx = 0;
        for (int i = 0; i < list.size(); i++) {
            CharNode[] uca = ((Node) list.get(i)).getChars();
            for (int j = 0; j < uca.length; j++) {
                chars[idx++] = uca[j];
            }
        }

        return chars;
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#getMove()
     */
    public Dimen getMove() {

        return move;
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#getShift()
     */
    public Dimen getShift() {

        return shift;
    }

    /**
     * Getter for targetDepth.
     *
     * @return the targetDepth.
     */
    public Dimen getTargetDepth() {

        return targetDepth;
    }

    /**
     * Getter for targetHeight.
     *
     * @return the targetHeight.
     */

    public Dimen getTargetHeight() {

        return targetHeight;
    }

    /**
     * Getter for targetWidth.
     *
     * @return the targetWidth.
     */
    public Dimen getTargetWidth() {

        return targetWidth;
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#iterator()
     */
    public NodeIterator iterator() {

        return new NodeIterator(list);
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#remove(int)
     */
    public Node remove(final int index) {

        Node node = (Node) list.remove(index);
        return node;
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#setMove(
     *      de.dante.extex.interpreter.type.dimen.FixedDimen)
     */
    public void setMove(final FixedDimen d) {

        move.set(d);
    }

    /**
     * @see de.dante.extex.typesetter.type.NodeList#setShift(
     *      de.dante.extex.interpreter.type.dimen.FixedDimen)
     */
    public void setShift(final FixedDimen d) {

        shift.set(d);
    }

    /**
     * Setter for the target depth.
     *
     * @param depth the target depth to set.
     */
    public void setTargetDepth(final FixedDimen depth) {

        if (this.targetDepth == null) {
            this.targetDepth = new Dimen(depth);
        } else {
            this.targetDepth.set(depth);
        }
    }

    /**
     * Setter for the target height.
     *
     * @param height the target height to set.
     */
    public void setTargetHeight(final FixedDimen height) {

        if (this.targetHeight == null) {
            this.targetHeight = new Dimen(height);
        } else {
            this.targetHeight.set(height);
        }
    }

    /**
     * Setter for the target width.
     *
     * @param width the target width to set.
     */
    public void setTargetWidth(final FixedDimen width) {

        if (this.targetWidth == null) {
            this.targetWidth = new Dimen(width);
        } else {
            this.targetWidth.set(width);
        }
    }

    /**
     * Return the size of the <code>NodeList</code>.
     *
     * @return the size of the <code>NodeList</code>
     */
    public int size() {

        return list.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer sb = new StringBuffer();
        toString(sb, "\n", Integer.MAX_VALUE, Integer.MAX_VALUE);
        return sb.toString();
    }

    /**
     * @see de.dante.extex.typesetter.type.Node#toString(java.lang.StringBuffer,
     *      java.lang.String, int, int)
     */
    public void toString(final StringBuffer sb, final String prefix,
            final int breadth, final int depth) {

        sb.append("(");
        sb.append(getHeight().toString());
        FixedDimen d = getDepth();
        if (d.ge(Dimen.ZERO)) {
            sb.append("+");
        }
        sb.append(d.toString());
        sb.append(")x");
        sb.append(getWidth().toString());

        if (shift.getValue() != 0) {
            sb.append(", shifted ");
            sb.append(shift.toString());
        }

        if (move.getValue() != 0) {
            sb.append(", moved ");
            sb.append(move.toString());
        }

        String prefix2 = prefix + ".";

        for (int i = 0; i < list.size() && i < breadth; i++) {
            sb.append(prefix2);
            if (depth >= 0) {
                ((Node) list.get(i)).toString(sb, prefix2, breadth, depth - 1);
            }
        }
    }

    /**
     * Provides a string representation of the current instance.
     *
     * @return the String representation of the object
     * @see "<logo>TeX</logo> &ndash; The Program [182]"
     */
    public String toText() {

        StringBuffer sb = new StringBuffer();
        toText(sb, "");
        return sb.toString();
    }

    /**
     * @see de.dante.extex.typesetter.type.Node#toText(java.lang.StringBuffer,
     *      java.lang.String)
     */
    public void toText(final StringBuffer sb, final String prefix) {

        String p = prefix + "  ";

        for (int i = 0; i < list.size(); i++) {
            ((Node) list.get(i)).toText(sb, p);
        }

        sb.append(")");
    }

    /**
     * @see de.dante.extex.typesetter.type.Node#visit(
     *      de.dante.extex.typesetter.type.NodeVisitor,
     *      java.lang.Object)
     */
    public Object visit(final NodeVisitor visitor, final Object value)
            throws GeneralException {

        throw new ImpossibleException(getClass().getName() + "#visit()");
    }

}
