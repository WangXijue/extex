/*
 * Copyright (C) 2003-2004 Gerd Neugebauer
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
package de.dante.extex.main;

import java.io.IOException;

import de.dante.extex.i18n.GeneralHelpingException;
import de.dante.extex.i18n.Messages;
import de.dante.extex.interpreter.ErrorHandler;
import de.dante.extex.interpreter.Interaction;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.logging.Logger;
import de.dante.extex.scanner.Token;
import de.dante.util.GeneralException;
import de.dante.util.Locator;
import de.dante.util.configuration.ConfigurationException;

/**
 * This is a simple implementation of the error handler interacting with the
 * user on the command line like TeX does.
 * <p>
 * The GeneralHelpingException is capable of carrying a name and two arguments
 * for the error message. This class can be queried to provide additional help
 * concerning the error at hand. 
 * See {@link GeneralHelpingException GeneralHelpingException} for details.
 * </p>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.6 $
 */
public class ErrorHandlerImpl implements ErrorHandler {
    /** The logger to write a protocol of the interaction to.
     *  Note that the error has already been logged when this handler is
     *  invoked. 
     */
    private Logger logger;

    /**
     * Creates a new object.
     *
     * @param logger the logger for the interaction logging
     */
    public ErrorHandlerImpl(final Logger logger) {
        super();
        this.logger = logger;
    }

    /**
     * @see de.dante.extex.interpreter.ErrorHandler#handleError(de.dante.extex.i18n.GeneralException,
     *      de.dante.extex.scanner.Token,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.interpreter.context.Context)
     */
    public boolean handleError(final GeneralException exception, final Token t,
        final TokenSource source, final Context context)
        throws GeneralException {
        Interaction interaction = context.getInteraction();
        GeneralException e = exception;

        //TODO: introduce an InteractionVisitor and eliminate the ugly switch
        if (interaction == Interaction.BATCHMODE) {
            return true;
        } else if (interaction == Interaction.NONSTOPMODE) {
            return true;
        } else if (interaction == Interaction.SCROLLMODE) {
            return false;
        }

        //if (interaction == Interaction.ERRORSTOPMODE) {
        Locator locator = source.getLocator();
        logger.severe("\n\n" + locator.getLine() + "\n"
                      + point(locator.getLinePointer()));

        logger.severe("\n! " + e.getMessage() + "\n");
        String file = locator.getFilename();
        logger.severe("<" + (file == null ? "" : file) + "> \n");
        logger.severe("l." + Integer.toString(locator.getLineno()) + " \n");

        // Interact with the user in case of an error
        try {
            boolean firstHelp = true;

            for (; ; ) {
                logger.severe(Messages.format("ErrorHandler.Prompt"));

                String line = readLine();
                if ( line == null ) {
                    throw new GeneralHelpingException("TTP.EOFonTerm");
                }
                logger.config(line);

                if (line.equals("")) {
                    return true;
                } else {
                    switch (line.charAt(0)) {
                    case '0':
                    case '9':
                    case '8':
                    case '7':
                    case '6':
                    case '5':
                    case '4':
                    case '3':
                    case '2':
                    case '1':
                        int count = line.charAt(0) - '0';
                        if (line.length() > 1
                            && Character.isDigit(line.charAt(1))) {
                            count = count * 10 + line.charAt(1) - '0';
                        }
                        while (count-- > 0) {
                            source.getToken();
                        }
                        firstHelp = false;
                        e = new GeneralHelpingException("");
                        break;
                    case 'd':
                    case 'D':
                        //TODO: support debug? TTP[84] TTP[1338]
                        break;
                    case 'e':
                    case 'E':
                        //TODO: support edit? TTP[84]
                        break;
                    case 'i':
                    case 'I':
                        source.addStream(source.getTokenStreamFactory()
                            .newInstance(line.substring(1), "ISO-8859-1"));
                        //TODO: better guess for the encoding?
                        break;
                    case 'h':
                    case 'H':

                        String help;

                        if (!firstHelp) {
                            help = Messages.format("ErrorHandler.noMoreHelp");
                        } else if ((help = e.getHelp()) == null) {
                            help = Messages.format("ErrorHandler.noHelp");
                        }

                        firstHelp = false;
                        logger.severe(help + "\n");
                        break;
                    case 'q':
                    case 'Q':
                        context.setInteraction(Interaction.BATCHMODE, true);
                        logger.info(Messages.format("ErrorHandler.batchmode")
                                    + "\n");
                        return true;
                    case 'r':
                    case 'R':
                        context.setInteraction(Interaction.NONSTOPMODE, true);
                        logger.info(Messages.format("ErrorHandler.nonstopmode")
                                    + "\n");
                        return true;
                    case 's':
                    case 'S':
                        context.setInteraction(Interaction.SCROLLMODE, true);
                        logger.info(Messages.format("ErrorHandler.scrollmode")
                                    + "\n");
                        return true;
                    case 'x':
                    case 'X':
                        return false;
                    default:
                        logger.severe(Messages.format("ErrorHandler.help")
                                      + "\n");
                    }
                }
            }
        } catch (IOException e1) {
            throw new GeneralException(e1);
        } catch (ConfigurationException e1) {
            throw new GeneralException(e1);
        } catch (GeneralException e1) {
            throw e1;
        }
    }

    /**
     * ...
     *
     * @return ...
     *
     * @throws IOException in case of an IO error
     */
    private String readLine() throws IOException {
        StringBuffer sb = new StringBuffer();

        for ( int c = System.in.read(); c!='\n'; c= System.in.read() ) {
            if ( c < 0 ) {
                return null;
            }
            sb.append((char) c);
        }

        return sb.toString();
    }
    
    private String point(final int n) {
        StringBuffer sb = new StringBuffer();
        for ( int i=n;i>=0;i-- ) {
            sb.append('_');
        }
        sb.append('^');
        return sb.toString();
    }
}
