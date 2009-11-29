package uk.ac.man.cs.mig.coode.protege.wizard.api;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import java.io.*;
import java.util.Properties;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Oct-2004
 */
public class WizardOptions {

    private static final String DEFAULT_OPTIONS_FILE = "resources/options.properties";

    private static final boolean debug = Trace.propertyTrace;

    public static final int OPTION_SHOW_RESULTS = 0;
    public static final int OPTION_SHOW_INTRO = 1;
    public static final int OPTION_SHOW_NAV = 2;
    public static final int OPTION_CREATE_BROWSER_SLOT = 3;
    public static final int OPTION_GENERATE_LISTS_DESCR = 4;
    public static final int OPTION_IMPORT_META = 5;
    public static final int DROPDOWN_THRESHOLD = 6;
    public static final int NUM_PROPS = 7;

    private static String[] keys = {"show results",
            "show intro",
            "show nav bar",
            "create new classes using browser slot",
            "generate annotations for OWL lists",
            "import meta ontology for patterns",
            "dropdown threshold"};

    private static Class[] types = {boolean.class,
            boolean.class,
            boolean.class,
            boolean.class,
            boolean.class,
            boolean.class,
            int.class};

    private File propsFile;

    private Properties properties;

    private Properties defaultProps = null;

    public WizardOptions(File propsFile) {
        InputStream defaultStream = WizardOptions.class.getResourceAsStream(DEFAULT_OPTIONS_FILE);
        defaultProps = loadProperties(null, defaultStream);

        this.propsFile = propsFile;
        properties = loadProperties(defaultProps, propsFile);
    }

    private Properties loadProperties(Properties defaultProps, File file) {
        Properties props = new Properties(defaultProps);
        try {
            props.load(new FileInputStream(file));
        }
        catch (IOException e) {
        }
        return props;
    }

    private Properties loadProperties(Properties defaultProps, InputStream iStream) {
        Properties props = new Properties(defaultProps);
        try {
            props.load(iStream);
        }
        catch (IOException e) {
            if (debug) System.err.println("could not find properties");
        }
        return props;
    }

    private void saveProperties(Properties props, File file) {
        if (debug) System.out.println("WizardOptions.saveProperties");
        try {
            props.store(new FileOutputStream(file), "properties for wizard");
        }
        catch (IOException e1) {
            if (debug) System.err.println("could not save wizard options");
        }
    }

    /**
     * Overloaded to make sure always saved to file
     *
     * @param property
     * @param value
     * @return previous value of the property
     */
    public Object setProperty(int property, String value) {
        if (debug) System.out.println("WizardOptions.setProperty");
        Object result = properties.setProperty(keys[property], value);
        saveProperties(properties, propsFile);
        return result;
    }

    public String getProperty(int property) {
        return properties.getProperty(keys[property]);
    }

    public void setBoolean(int property, boolean value) throws IllegalArgumentException {
        if (types[property] == boolean.class) {
            Boolean bValue = new Boolean(value);
            setProperty(property, bValue.toString());
        } else {
            throw new IllegalArgumentException("NOT BOOLEAN - property: " + keys[property] + " value: " + value);
        }
    }

    public boolean getBoolean(int property) throws IllegalArgumentException {
        if (types[property] == boolean.class) {
            return new Boolean(getProperty(property)).booleanValue();
        } else {
            throw new IllegalArgumentException("NOT BOOLEAN - property: " + keys[property]);
        }
    }

    public Class getType(int property) {
        return types[property];
    }

    public String getPropertyName(int property) {
        return keys[property];
    }

    public Integer getInteger(int property) {
        if (types[property] == int.class) {
            return new Integer(getProperty(property));
        } else {
            throw new IllegalArgumentException("NOT INTEGER - property: " + keys[property]);
        }
    }

    public void setInteger(int property, Integer value) throws IllegalArgumentException {
        if (types[property] == int.class) {
            setProperty(property, value.toString());
        } else {
            throw new IllegalArgumentException("NOT INTEGER - property: " + keys[property] + " value: " + value);
        }
    }
}
