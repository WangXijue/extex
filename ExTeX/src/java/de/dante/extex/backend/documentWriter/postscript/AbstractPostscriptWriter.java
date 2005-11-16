/*
 * Copyright (C) 2005 The ExTeX Group and individual authors listed below
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

package de.dante.extex.backend.documentWriter.postscript;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.dante.extex.backend.documentWriter.DocumentWriter;
import de.dante.extex.backend.documentWriter.MultipleDocumentStream;
import de.dante.extex.backend.documentWriter.OutputStreamFactory;
import de.dante.extex.backend.documentWriter.exception.DocumentWriterException;
import de.dante.extex.backend.documentWriter.postscript.util.FontManager;
import de.dante.extex.backend.documentWriter.postscript.util.HeaderManager;
import de.dante.extex.backend.documentWriter.postscript.util.PsBasicConverter;
import de.dante.extex.backend.documentWriter.postscript.util.PsBoxConverter;
import de.dante.extex.backend.documentWriter.postscript.util.PsConverter;
import de.dante.extex.color.ColorAware;
import de.dante.extex.color.ColorConverter;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.font.Font;
import de.dante.extex.typesetter.type.NodeList;
import de.dante.extex.typesetter.type.page.Page;
import de.dante.util.exception.GeneralException;
import de.dante.util.framework.configuration.Configurable;
import de.dante.util.framework.configuration.Configuration;
import de.dante.util.framework.configuration.exception.ConfigurationException;
import de.dante.util.resource.ResourceConsumer;
import de.dante.util.resource.ResourceFinder;

/**
 * This is the abstract base class for document writers producing PostScript
 * code. Here some utility methods of general nature are collected.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractPostscriptWriter
        implements
            DocumentWriter,
            Configurable,
            MultipleDocumentStream,
            ResourceConsumer,
            ColorAware {

    /**
     * The field <tt>boxed</tt> contains the indicator whether the box-only
     * converter should be used.
     */
    private boolean boxed;

    /**
     * The field <tt>colorConverter</tt> contains the color converter as set
     * from the managing instance.
     */
    private ColorConverter colorConverter = null;

    /**
     * The field <tt>finder</tt> contains the resource finder as set from the
     * managing instance.
     */
    private ResourceFinder finder = null;

    /**
     * The field <tt>parameter</tt> contains the map for parameters.
     */
    protected Map parameter = new HashMap();

    /**
     * The field <tt>writerFactory</tt> contains the output stream factory.
     */
    private OutputStreamFactory writerFactory;

    /**
     * Creates a new object.
     */
    public AbstractPostscriptWriter() {

        super();
        parameter.put("Creator", "ExTeX.psWriter");
        parameter.put("Title", "<title>");
        parameter.put("PageOrder", "Ascend");
    }

    /**
     * @see de.dante.util.framework.configuration.Configurable#configure(
     *      de.dante.util.configuration.Configuration)
     */
    public void configure(final Configuration config)
            throws ConfigurationException {

        String b = config.getAttribute("boxed");
        boxed = (b == null ? false : Boolean.valueOf(b).booleanValue());
    }

    /**
     * Getter for a named parameter.
     *
     * @param name the name of the parameter
     *
     * @return the value of the parameter or <code>null</code> if none exists
     */
    protected String getParameter(final String name) {

        return (String) parameter.get(name);
    }

    /**
     * Create a PostScript converter.
     *
     * @param headerManager the header manager
     *
     * @return the new converter
     *
     * @throws IOException in case of an IO error
     */
    protected PsConverter makeConverter(final HeaderManager headerManager)
            throws IOException {

        Dimen width = new Dimen(Dimen.ONE_INCH);
        width.multiply(2100, 254); // A4 paper
        Dimen height = new Dimen(Dimen.ONE_INCH);
        height.multiply(2970, 254); // A4 paper

        PsConverter converter;
        if (boxed) {
            converter = new PsBoxConverter(width, height);
        } else {
            converter = new PsBasicConverter(width, height);
        }
        if (converter instanceof ColorAware) {
            ((ColorAware) converter).setColorConverter(colorConverter);
        }
        if (converter instanceof ResourceConsumer) {
            ((ResourceConsumer) converter).setResourceFinder(finder);
        }
        converter.init(headerManager);
        return converter;
    }

    /**
     * Acquire a new output stream.
     *
     * @param type the type for the reference to the configuration file
     *
     * @return the new output stream
     *
     * @throws DocumentWriterException in case of an error
     */
    protected OutputStream newOutputStream(final String type)
            throws DocumentWriterException {

        return writerFactory.getOutputStream(type);
    }

    /**
     * @see de.dante.extex.color.ColorAware#setColorConverter(
     *      de.dante.extex.color.ColorConverter)
     */
    public void setColorConverter(final ColorConverter converter) {

        this.colorConverter = converter;
    }

    /**
     * @see de.dante.extex.documentWriter.MultipleDocumentStream#setOutputStreamFactory(
     *      de.dante.extex.documentWriter.OutputStreamFactory)
     */
    public void setOutputStreamFactory(final OutputStreamFactory factory) {

        this.writerFactory = factory;
    }

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#setParameter(
     *      java.lang.String,
     *      java.lang.String)
     */
    public void setParameter(final String name, final String value) {

        parameter.put(name, value);
    }

    /**
     * @see de.dante.util.resource.ResourceConsumer#setResourceFinder(
     *      de.dante.util.resource.ResourceFinder)
     */
    public void setResourceFinder(final ResourceFinder resourceFinder) {

        this.finder = resourceFinder;
    }

    /**
     * This is the entry point for the document writer. Here it receives a
     * complete node list to be sent to the output writer. It can be assumed
     * that all values for width, height, and depth of the node lists are
     * properly filled. Thus all information should be present to place the
     * ink on the paper.
     *
     * @param nodes the nodes to put on the paper
     * @param width the width of the page
     * @param height the height of the page
     *
     * @return the number of pages shipped out in this step. This is usually 1.
     *  But it can also be 0 if the page is skipped or a greater number is the
     *  page is split.
     *
     * @throws IOException in case of an IO error
     * @throws GeneralException in case of another error
     */
    protected abstract int shipout(final NodeList nodes, final Dimen width,
            final Dimen height) throws GeneralException, IOException;

    /**
     * @see de.dante.extex.documentWriter.DocumentWriter#shipout(
     *      de.dante.extex.typesetter.type.NodeList)
     */
    public final int shipout(final Page page)
            throws GeneralException,
                IOException {

        NodeList nodes = page.getNodes();
        Dimen width = new Dimen(Dimen.ONE_INCH);
        width.multiply(2100, 254); // A4 paper
        Dimen height = new Dimen(Dimen.ONE_INCH);
        height.multiply(2970, 254); // A4 paper

        return shipout(nodes, width, height);
    }

    /**
     * Write a meta comment according to the Document Structuring Conventions.
     *
     * @param stream the target stream to write to
     * @param name the name of the DSC comment
     *
     * @throws IOException in case of an error during writing
     */
    protected void writeDsc(final OutputStream stream, final String name)
            throws IOException {

        stream.write('%');
        stream.write('%');
        stream.write(name.getBytes());
        stream.write('\n');
    }

    /**
     * Write a meta comment according to the Document Structuring Conventions.
     *
     * @param stream the target stream to write to
     * @param name the name of the DSC comment
     * @param value the value of the DSC comment
     *
     * @throws IOException in case of an error during writing
     */
    protected void writeDsc(final OutputStream stream, final String name,
            final String value) throws IOException {

        stream.write('%');
        stream.write('%');
        stream.write(name.getBytes());
        stream.write(':');
        stream.write(' ');
        stream.write(value.getBytes());
        stream.write('\n');

    }

    /**
     * Write a meta comment according to the Document Structuring Conventions
     * containing the <tt>DocumentFonts</tt>.
     *
     * @param stream the target stream to write to
     * @param fontManager the font manager to ask for the fonts
     *
     * @throws IOException in case of an error during writing
     */
    protected void writeFonts(final OutputStream stream,
            final FontManager fontManager) throws IOException {

        stream.write("%%DocumentFonts:".getBytes());
        Font[] fonts = fontManager.listFonts();
        for (int i = 0; i < fonts.length; i++) {
            stream.write(' ');
            stream.write(fonts[i].getFontName().getBytes());
        }
        stream.write('\n');
    }
}
