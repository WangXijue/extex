/*
 * Copyright (C) 2004 Michael Niedermair
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

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.dante.extex.interpreter.type.Dimen;
import de.dante.extex.interpreter.type.Font;
import de.dante.extex.interpreter.type.Glue;
import de.dante.extex.main.MainFontException;
import de.dante.util.GeneralException;
import de.dante.util.UnicodeChar;
import de.dante.util.configuration.ConfigurationException;
import de.dante.util.file.FileFinder;

/**
 * This class implements a svg-font.
 * It use a fontfile in efm-format.
 * 
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.1 $
 */
public class SVGFont implements Font {

	/**
	 * The fontname
	 */
	private String name;

	/**
	 * Creates a new object.
	 */
	public SVGFont(String name, FileFinder fileFinder) throws GeneralException, ConfigurationException {
		super();
		// trim name !
		if (name != null) {
			this.name = name.trim();
		}
		loadFont(fileFinder);
	}

	/**
	 * load the Font
	 * @throws GeneralException, if a error is thrown.
	 */
	private void loadFont(FileFinder finder) throws GeneralException, ConfigurationException {
		if (name != null) {

			File fontfile = finder.findFile(name, "efm");

			System.err.println("load FONT " + fontfile); // TODO kill line after test

			if (fontfile.exists()) {

				try {

					// create a document with SAXBuilder (without validate)
					SAXBuilder builder = new SAXBuilder(false);
					Document doc = builder.build(fontfile);

					fontgroup = doc.getRootElement();

				} catch (JDOMException e) {
					throw new MainFontException(e.getMessage()); // TODO change
				} catch (IOException e) {
					throw new MainFontException(e.getMessage()); // TODO change
				}

			} else {
				throw new MainFontException("font not found"); // TODO change
			}
		} else {
			throw new MainFontException("fontname not valid"); // TODO change
		}
	}

	/**
	 * The fontgroup-element
	 */
	private Element fontgroup = null;

	/**
	 * @see de.dante.extex.interpreter.type.Font#getSpace()
	 */
	public Glue getSpace() {
		return new Glue(12 * Dimen.ONE);// TODO change
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#getEm()
	 */
	public Dimen getEm() {
		return new Dimen(12 * Dimen.ONE);// TODO change
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#getEx()
	 */
	public Dimen getEx() {
		return new Dimen(6 * Dimen.ONE);
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#getFontDimen(long)
	 */
	public Dimen getFontDimen(long index) {
		return null;
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#getFontName()
	 */
	public String getFontName() {
		return name;
	}

	public String toString() {
		return "<fontname: " + getFontName() + " >";
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#getDepth(de.dante.util.UnicodeChar)
	 */
	public Dimen getDepth(UnicodeChar c) {
		return null;
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#getHeight(de.dante.util.UnicodeChar)
	 */
	public Dimen getHeight(UnicodeChar c) {
		return null;
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#getWidth(de.dante.util.UnicodeChar)
	 */
	public Dimen getWidth(UnicodeChar c) {
		return null;
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#isDefined(de.dante.util.UnicodeChar)
	 */
	public boolean isDefined(UnicodeChar c) {
		return false;
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#kern(de.dante.util.UnicodeChar, de.dante.util.UnicodeChar)
	 */
	public Dimen kern(UnicodeChar c1, UnicodeChar c2) {
		return null;
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#ligature(de.dante.util.UnicodeChar, de.dante.util.UnicodeChar)
	 */
	public String ligature(UnicodeChar c1, UnicodeChar c2) {
		return null;
	}
	
	
	/**
	 * @see de.dante.extex.interpreter.type.Font#externalFileName()
	 */
	public String externalFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.dante.extex.interpreter.type.Font#isExternalFont()
	 */
	public boolean isExternalFont() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
