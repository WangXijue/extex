/*
 * Copyright (C) 2003-2004 The ExTeX Group and individual authors listed below
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

package de.dante.extex.documentWriter.svg;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import com.ibm.icu.text.DecimalFormat;

import de.dante.extex.documentWriter.DocumentWriter;
import de.dante.extex.documentWriter.DocumentWriterOptions;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.node.HorizontalListNode;
import de.dante.extex.interpreter.type.node.VerticalListNode;
import de.dante.extex.typesetter.Node;
import de.dante.extex.typesetter.NodeIterator;
import de.dante.extex.typesetter.NodeList;
import de.dante.extex.typesetter.NodeVisitor;
import de.dante.util.GeneralException;
import de.dante.util.Unit;
import de.dante.util.configuration.Configuration;

/**
 * This is a svg implementation of a document writer.
 *
 * At the moment, only one page!!!!
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.1 $
 */
public class SVGDocumentWriter implements DocumentWriter, NodeVisitor {

    /**
     * DIN-A4 width
     */
    private static final double DINA4WIDTH = 21.0d;

    /**
     * DIN-A4 height
     */
    private static final double DINA4HEIGHT = 29.7d;

    /**
     * State
     */
    private static class State {

        /**
         * Create a new object.
         */
        public State() {

            super();
        }
    }

    /**
     * in horizontal mode
     */
    private static final State HORIOZONTAL = new State();

    /**
     * in vertical mode
     */
    private static final State VERTICAL = new State();

    /**
     * the current mode
     */
    private State state = VERTICAL;

    /**
     * The field <tt>out</tt> ...
     */
    private OutputStream out = null;

    /**
     * The field <tt>shippedPages</tt> ...
     */
    private int shippedPages = 0;

    /**
     * the root Element
     */
    private Element root;

    /**
     * the parent element
     */
    private Element parent;

    /**
     * documentwriter options
     */
    private DocumentWriterOptions docoptions;

    /**
     * encdoing
     */
    private String encoding = "ISO-8859-1";

    /**
     * debug
     */
    private boolean debug = true;

    /**
     * Naemspace for svg
     */
    private static final Namespace SVGNAMESPACE = Namespace
            .getNamespace("http://www.w3.org/2000/svg");

    /**
     * Creates a new object.
     * @param cfg       the configuration
     * @param options   the options
     */
    public SVGDocumentWriter(final Configuration cfg,
            final DocumentWriterOptions options) {

        super();
        root = new Element("svg", SVGNAMESPACE);
        root.setAttribute("version", "1.1");
        docoptions = options;

        if (cfg != null) {
            String tmp = cfg.getAttribute("encoding");
            if (tmp != null && !tmp.equals("")) {
                encoding = tmp;
            }
            tmp = cfg.getAttribute("debug");
            if (tmp != null) {
                debug = Boolean.valueOf(tmp).booleanValue();
            }
        }
    }

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#close()
     */
    public void close() throws IOException {

        if (out != null) {
            // write to xml-file
            XMLOutputter xmlout = new XMLOutputter();
            xmlout.setEncoding(encoding);
            xmlout.setIndent("   ");
            xmlout.setNewlines(true);
            xmlout.setTrimAllWhite(true);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            Document doc = new Document();
            doc.setDocType(new DocType("svg", "//W3C//DTD SVG 1.1//EN",
                    "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"));
            doc.setRootElement(root);
            xmlout.output(doc, bout);
            bout.close();
        }
    }

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#getExtension()
     */
    public String getExtension() {

        return "svg";
    }

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#getPages()
     */
    public int getPages() {

        return shippedPages;
    }

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#setOutputStream(java.io.OutputStream)
     */
    public void setOutputStream(final OutputStream outStream) {

        out = outStream;
    }

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#setParameter(
     *      java.lang.String,
     *      java.lang.String)
     */
    public void setParameter(final String name, final String value) {

    }

    /**
     * paperwidth
     */
    private Dimen paperwidth;

    /**
     * paperheight
     */
    private Dimen paperheight;

    /**
     * current x position
     */
    private Dimen currentX = new Dimen();

    /**
     * current y postition
     */
    private Dimen currentY = new Dimen();

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#shipout(de.dante.extex.typesetter.NodeList)
     */
    public void shipout(final NodeList nodes) throws IOException,
            GeneralException {

        if (shippedPages == 0) {
            // TeX primitives should set the papersize in any way:
            // o \paperwidth   / \paperheight,
            // o \pdfpagewidth / \pdfpageheight <-- pdfTeX
            // o \mediawidth   / \mediaheight   <-- VTeX
            if (docoptions != null) {
                paperwidth = (Dimen) docoptions.getDimenOption("paperwidth");
                paperheight = (Dimen) docoptions.getDimenOption("paperheight");
                if (paperheight.getValue() == 0 || paperwidth.getValue() == 0) {
                    // use DIN A4
                    paperwidth = Unit.createDimenFromCM(DINA4WIDTH);
                    paperheight = Unit.createDimenFromCM(DINA4HEIGHT);
                }
            } else {
                // use DIN A4
                paperwidth = Unit.createDimenFromCM(DINA4WIDTH);
                paperheight = Unit.createDimenFromCM(DINA4HEIGHT);
            }
            setDimenLength(root, "width", paperwidth);
            setDimenLength(root, "height", paperheight);

            // set start point
            currentX.set(Dimen.ONE_INCH);
            currentY.set(Dimen.ONE_INCH);

            parent = root;

            Object o = nodes.visit(this, nodes);
            if (o instanceof Element) {
                parent.addContent((Element) o);
            }
            shippedPages++;
        }
    }

    /**
     * Set the Attribute for an element with sp, bp, mm
     * @param element   the element
     * @param name      the attribute-name
     * @param dimen     the dimen
     */
    private void setDimenLength(final Element element, final String name,
            final Dimen dimen) {

        Dimen d = dimen;
        if (dimen == null) {
            d = new Dimen();
        }
        element.setAttribute(name, format.format(Unit.getDimenAsMM(d)) + "mm");
    }

    /**
     * only for test
     */
    private static final DecimalFormat format = new DecimalFormat("#0.00");

    /**
     * return the node element
     * @param node      the node
     * @return Returns the node-element
     */
    private Element getNodeElement(final Node node) {

        Element element = null;
        try {
            Object o = node.visit(this, node);
            if (o != null) {
                if (o instanceof Element) {
                    element = (Element) o;
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        return element;
    }

    /**
     * Set the y-position for the baseline
     * @param nodelist  the horizontal nodelist
     */
    private void setBaseline(final HorizontalListNode nodelist) {

        Dimen maxd = new Dimen();

        NodeIterator it = nodelist.iterator();
        while (it.hasNext()) {
            Node node = it.next();
            if (maxd.le(node.getDepth())) {
                maxd.set(node.getDepth());
            }
        }
        currentY.add(nodelist.getHeight());
        currentY.add(nodelist.getDepth());
        currentY.subtract(maxd);
    }

    // ----------------------------------------------
    // ----------------------------------------------
    // ----------------------------------------------
    // ----------------------------------------------

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitAdjust(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitAdjust(final Object value, final Object value2) {

        //        Element element = new Element("adjust");
        //        AdjustNode node = (AdjustNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitAfterMath(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitAfterMath(final Object value, final Object value2) {

        //        Element element = new Element("aftermath");
        //        AfterMathNode node = (AfterMathNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitAlignedLeaders(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitAlignedLeaders(final Object value, final Object value2) {

        //        Element element = new Element("alignedleaders");
        //        AlignedLeadersNode node = (AlignedLeadersNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitBeforeMath(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitBeforeMath(final Object value, final Object value2) {

        //        Element element = new Element("beforemath");
        //        BeforeMathNode node = (BeforeMathNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitCenteredLeaders(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitCenteredLeaders(final Object value, final Object value2) {

        //        Element element = new Element("centeredleaders");
        //        CenteredLeadersNode node = (CenteredLeadersNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitChar(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitChar(final Object value, final Object value2) {

        //        Element element = new Element("char");
        //        CharNode node = (CharNode) value;
        //        UnicodeChar uc = node.getCharacter();
        //        currentX.add(node.getWidth());
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitDiscretionary(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitDiscretionary(final Object value, final Object value2) {

        //        Element element = new Element("discretionary");
        //        DiscretionaryNode node = (DiscretionaryNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitExpandedLeaders(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitExpandedLeaders(final Object value, final Object value2) {

        //        Element element = new Element("expandedleaders");
        //        ExpandedLeadersNode node = (ExpandedLeadersNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitGlue(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitGlue(final Object value, final Object value2) {

        //        Element element = new Element("glue");
        //        GlueNode node = (GlueNode) value;
        //        currentX.add(node.getWidth());
        //        currentY.add(node.getHeight());
        //        currentY.add(node.getDepth());
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitHorizontalList(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitHorizontalList(final Object value, final Object value2)
            throws GeneralException {

        Element element = new Element("horizontallist");
        HorizontalListNode node = (HorizontalListNode) value;
        setBaseline(node);

        Dimen saveX = new Dimen(currentX);
        Dimen saveY = new Dimen(currentY);

        NodeIterator it = node.iterator();
        while (it.hasNext()) {
            state = HORIOZONTAL;
            Node newnode = it.next();
            Object o = newnode.visit(this, node);
            if (o instanceof Element) {
                element.addContent((Element) o);
            }
        }
        currentX.set(saveX);
        currentY.set(saveY);
        currentX.add(node.getWidth());

        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitInsertion(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitInsertion(final Object value, final Object value2) {

        //        Element element = new Element("insertion");
        //        InsertionNode node = (InsertionNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitKern(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitKern(final Object value, final Object value2) {

        //        Element element = new Element("kern");
        //        KernNode node = (KernNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitLigature(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitLigature(final Object value, final Object value2) {

        //        Element element = new Element("ligature");
        //        LigatureNode node = (LigatureNode) value;
        //        Node first = node.getFirst();
        //        Node second = node.getSecond();
        //        if (first != null) {
        //            Element e = getNodeElement(first);
        //            if (e != null) {
        //                element.addContent(e);
        //            }
        //        }
        //        if (second != null) {
        //            Element e = getNodeElement(second);
        //            if (e != null) {
        //                element.addContent(e);
        //            }
        //        }
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitMark(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitMark(final Object value, final Object value2) {

        //        Element element = new Element("mark");
        //        MarkNode node = (MarkNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitPenalty(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitPenalty(final Object value, final Object value2) {

        //        Element element = new Element("penalty");
        //        PenaltyNode node = (PenaltyNode) value;
        //        element.setAttribute("penalty", String.valueOf(node.getPenalty()));
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitRule(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitRule(final Object value, final Object value2) {

        //        Element element = new Element("rule");
        //        RuleNode node = (RuleNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitSpace(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitSpace(final Object value, final Object value2) {

        //        Element element = new Element("space");
        //        SpaceNode node = (SpaceNode) value;
        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitVerticalList(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitVerticalList(final Object value, final Object value2)
            throws GeneralException {

        Element element = new Element("verticallist");
        VerticalListNode node = (VerticalListNode) value;

        Dimen saveX = new Dimen(currentX);
        Dimen saveY = new Dimen(currentY);

        NodeIterator it = node.iterator();
        while (it.hasNext()) {
            state = VERTICAL;
            Node newnode = it.next();

            Object o = newnode.visit(this, node);

            if (o instanceof Element) {
                element.addContent((Element) o);
            }
        }
        currentX.set(saveX);
        currentY.set(saveY);
        currentY.add(node.getDepth());
        currentY.add(node.getHeight());

        return null;
    }

    /**
     * @see de.dante.extex.typesetter.NodeVisitor#visitWhatsIt(java.lang.Object,
     * java.lang.Object)
     */
    public Object visitWhatsIt(final Object value, final Object value2) {

        //        Element element = new Element("whatsit");
        //        WhatsItNode node = (WhatsItNode) value;
        return null;
    }
}