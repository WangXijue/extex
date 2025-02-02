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

package de.dante.extex.interpreter.primitives.register.real;

import de.dante.extex.interpreter.Flags;
import de.dante.extex.interpreter.TokenSource;
import de.dante.extex.interpreter.context.Context;
import de.dante.extex.interpreter.context.ContextExtension;
import de.dante.extex.interpreter.exception.InterpreterException;
import de.dante.extex.interpreter.exception.InterpreterExtensionException;
import de.dante.extex.interpreter.type.AbstractAssignment;
import de.dante.extex.interpreter.type.Theable;
import de.dante.extex.interpreter.type.arithmetic.Advanceable;
import de.dante.extex.interpreter.type.arithmetic.Divideable;
import de.dante.extex.interpreter.type.arithmetic.Multiplyable;
import de.dante.extex.interpreter.type.count.CountConvertible;
import de.dante.extex.interpreter.type.real.Real;
import de.dante.extex.interpreter.type.real.RealConvertible;
import de.dante.extex.interpreter.type.tokens.Tokens;
import de.dante.extex.typesetter.Typesetter;
import de.dante.util.exception.GeneralException;

/**
 * This class provides an implementation for the real valued primitives.
 * It sets the named real register to the value given,
 * and as a side effect all prefixes are zeroed.
 *
 * <p>Example</p>
 * <pre>
 * \pi=3.1415
 * </pre>
 *
 * @author <a href="mailto:mgn@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.17 $
 */
public class NamedReal extends AbstractAssignment
        implements
            Theable,
            Advanceable,
            Multiplyable,
            Divideable,
            RealConvertible,
            CountConvertible {

    /**
     * Creates a new object.
     *
     * @param name the name for debugging
     */
    public NamedReal(final String name) {

        super(name);
    }

    /**
     * @see de.dante.extex.interpreter.type.AbstractAssignment#assign(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public void assign(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        if (context instanceof ContextExtension) {

            ContextExtension contextextex = (ContextExtension) context;

            String key = getKey(context, source);
            source.getOptionalEquals(context);
            Real value = new Real(context, source);
            contextextex.setReal(key, value, prefix.isGlobal());
            prefix.clearGlobal();

        } else {
            throw new InterpreterExtensionException();
        }
    }

    /**
     * set the value
     *
     * @param context    the interpreter context
     * @param value      the new value as Real
     * @throws InterpreterException if no extension is configured
     */
    public void set(final Context context, final Real value)
            throws InterpreterException {

        if (context instanceof ContextExtension) {
            ContextExtension contextextex = (ContextExtension) context;
            contextextex.setReal(getName(), value);
        } else {
            throw new InterpreterExtensionException();
        }
    }

    /**
     * Set the value
     *
     * @param context    the interpreter context
     * @param value      the new value as String
     * @throws InterpreterException if no extension is configured
     */
    public void set(final Context context, final String value)
            throws GeneralException {

        if (context instanceof ContextExtension) {
            ContextExtension contextextex = (ContextExtension) context;
            contextextex.setReal(getName(), new Real(value));
        } else {
            throw new InterpreterExtensionException();
        }
    }

    /**
     * @see de.dante.extex.interpreter.type.Theable#the(
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource, Typesetter)
     */
    public Tokens the(final Context context, final TokenSource source,
            final Typesetter typesetter) throws InterpreterException {

        if (context instanceof ContextExtension) {
            ContextExtension contextextex = (ContextExtension) context;
            String key = getKey(context, source);
            String s = contextextex.getReal(key).toString();
            return new Tokens(context, s);
        }
        throw new InterpreterExtensionException();
    }

    /**
     * Return the key (the name of the primitive) for the register.
     *
     * @param context   the context
     * @param source    the tokensource
     * @return the key
     * @throws InterpreterException in case of an error.
     */
    protected String getKey(final Context context, final TokenSource source)
            throws InterpreterException {

        return getName();
    }

    /**
     * @see de.dante.extex.interpreter.type.arithmetic.Advanceable#advance(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource)
     */
    public void advance(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        if (context instanceof ContextExtension) {

            ContextExtension contextextex = (ContextExtension) context;
            String key = getKey(context, source);
            Real real = contextextex.getReal(key);
            source.getKeyword(context, "by");
            Real value = new Real(context, source);
            real.add(value);
            if (prefix.isGlobal()) {
                contextextex.setReal(key, real, true);
            }
        } else {
            throw new InterpreterExtensionException();
        }
    }

    /**
     * @see de.dante.extex.interpreter.type.arithmetic.Multiplyable#multiply(
     *      de.dante.extex.interpreter.Flags,
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource)
     */
    public void multiply(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        if (context instanceof ContextExtension) {
            ContextExtension contextextex = (ContextExtension) context;

            String key = getKey(context, source);
            Real real = contextextex.getReal(key);
            source.getKeyword(context, "by");
            Real value = new Real(context, source);
            real.multiply(value);
            if (prefix.isGlobal()) {
                contextextex.setReal(key, real, true);
            }
        } else {
            throw new InterpreterExtensionException();
        }
    }

    /**
     * @see de.dante.extex.interpreter.type.arithmetic.Divideable#divide(
     *       de.dante.extex.interpreter.Flags,
     *       de.dante.extex.interpreter.context.Context,
     *       de.dante.extex.interpreter.TokenSource)
     */
    public void divide(final Flags prefix, final Context context,
            final TokenSource source, final Typesetter typesetter)
            throws InterpreterException {

        if (context instanceof ContextExtension) {
            ContextExtension contextextex = (ContextExtension) context;
            String key = getKey(context, source);
            Real real = contextextex.getReal(key);
            source.getKeyword(context, "by");
            Real value = new Real(context, source);
            real.divide(value);
            if (prefix.isGlobal()) {
                contextextex.setReal(key, real, true);
            }
        } else {
            throw new InterpreterExtensionException();
        }
    }

    /**
     * @see de.dante.extex.interpreter.type.real.RealConvertible#convertReal(
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource)
     */
    public Real convertReal(final Context context, final TokenSource source)
            throws InterpreterException {

        if (context instanceof ContextExtension) {
            ContextExtension contextextex = (ContextExtension) context;
            String key = getKey(context, source);
            Real r = contextextex.getReal(key);
            return (r != null ? r : Real.ZERO);
        }
        throw new InterpreterExtensionException();
    }

    /**
     * @see de.dante.extex.interpreter.type.count.CountConvertible#convertCount(
     *      de.dante.extex.interpreter.context.Context,
     *      de.dante.extex.interpreter.TokenSource,
     *      de.dante.extex.typesetter.Typesetter)
     */
    public long convertCount(final Context context, final TokenSource source,
            final Typesetter typesetter) throws InterpreterException {

        if (context instanceof ContextExtension) {
            ContextExtension contextextex = (ContextExtension) context;
            String key = getKey(context, source);
            Real r = contextextex.getReal(key);
            return (r != null ? r.getLong() : 0);
        }
        throw new InterpreterExtensionException();
    }
}