package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import edu.stanford.smi.protege.model.Cls;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.MultiSelectPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         30-Nov-2004
 */
public abstract class AbstractBulkClassOperationWizard extends AbstractWizard {

  private static final String classesProp = "classesProp";

  /**
   * If you override this, you MUST call this version first
   *
   * @throws NullPointerException
   * @throws WizardInitException
   */
  protected void initialise() throws NullPointerException, WizardInitException {
    addProperty(classesProp, null);
    addPage(new MultiSelectPage("0", classesProp, getKnowledgeBase().getRootCls()));
  }

  protected boolean handleFinished(Console output) // could return errors
  {
    boolean result = true;

    Collection selectedClasses = (Collection) getProperty(classesProp);

    getReadyToHandleClassOperations(output);

    if (selectedClasses != null) {
      for (Iterator i = selectedClasses.iterator(); i.hasNext() && (result == true);) {
        result = handleClassOperation((Cls) i.next(), output);
      }
    }

    return result;
  }

  protected abstract void getReadyToHandleClassOperations(Console output);

  protected abstract boolean handleClassOperation(Cls cls, Console output);
}
