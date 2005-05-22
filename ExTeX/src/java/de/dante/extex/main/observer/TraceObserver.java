/*
 * Copyright (C) 2004-2005 The ExTeX Group and individual authors listed below
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

package de.dante.extex.main.observer;

import java.util.logging.Logger;

import de.dante.extex.interpreter.observer.expand.ExpandObserver;
import de.dante.extex.scanner.type.Token;

/**
 * Observer for tracing macros. The macro is written to the log file preceeded
 * by a <tt>&gt;</tt> mark and a space character.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.5 $
 */
public class TraceObserver implements ExpandObserver {

    /**
     * The field <tt>logger</tt> contains the logger for output
     */
    private Logger logger;

    /**
     * Creates a new object.
     *
     * @param theLogger the logger for potential output
     */
    public TraceObserver(final Logger theLogger) {

        super();
        this.logger = theLogger;
    }

    /**
     * This method is meant to be invoked just before a token is expanded.
     *
     * @param token the token to be expanded
     */
    public void update(final Token token) {

        logger.fine("> " + token.toString() + ".\n");
    }
}