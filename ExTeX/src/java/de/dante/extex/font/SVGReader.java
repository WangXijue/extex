/*
 * Copyright (C) 2004 The ExTeX Group
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

package de.dante.extex.font;

import java.io.IOException;
import java.io.InputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class read a SVG-file.
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.3 $
 */
// TODO incomplete
public class SVGReader implements FontMetric {

    /**
     * efm-filename
     */
    private String efmname;

    /**
     * defaultsize
     */
    private String defaultsize;

    /**
     * init
     * @param svgin        Stream for Reading the afm-file
     * @param efmname      the name of the efmfile
     * @param defaultsize  the defaultsize of the font
     * @throws IOException ...
     * @throws JDOMException ...
     */
    public SVGReader(final InputStream svgin, final String efmname,
            final String defaultsize) throws IOException, JDOMException {

        this.efmname = efmname;
        this.defaultsize = defaultsize;

        readSVGFile(svgin);
        svgin.close();
    }

    /**
     * reads the svg-file and create e efm-element
     * @param in    the inputstream
     * @throws IOException ...
     */
    private void readSVGFile(final InputStream in) throws IOException,
            JDOMException {

        // create a document with SAXBuilder (without validate)
        SAXBuilder builder = new SAXBuilder(false);
        Document doc = builder.build(in);

        efmelement = new Element("fontgroup");

        //SVGDocument svgdoc = (SVGDocument)doc;
        // TODO incomplete
        System.err.println(doc);

    }

    /**
     * The efm-element
     */
    private Element efmelement;

    /**
     * @see de.dante.util.font.FontMetric#getFontMetric()
     */
    public Element getFontMetric() {
        return efmelement;
    }
}