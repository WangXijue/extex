/*
 * Copyright (C) 2004-2005 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.primitives.register.real;

import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.type.Theable;
import de.dante.extex.interpreter.type.count.CountConvertible;
import de.dante.extex.interpreter.type.real.Real;
import de.dante.extex.interpreter.type.real.RealConvertible;
import de.dante.util.exception.GeneralException;

/**
 * Math. the largest (closest to positive infinity) double value
 * that is not greater than the argument and is equal to a mathematical integer.
 *
 * <p>Example</p>
 * <pre>
 * \the\mathfloor 0.234
 * \real7=\mathfloor 0.56
 * \real8=\mathfloor\real7
 * \count99=\mathfloor 1.34
 * </pre>
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.5 $
 */
public class MathFloor extends AbstractMath
        implements
            Theable,
            RealConvertible,
            CountConvertible {

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     * @throws GeneralException ...
     */
    public MathFloor(final String name) throws GeneralException {

        super(name);

    }

    /**
     * Calculate
     * @param context   the context
     * @param source    the tokensource
     * @return  the real-value
     * @throws InterpreterException if a error occoured
     */
    protected Real calculate(final Context context, final TokenSource source)
            throws InterpreterException {

        Real real = new Real(context, source);
        return new Real(Math.floor(real.getValue()));
    }
}
