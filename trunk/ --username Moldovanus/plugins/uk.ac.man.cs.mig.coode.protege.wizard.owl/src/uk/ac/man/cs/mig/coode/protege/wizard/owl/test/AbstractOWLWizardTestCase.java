package uk.ac.man.cs.mig.coode.protege.wizard.owl.test;

import edu.stanford.smi.protegex.owl.testing.AbstractOWLTest;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Mar-2006
 */
public class AbstractOWLWizardTestCase extends AbstractOWLTest {

    protected void setUp() throws Exception {
//        super.setUp();
//
//        WizardManager wizardManager = new DefaultWizardManager(owlModel);
//        WizardAPI.setWizardManager(wizardManager);
//
//        try {
//            // @@TODO could look for a file with the current language
//            File localPropsFile = new File(PluginUtilities.getPluginsDirectory() + File.separator +
//                    getPluginDirectory() + File.separator + "plugin.properties");
//            WizardAPI.getWizardManager().addProperties(new FileInputStream(localPropsFile));
//        }
//        catch (IOException e) {
//        } // don't worry if no file is found
    }

    protected String getPluginDirectory() {
        return "uk.man.ac.cs.mig.coode.protege.wizard.owl";
    }
}
