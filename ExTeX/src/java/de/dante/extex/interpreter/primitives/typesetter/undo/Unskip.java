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

package de.dante.extex.interpreter.primitives.typesetter.undo;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.exception.helping.HelpingException;
import de.dante.extex.interpreter.type.AbstractCode;
import de.dante.extex.typesetter.Typesetter;
import de.dante.extex.typesetter.type.Node;
import de.dante.extex.typesetter.type.node.SkipNode;

/**
 * This class provides an implementation for the primitive
 * <code>&#x5c;unskip</code>.
 *
 * <doc name="unskip">
 * <h3>The Primitive <tt>&#x5c;unskip</tt></h3>
 * <p>
 *  The primitive <tt>&#x5c;unskip</tt> inspects the current list and
 *  removes the last node if it is a glue node. This includes leader nodes.
 *  If the current list is empty an error is raised.
 * </p>
 *
 * <h4>Syntax</h4>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;unskip&rang;
 *        &rarr; <tt>&#x5c;unskip</tt>  </pre>
 *
 * <h4>Examples</h4>
 *  <pre class="TeXSample">
 *    &#x5c;unskip  </pre>
 *
 * </doc>
 *
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.9 $
 */
public class Unskip extends AbstractCode {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 20060402L;

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public Unskip(final String name) {

        super(name);
    }

    /**
     * @see de.dante.extex.interpreter.type.Code#execute(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void execute(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        Node node = typesetter.getLastNode();
        if (node instanceof SkipNode) {
            typesetter.removeLastNode();
        } else if (node == null) {
            throw new HelpingException(getLocalizer(),
                    "TTP.CantDeleteLastSkip",
                    printableControlSequence(context), typesetter.getMode()
                            .toString());
        }
    }

}
