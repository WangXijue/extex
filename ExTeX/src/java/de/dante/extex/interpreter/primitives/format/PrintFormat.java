/*
 * Copyright (C) 2004-2005 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.primitives.format;

import java.text.DecimalFormat;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.exception.helping.CantUseAfterException;
import de.dante.extex.interpreter.exception.helping.UndefinedControlSequenceException;
import de.dante.extex.interpreter.type.AbstractCode;
import de.dante.extex.interpreter.type.Code;
import de.dante.extex.interpreter.type.Theable;
import de.dante.extex.interpreter.type.real.Real;
import de.dante.extex.interpreter.type.real.RealConvertible;
import de.dante.extex.interpreter.type.tokens.Tokens;
import de.dante.extex.scanner.type.token.CodeToken;
import de.dante.extex.scanner.type.token.ControlSequenceToken;
import de.dante.extex.scanner.type.token.Token;
import de.dante.extex.typesetter.Typesetter;

/**
 * This class provides an implementation for the primitive <code>\printformat</code>.
 * It format the next primitive for the output with the given pattern and
 * the default <code>Locale</code>.
 *
 * <p>
 * Example:
 * <pre>
 * \the\printformat{pattern}\real7
 * </pre>
 *
 * @see java.text.DecimalFormat
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.10 $
 */
public class PrintFormat extends AbstractCode implements Theable {

    /**
     * The field <tt>serialVersionUID</tt> contains the ...
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new object.
     *
     * @param name the name for tracing and debugging
     */
    public PrintFormat(final String name) {

        super(name);
    }

    /**
     * Get the next token (not expand) and print it with some format.
     *
     * @see de.dante.extex.interpreter.type.Code#execute(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void execute(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        source.push(the(context, source, typesetter));
    }

    /**
     * @see de.dante.extex.interpreter.type.Theable#the(
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource, Typesetter)
     */
    public Tokens the(final Context context, final TokenSource source,
            final Typesetter typesetter) throws InterpreterException {

        // \the\printformat{pattern}\real7

        String pattern = source.scanTokensAsString(context, getName());

        if (pattern == null || pattern.trim().length() == 0) {
            pattern = "0.00";
        }

        Token cs = source.getToken(context);

        if (!(cs instanceof ControlSequenceToken)) {
            throw new CantUseAfterException(cs.toText(),
                    printableControlSequence(context));
        }

        Code code = context.getCode((CodeToken) cs);

        if (code == null) {
            throw new UndefinedControlSequenceException(printable(context, cs));

        } else if (code instanceof RealConvertible) {
            Real val = ((RealConvertible) code).convertReal(context, source);
            DecimalFormat form = new DecimalFormat(pattern);
            Tokens toks = new Tokens(context, form.format(val.getValue()));
            return toks;
        } else {
            throw new CantUseAfterException(cs.toText(),
                    printableControlSequence(context));
        }
    }
}