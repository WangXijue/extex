/*
 * Copyright (C) 2005-2006 The ExTeX Group
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

package de.dante.extex.backend.documentWriter.pdf;

import java.io.IOException;
import java.io.OutputStream;

import de.dante.extex.backend.documentWriter.DocumentWriter;
import de.dante.extex.backend.documentWriter.DocumentWriterOptions;
import de.dante.extex.backend.documentWriter.MultipleDocumentStream;
import de.dante.extex.backend.documentWriter.exception.DocumentWriterException;
import de.dante.extex.backend.documentWriter.exception.DocumentWriterIOException;
import de.dante.extex.backend.outputStream.OutputStreamFactory;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.typesetter.type.NodeList;
import de.dante.extex.typesetter.type.NodeVisitor;
import de.dante.extex.typesetter.type.page.Page;
import de.dante.util.Unit;
import de.dante.util.exception.GeneralException;
import de.dante.util.framework.configuration.Configuration;

/**
 * Implementation of a pdf document writer.
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.4 $
 */
public class PdfSinglePageDocumentWriter
        implements
            DocumentWriter,
            MultipleDocumentStream {

    /**
     * width A4 in bp.
     */
    private static final int WIDTH_A4_BP = 595;

    /**
     * height A4 in bp.
     */
    private static final int HEIGHT_A4_BP = 842;

    /**
     * the output factory.
     */
    private OutputStreamFactory outFactory = null;

    /**
     * the number of page which are shipped out.
     */
    private int shippedPages = 0;

    /**
     * document writer options.
     */
    private DocumentWriterOptions docoptions;

    //    /**
    //     * the pdf-dokument
    //     */
    //    private PdfDocument document;
    //
    //    /**
    //     * the pdf content
    //     */
    //    private PdfContentByte cb;

    /**
     * Creates a new object.
     * @param cfg       the configuration
     * @param options   the options
     */
    public PdfSinglePageDocumentWriter(final Configuration cfg,
            final DocumentWriterOptions options) {

        super();
        docoptions = options;

        //        if (cfg != null) {
        //            String tmp = cfg.getAttribute("encoding");
        //            if (tmp != null && !tmp.equals("")) {
        //                encoding = tmp;
        //            }
        //        }
    }

    /**
     * @see de.dante.extex.backend.documentWriter.DocumentWriter#close()
     */
    public void close() throws DocumentWriterIOException {

        // do nothing
    }

    /**
     * @see de.dante.extex.backend.documentWriter.DocumentWriter#getExtension()
     */
    public String getExtension() {

        return "pdf";
    }

    /**
     * @see de.dante.extex.backend.documentWriter.DocumentWriter#getPages()
     */
    public int getPages() {

        return shippedPages;
    }

    /**
     * @see de.dante.extex.backend.documentWriter.MultipleDocumentStream#setOutputStreamFactory(
     *      de.dante.extex.backend.outputStream.OutputStreamFactory)
     */
    public void setOutputStreamFactory(final OutputStreamFactory writerFactory) {

        outFactory = writerFactory;
    }

    /**
     * @see de.dante.extex.backend.documentWriter.DocumentWriter#setParameter(
     *      java.lang.String,
     *      java.lang.String)
     */
    public void setParameter(final String name, final String value) {

    }

    /**
     * paper width.
     */
    private Dimen paperwidth = new Dimen();

    /**
     * paper height.
     */
    private Dimen paperheight = new Dimen();

    /**
     * current x position.
     */
    private Dimen currentX = new Dimen();

    /**
     * current y position.
     */
    private Dimen currentY = new Dimen();

    /**
     * the pdf node visitor.
     */
    private NodeVisitor nodeVisitor;

    /**
     * @see de.dante.extex.backend.documentWriter.DocumentWriter#shipout(
     *      de.dante.extex.typesetter.type.NodeList)
     */
    public int shipout(final Page page) throws DocumentWriterException {

        NodeList nodes = page.getNodes();
        try {

            // get the output from the factory
            OutputStream out = outFactory.getOutputStream(null, null);

            // create a pdf document
            //            document = new PdfDocument(out);
            //            document.open();
            //            cb = document.getDirectContent();
            //            visitor = new PdfNodeVisitor(cb, currentX, currentY);

            // TeX primitives should set the papersize in any way:
            // o \paperwidth   / \paperheight,
            // o \pdfpagewidth / \pdfpageheight <-- pdfTeX
            // o \mediawidth   / \mediaheight   <-- VTeX
            Unit.setDimenFromCM(paperwidth, WIDTH_A4_BP);
            Unit.setDimenFromCM(paperheight, HEIGHT_A4_BP);
            if (docoptions != null) {
                Dimen w = (Dimen) docoptions.getDimenOption("paperwidth");
                Dimen h = (Dimen) docoptions.getDimenOption("paperheight");
                if (!(h.getValue() == 0 || w.getValue() == 0)) {
                    paperheight.set(h);
                    paperwidth.set(w);
                }
            }

            // set page size and margin
            //            Rectangle pagesize = createRectangle(paperwidth, paperheight);
            //            document.setPageSize(pagesize);

            // set start point
            currentX.set(Dimen.ONE_INCH);
            currentY.set(Dimen.ONE_INCH);

            // Changes the default coordinate system so that the origin
            // is in the upper left corner instead of the lower left corner.
            //            cb.concatCTM(1f, 0f, 0f, -1f, 0f, pagesize.height());
            //
            //            // -------------------------------------
            //            cb.setColorStroke(Color.RED);
            //            cb.moveTo(0, 0);
            //            cb.lineTo(0, pagesize.height());
            //            cb.stroke();
            //            cb.setColorStroke(Color.GREEN);
            //            cb.moveTo(0, 0);
            //            cb.lineTo(pagesize.width(), 0);
            //            cb.stroke();
            //            cb.setColorStroke(Color.BLUE);
            //            cb.moveTo(pagesize.width(), 0);
            //            cb.lineTo(pagesize.width(), pagesize.height());
            //            cb.stroke();
            //            cb.setColorStroke(Color.YELLOW);
            //            cb.moveTo(0, pagesize.height());
            //            cb.lineTo(pagesize.width(), pagesize.height());
            //            cb.stroke();

            //            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
            //                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            //            cb.beginText();
            //            cb.setColorFill(Color.CYAN);
            //            cb.setFontAndSize(bf, 12);
            //            cb
            //                    .showTextAligned(PdfContentByte.ALIGN_LEFT, "\u003A", 100,
            //                            100, 0);
            //            cb.endText();

            // -----------------------------

            nodes.visit(nodeVisitor, nodes);

            // close the page output
            out.close();
            shippedPages++;

        } catch (GeneralException e) {
            throw new DocumentWriterException(e);
            //        } catch (PdfException e) {
            //            throw new DocumentWriterPdfDocumentException(e);
        } catch (IOException e) {
            throw new DocumentWriterIOException(e);
        }
        return 1;
    }

    //    /**
    //     * Create a new <code>Rectangle</code>.
    //     * @param w the width as Dimen
    //     * @param h the height as Dimen
    //     * @return Returns the new Rectangle
    //     */
    //    private Rectangle createRectangle(final Dimen w, final Dimen h) {
    //
    //        return new Rectangle((float) Unit.getDimenAsBP(w), (float) Unit
    //                .getDimenAsBP(h));
    //    }

    //    /**
    //     * return the node element
    //     * @param node      the node
    //     * @return Returns the node-element
    //     */
    //    private Element getNodeElement(final Node node) {
    //
    //        Element element = null;
    //        try {
    //            Object o = node.visit(this, node);
    //            if (o != null) {
    //                if (o instanceof Element) {
    //                    element = (Element) o;
    //                }
    //            }
    //        } catch (GeneralException e) {
    //            e.printStackTrace();
    //        }
    //        return element;
    //    }

}
