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

package de.dante.util.font;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import de.dante.extex.font.TTFReader;

/**
 * Convert a TTF-file to a EFM-file
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.2 $
 */
public class TTF2EFM {

    /**
     * main
     * @param args  the commandlinearguments
     * @throws IOException ...
     */
    public static void main(final String[] args) throws IOException {

        if (args.length != 2) {
            System.err
                    .println("java de.dante.util.font.TTF2EFM <ttf-file> <efm-file>");
            System.exit(1);
        }

        File ttffile = new File(args[0]);
        File efmfile = new File(args[1]);

        TTFReader ttfr = new TTFReader(ttffile);

        // write to efm-file
        XMLOutputter xmlout = new XMLOutputter("   ", true);
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(efmfile), 0x8000);
        Document doc = new Document(ttfr.getFontMetric());
        xmlout.output(doc, out);
        out.close();
    }
}
