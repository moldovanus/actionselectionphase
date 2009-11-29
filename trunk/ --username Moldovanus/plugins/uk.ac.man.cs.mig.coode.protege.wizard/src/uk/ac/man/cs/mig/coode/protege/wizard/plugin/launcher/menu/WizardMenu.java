package uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.menu;

import uk.ac.man.cs.mig.coode.protege.wizard.action.WizardStartAction;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.WizardContainer;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
class WizardMenu extends JMenu implements WizardContainer {

  private HashMap menuItems;

  public WizardMenu(String title) {
    super(title);

    menuItems = new HashMap();

    addMenuListener(new MenuListener() {
      public void menuCanceled(MenuEvent e) {
      }

      public void menuDeselected(MenuEvent e) {
      }

      public void menuSelected(MenuEvent e) {
        Collection wizardActions = menuItems.keySet();
        for (Iterator i = wizardActions.iterator(); i.hasNext();) {
          ProtegeWizard currentWizard = (ProtegeWizard) i.next();
          JMenuItem current = (JMenuItem) menuItems.get(currentWizard);
          Collection errors = currentWizard.canBeLaunched();
          if (errors == null) {
            current.setEnabled(true);
            current.setToolTipText(currentWizard.getWizardDescription());
          }
          else {
            current.setEnabled(false);
            current.setToolTipText((String) errors.iterator().next());
          }
        }
      }
    });
  }

  public boolean addWizard(ProtegeWizard theWizard) {
    JMenuItem theItem = add(new WizardStartAction(theWizard));

    theItem.setToolTipText(theWizard.getWizardDescription());

    menuItems.put(theWizard, theItem);

    setVisible(true);

    return (theItem != null);
  }

  public boolean removeWizard(ProtegeWizard theWizard) {
    JMenuItem theItem = (JMenuItem) menuItems.remove(theWizard);
    remove(theItem);

    if (menuItems.size() < 1) {
      setVisible(false);
    }

    return (theItem != null);
  }
}