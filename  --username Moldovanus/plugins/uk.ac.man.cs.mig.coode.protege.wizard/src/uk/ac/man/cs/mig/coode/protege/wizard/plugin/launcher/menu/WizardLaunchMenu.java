package uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.menu;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.WizardContainer;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;

import javax.swing.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class WizardLaunchMenu implements WizardContainer {

    private static final String parentMenuName = "Tools";

    public static final int STATE_IDLE = 0;
    public static final int STATE_READY_TO_DISPLAY = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_SHUTDOWN = 3;

    private int currentState = STATE_IDLE;

    private String subMenuName = "General";

    private AbstractList loadedWizards = new ArrayList();
    private WizardContainer wizardLauncher = null;

    private JMenu toolsMenu = null;

//  private static WizardOptionsAction optionsAction = null;

    public WizardLaunchMenu(String name) {
        if (name != null) {
            this.subMenuName = name;
        }
    }

    public boolean initialiseGUI(JMenuBar projectMenuBar) throws WizardInitException {
        if (currentState == STATE_IDLE) {

            toolsMenu = getToolsMenu(projectMenuBar);
            //protegeMenu = projectMenuBar;

            if (loadedWizards.size() > 0) {
                createMenu();

                // add all of the wizards currently loaded to the new menu
                Iterator i = loadedWizards.iterator();
                while (i.hasNext()) {
                    if (Trace.frameworkTrace) System.out.println("adding a wizard to the menu");
                    wizardLauncher.addWizard((ProtegeWizard) i.next());
                }
            } else {
                setState(STATE_READY_TO_DISPLAY);
            }
        } else {
            throw new WizardInitException("Already initialised this wizard menu GUI");
        }

        return (wizardLauncher != null);
    }

    private JMenu getToolsMenu(JMenuBar projectMenuBar) {
        JMenu toolsMenu = null;

        for (int i = 0; (i < projectMenuBar.getMenuCount()) && (toolsMenu == null); i++) {
            JMenu current = projectMenuBar.getMenu(i);
            String currentName = current.getText();
            if (currentName.equals(parentMenuName)) {
                toolsMenu = current;
            }
        }

//    if (optionsAction == null) {
//      optionsAction = new WizardOptionsAction(options);
//    }

        if (toolsMenu == null) {
            // create it, if it doesn't already exist
            toolsMenu = new JMenu(parentMenuName);
            projectMenuBar.add(toolsMenu);
//      toolsMenu.add(optionsAction);
        }
//    else {
//      boolean found = false;
//      int items = toolsMenu.getItemCount();
//      for (int i = 0; i < items; i++) {
//        if (toolsMenu.getItem(i).getAction() == optionsAction) {
//          found = true;
//        }
//      }
//      if (!found) {
//        toolsMenu.add(optionsAction);
//      }
//    }

        return toolsMenu;
    }

    public boolean killGUI() {
        if (Trace.frameworkTrace) System.out.println("killing the GUI");
        if (currentState == STATE_RUNNING) {
            if (Trace.frameworkTrace) System.out.println("removing the menu");
            toolsMenu.remove((JComponent) wizardLauncher);

            setState(STATE_SHUTDOWN);

            wizardLauncher = null;
            removeAllWizards();

            setState(STATE_IDLE);
        } else {
            if (Trace.menuStateTrace) System.out.println("killing error, state = " + printState(currentState));
            setState(STATE_IDLE);
        }
        return true;
    }

    /**
     * Add an already existing wizard to the manager
     *
     * @param theWizard
     * @return true, if added correctly, false is the wizard is not suitable for the
     *         knowledgebase or is already loaded.
     */
    public boolean addWizard(ProtegeWizard theWizard) {
        boolean done = false;

        if (!loadedWizards.contains(theWizard)) {
            KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
            if ((theWizard.isSuitable(kb))) {
                if (currentState == STATE_READY_TO_DISPLAY) {
                    createMenu();
                }

                loadedWizards.add(theWizard);

                if (currentState == STATE_RUNNING) {
                    wizardLauncher.addWizard(theWizard);
                }
                done = true;
            }
        } else {
            System.err.println("Cannot add duplicate of wizard (" + theWizard.getWizardLabel() + ")");
        }
        return done;
    }

    public boolean removeWizard(ProtegeWizard theWizard) {
        boolean done = false;
        if (loadedWizards.contains(theWizard)) {
            loadedWizards.remove(theWizard);
            if (currentState == STATE_RUNNING) {
                wizardLauncher.removeWizard(theWizard);
                if (loadedWizards.size() < 1) {
                    killGUI();
                }
            }
            done = true;
        }

        return done;
    }

    public boolean removeAllWizards() {
        boolean done = false;
        if (loadedWizards.size() > 0) {
            Iterator i = loadedWizards.iterator();
            while (i.hasNext()) {
                ProtegeWizard theWizard = (ProtegeWizard) i.next();
                i.remove();
                if (currentState == STATE_RUNNING) {
                    wizardLauncher.removeWizard(theWizard);
                    if (loadedWizards.size() < 1) {
                        killGUI();
                    }
                }
            }
            done = true;
        }
        return done;
    }

    private void createMenu() {
        if (Trace.frameworkTrace) System.out.println("creating a new wizard menu: " + subMenuName);

        wizardLauncher = new WizardMenu(subMenuName);

        toolsMenu.add((JMenu) wizardLauncher);

        setState(STATE_RUNNING);
    }

    private void setState(int newState) {
        if (Trace.menuStateTrace) System.out.println("state change [" + printState(newState) + "]");
        currentState = newState;
    }

    private String printState(int state) {
        String string;
        switch (state) {
            case STATE_IDLE:
                string = "idle";
                break;
            case STATE_RUNNING:
                string = "running";
                break;
            case STATE_SHUTDOWN:
                string = "shutdown";
                break;
            case STATE_READY_TO_DISPLAY:
                string = "ready to display";
                break;
            default:
                string = "undefined state";
        }
        return string;
    }

    public ProtegeWizard getWizard(String name) {
        ProtegeWizard foundWizard = null;
        Iterator i = loadedWizards.iterator();
        while (i.hasNext() && (foundWizard == null)) {
            ProtegeWizard current = (ProtegeWizard) i.next();
            if (current.getWizardLabel().equals(name)) {
                foundWizard = current;
            }
        }
        return foundWizard;
    }

    public String getName() {
        return subMenuName;
    }
}
