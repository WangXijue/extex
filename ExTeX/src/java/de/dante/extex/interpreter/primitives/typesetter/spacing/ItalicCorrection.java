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

package de.dante.extex.interpreter.primitives.typesetter.spacing;

import de.dante.extex.font.Glyph;
import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.type.AbstractCode;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.font.Font;
import de.dante.extex.interpreter.type.node.CharNode;
import de.dante.extex.interpreter.type.node.ExplicitKernNode;
import de.dante.extex.typesetter.Node;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.GeneralException;
import de.dante.util.UnicodeChar;

/**
 * This class provides an implementation for the primitive <code>\ </code>.
 *
 * <doc name="/">
 * <h3>The Primitive <tt>\/</tt></h3>
 * <p>
 *  TODO missing documentation
 * </p>
 * <p>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;italic correction&rang;
 *        &rarr; <tt>\/</tt>  </pre>
 * </p>
 * <p>
 *  Examples:
 *  <pre class="TeXSample">
 *    123\/456  </pre>
 * </p>
 * </doc>
 *
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.3 $
 */
public class ItalicCorrection extends AbstractCode {

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public ItalicCorrection(final String name) {

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

        Node node = typesetter.getLastNode();

        if (node instanceof CharNode) {
            Dimen ic = italicCorrection(//
                    ((CharNode) node).getCharacter(), //
                    ((CharNode) node).getTypesettingContext().getFont());
            typesetter.add(new ExplicitKernNode(ic));
        }
        return true;
    }

    /**
     * Determine the italic correction for a character in a font. If no
     * information can be found in the font then opt is returned.
     *
     * @param uc the unicode charcter to compute the italic correction for
     * @param font the font to use
     *
     * @return the italic correction
     */
    private Dimen italicCorrection(final UnicodeChar uc,
            final Font font) {

        Glyph g = font.getGlyph(uc);
        if (null == g) {
            return Dimen.ZERO_PT;
        }
        return g.getItalicCorrection();
    }
}