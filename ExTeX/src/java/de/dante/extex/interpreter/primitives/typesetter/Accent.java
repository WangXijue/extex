/*
 * Copyright (C) 2004-2006 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.primitives.typesetter;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.context.tc.TypesettingContext;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.exception.helping.EofException;
import de.dante.extex.interpreter.exception.helping.HelpingException;
import de.dante.extex.interpreter.type.AbstractCode;
import de.dante.extex.interpreter.type.count.Count;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.font.Font;
import de.dante.extex.scanner.type.Catcode;
import de.dante.extex.scanner.type.token.Token;
import de.dante.extex.typesetter.Typesetter;
import de.dante.extex.typesetter.type.Node;
import de.dante.extex.typesetter.type.node.AccentKernNode;
import de.dante.util.UnicodeChar;
import de.dante.util.framework.configuration.exception.ConfigurationException;

/**
 * This class provides an implementation for the primitive <code>\accent</code>.
 *
 * <doc name="accent">
 * <h3>The Primitive <tt>\accent</tt></h3>
 * <p>
 *  TODO missing documentation
 * </p>
 *
 * <h4>Syntax</h4>
 *  The formal description of this primitive is the following:
 *  <pre class="syntax">
 *    &lang;accent&rang;
 *    &rarr; <tt>\accent</tt> ... </pre>
 *
 * <h4>Examples</h4>
 *  <pre class="TeXSample">
 *    \accent 13 a  </pre>
 *
 * </doc>
 *
 * @see "TTP [1123]"
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.25 $
 */
public class Accent extends AbstractCode {

    /**
     * The constant <tt>serialVersionUID</tt> contains the id for serialization.
     */
    protected static final long serialVersionUID = 2005L;

    /**
     * The constant <tt>UNIT</tt> contains the unit amount.
     */
    private static final int UNIT = 65536;

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public Accent(final String name) {

        super(name);
    }

    /**
     * This method takes the first token and executes it. The result is placed
     * on the stack. This operation might have side effects. To execute a token
     * it might be necessary to consume further tokens.
     *
     * @param prefix the prefix controlling the execution
     * @param context the interpreter context
     * @param source the token source
     * @param typesetter the typesetter
     *
     * @see de.dante.extex.interpreter.type.Code#execute(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     * @see "TTP [1123,1124,1125]"
     */
    public void execute(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        if (typesetter.getMode().isMath()) {
            throw new HelpingException(getLocalizer(), //
                    "TTP.AccentInMathMode", printableControlSequence(context),
                    context.esc("mathaccent"));
        }
        UnicodeChar accent = source.scanCharacterCode(context, typesetter,
                getName());
        Token token = source.getToken(context);
        TypesettingContext tc = context.getTypesettingContext();
        Font currentFont = tc.getFont();
        long a = -1;
        long s = 0;
        if (currentFont.hasGlyph(accent)) {
            a = currentFont.getWidth(accent).getLength().getValue();
            s = currentFont.getItalicCorrection(accent).getValue(); // TODO gene: correct?
        }
        long x = currentFont.getEx().getValue();

        if (token == null) {

            throw new EofException(printableControlSequence(context));

        } else if (token.isa(Catcode.LETTER) || token.isa(Catcode.OTHER)) {
            UnicodeChar c = token.getChar();

            if (currentFont.hasGlyph(accent)) {
                if (!currentFont.hasGlyph(c)) {
                    typesetter.letter(accent, tc, context, source, source.getLocator());
                } else {
                    Node node = typesetter.getNodeFactory().getNode(tc, accent);
                    if (node == null) {
                        //TODO gene: undefined character
                        return;
                    }
                    long w = currentFont.getWidth(c).getLength().getValue();
                    long h = currentFont.getHeight(c).getLength().getValue();
                    Dimen d = new Dimen();
                    //                    if (h != x) {
                    //                        NodeList n = new HorizontalListNode(node);
                    //                        d.set(x - h);
                    //                        n.setShift(d);
                    //                        node = n;
                    //                    }
                    // compute delta TTP [1125]
                    long delta = (w - a) / 2 + (h - x) * s / UNIT;
                    d.set(delta);
                    try {
                        typesetter.add(new AccentKernNode(d));
                        typesetter.add(node);
                        d.set(-a - delta);
                        typesetter.add(new AccentKernNode(d));
                        typesetter.letter(c, tc, context, source, source.getLocator());
                    } catch (ConfigurationException e) {
                        throw new InterpreterException(e);
                    }
                }
            } else if (currentFont.hasGlyph(c)) {
                typesetter.letter(c, tc, context, source, source.getLocator());
            } else {
                //TODO gene: letter and accent are undefined
                throw new RuntimeException("unimplemented");
            }

        } else if (token.isa(Catcode.LEFTBRACE)) {
            source.push(token);

            //TODO gene: unimplemented
            throw new RuntimeException("unimplemented");

        } else {
            //TODO gene: unimplemented
            throw new RuntimeException("unimplemented");
        }

        typesetter.setSpacefactor(Count.THOUSAND);
    }

}
