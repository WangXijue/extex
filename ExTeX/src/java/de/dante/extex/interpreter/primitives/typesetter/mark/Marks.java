/*
 * Copyright (C) 2005-2006 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.primitives.typesetter.mark;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.type.tokens.Tokens;
import de.dante.extex.typesetter.Typesetter;
import de.dante.extex.typesetter.type.node.MarkNode;
import de.dante.util.framework.configuration.exception.ConfigurationException;

/**
 * This class provides an implementation for the primitive
 * <code>\marks</code>.
 *
 * <doc name="marks">
 * <h3>The Primitive <tt>\marks</tt></h3>
 * <p>
 *  The primitive <tt>\marks</tt> places a mark in the current list. The
 *  marks on the last page can be retrieved when the page is mounted in the
 *  output routine.
 *  The text stored within the mark is a list of tokens.
 * </p>
 * <p>
 *  The marks can be classified with an identifier. In <logo>eTeX</logo> the
 *  identifier is a number. In <logo>ExTeX</logo> in addition arbitrary
 *  token lists can be used as identifier.
 * </p>
 * <p>
 *  The <tt>\marks0</tt> is equivalent to <tt>\mark</tt>.
 * </p>
 *
 * <h4>Syntax</h4>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;marks&rang;
 *      &rarr; <tt>\marks</tt> {@linkplain
 *        de.dante.extex.interpreter.primitives.typesetter.mark.AbstractMarksCode#getKey(Context,TokenSource,Typesetter)
 *        &lang;mark name&rang;} {@linkplain
 *        de.dante.extex.interpreter.TokenSource#getTokens(Context,TokenSource,Typesetter)
 *        &lang;tokens&rang;}  </pre>
 *
 * <h4>Examples</h4>
 *  <pre class="TeXSample">
 *    \marks123{abc}  </pre>
 *
 * </doc>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.16 $
 */
public class Marks extends AbstractMarkCode {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 20060406L;

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public Marks(final String name) {

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

        String index = getKey(context, source, typesetter);
        Tokens toks = source.scanUnprotectedTokens(context, false, false,
                getName());
        try {
            typesetter.add(new MarkNode(toks, index));
        } catch (ConfigurationException e) {
            throw new InterpreterException(e);
        }
    }

}
