/*
 * Copyright (C) 2003-2004 Gerd Neugebauer, Michael Niedermair
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
package de.dante.extex.interpreter;

import de.dante.extex.interpreter.context.Context;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.GeneralException;

/**
 * This is the abstract base class which can be used for all classes
 * implementing the interface Code. It provides some useful definitions for
 * most of the methods.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @author <a href="m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.10 $
 */
public class AbstractCode implements Code {
    /**
     * This variable contains the name of this code for debugging.
     */
    private String name = "";

    /**
     * Creates a new object.
     *
     * @param codeName the name of the primitive
     */
    public AbstractCode(final String codeName) {
        super();
        this.name = codeName;
    }

    /**
     * @see de.dante.extex.interpreter.Code#isIf()
     */
    public boolean isIf() {
        return false;
    }

    /**
     * Setter for the name of this code instance. This information is primarily
     * needed for debugging.
     *
     * @param theName the name
     */
    public void setName(final String theName) {
        this.name = theName;
    }

    /**
     * @see de.dante.extex.interpreter.Code#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * ...
     *
     * @param context the interpreter context
     * @param source the source for new tokens
     *
     * @throws GeneralException in case of an error. This implementation does
     *             nothing; not even throwing an Exception.
     */
    public void doAfterAssignment(final Context context,
            final TokenSource source) throws GeneralException {
        // TODO: to be completed
    }

    /**
     * @see de.dante.extex.interpreter.Code#execute(de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void execute(final Flags prefix, final Context context,
        final TokenSource source, final Typesetter typesetter)
        throws GeneralException {
        prefix.clear();
    }

    /**
     * In general this method is simply a noop. Classes which need this feature
     * can overwrite this method.
     * 
     * @see de.dante.extex.interpreter.Code#set(java.lang.String)
     */
    public void set(final Context context, final String value)
        throws GeneralException {
        // nothing to do
    }

}
