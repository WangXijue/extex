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

package de.dante.extex.language.ligature.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import de.dante.extex.font.FontByteArray;
import de.dante.extex.font.Glyph;
import de.dante.extex.font.Kerning;
import de.dante.extex.font.Ligature;
import de.dante.extex.font.type.other.NullFont;
import de.dante.extex.font.type.tfm.TFMFixWord;
import de.dante.extex.interpreter.context.tc.TypesettingContext;
import de.dante.extex.interpreter.context.tc.TypesettingContextImpl;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.dimen.FixedDimen;
import de.dante.extex.interpreter.type.glue.Glue;
import de.dante.extex.language.hyphenation.exception.HyphenationException;
import de.dante.extex.language.ligature.LigatureBuilder;
import de.dante.extex.language.ligature.impl.LigatureBuilderImpl;
import de.dante.extex.scanner.type.token.ActiveCharacterTokenTest;
import de.dante.extex.typesetter.type.Node;
import de.dante.extex.typesetter.type.NodeList;
import de.dante.extex.typesetter.type.node.CharNode;
import de.dante.extex.typesetter.type.node.GlueNode;
import de.dante.extex.typesetter.type.node.HorizontalListNode;
import de.dante.extex.typesetter.type.node.LigatureNode;
import de.dante.util.UnicodeChar;

/**
 * This is a test suite for the <tt>LigatureBuilderImpl</tt>.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.12 $
 */
public class LigatureBuilderImplTest extends TestCase {

    /**
     * This class provides a mock implementation for a font.
     *
     * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
     * @version $Revision: 1.12 $
     */
    private class MockFont extends NullFont {

        /**
         * The constant <tt>serialVersionUID</tt> contains the id for serialization.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The field <tt>cache</tt> contains the ...
         */
        private Map cache = new HashMap();

        /**
         * Creates a new object.
         */
        public MockFont() {

            super();
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Font#getGlyph(de.dante.util.UnicodeChar)
         */
        public Glyph getGlyph(final UnicodeChar c) {

            Glyph g = (Glyph) cache.get(c);
            if (g == null) {
                switch (c.getCodePoint()) {
                    case 'a':
                        g = new MockGlyph('a');
                        break;
                    case 'f':
                        g = new MockGlyph('f');
                        break;
                    case 'l':
                        g = new MockGlyph('l');
                        break;
                    case CC_FF:
                        g = new MockGlyph(CC_FF);
                        break;
                    case CC_FL:
                        g = new MockGlyph(CC_FL);
                        break;
                    case CC_FFL:
                        g = new MockGlyph(CC_FFL);
                        break;
                }
                cache.put(c, g);
            }
            return g;
        }

        /**
         * @see de.dante.extex.font.type.other.NullFont#hasGlyph(de.dante.util.UnicodeChar)
         */
        public boolean hasGlyph(final UnicodeChar uc) {

            return getGlyph(uc) != null;
        }

        /**
         * @see de.dante.extex.font.type.other.NullFont#getLigature(de.dante.util.UnicodeChar, de.dante.util.UnicodeChar)
         */
        public UnicodeChar getLigature(final UnicodeChar uc1,
                final UnicodeChar uc2) {

            switch (uc1.getCodePoint()) {
                case 'f':
                    if (uc2.getCodePoint() == 'f') {
                        return UnicodeChar.get(CC_FF);
                    } else if (uc2.getCodePoint() == 'l') {
                        return UnicodeChar.get(CC_FL);
                    }
                    break;
                case CC_FF:
                    if (uc2.getCodePoint() == 'l') {
                        return UnicodeChar.get(CC_FFL);
                    }
                    break;
            }

            return null;
        }

    }

    /**
     * This class provides a mock implementation for a glyph.
     *
     * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
     * @version $Revision: 1.12 $
     */
    private class MockGlyph implements Glyph {

        /**
         * @see de.dante.extex.font.Glyph#setDepth(de.dante.extex.font.type.tfm.TFMFixWord, de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setDepth(final TFMFixWord size, final Dimen em) {

        }

        /**
         * @see de.dante.extex.font.Glyph#setHeight(de.dante.extex.font.type.tfm.TFMFixWord, de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setHeight(final TFMFixWord size, final Dimen em) {

        }

        /**
         * @see de.dante.extex.font.Glyph#setItalicCorrection(de.dante.extex.font.type.tfm.TFMFixWord, de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setItalicCorrection(final TFMFixWord size, final Dimen em) {

        }

        /**
         * @see de.dante.extex.font.Glyph#setWidth(de.dante.extex.font.type.tfm.TFMFixWord, de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setWidth(final TFMFixWord size, final Dimen em) {

        }

        /**
         * The field <tt>c</tt> contains the encapsulatec character.
         */
        private int c;

        /**
         * Creates a new object.
         *
         * @param cp the character code point
         */
        public MockGlyph(final int cp) {

            super();
            c = cp;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#addKerning(
         *      de.dante.extex.interpreter.type.font.Kerning)
         */
        public void addKerning(final Kerning kern) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#addLigature(
         *      de.dante.extex.interpreter.type.font.Ligature)
         */
        public void addLigature(final Ligature lig) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getDepth()
         */
        public Dimen getDepth() {

            return Dimen.ZERO_PT;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getExternalFile()
         */
        public FontByteArray getExternalFile() {

            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getHeight()
         */
        public Dimen getHeight() {

            return Dimen.ONE_INCH;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getItalicCorrection()
         */
        public Dimen getItalicCorrection() {

            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getKerning(
         *      de.dante.util.UnicodeChar)
         */
        public Dimen getKerning(final UnicodeChar uc) {

            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getLeftSpace()
         */
        public Dimen getLeftSpace() {

            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getLigature(
         *      de.dante.util.UnicodeChar)
         */
        public UnicodeChar getLigature(final UnicodeChar uc) {

            if (c == 'f') {
                switch (uc.getCodePoint()) {
                    case 'f':
                        return FF;
                    case 'l':
                        return FL;
                }
            } else if (c == CC_FF) {
                switch (uc.getCodePoint()) {
                    case 'l':
                        return FFL;
                }
            }
            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getName()
         */
        public String getName() {

            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getNumber()
         */
        public String getNumber() {

            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getRightSpace()
         */
        public Dimen getRightSpace() {

            return null;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#getWidth()
         */
        public Dimen getWidth() {

            return Dimen.ONE_INCH;
        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setDepth(de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setDepth(final Dimen d) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setDepth(java.lang.String, de.dante.extex.interpreter.type.dimen.Dimen, int)
         */
        public void setDepth(final String gsize, final Dimen em,
                final int unitsperem) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setExternalFile(de.dante.extex.interpreter.type.font.FontFile)
         */
        public void setExternalFile(final FontByteArray file) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setHeight(de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setHeight(final Dimen h) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setHeight(java.lang.String, de.dante.extex.interpreter.type.dimen.Dimen, int)
         */
        public void setHeight(final String gsize, final Dimen em,
                final int unitsperem) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setItalicCorrection(de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setItalicCorrection(final Dimen d) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setItalicCorrection(java.lang.String, de.dante.extex.interpreter.type.dimen.Dimen, int)
         */
        public void setItalicCorrection(final String gsize, final Dimen em,
                final int unitsperem) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setLeftSpace(de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setLeftSpace(final Dimen ls) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setName(java.lang.String)
         */
        public void setName(final String n) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setNumber(
         *      java.lang.String)
         */
        public void setNumber(final String nr) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setRightSpace(
         *      de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setRightSpace(final Dimen rs) {

        }

        /**
         * @see de.dante.extex.font.Glyph#setWidth(
         *      de.dante.extex.interpreter.type.dimen.Dimen)
         */
        public void setWidth(final Dimen w) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setWidth(FixedDimen)
         */
        public void setWidth(final FixedDimen w) {

        }

        /**
         * @see de.dante.extex.interpreter.type.font.Glyph#setWidth(
         *      java.lang.String,
         *      de.dante.extex.interpreter.type.dimen.Dimen, int)
         */
        public void setWidth(final String gsize, final Dimen em,
                final int unitsperem) {

        }
    }

    /**
     * The field <tt>builder</tt> contains the ligature builder.
     */
    private static LigatureBuilder builder = new LigatureBuilderImpl();

    /**
     * The constant <tt>CC_FF</tt> contains the ...
     */
    private static final int CC_FF = '�';

    /**
     * The constant <tt>CC_FFL</tt> contains the ...
     */
    private static final int CC_FFL = '&';

    /**
     * The constant <tt>CC_FL</tt> contains the ...
     */
    private static final int CC_FL = '$';

    /**
     * The constant <tt>FF</tt> contains the ...
     */
    private static final UnicodeChar FF = UnicodeChar.get(CC_FF);

    /**
     * The constant <tt>FFL</tt> contains the ...
     */
    private static final UnicodeChar FFL = UnicodeChar.get(CC_FFL);

    /**
     * The constant <tt>FL</tt> contains the ...
     */
    private static final UnicodeChar FL = UnicodeChar.get(CC_FL);

    /**
     * The field <tt>tc1</tt> contains the typesetting context.
     */
    private static TypesettingContext tc1;

    /**
     * Command line interface.
     * @param args the arguments
     */
    public static void main(final String[] args) {

        junit.textui.TestRunner.run(ActiveCharacterTokenTest.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        tc1 = new TypesettingContextImpl(new MockFont());
        super.setUp();
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testEmpty() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        builder.insertLigatures(nodes, 0);
        assertEquals(0, nodes.size());
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testFour1() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        nodes.add(new CharNode(tc1, UnicodeChar.get('a')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('l')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        builder.insertLigatures(nodes, 0);
        assertEquals(3, nodes.size());
        assertTrue(nodes.get(0) instanceof CharNode);
        assertTrue(nodes.get(1) instanceof LigatureNode);
        LigatureNode lig = (LigatureNode) nodes.get(1);
        assertEquals(CC_FL, lig.getCharacter().getCodePoint());
        assertEquals('f', ((CharNode) nodes.get(2)).getCharacter()
                .getCodePoint());
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testOne1() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        Node n = new CharNode(tc1, UnicodeChar.get('a'));
        nodes.add(n);
        builder.insertLigatures(nodes, 0);
        assertEquals(1, nodes.size());
        assertEquals(n, nodes.get(0));
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testOne2() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        Node n = new GlueNode(new Glue(3), true);
        nodes.add(n);
        builder.insertLigatures(nodes, 0);
        assertEquals(1, nodes.size());
        assertEquals(n, nodes.get(0));
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testThree0() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        nodes.add(new CharNode(tc1, UnicodeChar.get('a')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('a')));
        builder.insertLigatures(nodes, 0);
        assertEquals(3, nodes.size());
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testThree1() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('l')));
        builder.insertLigatures(nodes, 0);
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof LigatureNode);
        LigatureNode lig = (LigatureNode) nodes.get(0);
        assertEquals(CC_FFL, lig.getCharacter().getCodePoint());
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testThree2() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        nodes.add(new CharNode(tc1, UnicodeChar.get('a')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('l')));
        builder.insertLigatures(nodes, 0);
        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0) instanceof CharNode);
        assertTrue(nodes.get(1) instanceof LigatureNode);
        LigatureNode lig = (LigatureNode) nodes.get(1);
        assertEquals(CC_FL, lig.getCharacter().getCodePoint());
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testTwo1() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        builder.insertLigatures(nodes, 0);
        assertEquals(1, nodes.size());
        assertTrue(nodes.get(0) instanceof LigatureNode);
        LigatureNode lig = (LigatureNode) nodes.get(0);
        assertEquals(CC_FF, lig.getCharacter().getCodePoint());
    }

    /**
     * ...
     * @throws HyphenationException in case of an error
     */
    public void testTwo2() throws HyphenationException {

        NodeList nodes = new HorizontalListNode();
        nodes.add(new CharNode(tc1, UnicodeChar.get('f')));
        nodes.add(new CharNode(tc1, UnicodeChar.get('a')));
        builder.insertLigatures(nodes, 0);
        assertEquals(2, nodes.size());
    }

}