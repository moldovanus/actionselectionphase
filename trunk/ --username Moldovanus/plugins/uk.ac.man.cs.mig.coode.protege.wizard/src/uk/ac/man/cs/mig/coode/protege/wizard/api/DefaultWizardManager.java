package uk.ac.man.cs.mig.coode.protege.wizard.api;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.util.ClassIdentifierTester;
import uk.ac.man.cs.mig.coode.protege.wizard.util.IdGenerator;
import uk.ac.man.cs.mig.coode.protege.wizard.util.NumericConceptIdGenerator;
import uk.ac.man.cs.mig.coode.protege.wizard.util.StandardProtegeClassIdentifier;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.ValidatorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.BasicNameValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.ProtegeFrameNameValidatorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         02-Jun-2005
 */
public class DefaultWizardManager implements WizardManager {

    private static final String DEFAULT_PROPERTIES_FILE = "resources/default.properties";

    // stored in the resources directory (stored with the classes in the jar)
    private Properties defaultProperties;

    // the options, defaults found in the resources dir
    private WizardOptions editableOptions;

    private IdGenerator defaultIdGenerator;
    private ValidatorFactory defaultValidatorFactory;
    private KnowledgeBase kb;

    /**
     * @param kb - the KnowledgeBase
     */
    public DefaultWizardManager(KnowledgeBase kb) {

        // set the KB we are working with
        this.kb = kb;

        // load the general wizard properties
        defaultProperties = new Properties();
        String res = DEFAULT_PROPERTIES_FILE;
        try {
            //URL resUrl = DefaultWizardManager.class.getResource(res);
            //System.out.println("resUrl.getPath() = " + resUrl.getPath());
            InputStream iStream = DefaultWizardManager.class.getResourceAsStream(res);
            defaultProperties.load(iStream);
        }
        catch (IOException e) {
            System.err.println("couldn't load " + res);
        }

        File optionsFile = null;
        try {
        // load the user-changeable properties
        	optionsFile = new File(getDefaultDirectory() + File.separator +
                defaultProperties.getProperty("options.filename"));
        } catch (Exception e) {
        	//do nothing
        }
        editableOptions = new WizardOptions(optionsFile);

        // show the wizard version info for debugging
        String versionString = defaultProperties.getProperty("version");
        System.out.println(defaultProperties.getProperty("startup") + " " + versionString);

        // @@TODO is this needed anymore now that the util classes are not static?
        ClassIdentifierTester.registerClassIdentifier(new StandardProtegeClassIdentifier());

        // Create a default ID generator (for when a browser slot is used)
        String defId = defaultProperties.getProperty("id.prefix");
        setDefaultIdGenerator(new NumericConceptIdGenerator(defId, 0, 6, kb));

        // Create a validator factory
        setDefaultValidatorFactory(new ProtegeFrameNameValidatorFactory());
    }

    public Frame getMainWindow() {
        return null;
    }

    public KnowledgeBase getKnowledgeBase() {
        return kb;
    }

    public void setDefaultIdGenerator(IdGenerator idGen) {
        // @@TODO this needs to save the prefix and padding back to the wizard.properties
        defaultIdGenerator = idGen;
    }

    public IdGenerator getDefaultIdGenerator() {
        return defaultIdGenerator;
    }

    public void setDefaultValidatorFactory(ValidatorFactory fac) {
        defaultValidatorFactory = fac;
    }

    public BasicNameValidator getDefaultValidator(KnowledgeBase kb, PropertiesCollection pc) {
        BasicNameValidator val = null;
        if (kb != null) {
            val = defaultValidatorFactory.create(kb, pc);
        } else {
            System.err.println("");
        }
        return val;
    }

    public void updateUI() {
        // do nothing
    }

    public void setActiveFrameInUI(edu.stanford.smi.protege.model.Frame f) {
        // do nothing
    }

    public java.util.List getActiveFramesFromUI() {
        return null;
    }

    public String getDefaultDirectory() {
        return "";
    }

    public void handleErrors(java.util.List errors) {
        String message;
        if (errors != null) {
            message = defaultProperties.getProperty("error");
            for (Iterator i = errors.iterator(); i.hasNext();) {
                message += i.next() + "\n";
            }
        } else {
            message = defaultProperties.getProperty("unknownerror");
        }
        JOptionPane.showMessageDialog(getMainWindow(),
                message,
                defaultProperties.getProperty("errorDialogHeader"),
                JOptionPane.ERROR_MESSAGE);
    }

    public void handleErrors(Exception e) {
        List errors = new ArrayList();
        StackTraceElement[] trace = e.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            errors.add(trace[i]);
        }
        handleErrors(errors);
    }

    public WizardOptions getUserOptions() {
        return editableOptions;
    }

    public Properties getProperties() {
        return defaultProperties;
    }

    public void addProperties(InputStream propertiesStream) throws IOException {
        defaultProperties.load(propertiesStream);
    }

    public void kill() {
        defaultIdGenerator = null;
        defaultValidatorFactory = null;
    }
}

