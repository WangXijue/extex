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

package de.dante.extex.interpreter.context.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dante.extex.documentWriter.DocumentWriterOptions;
import de.dante.extex.font.FontFactory;
import de.dante.extex.font.type.other.NullFont;
import de.dante.extex.hyphenation.HyphenationManager;
import de.dante.extex.hyphenation.HyphenationTable;
import de.dante.extex.hyphenation.impl.HyphenationManagerImpl;
import de.dante.extex.i18n.HelpingException;
import de.dante.extex.interpreter.Conditional;
import de.dante.extex.interpreter.ConditionalSwitch;
import de.dante.extex.interpreter.Interaction;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.Tokenizer;
import de.dante.extex.interpreter.context.CodeChangeObserver;
import de.dante.extex.interpreter.context.Color;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.context.CountChangeObserver;
import de.dante.extex.interpreter.context.Direction;
import de.dante.extex.interpreter.context.TypesettingContext;
import de.dante.extex.interpreter.context.TypesettingContextFactory;
import de.dante.extex.interpreter.type.Code;
import de.dante.extex.interpreter.type.box.Box;
import de.dante.extex.interpreter.type.count.Count;
import de.dante.extex.interpreter.type.count.FixedCount;
import de.dante.extex.interpreter.type.dimen.Dimen;
import de.dante.extex.interpreter.type.dimen.FixedDimen;
import de.dante.extex.interpreter.type.file.InFile;
import de.dante.extex.interpreter.type.file.OutFile;
import de.dante.extex.interpreter.type.font.Font;
import de.dante.extex.interpreter.type.glue.FixedGlue;
import de.dante.extex.interpreter.type.glue.Glue;
import de.dante.extex.interpreter.type.muskip.Muskip;
import de.dante.extex.interpreter.type.tokens.FixedTokens;
import de.dante.extex.interpreter.type.tokens.Tokens;
import de.dante.extex.scanner.Catcode;
import de.dante.extex.scanner.CodeToken;
import de.dante.extex.scanner.Token;
import de.dante.extex.scanner.TokenFactory;
import de.dante.extex.scanner.stream.TokenStream;
import de.dante.extex.scanner.stream.TokenStreamOptions;
import de.dante.extex.scanner.stream.impl.TokenStreamBaseImpl;
import de.dante.extex.typesetter.Typesetter;
import de.dante.extex.typesetter.TypesetterOptions;
import de.dante.extex.typesetter.paragraphBuilder.ParagraphShape;
import de.dante.util.GeneralException;
import de.dante.util.Locator;
import de.dante.util.UnicodeChar;
import de.dante.util.configuration.Configuration;
import de.dante.util.configuration.ConfigurationException;
import de.dante.util.configuration.ConfigurationMissingException;
import de.dante.util.framework.i18n.Localizable;
import de.dante.util.framework.i18n.Localizer;
import de.dante.util.observer.NotObservableException;
import de.dante.util.observer.Observable;
import de.dante.util.observer.Observer;
import de.dante.util.observer.ObserverList;


/**
 * This is a reference implementation for an interpreter context.
 *
 * The groups are implemented as a linked list of single groups. In contrast to
 * the Knuthian implementation in TeX no undo stack is used.
 * <p>
 * Several operations have to be dealt with:
 * </p>
 * <ul>
 * <li>For each new group a new instance of a {@link Group Group} is created
 * with the old one as next group.</li>
 * <li>If a group is closed then the next group is used as current group and
 * the formerly current group is discarted.</li>
 * <li>If a value has to be found in a group then the next chain has to be
 * traced down until the value is found. <br />An implementation variant might
 * want to insert the value found into the higher groups; all or some of them
 * to speed up the next access. This optimization is currently not implemented.
 * </li>
 * <li>If a local value has to be stored then it can be stored in the local
 * group only.</li>
 * <li>If a global value has to be stored then the group chain has to be
 * traversed and the value has to be set in all approrpiate groups: There are
 * several implementation variants
 * <ul>
 * <li>Clear the value in all groups and set it in the bottommost group.</li>
 * <li>Set the value in all groups where it has a local value.</li>
 * <li>Set the value in all groups.</li>
 * </ul>
 * Here the third approach is used which is suspected to be a little more
 * efficient on the cost of slightly more memory consumption.</li>
 * </ul>
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.53 $
 */
public class ContextImpl
        implements
            Context,
            Tokenizer,
            DocumentWriterOptions,
            TypesetterOptions,
            TokenStreamOptions,
            Observable,
            Localizable,
            Serializable {

    /**
     * ...
     * @param tokens ...
     *
     * @return ...
     *
     * @throws GeneralException ....
     *
     * @see de.dante.extex.interpreter.context.Context#expand(
     *      de.dante.extex.interpreter.type.tokens.Tokens, Typesetter)
     */
    public Tokens expand(final Tokens tokens, final Typesetter typesetter)
            throws GeneralException {

        Tokens result = new Tokens();
        //TODO use interface instead of implementation
        TokenStreamBaseImpl stream = new TokenStreamBaseImpl(false, tokens);

        /*
        while (!stream.isEof()) {
            Token t = stream.get(null, null);

            while (t instanceof CodeToken) {
                Code code = getCode((CodeToken) t);
                if (code instanceof ExpandableCode) {
                    ((ExpandableCode) code).expand(Flags.NONE, (Context) this,
                            (TokenStream) stream, typesetter);
                    t = stream.get(null, null);
                }
            }

        }

        return result;
        */
        // TODO expand() unimplemented
        throw new RuntimeException("unimplemented");
    }

    /**
     * The constant <tt>GROUP_TAG</tt> contains the name of the tag for the
     * sub-configuration for the group factory.
     */
    private static final String GROUP_TAG = "Group";

    /**
     * The constant <tt>MAGNIFICATION_MAX</tt> contains the maximal allowed
     * magnification value. This is the fallback value which can be changed in
     * the configuration.
     */
    private static final long MAGNIFICATION_MAX = 0x8000;

    /**
     * The constant <tt>TYPESETTING_CONTEXT_TAG</tt> contains the name of the
     * configuration tag for the typesetting context.
     */
    private static final String TYPESETTING_CONTEXT_TAG = "TypesettingContext";

    /**
     * The field <tt>afterassignment</tt> contains the token to be inserted
     * after a assignemnt is completed or <code>null</code>.
     */
    private Token afterassignment = null;

    /**
     * The field <tt>codeChangeObservers</tt> contains the list of observers
     * registered for change event on the code.
     */
    private transient Map codeChangeObservers;

    /**
     * The field <tt>conditionalStack</tt> contains the stack for conditionals.
     */
    private List conditionalStack = new ArrayList();

    /**
     * The field <tt>countChangeObservers</tt> contains the list of observers
     * registered for change event on the count registers.
     */
    private transient Map countChangeObservers = new HashMap();

    /**
     * The field <tt>fontFactory</tt> contains the font factory to use.
     */
    private transient FontFactory fontFactory;

    /**
     * The field <tt>group</tt> contains the entry to the linked list of groups.
     * The current group is the first one.
     */
    private Group group = null;

    /**
     * The field <tt>groupFactory</tt> contains the factory to acquire
     * a new group.
     */
    private transient GroupFactory groupFactory;

    /**
     * The field <tt>hyphenationManager</tt> contains the hyphenation manager.
     */
    private transient HyphenationManager hyphenationManager = new HyphenationManagerImpl();

    /**
     * The field <tt>id</tt> contains the is string.
     * The id string is the classification of the
     * original source as given in the fmt file. The id string can be
     * <code>null</code> if not known yet.
     */
    private String id = null;

    /**
     * The field <tt>localizer</tt> contains the localizer to use.
     */
    private transient Localizer localizer = null;

    /**
     * The field <tt>magnification</tt> contains the magnification for the
     * whole document in permille. The value is always greater than 0 and
     * less or equal to <tt>magnificationMax</tt>.
     */
    private long magnification = Math.min(1000, MAGNIFICATION_MAX);

    /**
     * The field <tt>magnificationLock</tt> is used to determine whether the
     * magnification has already been set to a new value.
     * It it is <code>true</code> then it is not
     * desirable to change the value of <i>magnification</i>.
     */
    private boolean magnificationLock = false;

    /**
     * The field <tt>magnificationMax</tt> contains the maximal allowed
     * maginification value. This is initialized to MAGNIFICATION_MAX and
     * may be overwritten from within the configuration.
     */
    private transient long magnificationMax = MAGNIFICATION_MAX;

    /**
     * The field <tt>observersInteraction</tt> contains the observer list which
     * is used for the observers registered to receive notifications
     * when the interaction is changed. The argument is the new interaction
     * mode.
     */
    private transient ObserverList observersInteraction = new ObserverList();

    /**
     * The field <tt>parshape</tt> contains the object containing the
     * dimensions of the paragraph.
     */
    private ParagraphShape parshape = null;

    /**
     * The field <tt>standardTokenStream</tt> contains the standard token
     * stream. This token stream usually is fed by the user.
     */
    private transient TokenStream standardTokenStream = null;

    /**
     * The field <tt>tcFactory</tt> contains the factory to acquire new
     * instances of a TypesettingContext.
     */
    private transient TypesettingContextFactory tcFactory;

    /**
     * The field <tt>tokenFactory</tt> contains the token factory implementation
     * to use.
     */
    private transient TokenFactory tokenFactory;

    /**
     * Creates a new object.
     *
     */
    protected ContextImpl() {

        super();
        codeChangeObservers = new HashMap();
    }

    /**
     * Creates a new object.
     *
     * @param configuration the configuration to use
     *
     * @throws ConfigurationException in case of an configuration error
     * @throws GeneralException in case of an execution error
     */
    public ContextImpl(final Configuration configuration)
            throws ConfigurationException,
                GeneralException {

        this();
        groupFactory = new GroupFactory(configuration
                .getConfiguration(GROUP_TAG));
        openGroup();

        Configuration typesettingConfig = configuration
                .getConfiguration(TYPESETTING_CONTEXT_TAG);

        if (typesettingConfig == null) {
            throw new ConfigurationMissingException(TYPESETTING_CONTEXT_TAG,
                    configuration.toString());
        }

        tcFactory = new TypesettingContextFactory(typesettingConfig);
        TypesettingContext typesettingContext = tcFactory.newInstance();

        typesettingContext.setFont(new NullFont());
        //typesettingContext.setLanguage(config.getValue("Language"));
        setTypesettingContext(typesettingContext);

        magnificationMax = configuration.getValueAsInteger(
                "maximalMagnification", (int) MAGNIFICATION_MAX);

    }

    /**
     * @see de.dante.extex.interpreter.context.Context#afterGroup(
     *      de.dante.util.observer.Observer)
     */
    public void afterGroup(final Observer observer) {

        group.afterGroup(observer);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#afterGroup(
     *      de.dante.extex.scanner.Token)
     */
    public void afterGroup(final Token t) {

        group.afterGroup(t);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#closeGroup(
     *      de.dante.extex.typesetter.Typesetter,
     *     de.dante.extex.interpreter.TokenSource)
     */
    public void closeGroup(final Typesetter typesetter, final TokenSource source)
            throws GeneralException {

        Group next = group.getNext();

        if (next == null) {
            throw new HelpingException(localizer, "TTP.TooManyRightBraces");
        }

        if (group.getInteraction() != next.getInteraction()) {
            observersInteraction.update(this, next.getInteraction());
        }

        group.runAfterGroup(this, typesetter);

        Tokens toks = group.getAfterGroup();
        group = next;

        if (toks != null) {
            source.push(toks);
        }

    }

    /**
     * Setter for the localizer.
     *
     * @param localizer the localizer to use
     *
     * @see de.dante.util.framework.i18n.Localizable#enableLocalization(
     *      de.dante.util.framework.i18n.Localizer)
     */
    public void enableLocalization(final Localizer localizer) {

        this.localizer = localizer;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getAfterassignment()
     */
    public Token getAfterassignment() {

        return afterassignment;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getBox(java.lang.String)
     */
    public Box getBox(final String name) {

        return group.getBox(name);
    }

    /**
     * Get the {@link Catcode Catcode} for a given Unicode character.
     *
     * @param uc the Unicode character to get the catcode for.
     *
     * @return the catcode for the character
     *
     * @see de.dante.extex.interpreter.Tokenizer#getCatcode(de.dante.util.UnicodeChar)
     */
    public Catcode getCatcode(final UnicodeChar uc) {

        return group.getCatcode(uc);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getCode(
     *      de.dante.extex.scanner.Token)
     */
    public Code getCode(final Token t) throws GeneralException {

        return group.getCode((CodeToken) t);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getCount(
     *      java.lang.String)
     */
    public Count getCount(final String name) {

        return group.getCount(name);
    }

    /**
     * @see de.dante.extex.typesetter.TypesetterOptions#getCountOption(java.lang.String)
     */
    public FixedCount getCountOption(final String name) {

        return group.getCount(name);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getDelcode(
     *      de.dante.util.UnicodeChar)
     */
    public Count getDelcode(final UnicodeChar c) {

        return group.getDelcode(c);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getDimen(
     *      java.lang.String)
     */
    public Dimen getDimen(final String name) {

        return group.getDimen(name);
    }

    /**
     * @see de.dante.extex.typesetter.TypesetterOptions#getDimenOption(java.lang.String)
     */
    public FixedDimen getDimenOption(final String name) {

        return group.getDimen(name);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getFont(java.lang.String)
     */
    public Font getFont(final String name) {

        return this.group.getFont(name);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getFontFactory()
     */
    public FontFactory getFontFactory() {

        return fontFactory;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getGlue(java.lang.String)
     */
    public Glue getGlue(final String name) {

        return group.getSkip(name);
    }

    /**
     * @see de.dante.extex.typesetter.TypesetterOptions#getGlueOption(java.lang.String)
     */
    public FixedGlue getGlueOption(final String name) {

        return group.getSkip(name);
    }

    /**
     * Getter for group.
     *
     * @return the group.
     */
    protected Group getGroup() {

        return group;
    }

    /**
     * Getter for the group level. The group level is the number of groups which
     * are currently open. Thus this number of groups can be closed.
     *
     * @return the group level
     *
     * @see de.dante.extex.interpreter.context.Context#getGroupLevel()
     */
    public long getGroupLevel() {

        return group.getLevel();
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getHyphenationTable(int)
     */
    public HyphenationTable getHyphenationTable(final int language) {

        return hyphenationManager.getHyphenationTable(Integer
                .toString(language));
    }

    /**
     * Getter for the id string. The id string is the classification of the
     * original source as given in the fmt file. The id string can be
     * <code>null</code> if not known yet.
     *
     * @return the id string
     *
     * @see de.dante.extex.interpreter.context.Context#getId()
     */
    public String getId() {

        return id;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getInFile(
     *      java.lang.String)
     */
    public InFile getInFile(final String name) {

        return group.getInFile(name);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getInteraction()
     */
    public Interaction getInteraction() {

        return group.getInteraction();
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getLccode(
     *      de.dante.util.UnicodeChar)
     */
    public UnicodeChar getLccode(final UnicodeChar uc) {

        return group.getLccode(uc);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getMagnification()
     */
    public long getMagnification() {

        return magnification;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getMathcode(
     *      de.dante.util.UnicodeChar)
     */
    public Count getMathcode(final UnicodeChar c) {

        return group.getMathcode(c);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getMuskip(
     *      java.lang.String)
     */
    public Muskip getMuskip(final String name) {

        return group.getMuskip(name);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getNamespace()
     */
    public String getNamespace() {

        return group.getNamespace();
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getOutFile(
     *      java.lang.String)
     */
    public OutFile getOutFile(final String name) {

        return group.getOutFile(name);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getParshape()
     */
    public ParagraphShape getParshape() {

        //TODO: How does \parshape interact with groups?
        return this.parshape;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getSfcode(
     *      de.dante.util.UnicodeChar)
     */
    public Count getSfcode(final UnicodeChar c) {

        return group.getSfcode(c);
    }

    /**
     * Getter for the token factory.
     *
     * @return the token factory
     */
    public TokenFactory getTokenFactory() {

        return tokenFactory;
    }

    /**
     * Getter for the tokenizer.
     *
     * @return the tokenizer
     */
    public Tokenizer getTokenizer() {

        return (Tokenizer) group;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getToks(java.lang.String)
     */
    public Tokens getToks(final String name) {

        return group.getToks(name);
    }

    /**
     * @see de.dante.extex.scanner.stream.TokenStreamOptions#getToksOption(
     *      java.lang.String)
     */
    public FixedTokens getToksOption(final String name) {

        return group.getToks(name);
    }

    /**
     * Getter for the typesetting context.
     *
     * @return the typesetting context
     */
    public TypesettingContext getTypesettingContext() {

        return group.getTypesettingContext();
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#getUccode(
     *      de.dante.util.UnicodeChar)
     */
    public UnicodeChar getUccode(final UnicodeChar lc) {

        return group.getUccode(lc);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#isGlobalGroup()
     */
    public boolean isGlobalGroup() {

        return (group.getNext() == null);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#openGroup()
     */
    public void openGroup() throws ConfigurationException {

        group = groupFactory.newInstance(group);
        group.setStandardTokenStream(standardTokenStream);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#popConditional()
     */
    public Conditional popConditional() throws GeneralException {

        int size = conditionalStack.size();
        if (size <= 0) {
            return null;
        }
        return ((Conditional) conditionalStack.remove(size - 1));
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#pushConditional(
     *      de.dante.util.Locator, boolean)
     */
    public void pushConditional(final Locator locator,
            final boolean isIfThenElse) {

        conditionalStack.add(isIfThenElse
                ? new Conditional(locator)
                : new ConditionalSwitch(locator));
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#registerCodeChangeObserver(de.dante.extex.interpreter.context.CodeChangeObserver, de.dante.extex.scanner.Token)
     */
    public void registerCodeChangeObserver(final CodeChangeObserver observer,
            final Token name) {

        List observerList = (List) codeChangeObservers.get(name);
        if (null == observerList) {
            observerList = new ArrayList();
            codeChangeObservers.put(name, observerList);
        }
        observerList.add(observer);
    }

    /**
     * @see de.dante.util.observer.Observable#registerObserver(
     *      java.lang.String,
     *      de.dante.util.observer.Observer)
     */
    public void registerObserver(final String name, final Observer observer)
            throws NotObservableException {

        if ("interaction".equals(name)) {
            observersInteraction.add(observer);
        } else {
            throw new NotObservableException(name);
        }
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setActive(java.lang.String,
     *      de.dante.extex.interpreter.type.Code, boolean)
     */
    public void setActive(final Token token, final Code code,
            final boolean global) {

        group.setCode(token, code, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setAfterassignment(
     *      de.dante.extex.scanner.Token)
     */
    public void setAfterassignment(final Token token) {

        afterassignment = token;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setBox(java.lang.String,
     *      de.dante.extex.interpreter.type.box.Box, boolean)
     */
    public void setBox(final String name, final Box value, final boolean global) {

        group.setBox(name, value, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setCatcode(
     *      de.dante.util.UnicodeChar,
     *      de.dante.extex.scanner.Catcode, boolean)
     */
    public void setCatcode(final UnicodeChar c, final Catcode cc,
            final boolean global) {

        group.setCatcode(c, cc, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setCode(
     *      de.dante.extex.scanner.Token,
     *      de.dante.extex.interpreter.type.Code, boolean)
     */
    public void setCode(final Token t, final Code code, final boolean global)
            throws GeneralException {

        if (!(t instanceof CodeToken)) {
            throw new HelpingException(localizer, "TTP.MissingCtrlSeq");
        }
        group.setCode(t, code, global);

        List observerList = (List) codeChangeObservers.get(t);
        if (null != observerList) {
            int len = observerList.size();
            for (int i = 0; i < len; i++) {
                ((CodeChangeObserver) observerList.get(i)).receiveCodeChange(t,
                        code);
            }
        }

    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setCount(
     *      java.lang.String,
     *      long, boolean)
     */
    public void setCount(final String name, final long value,
            final boolean global) {

        Count count = new Count(value);
        group.setCount(name, count, global);

        List observerList = (List) countChangeObservers.get(name);
        if (null != observerList) {
            int len = observerList.size();
            for (int i = 0; i < len; i++) {
                ((CountChangeObserver) observerList.get(i)).receiveCountChange(
                        name, count);
            }
        }

    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setDelcode(
     *      de.dante.util.UnicodeChar,
     *      de.dante.extex.interpreter.type.count.Count, boolean)
     */
    public void setDelcode(final UnicodeChar c, final Count code,
            final boolean global) {

        group.setDelcode(c, code, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setDimen(
     *      java.lang.String,
     *      de.dante.extex.interpreter.type.dimen.Dimen, boolean)
     */
    public void setDimen(final String name, final Dimen value,
            final boolean global) {

        group.setDimen(name, value, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setDimen(
     *      java.lang.String,
     *      long, boolean)
     */
    public void setDimen(final String name, final long value,
            final boolean global) {

        Dimen dimen = new Dimen(value);
        group.setDimen(name, dimen, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setFont(
     *      java.lang.String,
     *      de.dante.extex.interpreter.type.font.Font,
     *      boolean)
     */
    public void setFont(final String name, final Font font, final boolean global) {

        this.group.setFont(name, font, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setFontFactory(
     *      de.dante.extex.font.FontFactory)
     */
    public void setFontFactory(final FontFactory factory) {

        this.fontFactory = factory;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setGlue(java.lang.String,
     *      de.dante.extex.interpreter.type.glue.Glue, boolean)
     */
    public void setGlue(final String name, final Glue value,
            final boolean global) {

        group.setSkip(name, value, global);
    }

    /**
     * Setter for the id string. The id string is the classification of the
     * original source like given in the fmt file.
     *
     * @param theId the id string
     *
     * @see de.dante.extex.interpreter.context.Context#setId(java.lang.String)
     */
    public void setId(final String theId) {

        this.id = theId;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setInFile(
     *      java.lang.String,
     *      de.dante.extex.interpreter.type.file.InFile, boolean)
     */
    public void setInFile(final String name, final InFile file,
            final boolean global) {

        group.setInFile(name, file, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setInteraction(
     *      de.dante.extex.interpreter.Interaction,
     *      boolean)
     */
    public void setInteraction(final Interaction interaction,
            final boolean global) throws GeneralException {

        group.setInteraction(interaction, global);
        observersInteraction.update(this, interaction);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setLccode(
     *      de.dante.util.UnicodeChar,
     *      de.dante.util.UnicodeChar)
     */
    public void setLccode(final UnicodeChar uc, final UnicodeChar lc) {

        group.setLccode(uc, lc);
    }

    /**
     * Setter for the magnification. The magnification is a global value which
     * can be assigned at most once. It contains the magnification factor in
     * permille. The default value is 1000. It can only take positive numbers
     * as values. The maximal value is taken from the configuration option
     * <tt>maximalMaginification</tt>.
     * The default value for the maximal magnification is 32768.
     *
     * @see de.dante.extex.interpreter.context.Context#setMagnification(long)
     */
    public void setMagnification(final long mag) throws HelpingException {

        if (magnificationLock && this.magnification != mag) {
            throw new HelpingException(localizer, "TTP.IncompatibleMag", //
                    Long.toString(mag));
        }

        magnificationLock = true;

        if (mag < 1 || mag > magnificationMax) {
            throw new HelpingException(localizer, "TTP.IllegalMag", //
                    Long.toString(mag));
        }

        magnification = mag;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setMathcode(
     *      de.dante.util.UnicodeChar,
     *      Count, boolean)
     */
    public void setMathcode(final UnicodeChar c, final Count code,
            final boolean global) {

        group.setMathcode(c, code, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setMuskip(
     *      java.lang.String,
     *      de.dante.extex.interpreter.type.muskip.Muskip, boolean)
     */
    public void setMuskip(final String name, final Muskip value,
            final boolean global) {

        group.setMuskip(name, value, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setNamespace(
     *      java.lang.String, boolean)
     */
    public void setNamespace(final String namespace, final boolean global) {

        group.setNamespace(namespace, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setOutFile(
     *      java.lang.String,
     *      de.dante.extex.interpreter.type.file.OutFile, boolean)
     */
    public void setOutFile(final String name, final OutFile file,
            final boolean global) {

        group.setOutFile(name, file, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setParshape(de.dante.extex.typesetter.paragraphBuilder.ParagraphShape)
     */
    public void setParshape(final ParagraphShape shape) {

        this.parshape = shape;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setSfcode(
     *      de.dante.util.UnicodeChar,
     *      de.dante.extex.interpreter.type.count.Count, boolean)
     */
    public void setSfcode(final UnicodeChar c, final Count code,
            final boolean global) {

        group.setSfcode(c, code, global);
    }

    /**
     * Setter for standardTokenStream.
     *
     * @param standardTokenStream the standardTokenStream to set.
     */
    public void setStandardTokenStream(final TokenStream standardTokenStream) {

        this.standardTokenStream = standardTokenStream;
        group.setStandardTokenStream(standardTokenStream);
    }

    /**
     * Setter for the token factory
     *
     * @param factory the new value of the factory
     *
     * @see de.dante.extex.interpreter.context.Context#setTokenFactory(
     *      de.dante.extex.scanner.TokenFactory)
     */
    public void setTokenFactory(final TokenFactory factory) {

        tokenFactory = factory;
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setToks(java.lang.String,
     *      de.dante.extex.interpreter.type.tokens.Tokens, boolean)
     */
    public void setToks(final String name, final Tokens toks,
            final boolean global) {

        group.setToks(name, toks, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setTypesettingContext(
     *      de.dante.extex.interpreter.context.Color)
     */
    public void setTypesettingContext(final Color color)
            throws ConfigurationException {

        group.setTypesettingContext(tcFactory.newInstance(group
                .getTypesettingContext(), color));
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setTypesettingContext(
     *      de.dante.extex.interpreter.context.Direction)
     */
    public void setTypesettingContext(final Direction direction)
            throws ConfigurationException {

        group.setTypesettingContext(tcFactory.newInstance(group
                .getTypesettingContext(), direction));
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setTypesettingContext(
     *      de.dante.extex.interpreter.type.font.Font)
     */
    public void setTypesettingContext(final Font font)
            throws ConfigurationException {

        group.setTypesettingContext(tcFactory.newInstance(group
                .getTypesettingContext(), font));
    }

    /**
     * Setter for the typesetting context in the current group.
     *
     * @param context the new context to use
     */
    public void setTypesettingContext(final TypesettingContext context) {

        group.setTypesettingContext(context);
    }

    /**
     * Setter for the typesetting context in the specified groups.
     *
     * @param context the new context to use
     * @param global if <code>true</code> then the new value is set in all
     *            groups, otherwise only in the current group.
     */
    public void setTypesettingContext(final TypesettingContext context,
            final boolean global) {

        group.setTypesettingContext(context, global);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#setUccode(
     *      de.dante.util.UnicodeChar,
     *      de.dante.util.UnicodeChar)
     */
    public void setUccode(final UnicodeChar lc, final UnicodeChar uc) {

        group.setUccode(lc, uc);
    }

    /**
     * @see de.dante.extex.interpreter.context.Context#unregisterCodeChangeObserver(de.dante.extex.interpreter.context.CodeChangeObserver, de.dante.extex.scanner.Token)
     */
    public void unregisterCodeChangeObserver(final CodeChangeObserver observer,
            final Token name) {

        List observerList = (List) codeChangeObservers.get(name);
        if (null == observerList) {
            return;
        }
        observerList.remove(observer);
    }
}