/*
 * Copyright (C) 2006 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.primitives.omega.translation;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.primitives.file.AbstractFileCode;
import de.dante.extex.interpreter.primitives.omega.mode.AbstractModeCode;
import de.dante.extex.interpreter.primitives.omega.ocp.util.OcpUtil;
import de.dante.extex.interpreter.type.file.InFile;
import de.dante.extex.typesetter.Typesetter;

/**
 * This class provides an implementation for the primitive
 * <code>\InputTranslation</code>.
 *
 * <doc name="InputTranslation">
 * <h3>The Primitive <tt>\InputTranslation</tt></h3>
 * <p>
 *  TODO missing documentation
 * </p>
 * <h4>Syntax</h4>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;InputTranslation&rang;
 *      &rarr; ...  </pre>
 *
 * <h4>Examples</h4>
 * <pre class="TeXSample">
 * \InputTranslation... </pre>
 * </doc>
 *
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.3 $
 */
public class InputTranslation extends AbstractModeCode {

    /**
     * The field <tt>serialVersionUID</tt> contains the version number for
     * serialization.
     */
    protected static final long serialVersionUID = 2006L;

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public InputTranslation(final String name) {

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

        String file;
        if (source.getKeyword(context, "currentfile")) {
            file = null;
        } else {
            file = AbstractFileCode.scanInFileKey(context,source,typesetter);
        }
        String resource = OcpUtil.scanOcpFileName(source, context);
        InFile inFile = context.getInFile(file);

        //TODO gene: unimplemented
        throw new RuntimeException("unimplemented");
    }

}
