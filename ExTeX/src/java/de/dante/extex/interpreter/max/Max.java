/*
 * Copyright (C) 2003-2004 The ExTeX Group and individual authors listed below
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

package de.dante.extex.interpreter.max;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Iterator;

import de.dante.extex.font.FontFactory;
import de.dante.extex.i18n.HelpingException;
import de.dante.extex.i18n.PanicException;
import de.dante.extex.interpreter.ErrorHandler;
import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.FlagsImpl;
import de.dante.extex.interpreter.Interaction;
import de.dante.extex.interpreter.Interpreter;
import de.dante.extex.interpreter.Namespace;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.context.ContextFactory;
import de.dante.extex.interpreter.loader.LoaderException;
import de.dante.extex.interpreter.loader.SerialLoader;
import de.dante.extex.interpreter.type.Code;
import de.dante.extex.interpreter.type.ExpandableCode;
import de.dante.extex.interpreter.type.InitializableCode;
import de.dante.extex.interpreter.type.tokens.Tokens;
import de.dante.extex.scanner.ActiveCharacterToken;
import de.dante.extex.scanner.Catcode;
import de.dante.extex.scanner.CodeToken;
import de.dante.extex.scanner.ControlSequenceToken;
import de.dante.extex.scanner.CrToken;
import de.dante.extex.scanner.LeftBraceToken;
import de.dante.extex.scanner.LetterToken;
import de.dante.extex.scanner.MacroParamToken;
import de.dante.extex.scanner.MathShiftToken;
import de.dante.extex.scanner.OtherToken;
import de.dante.extex.scanner.RightBraceToken;
import de.dante.extex.scanner.SpaceToken;
import de.dante.extex.scanner.SubMarkToken;
import de.dante.extex.scanner.SupMarkToken;
import de.dante.extex.scanner.TabMarkToken;
import de.dante.extex.scanner.Token;
import de.dante.extex.scanner.TokenFactory;
import de.dante.extex.scanner.TokenFactoryImpl;
import de.dante.extex.scanner.TokenVisitor;
import de.dante.extex.scanner.stream.TokenStream;
import de.dante.extex.scanner.stream.TokenStreamFactory;
import de.dante.extex.typesetter.Mode;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.GeneralException;
import de.dante.util.Switch;
import de.dante.util.configuration.Configuration;
import de.dante.util.configuration.ConfigurationClassNotFoundException;
import de.dante.util.configuration.ConfigurationException;
import de.dante.util.configuration.ConfigurationInstantiationException;
import de.dante.util.configuration.ConfigurationMissingAttributeException;
import de.dante.util.configuration.ConfigurationMissingException;
import de.dante.util.configuration.ConfigurationWrapperException;
import de.dante.util.observer.NotObservableException;
import de.dante.util.observer.Observable;
import de.dante.util.observer.Observer;
import de.dante.util.observer.ObserverList;
import de.dante.util.observer.SwitchObserver;
import de.dante.util.resource.ResourceFinder;

/**
 * This is a reference implementation for a <b>MA</b>cro e<b>X</b>pander. The
 * macro expander is the core engine driving ExTeX.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.34 $
 */
public class Max extends Moritz
        implements
            Interpreter,
            TokenSource,
            Observable,
            TokenVisitor {

    /**
     * The constant <tt>CLASS_ATTRIBUTE</tt> contains the name of the attribute
     * to be used to extract the class name for the configuration.
     */
    private static final String CLASS_ATTRIBUTE = "class";

    /**
     * The constant <tt>MAX_ERRORS_DEFAULT</tt> contains the default value for
     * maximal allowed number of errors after which the ExTeX run is terminated
     * automatically.
     */
    private static final int MAX_ERRORS_DEFAULT = 100;

    /**
     * The constant <tt>MINUTES_PER_HOUR</tt> contains the number of minutes
     * per hour.
     */
    private static final int MINUTES_PER_HOUR = 60;

    /**
     * The constant <tt>NAME_ATTRIBUTE</tt> contains the name of the attribute
     * holding the name of the primitive to define.
     */
    private static final String NAME_ATTRIBUTE = "name";

    /**
     * The field <tt>calendar</tt> contains the time and date when ExTeX has
     * been started.
     */
    private Calendar calendar = Calendar.getInstance();

    /**
     * The field <tt>context</tt> contains the processing context. Here nearly
     * all relevant information can be found.
     */
    private Context context = null;

    /**
     * The field <tt>errorCount</tt> contains the count for the number of
     * errors already encountered.
     */
    private int errorCount = 0; //TODO: make proper use of the error count

    /**
     * The error handler is invoked whenever an error is detected. If none is
     * registered then the default behavior is shown.
     */
    private ErrorHandler errorHandler = null;

    /**
     * The field <tt>everyRun</tt> contains the String to be inserted at the
     * beginning of each run. It can be <code>null</code> to denote that
     * nothing should be inserted.
     */
    private Configuration everyRun = null;

    /**
     * The field <tt>maxErrors</tt> contains the number of errors after which
     * the run is terminated. This value can be overwritten in the
     * configuration.
     */
    private int maxErrors = MAX_ERRORS_DEFAULT;

    /**
     * This observer list is used for the observers which are registered to
     * receive a notification when an error occurs. The argument is the
     * exception encountered.
     */
    private ObserverList observersError = new ObserverList();

    /**
     * This observer list is used for the observers which are registered to
     * receive a notification when a new token is about to be expanded. The
     * argument is the token to be expanded.
     */
    private ObserverList observersExpand = new ObserverList();

    /**
     * This observer list is used for the observers which are registered to
     * receive a notification when a macro is expanded.
     */
    private ObserverList observersMacro = new ObserverList();

    /**
     * This is the prefix for the next invocations.
     */
    private Flags prefix = new FlagsImpl();

    /**
     * The field <tt>typesetter</tt> contains the typesetter for handling
     * "left-over" material.
     */
    private Typesetter typesetter = null;

    /**
     * Creates a new object.
     */
    public Max() {

        super();
    }

    /**
     * Apply the configuration options found in the given configuration object.
     *
     * @param configuration the configuration object to consider.
     *
     * @throws ConfigurationException in case of a configuration error
     */
    public void configure(final Configuration configuration)
            throws ConfigurationException {

        super.configure(configuration);

        if (configuration == null) {
            throw new ConfigurationMissingException("Interpreter");
        }

        context = new ContextFactory(configuration.getConfiguration("Context"))
                .newInstance(null);
        context.setTokenFactory(new TokenFactoryImpl()); //TODO I hate Impls in code

        try {
            context.setInteraction(Interaction.ERRORSTOPMODE, true);
        } catch (GeneralException e) {
            throw new ConfigurationWrapperException(e);
        }

        maxErrors = configuration.getValueAsInteger("maxErrors", maxErrors);

        TokenFactory tokenFactory = context.getTokenFactory();

        Iterator iterator = configuration.iterator("primitives");

        while (iterator.hasNext()) {
            Configuration cfg = (Configuration) iterator.next();
            definePrimitives(cfg, tokenFactory);
        }

        definePrimitives(configuration, tokenFactory);

        context.setCount("day", calendar.get(Calendar.DAY_OF_MONTH), true);
        context.setCount("month", calendar.get(Calendar.MONTH), true);
        context.setCount("year", calendar.get(Calendar.YEAR), true);
        context.setCount("time", calendar.get(Calendar.HOUR_OF_DAY)
                * MINUTES_PER_HOUR + calendar.get(Calendar.MINUTE), true);

        everyRun = configuration.findConfiguration("everyjob");
    }

    /**
     * Scan a configuration and define primitives found.
     *
     * @param configuration the configuration to scan
     * @param tokenFactory the token factory to use
     *
     * @throws ConfigurationException in case of an error
     * <ul>
     *  <li>ConfigurationMissingAttributeException in case of a missing argument</li>
     *  <li>ConfigurationInstantiationException in case of an error during instantiation</li>
     *  <li>ConfigurationClassNotFoundException in case of a missing class</li>
     *  <li>ConfigurationWrapperException in case of another error which is wrapped</li>
     * </ul>
     */
    private void definePrimitives(final Configuration configuration,
            final TokenFactory tokenFactory) throws ConfigurationException {

        Iterator iterator = configuration.iterator("define");

        while (iterator.hasNext()) {
            Configuration cfg = (Configuration) iterator.next();
            String name = cfg.getAttribute(NAME_ATTRIBUTE);

            if (name == null || name.equals("")) {
                throw new ConfigurationMissingAttributeException(
                        NAME_ATTRIBUTE, cfg);
            }

            String classname = cfg.getAttribute(CLASS_ATTRIBUTE);

            if (classname == null || classname.equals("")) {
                throw new ConfigurationMissingAttributeException(
                        CLASS_ATTRIBUTE, cfg);
            }

            try {
                Code code = (Code) (Class.forName(classname).getConstructor(
                        new Class[]{String.class})
                        .newInstance(new Object[]{name}));
                context.setCode(tokenFactory.createToken(Catcode.ESCAPE, name,
                        Namespace.DEFAULT_NAMESPACE), code, true);
                if (code instanceof InitializableCode) {
                    ((InitializableCode) code).init(context, cfg.getValue());
                }
            } catch (IllegalArgumentException e) {
                throw new ConfigurationInstantiationException(e);
            } catch (SecurityException e) {
                throw new ConfigurationInstantiationException(e);
            } catch (InstantiationException e) {
                throw new ConfigurationInstantiationException(e);
            } catch (IllegalAccessException e) {
                throw new ConfigurationInstantiationException(e);
            } catch (InvocationTargetException e) {
                Throwable c = e.getCause();
                if (c != null && c instanceof ConfigurationException) {
                    throw (ConfigurationException) c;
                }
                throw new ConfigurationInstantiationException(e);
            } catch (NoSuchMethodException e) {
                throw new ConfigurationInstantiationException(e);
            } catch (ClassNotFoundException e) {
                throw new ConfigurationClassNotFoundException(classname, configuration);
            } catch (GeneralException e) {
                throw new ConfigurationWrapperException(e);
            }
        }
    }

    /**
     * This method contains the main execution loop.
     *
     * @param onceMore switch to control the termination of the execution
     *
     * @throws GeneralException in case of an error
     */
    private void execute(final Switch onceMore) throws GeneralException {

        for (Token current = getToken(); //
        current != null && onceMore.isOn(); //
        current = getToken()) {
            observersExpand.update(this, current);
            try {
                current.visit(this, null, null);
            } catch (PanicException e) {
                throw e;
            } catch (GeneralException e) {
                if (++errorCount > maxErrors) { // cf. TTP[82]
                    throw new PanicException("TTP.ErrorLimitReached", Integer
                            .toString(maxErrors));
                } else if (errorHandler != null) {
                    if (!errorHandler.handleError(e, current, this, context)) {
                        return;
                    }
                } else {
                    throw e;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new PanicException(e);
            }
        }
    }

    /**
     * @see de.dante.extex.interpreter.TokenSource#executeGroup()
     */
    public void executeGroup() throws GeneralException {

        Switch b = new Switch(true);
        context.afterGroup(new SwitchObserver(b, false));
        execute(b);
    }

    /**
     * @see de.dante.extex.interpreter.max.Moritz#expand(
     *      de.dante.extex.scanner.Token)
     */
    protected Token expand(final Token token) throws GeneralException {

        Code code;
        Token t = token;

        while (t != null) { //TODO ???
            if (t instanceof CodeToken) {
                observersMacro.update(this, t);
                code = context.getCode((CodeToken) t);
            } else {
                return t;
            }
            if (code instanceof ExpandableCode) {
                ((ExpandableCode) code).expand(prefix, context, this,
                        typesetter);
            } else {
                return t;
            }
            t = getToken();
        }
        return t;
    }

    /**
     * @see de.dante.extex.interpreter.max.Moritz#getContext()
     */
    public Context getContext() {

        return context;
    }

    /**
     * Getter for the error handler. The error handler might not be set. In this
     * case <code>null</code> is returned.
     *
     * @return the error handler currently registered
     */
    public ErrorHandler getErrorHandler() {

        return errorHandler;
    }

    /**
     * Getter for the interaction mode.
     *
     * @return the interaction mode
     */
    public Interaction getInteraction() {

        return context.getInteraction();
    }

    /**
     * @see de.dante.extex.interpreter.max.Moritz#getTypesetter()
     */
    public Typesetter getTypesetter() {

        return this.typesetter;
    }

    /**
     * Load the format from an external source.
     *
     * @param stream the stream to read the format information from
     *
     * @throws LoaderException in case that a class could not be found
     *  on the class path or a wrong class is contained in the format
     * @throws IOException in case that an IO error occurs during the reading
     *  of the format
     *
     * @see de.dante.extex.interpreter.Interpreter#loadFormat(java.io.InputStream)
     */
    public void loadFormat(final InputStream stream)
            throws IOException, LoaderException {

        SerialLoader loader = new SerialLoader();
        Context newContext = loader.load(stream);

        newContext.setCount("day", calendar.get(Calendar.DAY_OF_MONTH), true);
        newContext.setCount("month", calendar.get(Calendar.MONTH), true);
        newContext.setCount("year", calendar.get(Calendar.YEAR), true);
        newContext.setCount("time", calendar.get(Calendar.HOUR_OF_DAY)
                * MINUTES_PER_HOUR + calendar.get(Calendar.MINUTE), true);
        newContext.setFontFactory(context.getFontFactory());
        newContext.setTokenFactory(context.getTokenFactory());
        // TODO incomplete?
        context = newContext;
    }

    /**
     * This method can be used to register observers for certain events.
     *
     * The following events are currently supported:
     * <table>
     *  <tr>
     *   <th>Name</th>
     *   <th>Description</th>
     *  </tr>
     *  <tr>
     *   <td>error</td>
     *   <td>in case of an error</td>
     *  </tr>
     *  <tr>
     *   <td>expand</td>
     *   <td>in case of an expansion</td>
     *  </tr>
     *  <tr>
     *   <td>pop</td>
     *   <td>inherited from the super class {@link Moritz Moritz}</td>
     *  </tr>
     *  <tr>
     *   <td>push</td>
     *   <td>inherited from the super class {@link Moritz Moritz}</td>
     *  </tr>
     *  <tr>
     *   <td>EOF</td>
     *   <td>inherited from the super class {@link Moritz Moritz}</td>
     *  </tr>
     * </table>
     *
     * @see de.dante.util.observer.Observable#registerObserver(java.lang.String,
     *      de.dante.util.observer.Observer)
     */
    public void registerObserver(final String name, final Observer observer)
            throws NotObservableException {

        if ("expand".equals(name)) {
            observersExpand.add(observer);
        } else if ("macro".equals(name)) {
            observersExpand.add(observer);
        } else if ("error".equals(name)) {
            observersError.add(observer);
        } else {
            super.registerObserver(name, observer);
        }
    }

    /**
     * @see de.dante.extex.interpreter.Interpreter#run()
     */
    public void run() throws ConfigurationException, GeneralException {

        if (typesetter == null) {
            throw new NoTypesetterException(getClass().getName() + "#run()");
        }

        if (getTokenStreamFactory() == null) {
            throw new NoTokenStreamFactoryException(getClass().getName()
                    + "#run()");
        }

        if (everyRun != null) {
            String toks = everyRun.getValue();
            if (toks != null && toks.length() > 0) {
                addStream(getTokenStreamFactory().newInstance(toks));
            }
        }

        push(context.getToks("everyjob"));

        execute(new Switch(true));
        typesetter.finish();

        //TODO TTP[1335]
    }

    /**
     * Add a token stream and start processing it.
     *
     * @param stream the input stream to consider
     *
     * @throws ConfigurationException in case of a configuration error
     * @throws GeneralException in case of another error
     *
     * @see #run()
     */
    public void run(final TokenStream stream)
            throws ConfigurationException,
                GeneralException {

        addStream(stream);
        run();
    }

    /**
     * Setter for calendar.
     *
     * @param theCalendar the calendar to set.
     */
    public void setCalendar(final Calendar theCalendar) {

        this.calendar = theCalendar;
    }

    /**
     * Setter for the error handler. The value of <code>null</code> can be
     * used to delete the error handler currently set.
     *
     * @param handler the new error handler
     */
    public void setErrorHandler(final ErrorHandler handler) {

        errorHandler = handler;
    }

    /**
     * Setter for the file finder.
     *
     * @param fileFinder the new file finder
     */
    public void setResourceFinder(final ResourceFinder fileFinder) {

        //finder = fileFinder;
    }

    /**
     * @see de.dante.extex.interpreter.Interpreter#setFontFactory(
     *      de.dante.extex.font.FontFactory)
     */
    public void setFontFactory(final FontFactory fontFactory) {

        context.setFontFactory(fontFactory);
    }

    /**
     * Setter for the interaction mode. The interaction mode is set globally.
     *
     * @param interaction the interaction mode
     *
     * @throws GeneralException in case of an error
     */
    public void setInteraction(final Interaction interaction)
            throws GeneralException {

        context.setInteraction(interaction, true);
    }

    /**
     * @see de.dante.extex.interpreter.Interpreter#setJobname(java.lang.String)
     */
    public void setJobname(final String jobname) throws GeneralException {

        context.setToks("jobname", new Tokens(context, jobname), true);
    }

    /**
     * @see de.dante.extex.interpreter.max.Moritz#setTokenStreamFactory(
     *      de.dante.extex.scanner.stream.TokenStreamFactory)
     */
    public void setTokenStreamFactory(final TokenStreamFactory factory)
            throws ConfigurationException {

        super.setTokenStreamFactory(factory);
        context.setStandardTokenStream(factory
                .newInstance(new InputStreamReader(System.in)));
    }

    /**
     * @see de.dante.extex.interpreter.Interpreter#setTypesetter(
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void setTypesetter(final Typesetter theTypesetter) {

        this.typesetter = theTypesetter;
    }

    /**
     * This visit method is invoked on an active token.
     * In TeX this is e.g. ~.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitActive(
     *      de.dante.extex.scanner.ActiveCharacterToken, java.lang.Object)
     */
    public Object visitActive(final ActiveCharacterToken token,
            final Object ignore) throws GeneralException {

        Code code = context.getCode(token);
        if (code == null) {
            throw new HelpingException("TTP.UndefinedToken", //
                    token.toString());
        }
        if (code.execute(prefix, context, this, typesetter)) {
            prefix.clear();
        }
        return null;
    }

    /**
     * This visit method is invoked on a cr token.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitCr(
     *      de.dante.extex.scanner.CrToken, java.lang.Object)
     */
    public Object visitCr(final CrToken token, final Object ignore)
            throws GeneralException {

        //TODO unimplemented
        throw new RuntimeException("unimplemented");
    }

    /**
     * This visit method is invoked on an escape token.
     * In TeX this normally means a control sequence.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitEscape(
     *      de.dante.extex.scanner.ControlSequenceToken, java.lang.Object)
     */
    public Object visitEscape(final ControlSequenceToken token,
            final Object ignore) throws GeneralException {

        observersMacro.update(this, token);
        Code code = context.getCode(token);
        if (code == null) {
            throw new HelpingException("TTP.UndefinedToken", //
                    token.toString());
        }
        if (code.execute(prefix, context, this, typesetter)) {
            prefix.clear();
        }
        return null;
    }

    /**
     * This visit method is invoked on a left brace token.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see "TeX -- The Program [1063]"
     * @see de.dante.extex.scanner.TokenVisitor#visitLeftBrace(
     *      de.dante.extex.scanner.LeftBraceToken, java.lang.Object)
     */
    public Object visitLeftBrace(final LeftBraceToken token, final Object ignore)
            throws GeneralException {

        try {
            context.openGroup();
        } catch (ConfigurationException e) {
            throw new GeneralException(e);
        }

        return null;
    }

    /**
     * This visit method is invoked on a letter token.
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitLetter(
     *      de.dante.extex.scanner.LetterToken, java.lang.Object)
     */
    public Object visitLetter(final LetterToken token, final Object ignore)
            throws GeneralException {

        typesetter.add(context.getTypesettingContext(), token.getChar());
        return null;
    }

    /**
     * This visit method is invoked on a macro parameter token.
     * In TeX this normally is a <tt>#</tt>.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitMacroParam(
     *      de.dante.extex.scanner.MacroParamToken, java.lang.Object)
     */
    public Object visitMacroParam(final MacroParamToken token,
            final Object ignore) throws GeneralException {

        throw new HelpingException("TTP.CantUseIn", token.toString(),
                typesetter.getMode().toString());
    }

    /**
     * This visit method is invoked on a math shift token.
     * In TeX this normally is a <tt>$</tt>.
     *
     *
     *
     * <doc name="everymath" type="register">
     * <h3>The Parameter <tt>\everymath</tt></h3>
     *
     * <p>
     *
     * </p>
     * </doc>
     *
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see "TeX -- The Program [1137]"
     * @see de.dante.extex.scanner.TokenVisitor#visitMathShift(
     *      de.dante.extex.scanner.MathShiftToken, java.lang.Object)
     */
    public Object visitMathShift(final MathShiftToken token, final Object ignore)
            throws GeneralException {

        if (typesetter.getMode() == Mode.MATH) {
            typesetter.toggleMath();
            return null;
        }

        Token next = getToken();

        if (next == null) {
            throw new HelpingException("TTP.MissingDollar");
        } else if (next.isa(Catcode.MATHSHIFT)) {
            typesetter.toggleDisplaymath();
        } else {
            push(next);
            typesetter.toggleMath();
            push(context.getToks("everymath"));
        }

        return null;
    }

    /**
     * This visit method is invoked on an other token.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitOther(
     *      de.dante.extex.scanner.OtherToken, java.lang.Object)
     */
    public Object visitOther(final OtherToken token, final Object ignore)
            throws GeneralException {

        typesetter.add(context.getTypesettingContext(), token.getChar());
        return null;
    }

    /**
     * This visit method is invoked on a right brace token.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see "TeX -- The Program [1067]"
     * @see de.dante.extex.scanner.TokenVisitor#visitRightBrace(
     *      de.dante.extex.scanner.RightBraceToken, java.lang.Object)
     */
    public Object visitRightBrace(final RightBraceToken token,
            final Object ignore) throws GeneralException {

        context.closeGroup(typesetter, this);
        return null;
    }

    /**
     * This visit method is invoked on a space token.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitSpace(
     *      de.dante.extex.scanner.SpaceToken, java.lang.Object)
     */
    public Object visitSpace(final SpaceToken token, final Object ignore)
            throws GeneralException {

        typesetter.addSpace(context.getTypesettingContext(), null);
        return null;
    }

    /**
     * This visit method is invoked on a sub mark token.
     * In TeX this normally is a <tt>_</tt>.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitSubMark(
     *      de.dante.extex.scanner.SubMarkToken, java.lang.Object)
     */
    public Object visitSubMark(final SubMarkToken token, final Object ignore)
            throws GeneralException {

        Mode mode = typesetter.getMode();
        if (mode == Mode.MATH || mode == Mode.DISPLAYMATH) {
            //TODO unimplemented
            throw new RuntimeException("unimplemented");
        }

        throw new HelpingException("TTP.MissingDollar");
    }

    /**
     * This visit method is invoked on a sup mark token.
     * In TeX this normally is a <tt>^</tt>.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitSupMark(
     *      de.dante.extex.scanner.SupMarkToken, java.lang.Object)
     */
    public Object visitSupMark(final SupMarkToken token, final Object ignore)
            throws GeneralException {

        Mode mode = typesetter.getMode();
        if (mode == Mode.MATH || mode == Mode.DISPLAYMATH) {
            //TODO unimplemented
            throw new RuntimeException("unimplemented");
        }

        throw new HelpingException("TTP.MissingDollar");
    }

    /**
     * This visit method is invoked on a tab mark token.
     * In TeX this normally is a <tt>&amp;</tt>.
     *
     * @param token the first argument to pass is the token to expand.
     * @param ignore the second argument is ignored
     *
     * @return <code>null</code>
     *
     * @throws GeneralException in case of an error
     *
     * @see de.dante.extex.scanner.TokenVisitor#visitTabMark(
     *      de.dante.extex.scanner.TabMarkToken, java.lang.Object)
     */
    public Object visitTabMark(final TabMarkToken token, final Object ignore)
            throws GeneralException {

        // TODO unimplemented
        throw new RuntimeException("unimplemented");
    }
}