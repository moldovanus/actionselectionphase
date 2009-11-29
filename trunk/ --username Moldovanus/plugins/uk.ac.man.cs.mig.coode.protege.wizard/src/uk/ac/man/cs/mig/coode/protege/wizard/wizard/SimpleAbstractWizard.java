package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.console.StandardOut;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ProtegeWizardEvent;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ProtegeWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.GuiUtils;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.BasicNameValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
abstract class SimpleAbstractWizard extends PropertiesCollection
        implements ProtegeWizard {

    private static final boolean debug = Trace.overallTrace;

    protected static final String BUTTON_PREV_LABEL = "button.prev.label";
    protected static final String BUTTON_NEXT_LABEL = "button.next.label";
    protected static final String BUTTON_FINISH_LABEL = "button.finish.label";
    protected static final String BUTTON_CANCEL_LABEL = "button.cancel.label";

    private static final String WIZARD_WIDTH_PROP = "ui.defaultwidth";
    private static final String WIZARD_HEIGHT_PROP = "ui.defaultheight";

    // validating the flow of control
    WizardStateMachine stateMachine;

    // the wizard object being wrapped
    WizardDialog wizardDialog;

    // the pages to be added to the wizard
    List pages = new ArrayList();

    // used when adding unique properties to the wizard
    private BasicNameValidator defaultValidator = null;

    private WizardEventBehaviourAdapter wizAdapter = null;

    private WindowListener winAdapter = null;

    Console defaultConsole = new StandardOut();

    private edu.stanford.smi.protege.model.Frame kbEntity = null;

    private Properties wizardProperties;

    private List errors;

    private int returnCode;

    private static Dimension defaultWizardSize = null;
    private static Point defaultWizardPosition = null;
    ;


    /**
     * The default constructor expects there to be a properties file called x.properties
     * (where x is the wizard class name) in a subfolder of the class' package called "resources/"
     * This properties file should contain all the labels for the wizard.
     */
    public SimpleAbstractWizard() {
        this(null);
    }

    /**
     * Constructor to be used when anonymous wizards are created
     *
     * @param propertiesFileName expected to be in a subfolder called "resources/"
     */
    public SimpleAbstractWizard(String propertiesFileName) {
        this(null, null, propertiesFileName);
    }

    public SimpleAbstractWizard(String wizardName, String wizardDescription) {
        this(wizardName, wizardDescription, null);

        System.err.println("This constructor should only be used when first developing a wizard.\n" +
                "To help support multi-languages, please call the alternative constructor super()");
    }

    private SimpleAbstractWizard(String wizardName, String wizardDescription, String propertiesFileName) {
        if (WizardAPI.getWizardManager() == null) {
            System.err.println("Before creating wizards, please initialise the Wizard API.\n");
            System.err.println("You must call WizardAPI.setWizardManager()!!");
        }
        else {
            loadWizardProperties(propertiesFileName);

            if (wizardName != null) {
                wizardProperties.setProperty("label", wizardName);
            }

            if (wizardDescription != null) {
                wizardProperties.setProperty("description", wizardDescription);
            }

            stateMachine = new WizardStateMachine(this);
            stateMachine.sendEvent(ProtegeWizardEvent.CREATE_WIZARD_EVENT);
        }
    }

    private void loadWizardProperties(String propertiesFileName) {

        if (propertiesFileName == null) { // assume the name of the wizard class
            String packageName = getClass().getPackage().getName();
            String classNameWithPackage = getClass().getName();
            String className = classNameWithPackage.substring(packageName.length() + 1);
            propertiesFileName = className + ".properties";
        }

        // default to the wizard manager's properties which contain all the button labels
        // and errors etc
        wizardProperties = new Properties(WizardAPI.getWizardManager().getProperties());
        String res = "resources/" + propertiesFileName;
        try {
            wizardProperties.load(getClass().getResourceAsStream(res));
        }
        catch (Exception e) {
            System.err.println("couldn't load " + res);
            warnAboutResources();
        }
    }

    public final String getError(String errorProp) {
        return getWizardProperty("error." + errorProp);
    }

    public final String getWizardProperty(String key) {
        return wizardProperties.getProperty(key);
    }

    public final String getWizardLabel() {
        return getWizardProperty("label");
    }

    public final String getWizardDescription() {
        return getWizardProperty("description");
    }

    public boolean isSuitable(KnowledgeBase kb) {
        return true;
    }

    /**
     * The user should override this if the wizard, for example, depends
     * on certain conditions holding in the KB or the interface
     *
     * @return null if everything is OK
     *         collection of error strings if the wizard can't currently be launched
     */
    public List canBeLaunched() {
        return null;
    }

    /**
     * Has protected visibility to allow subclassing outside of package
     * Do the actual GUI creation etc in this method, including:
     * - setting up properties
     * - adding pages
     */
    protected abstract void initialise() throws NullPointerException, WizardInitException;

    /**
     * Has protected visibility to allow subclassing outside of package
     *
     * @return
     */
    protected abstract boolean handleFinished(Console output);

    /////////////////////////////////// API

    public final String getPageLabel(String pageName) {
        return getPageProperty(pageName, "label");
    }

    public final String getPagePrompt(String pageName) {
        return getPageProperty(pageName, "prompt");
    }

    public final String getPageProperty(String pageName, String key) {
        String string = getWizardProperty("page." + pageName + "." + key);
        if (string == null) {
            System.err.println("WARNING - you should set " + key + " for page " + pageName + " on Wizard " + getWizardLabel());
            string = "String not set";
        }
        return string;
    }

    public final void setKbEntity(edu.stanford.smi.protege.model.Frame entity) {
        kbEntity = entity;
    }

    /**
     * Pages will be accessed in the order they were added
     * - Cannot add any pages after the wizard has been started
     * - Cannot add the same page twice
     *
     * @param thePage the page to add
     * @return if this has been successful, given the above criteria
     */
    public final boolean addPage(ProtegeWizardPage thePage) throws WizardInitException {
        return addPage(-1, thePage);
    }

    /**
     * Insert a page into the list at the index provided
     *
     * @see <code>addPage(AbstractWizardPAge thePage)</code>
     */
    public final boolean addPage(int index, ProtegeWizardPage thePage)
            throws WizardInitException {
        boolean done = false; // could throw an exception

        if (!pages.contains(thePage)) {
            try {
                pages.add(index, thePage);
            }
            catch (IndexOutOfBoundsException ie) {
                pages.add(thePage);
            }
            done = true;
        }
        else {
            throw new WizardInitException("Duplicate pages cannot currently be added to the wizard (page = " + thePage.getName() + ")");
        }

        return done;
    }

    public int showModalDialog() {
        returnCode = WIZARD_FAIL;
        try {
            errors = canBeLaunched();
            if (errors == null) {
                stateMachine.sendEvent(ProtegeWizardEvent.USER_LAUNCH_EVENT);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    public List getErrors() {
        return errors;
    }

    public final String addProperty(String propName, Object value) {
        String error = null;
        //if (stateMachine.sendEvent(ProtegeWizardEvent.ADD_PROP_EVENT)) {
        error = super.createProperty(propName, value);
        //}
        return error;
    }

    public final String addUniqueProperty(String propName, Object value) throws WizardInitException {
        String error = null;
        //if (stateMachine.sendEvent(ProtegeWizardEvent.ADD_PROP_EVENT)) {
        error = super.createProperty(propName, value);
        if (error == null) {
            if (defaultValidator == null) {
                defaultValidator = WizardAPI.getWizardManager().getDefaultValidator(getKnowledgeBase(), this);
            }
            defaultValidator.addUniqueProperty(propName);
        }
//    } else {
//      throw new WizardInitException("Cannot add properties outside of the setComponent() method (property = " + propName + ")");
//    }
        return error;
    }

    public BasicNameValidator getDefaultValidator() {
        return defaultValidator;
    }

    protected final KnowledgeBase getKnowledgeBase() {
        return WizardAPI.getWizardManager().getKnowledgeBase();
    }

    protected final KbHelper getHelper() {
        return KbHelper.getHelper(getKnowledgeBase());
    }

    protected final edu.stanford.smi.protege.model.Frame getKbEntity() {
        return kbEntity;
    }

    Console getDefaultConsole() {
        return defaultConsole;
    }

    void setDefaultConsole(Console defaultConsole) {
        this.defaultConsole = defaultConsole;
    }

    /////////////////////////////////// Internal control

    /**
     *
     */
    void primaryWizardInit() {

        // create the wizard and set the labels for the buttons
        wizardDialog = new WizardDialog(getParentWindow(),
                                        getWizardLabel(),
                                        getWizardProperty(BUTTON_PREV_LABEL),
                                        getWizardProperty(BUTTON_NEXT_LABEL),
                                        getWizardProperty(BUTTON_FINISH_LABEL),
                                        getWizardProperty(BUTTON_CANCEL_LABEL));

        // current default for Wizard is to ignore, so we add this back in
        wizardDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // behaviour adapter must be added in startWizardSetup() so any further listeners
        // do not get added until later as it makes sure certain code is called
        wizAdapter = getBehaviourAdapter();
        wizardDialog.addWizardListener(wizAdapter);

        winAdapter = getWindowBehaviourAdapter();
        wizardDialog.addWindowListener(winAdapter);
    }

    protected Frame getParentWindow() {
        return WizardAPI.getWizardManager().getMainWindow();
    }

    /**
     * @throws WizardInitException
     */
    void finalWizardInit() throws WizardInitException {

        if (pages.size() > 0) {
            WizardPage[] wizardPagesArray = new WizardPage[pages.size()];

            // create the wizard pages array from the pages list
            // and set the properties source for each page to be this wizard
            int j = 0;
            for (Iterator i = pages.iterator(); i.hasNext();) {
                ProtegeWizardPage currentPage = (ProtegeWizardPage) i.next();
                currentPage.setProtegeWizard(this);
                currentPage.addKeyListener(wizAdapter);
                wizardPagesArray[j++] = currentPage.getWizardPage();
            }

            // add all the pages to the wizard
            wizardDialog.setPages(wizardPagesArray);

            // useful for it to be resizeable for matrix etc
            wizardDialog.setResizable(true);
        }
        else {
            throw new WizardInitException(getError("noPages"));
        }
    }

    final void showWizard() {

        if (defaultWizardSize == null) {
            int width = Integer.parseInt(getWizardProperty(WIZARD_WIDTH_PROP));
            int height = Integer.parseInt(getWizardProperty(WIZARD_HEIGHT_PROP));
            defaultWizardSize = new Dimension(width, height);
        }

        if (defaultWizardPosition == null) {
            wizardDialog.setSize(defaultWizardSize);
            wizardDialog.setLocationRelativeTo(null);
        }
        else {
            wizardDialog.setBounds(new Rectangle(defaultWizardPosition, defaultWizardSize));
        }

        try {
            // display the wizard
            if (wizardDialog.showWizard() != WizardDialog.OPTION_APPROVE) {
                //throw new WizardInitException("Cannot show the Wizard");
            }
        }
        catch (Exception e) {
            System.err.println("WIZARD: EXCEPTION CAUGHT");
            e.printStackTrace(); // @@TODO some nice error handling
            returnCode = WIZARD_FAIL;
            wizardDialog.dispose();
            shutDownWizard();
        }
    }

    WizardEventBehaviourAdapter getBehaviourAdapter() {
        return new WizardEventBehaviourAdapter();
    }

    WindowListener getWindowBehaviourAdapter() {
        return new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                defaultWizardPosition = wizardDialog.getLocationOnScreen();
                defaultWizardSize = wizardDialog.getSize();
            }

            public void windowClosed(WindowEvent e) {
                stateMachine.sendEvent(ProtegeWizardEvent.SHUTDOWN_REQUEST_EVENT);
                if (returnCode != WIZARD_SUCCESS) {
                    returnCode = WIZARD_CANCEL;
                }
            }
        };
    }

    final boolean shutDownWizard() {
        clearAllProperties();
        removeAllPropertyChangeListeners();

        // remove the key behaviour adapter from each wizard page
        for (Iterator i = pages.iterator(); i.hasNext();) {
            ProtegeWizardPage wp = (ProtegeWizardPage) i.next();
            wp.removeKeyListener(wizAdapter);
        }

        pages.clear();

        // remove the behaviour and window adapter from the wizard
        if (wizardDialog != null) {
            wizardDialog.removeListener(wizAdapter);
            wizAdapter = null;
            wizardDialog.removeWindowListener(winAdapter);
            winAdapter = null;
            wizardDialog = null;
        }

        kbEntity = null;

        return true;
    }

    void pageHasChanged(WizardPage currentPage) {
    }

    public boolean doFinish() {
        boolean result = false;
        wizardDialog.setNextButtonEnabled(false);

        Console out = getDefaultConsole();
        GuiUtils.setConsole(out);

        long startTime = System.currentTimeMillis();

        // knowledgebase transaction handling
        try {
            getKnowledgeBase().beginTransaction(getWizardLabel());
            out.out("Wizard transaction: started");
            //getDefaultConsole().out("Wizard transaction: to be reenabled");

            result = handleFinished(out);
        }
        catch (Exception ex) {
            out.err("The wizard transaction failed for " + getWizardLabel());
            StackTraceElement[] stack = ex.getStackTrace();
            for (int i = 0; i < stack.length; i++) {
                out.err(stack[i].toString());
            }
        }
        finally {
            double secs = (System.currentTimeMillis() - startTime) / 1000.0;
            out.out("Wizard transaction: ended after " + secs + " seconds");
            if (!result) {
                out.err("The wizard failed to complete");

                out.err("please report this to protege-owl@lists.stanford.edu");
            }
            else {
                out.out("Success!");
                returnCode = WIZARD_SUCCESS;

                wizardDialog.setNextButtonEnabled(true);

                // can't go back, as already completed
                wizardDialog.setPrevButtonEnabled(false);

                // can't cancel, as already completed
                wizardDialog.setCancelButtonEnabled(false);
            }

            if (getKnowledgeBase().endTransaction(result)) {
                out.out("Transaction handling succeeded");
            }
            else {
                out.err("Transaction handling failed");
            }
        }
        return result;
    }

    ///////////////////////////////// Internal classes for modified wizard behaviour

    /**
     * required behavioural changes to the wizard buttons and key presses
     */
    class WizardEventBehaviourAdapter extends KeyAdapter implements WizardEventListener {

        // does not need to be in here, but keeps it all together
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    if (e.isControlDown()) {
                        if (wizardDialog.getNextButtonEnabled()) {
                            wizardDialog.nextButtonPressed();
                        }
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (e.isControlDown()) {
                        if (wizardDialog.getPrevButtonEnabled()) {
                            wizardDialog.prevButtonPressed();
                        }
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (e.isControlDown()) {
                        if (wizardDialog.getNextButtonEnabled()) {
                            wizardDialog.nextButtonPressed();
                        }
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (debug) System.out.println("KILL WIZARD");
                    wizardDialog.cancelButtonPressed();
                    break;
                default:
                    //wizardDialog.handleKeyEvent(e);
            }
        }

        public void nextPressed(WizardEvent event) {
            WizardDialog parent = event.getSource();
            parent.getCurrentPage().nextButtonPressed(parent);
        }

        public void prevPressed(WizardEvent event) {
            WizardDialog parent = event.getSource();
            parent.getCurrentPage().prevButtonPressed(parent);
        }

        public void pageChanged(WizardEvent event) {
            pageHasChanged(wizardDialog.getCurrentPage());
        }

        public void finishPressed(WizardEvent event) {
            wizardDialog.getCurrentPage().nextButtonPressed(wizardDialog);
        }

        public void cancelPressed(WizardEvent event) {
            returnCode = WIZARD_CANCEL;
        }
    }

    public String toString() {
        StringBuffer descr = new StringBuffer("Wizard: ");
        descr.append(getWizardLabel());
        descr.append("(");
        for (Iterator i = pages.iterator(); i.hasNext();) {
            descr.append((ProtegeWizardPage) i.next());
            if (i.hasNext()) {
                descr.append(", ");
            }
        }
        descr.append(")");
        return descr.toString();
    }

    private void warnAboutResources() {
        System.err.println("WARNING: INCOMPLETE WIZARD IMPLEMENTATION");
        System.err.println("*******************************************************************************");
        System.err.println("You must now create a resource bundle for your new wizard: " + getClass());
        System.err.println("Create a subpackage called resources/ in you wizards package");
        System.err.println("Create a file called x.properties, where x is the classname of your wizard");
        System.err.println("(If your wizard is an anonymous java class, you can alternatively specify a name)");
        System.err.println("Add the following properties:");
        System.err.println("  label=name of your wizard");
        System.err.println("  description=short description of your wizard");
        System.err.println("  intro=relative path to html describing your wizard (optional)");
        System.err.println("  page.x.label=for each page (where x is the pagename) this is the readable label");
        System.err.println("  page.x.prompt=for each page (where x is the pagename) this is the prompt text");
        System.err.println("Create your wizard using the default constructor with no params, or the one specifying the properties filename");
        System.err.println("For more details (or questions) please see www.co-ode.org for contact details");
        System.err.println("*******************************************************************************");
    }
}
