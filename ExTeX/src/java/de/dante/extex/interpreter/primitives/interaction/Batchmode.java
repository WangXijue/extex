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

package de.dante.extex.interpreter.primitives.interaction;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.interaction.Interaction;
import de.dante.extex.interpreter.type.AbstractAssignment;
import de.dante.extex.typesetter.Typesetter;

/**
 * This class provides an implementation for the primitive
 * <code>\batchmode</code>. It does simply nothing, but as a side
 * effect all prefixes are zeroed and the interaction mode is set to
 * <tt>batchmode</tt>.
 *
 * <doc name="batchmode">
 * <h3>The Primitive <tt>\batchmode</tt></h3>
 * <p>
 *  This primitive sets the interaction mode to batch mode.
 *  In batch mode the processing is terminated if the program needs input from
 *  the terminal or an error occurs. The output to the terminal is reduced to a
 *  minimum.
 * </p>
 * <p>
 *  The setting of the interaction mode is an assignment. The mode is always
 *  processed globally. This means it does not interact with the group concept.
 * </p>
 *
 * <h4>Syntax</h4>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;batchmode&rang;
 *      &rarr; <tt>\batchmode</tt>  </pre>
 *
 * <h4>Examples</h4>
 *  <pre class="TeXSample">
 *    \batchmode  </pre>
 *
 * </doc>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.19 $
 */
public class Batchmode extends AbstractAssignment {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 2005L;

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public Batchmode(final String name) {

        super(name);
    }

    /**
     * @see de.dante.extex.interpreter.type.AbstractAssignment#assign(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void assign(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        context.setInteraction(Interaction.BATCHMODE);
    }

}
