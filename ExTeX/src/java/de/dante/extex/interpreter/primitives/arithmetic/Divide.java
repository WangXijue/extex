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

package de.dante.extex.interpreter.primitives.arithmetic;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.CantUseAfterException;
import de.dante.extex.interpreter.exception.EofException;
import de.dante.extex.interpreter.exception.UndefinedControlSequenceException;
import de.dante.extex.interpreter.type.AbstractAssignment;
import de.dante.extex.interpreter.type.Code;
import de.dante.extex.interpreter.type.arithmetic.Divideable;
import de.dante.extex.scanner.CodeToken;
import de.dante.extex.scanner.Token;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.GeneralException;

/**
 * This class provides an implementation for the primitive <code>\divide</code>.
 *
 * <doc name="divide">
 * <h3>The Primitive <tt>\divide</tt></h3>
 * <p>
 *  This primitive implements an assignment. The variable given as next tokens
 *  is divided by the quantity given after the optional <tt>by</tt>.
 * </p>
 * <p>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *   &lang;divide&rang;
 *     &rarr; <tt>\divide</tt> &lang;dividable&rang;
 *
 *   &lang;dividable&rang;
 *     &rarr; &lang;integer variable&rang; &lang;optional <tt>by</tt>&rang; {@linkplain
 *      de.dante.extex.interpreter.TokenSource#scanNumber()
 *      &lang;8-bit&nbsp;number&rang;}
 *      |  &lang;dimen variable&rang; &lang;optional <tt>by</tt>&rang; {@linkplain
 *      de.dante.extex.interpreter.TokenSource#scanNumber()
 *      &lang;8-bit&nbsp;number&rang;}
 *      |  &lang;glue variable&rang; &lang;optional <tt>by</tt>&rang; {@linkplain
 *      de.dante.extex.interpreter.TokenSource#scanNumber()
 *      &lang;8-bit&nbsp;number&rang;}
 *      |  &lang;muglue variable&rang; &lang;optional <tt>by</tt>&rang; {@linkplain
 *      de.dante.extex.interpreter.TokenSource#scanNumber()
 *      &lang;8-bit&nbsp;number&rang;}
 *
 *   &lang;optional <tt>by</tt>&rang;
 *     &rarr; [by]
 *      |  &lang;optional spaces&rang;
 *   </pre>
 * </p>
 * <p>
 *  Examples:
 *  <pre class="TeXSample">
 *    \divide\count12 345  </pre>
 *  <pre class="TeXSample">
 *    \divide\count12 by -345  </pre>
 * </p>
 * </doc>
 *
 *
 * @see de.dante.extex.interpreter.type.arithmetic.Divideable
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.14 $
 */
public class Divide extends AbstractAssignment {

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public Divide(final String name) {

        super(name);
    }

    /**
     * @see de.dante.extex.interpreter.type.Code#execute(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void assign(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws GeneralException {

        Token cs = source.getToken(context);

        if (cs instanceof CodeToken) {
            Code code = context.getCode((CodeToken) cs);

            if (code instanceof Divideable) {

                ((Divideable) code).divide(prefix, context, source);
                return;

            } else if (code == null) {
                throw new UndefinedControlSequenceException(//
                        printable(context, cs));
            }
        } else if (cs == null) {
            throw new EofException(printableControlSequence(context));
        }
        throw new CantUseAfterException(cs.toText(),
                printableControlSequence(context));
    }
}