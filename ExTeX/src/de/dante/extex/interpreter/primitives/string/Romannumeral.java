/*
 * Copyright (C) 2004 The ExTeX Group and individual authors listed below
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
package de.dante.extex.interpreter.primitives.string;

import de.dante.extex.interpreter.AbstractCode;
import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.type.Tokens;
import de.dante.extex.scanner.Catcode;
import de.dante.extex.scanner.TokenFactory;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.GeneralException;

/**
 * This class provides an implementation for the primitive
 * <code>\romannumeral</code>.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.1 $
 */
public class Romannumeral extends AbstractCode {

    /**
     * Creates a new object.
     *
     * @param name the name for tracing and debugging
     */
    public Romannumeral(final String name) {

        super(name);
    }

    /**
     * @see "TeX -- the Program [69]"
     * @see de.dante.extex.interpreter.Code#execute(de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void execute(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws GeneralException {

        long n = source.scanInteger();
        Tokens toks = new Tokens();
        TokenFactory factory = context.getTokenFactory();
        String magic = "m2d5c2l5x2v5i";
        int j = 0;
        int v = 1000;

        for (;;) {
            while (n >= v) {
                toks.add(factory.newInstance(Catcode.LETTER, magic.charAt(j)));
                n = n - v;
            }

            if (n <= 0) {
                return;  //nonpositive input produces no output
            }

            int k = j + 2;
            int u = v / (magic.charAt(k - 1) - '0');
            if (magic.charAt(k - 1) == '2') {
                k = k + 2;
                u = u / (magic.charAt(k - 1) - '0');
            }
            if (n + u >= v) {
                toks.add(factory.newInstance(Catcode.LETTER, magic.charAt(k)));
                n = n + u;
            } else {
                j = j + 2;
                v = v / (magic.charAt(j - 1) - '0');
            }
        }
    }

}
