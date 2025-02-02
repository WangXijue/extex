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

package de.dante.extex.backend.outputStream;

import java.io.OutputStream;

import de.dante.extex.backend.documentWriter.exception.DocumentWriterException;

/**
 * This is the factory for new OutputStreams.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.3 $
 */
public interface OutputStreamFactory {

    /**
     * Getter for a new OutputStream.
     *
     * @param name the name segment to characterize the stream externally.
     *  This segment might be used as part of the output file. If the name is
     *  <code>null</code> then it is treated as not given at all.
     * @param type the type of the stream to acquire. In general this should
     *  correspond to the extension of a file of this type
     *
     * @return the new OutputStream
     * @throws DocumentWriterException in case of an error
     */
    OutputStream getOutputStream(String name, String type)
            throws DocumentWriterException;

    /**
     * Register an observer which is invoked to notify about any output
     * stream requested via a call to getOututStream(),
     *
     * @param observer the observers to register
     */
    public void register(final OutputStreamObserver observer);

    /**
     * Setter for the default extension.
     * The default extension is used when the type specified is <code>null</code>.
     *
     * @param extension the default extension
     */
    void setExtension(String extension);

}
