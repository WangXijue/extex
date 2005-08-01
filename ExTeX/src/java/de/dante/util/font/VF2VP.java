/*
 * Copyright (C) 2005 The ExTeX Group and individual authors listed below
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import de.dante.extex.font.FontFactory;
import de.dante.extex.font.type.PlWriter;
import de.dante.extex.font.type.vf.VFFont;
import de.dante.util.configuration.Configuration;
import de.dante.util.configuration.ConfigurationClassNotFoundException;
import de.dante.util.configuration.ConfigurationException;
import de.dante.util.configuration.ConfigurationFactory;
import de.dante.util.configuration.ConfigurationInstantiationException;
import de.dante.util.configuration.ConfigurationMissingAttributeException;
import de.dante.util.configuration.ConfigurationNoSuchMethodException;
import de.dante.util.file.random.RandomAccessInputStream;
import de.dante.util.resource.ResourceFinder;
import de.dante.util.resource.ResourceFinderFactory;

/**
 * Convert a VF-file to a VPL-file
 *
 * @author <a href="mailto:m.g.n@gmx.de">Michael Niedermair</a>
 * @version $Revision: 1.2 $
 */
public final class VF2VP {

    /**
     * private: no instance
     */
    private VF2VP() {

    }

    /**
     * parameter
     */
    private static final int PARAMETER = 2;

    /**
     * main
     * @param args      the comandlinearguments
     * @throws Exception  in case of an error
     */
    public static void main(final String[] args) throws Exception {

        if (args.length != PARAMETER) {
            System.err
                    .println("java de.dante.util.font.VF2VP <vf-file> <vp-file>");
            System.exit(1);
        }

        File plfile = new File(args[1]);
        String fontname = args[0].replaceAll("\\.vf|\\.VF", "");

        Configuration config = new ConfigurationFactory()
                .newInstance("config/extex.xml");

        // Configuration cfgfonts = config.getConfiguration("Fonts");

        Properties prop = new Properties();
        try {
            InputStream in = new FileInputStream(".extex");
            prop.load(in);
        } catch (Exception e) {
            prop.setProperty("extex.fonts", "src/font");
        }

        ResourceFinder finder = (new ResourceFinderFactory())
                .createResourceFinder(config.getConfiguration("Resource"),
                        null, prop);

        // EncFactory ef = new EncFactory(finder);

        // vf-file
        InputStream vfin = finder.findResource(args[0], "");

        if (vfin == null) {
            System.err.println(args[0] + " not found!");
            System.exit(1);
        }

        // psfonts.map
        InputStream psin = finder.findResource("psfonts.map", "");

        if (psin == null) {
            System.err.println("psfonts.map not found!");
            System.exit(1);
        }
        // PSFontsMapReader psfm = new PSFontsMapReader(psin);

        // RandomAccessInputFileDebug rar = new RandomAccessInputFileDebug("src/font/aer12.vf");

        VFFont font = new VFFont(new RandomAccessInputStream(vfin), fontname,
                makeFontFactory());

        //font.setFontMapEncoding(psfm, ef);

        PlWriter out = new PlWriter(new BufferedOutputStream(
                new FileOutputStream(plfile)));
        font.toPL(out);
        out.close();
    }

    /**
     * make a font factroy
     * @return  Return the fontfactory
     * @throws Exception if an error occurs.
     */

    private static FontFactory makeFontFactory() throws Exception {

        Configuration config = new ConfigurationFactory()
                .newInstance("config/extex.xml");

        FontFactory fontFactory = makeFontFactory(config
                .getConfiguration("Fonts"));

        return fontFactory;
    }

    /**
     * Create a new font factory.
     *
     * @param config the configuration object for the font factory
     *
     * @return the new font factory
     *
     * @throws ConfigurationException in case that some kind of problems have
     * been detected in the configuration
     */
    protected static FontFactory makeFontFactory(final Configuration config)
            throws ConfigurationException {

        FontFactory fontFactory;
        String fontClass = config.getAttribute("class");

        if (fontClass == null || fontClass.equals("")) {
            throw new ConfigurationMissingAttributeException("class", config);
        }

        ResourceFinder fontFinder = (new ResourceFinderFactory())
                .createResourceFinder(config.getConfiguration("Resource"),
                        null, getProps());
        if (Boolean.valueOf(getProps().getProperty("extex.trace.font.files"))
                .booleanValue()) {
            fontFinder.enableTracing(true);
        }

        try {
            fontFactory = (FontFactory) (Class.forName(fontClass)
                    .getConstructor(
                            new Class[]{Configuration.class,
                                    ResourceFinder.class})
                    .newInstance(new Object[]{config, fontFinder}));
        } catch (IllegalArgumentException e) {
            throw new ConfigurationInstantiationException(e);
        } catch (SecurityException e) {
            throw new ConfigurationInstantiationException(e);
        } catch (InstantiationException e) {
            throw new ConfigurationInstantiationException(e);
        } catch (IllegalAccessException e) {
            throw new ConfigurationInstantiationException(e);
        } catch (InvocationTargetException e) {
            throw new ConfigurationInstantiationException(e);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationNoSuchMethodException(e);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationClassNotFoundException(fontClass);
        }

        return fontFactory;
    }

    /**
     * Getter for props.
     *
     * @return the props
     */
    private static Properties getProps() {

        if (props == null) {
            props = System.getProperties();

            File file = new File(".extex");
            if (file.canRead()) {
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    props.load(inputStream);
                    inputStream.close();
                } catch (IOException e) {
                    // ignored on purpose
                }
            }
        }
        return (Properties) props.clone();
    }

    /**
     * The field <tt>props</tt> contains the merged properties from the
     * system properties and the properties loaded from <tt>.extex-test</tt>.
     */
    private static Properties props = null;

}