package uk.ac.man.cs.mig.coode.protege.wizard.wizard.test;

import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardEvent;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardEventListener;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardPage;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 7, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class WizardPage1 extends WizardPage {

  WizardDialog w;

  public WizardPage1(WizardDialog w) {
    super("EnterClassNamePage");


    this.w = w;

    w.addWizardListener(new WizardEventListener() {
      public void cancelPressed(WizardEvent e) {
        if (e.getSource().getCurrentPage() == WizardPage1.this) {
          System.out.println(getName() + " CancelPressed");
        }
      }

      public void finishPressed(WizardEvent e) {
        if (e.getSource().getCurrentPage() == WizardPage1.this) {
          System.out.println(getName() + " FinishPressed");
        }
      }

      public void nextPressed(WizardEvent e) {
        if (e.getSource().getCurrentPage() == WizardPage1.this) {
          System.out.println(getName() + " NextPressed");
        }
      }

      public void pageChanged(WizardEvent e) {
        if (e.getSource().getCurrentPage() == WizardPage1.this) {
          System.out.println(getName() + "PageChg");
        }
      }

      public void prevPressed(WizardEvent e) {
      }
    });
  }
}
