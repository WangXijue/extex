/*
 * Copyright (C) 2004 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.primitives.math.numbering;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.CantUseInException;
import de.dante.extex.interpreter.exception.MissingMathException;
import de.dante.extex.interpreter.primitives.math.AbstractMathCode;
import de.dante.extex.typesetter.ListMaker;
import de.dante.extex.typesetter.Typesetter;
import de.dante.extex.typesetter.listMaker.EqConsumer;
import de.dante.extex.typesetter.listMaker.NoadConsumer;
import de.dante.util.GeneralException;

/**
 * This class provides an implementation for the primitive <code>\eqno</code>.
 *
 * <doc name="eqno">
 * <h3>The Primitive <tt>\eqno</tt></h3>
 * <p>
 *  TODO missing documentation
 * </p>
 * <p>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;eqno&rang;
 *       &rarr; <tt>\eqno</tt>  </pre>
 * </p>
 * <p>
 *  Examples:
 *  <pre class="TeXSample">
 *    \eqno  </pre>
 * </p>
 * </doc>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.2 $
 */
public class Eqno extends AbstractMathCode {

    /**
     * Creates a new object.
     *
     * @param name the name for tracing and debugging
     */
    public Eqno(final String name) {

        super(name);
    }

    /**
     * @see de.dante.extex.interpreter.type.Code#execute(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public boolean execute(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws GeneralException {

        ListMaker lm = typesetter.getListMaker();
        if (!(lm instanceof EqConsumer)) {
            throw new CantUseInException(
                    printableControlSequence(context), //
                    typesetter.getMode().toString());
        }

        try {

            ((EqConsumer) lm).switchToNumber(false);

        } catch (CantUseInException e) {
            throw new CantUseInException(
                    printableControlSequence(context), //
                    typesetter.getMode().toString());
        }
        return true;
    }

}