package uk.ac.man.cs.mig.coode.protege.wizard.api;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class WizardAPI {

    private static final boolean debug = Trace.frameworkTrace;

    private static WizardManager manager = null;

    public static void setWizardManager(WizardManager m) {
//        if ((manager == null) || (m == null)){
        manager = m;
//        }
//        else{
//            System.err.println("WARNING - you have already set the manager");
//        }
    }

    public static WizardManager getWizardManager() {
        return manager;
    }

    public static String getUserOption(int prop) {
        return getWizardManager().getUserOptions().getProperty(prop);
    }

    public static int getUserOptionAsInt(int prop) {
        return getWizardManager().getUserOptions().getInteger(prop).intValue();
    }

    public static void setShowResults(boolean show) {
        getWizardManager().getUserOptions().setBoolean(WizardOptions.OPTION_SHOW_RESULTS, show);
    }

    public static void setShowIntro(boolean show) {
        getWizardManager().getUserOptions().setBoolean(WizardOptions.OPTION_SHOW_INTRO, show);
    }

    public static boolean getIntroVisible() {
        return getWizardManager().getUserOptions().getBoolean(WizardOptions.OPTION_SHOW_INTRO);
    }

    public static boolean getResultsVisible() {
        return getWizardManager().getUserOptions().getBoolean(WizardOptions.OPTION_SHOW_RESULTS);
    }

    public static boolean getNavVisible() {
        return getWizardManager().getUserOptions().getBoolean(WizardOptions.OPTION_SHOW_NAV);
    }

    public static boolean getCreateWithBrowserSlot() {
        return getWizardManager().getUserOptions().getBoolean(WizardOptions.OPTION_CREATE_BROWSER_SLOT);
    }

    public static boolean getMetaActive() {
        return getWizardManager().getUserOptions().getBoolean(WizardOptions.OPTION_IMPORT_META);
    }

    public static String getProperty(String key) {
        return getWizardManager().getProperties().getProperty(key, "string not set");
    }
}
