package uk.ac.man.cs.mig.coode.protege.wizard.plugin;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.ui.ConfigureProjectPanel;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.DefaultWizardManager;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.menu.WizardLaunchMenu;
import uk.ac.man.cs.mig.coode.protege.wizard.util.GuiUtils;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         02-Jun-2005
 */
public class ProtegeGuiWizardManager extends DefaultWizardManager {

    public static final boolean debug = false;

    private WizardLaunchMenu defaultWizardMenu;
    private java.util.List userWizardMenus;
    private JMenuBar menuBar;
    private Frame protegeWindow;

    public ProtegeGuiWizardManager(KnowledgeBase kb, JMenuBar menuBar) {
        super(kb);
        defaultWizardMenu = new WizardLaunchMenu("Wizards");

        this.menuBar = menuBar;
        protegeWindow = (Frame) menuBar.getTopLevelAncestor();

        try {
            ConfigureProjectPanel.registerConfigureTab("Wizards",
                    "uk.ac.man.cs.mig.coode.protege.wizard.api.WizardOptionsPanel");

            defaultWizardMenu.initialiseGUI(menuBar);
            if (userWizardMenus != null) {
                Iterator i = userWizardMenus.iterator();
                while (i.hasNext()) {
                    ((WizardLaunchMenu) i.next()).initialiseGUI(menuBar);
                }
            }
        }
        catch (WizardInitException e) {
            e.printStackTrace();  //@@TODO replace default frameworkTrace
        }
    }

    public Frame getMainWindow() {
        return protegeWindow;
    }

    public String getDefaultDirectory() {
    	File pluginDir = PluginUtilities.getPluginsDirectory();
    	
        return pluginDir == null ? null : pluginDir.toString() + File.separator +
                getProperties().getProperty("plugin.dir");
    }

    public void updateUI() {
        ProjectManager.getProjectManager().reloadUI(true);
    }

    public java.util.List getActiveFramesFromUI() {
        java.util.List selection = null;
        try {
            java.util.List path = new ArrayList(GuiUtils.getClassTab().getSelection());
            selection = Collections.singletonList(path.get(path.size() - 1));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR : INTERFACE NOT READY");
        }

        return selection;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public WizardLaunchMenu getDefaultWizardMenu() {
        if (defaultWizardMenu == null) {
            System.err.println("WARNING: HAVE NOT YET SET UP THE DEFAULT WIZARD MENU");
        }
        return defaultWizardMenu;
    }

    public WizardLaunchMenu getNewWizardMenu(String newMenuName) {
        if (Trace.frameworkTrace) System.out.println("WizardAPI.getNewWizardMenu");

        WizardLaunchMenu theMenu = null;

        if (defaultWizardMenu != null) {
            // go and get the submenu if it already exists
            theMenu = getSubMenu(newMenuName);
            boolean newMenuCreated = false;

            if (userWizardMenus == null) {
                theMenu = new WizardLaunchMenu(newMenuName);
                userWizardMenus = CollectionUtilities.createList(theMenu);
                newMenuCreated = true;
            } else if (theMenu == null) {
                theMenu = new WizardLaunchMenu(newMenuName);
                userWizardMenus.add(theMenu);
                newMenuCreated = true;
            }

            // check if we've already created menus
            if ((defaultWizardMenu != null) && (newMenuCreated)) {
                try {
                    // so we need to create each one as we go
                    theMenu.initialiseGUI(menuBar);
                }
                catch (WizardInitException e) {
                    e.printStackTrace();  //@@TODO replace default frameworkTrace
                }
            }
        } else {
            System.err.println("WARNING - cannot create a new wizard menu - no menubar specified");
        }
        return theMenu;
    }

    private WizardLaunchMenu getSubMenu(String name) {
        WizardLaunchMenu theMenu = null;
        if (userWizardMenus != null) {
            Iterator i = userWizardMenus.iterator();
            while (i.hasNext() && theMenu == null) {
                WizardLaunchMenu current = (WizardLaunchMenu) i.next();
                if (current.getName().equals(name)) {
                    theMenu = current;
                }
            }
        }
        return theMenu;
    }

    public void kill() {
        super.kill();
        defaultWizardMenu.killGUI();

        if (userWizardMenus != null) {
            Iterator i = userWizardMenus.iterator();
            while (i.hasNext()) {
                ((WizardLaunchMenu) i.next()).killGUI();
            }
        }

        ConfigureProjectPanel.unregisterConfigureTab("Wizards");

        userWizardMenus = null;
    }
}
