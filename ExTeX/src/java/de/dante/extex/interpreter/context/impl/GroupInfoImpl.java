/*
 * Copyright (C) 2006 The ExTeX Group and individual authors listed below
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

import de.dante.extex.interpreter.context.group.GroupInfo;
import de.dante.extex.interpreter.context.group.GroupType;
import de.dante.extex.scanner.type.token.Token;
import de.dante.util.Locator;

/**
 * This class provides a reference implementation for a
 * {@link de.dante.extex.interpreter.context.group.GroupInfo GroupInfo}.
 * It is a mere container with some getters.
 *
 * @author <a href="mailto:gene@gerd-neugebauer.de">Gerd Neugebauer</a>
 * @version $Revision: 1.2 $
 */
public class GroupInfoImpl implements GroupInfo {

    /**
     * The field <tt>groupStart</tt> contains the group starting token.
     */
    private Token groupStart;

    /**
     * The field <tt>groupType</tt> contains the group type.
     */
    private GroupType groupType;

    /**
     * The field <tt>locator</tt> contains the locator.
     */
    private Locator locator;

    /**
     * Creates a new object.
     */
    public GroupInfoImpl() {

        super();
    }

    /**
     * Creates a new object.
     *
     * @param locator the locator
     * @param groupType the group type
     * @param groupStart the starting token
     */
    public GroupInfoImpl(final Locator locator, final GroupType groupType,
            final Token groupStart) {

        super();
        this.locator = locator;
        this.groupType = groupType;
        this.groupStart = groupStart;
    }

    /**
     * @see de.dante.extex.interpreter.context.group.GroupInfo#getGroupStart()
     */
    public Token getGroupStart() {

        return groupStart;
    }

    /**
     * @see de.dante.extex.interpreter.context.group.GroupInfo#getGroupType()
     */
    public GroupType getGroupType() {

        return groupType;
    }

    /**
     * @see de.dante.extex.interpreter.context.group.GroupInfo#getLocator()
     */
    public Locator getLocator() {

        return locator;
    }

    /**
     * Setter for groupStart.
     *
     * @param groupStart the groupStart to set
     */
    public void setGroupStart(Token groupStart) {

        this.groupStart = groupStart;
    }

    /**
     * Setter for groupType.
     *
     * @param groupType the groupType to set
     */
    public void setGroupType(final GroupType type) {

        this.groupType = type;
    }

    /**
     * Setter for locator.
     *
     * @param locator the locator to set
     */
    public void setLocator(Locator locator) {

        this.locator = locator;
    }

}
