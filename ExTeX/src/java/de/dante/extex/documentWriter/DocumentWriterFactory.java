/*
 * Copyright (C) 2003-2005 The ExTeX Group and individual authors listed below
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
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

package de.dante.extex.documentWriter;

import java.io.FileNotFoundException;
import java.io.OutputStream;

import de.dante.util.configuration.Configuration;
import de.dante.util.configuration.ConfigurationException;
import de.dante.util.file.OutputFactory;
import de.dante.util.framework.AbstractFactory;

/**
 * This is the factory to provide an instance of a document writer.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.16 $
 */
public class DocumentWriterFactory extends AbstractFactory {

    /**
     * Creates a new object.
     *
     * @param configuration the configuration to use for the factory
     *
     * @throws ConfigurationException in case of an error
     */
    public DocumentWriterFactory(final Configuration configuration)
            throws ConfigurationException {

        super();
        configure(configuration);
    }

    /**
     * Provide a new instance of a document writer.
     * The new instance is initiated with the sub-configuration describing it.
     * <p>
     * If the generated instance implements the interface SingleDocumentStream
     * then the method setOutputStream of this interface is invoked.
     * </p>
     * <p>
     * If the generated instance implements the interface MultipleDocumentStream
     * then the method setOutputStreamFactory of this interface is invoked.
     * </p>
     *
     * @param type the type of the document writer
     * @param options the dynamic access to the readable part of the context
     * @param outStream the firts and primary output stream or
     *  <code>null</code> if none given
     * @param outFactory the factory for further output streams
     * @param basename the base name of the output stream
     *
     * @return the new instance
     *
     * @throws ConfigurationException in case of a configuration problem
     * @throws FileNotFoundException in case of a problem opening the output
     *  stream
     */
    public DocumentWriter newInstance(final String type,
            final DocumentWriterOptions options, final OutputStream outStream,
            final OutputFactory outFactory, final String basename)
            throws FileNotFoundException,
                ConfigurationException {

        DocumentWriter documentWriter = (DocumentWriter) createInstance(type,
                DocumentWriter.class, DocumentWriterOptions.class, options);

        if (documentWriter instanceof SingleDocumentStream) {
            OutputStream stream = (outStream != null
                    ? outStream
                    : outFactory.createOutputStream(basename, documentWriter
                            .getExtension()));
            ((SingleDocumentStream) documentWriter).setOutputStream(stream);
        }
        if (documentWriter instanceof MultipleDocumentStream) {
            ((MultipleDocumentStream) documentWriter)
                    .setOutputStreamFactory(outFactory);
        }
        return documentWriter;
    }

}