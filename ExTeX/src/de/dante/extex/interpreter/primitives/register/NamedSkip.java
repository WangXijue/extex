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
package de.dante.extex.interpreter.primitives.register;

import de.dante.extex.interpreter.AbstractAssignment;
import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.Theable;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.type.Glue;
import de.dante.extex.interpreter.type.Tokens;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.GeneralException;

/**
 * This class provides an implementation for the primitive <code>\skip</code>.
 * It sets the named dimen register to the value given,
 * and as a side effect all prefixes are zeroed.
 *
 * <p>Example</p>
 * <pre>
 * \xxx=345pt plus 123em
 * </pre>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.4 $
 */
public class NamedSkip extends AbstractAssignment implements Theable {

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public NamedSkip(final String name) {
        super(name);
    }

    /**
     * Return the key (the name of the primitive) for the register.
     *
     * @param source the source for new tokens
     *
     * @return the key for the current register
     *
     * @throws GeneralException in case that a derived class need to throw an
     *             Exception this one is declared.
     */
    protected String getKey(final TokenSource source) throws GeneralException {
        return getName();
    }

    /**
     * @see de.dante.extex.interpreter.Code#execute(de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void assign(final Flags prefix, final Context context,
        final TokenSource source, final Typesetter typesetter)
        throws GeneralException {

        String key = getKey(source);
        source.scanOptionalEquals();
        
        Glue g = new Glue(source, context);
        context.setGlue(key, g, prefix.isGlobal());
    }

    /**
     * ...
     *
     * @param context the interpreter context
     * @param value ...
     *
     * @throws GeneralException in case of an error
     */
    public void set(final Context context, final String value)
             throws GeneralException {
                 //TODO
//        context.setDimen(getName(),
//                         (value.equals("") ? 0 : ...));
    }

    /**
     * @see de.dante.extex.interpreter.Theable#the(de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource)
     */
    public Tokens the(final Context context, final TokenSource source)
            throws GeneralException {

        String key = getKey(source);
        return context.getGlue(key).toToks(context.getTokenFactory());
    }
}
