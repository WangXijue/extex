/*
 * Copyright (C) 2004-2006 The ExTeX Group and individual authors listed below
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package de.dante.extex.interpreter.primitives.register.box;

import java.io.Serializable;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.type.box.Box;
import de.dante.extex.interpreter.type.box.Boxable;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.framework.configuration.exception.ConfigurationException;

/**
 * This class provides an implementation for the primitive <code>\copy</code>.
 *
 * <doc name="copy">
 * <h3>The Primitive <tt>\copy</tt></h3>
 * <p>
 *  The primitive <tt>\copy</tt> inserts the contents of the named box register
 *  at the current position. In contrast to the
 *  {@link de.dante.extex.interpreter.primitives.register.box.BoxPrimitive <tt>\box</tt>}
 *  primitive the box register is not cleared.
 * </p>
 * <p>
 *  If the primitive is used on the right hand side of a box assignment then
 *  the box contents is used for the assignment.
 * </p>
 *
 * <h4>Syntax</h4>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;copy&rang;
 *      &rarr; <tt>\copy</tt> {@linkplain
 *        de.dante.extex.interpreter.primitives.register.box.AbstractBox#getKey(Context,Source,Typesetter,String)
 *        &lang;box register name&rang;} </pre>
 *
 * <h4>Examples</h4>
 *  <pre class="TeXSample">
 *    \copy42  </pre>
 *
 * </doc>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.28 $
 */
public class Copy extends BoxPrimitive implements Boxable, Serializable {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 2005L;

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public Copy(final String name) {

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

        String key = getKey(context, source, typesetter, getName());
        Box box = context.getBox(key);
        if (box != null) {
            try {
                typesetter.add(box.getNodes());
            } catch (ConfigurationException e) {
                throw new InterpreterException(e);
            }
        }
    }

}
