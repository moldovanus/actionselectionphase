package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import edu.stanford.smi.protege.model.Frame;
import uk.ac.man.cs.mig.coode.protege.wizard.component.multiselect.SwitchableMultiSelectComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.OptionsPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         30-Nov-2004
 */
public abstract class AbstractBulkResourceWizard extends AbstractWizard {

  public static final String OPTION_CLASSES = "Classes";
  public static final String OPTION_PROPERTIES = "Properties";
  //public static final String OPTION_INDIVIDUALS = "Individuals";

  private static final String resourceTypeProp = "resourceTypeProp";
  private static final String resourcesProp = "resourcesProp";

  private SwitchableMultiSelectComponent multi = null;

  /**
   * If you override this, you MUST call this version first
   *
   * @throws NullPointerException
   * @throws uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException
   */
  protected void initialise() throws NullPointerException, WizardInitException {

    addProperty(resourceTypeProp, OPTION_CLASSES);
    addProperty(resourcesProp, null);

    addPropertyChangeListener(resourceTypeProp, new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue().equals(OPTION_CLASSES)){
          multi.setRoot(getHelper().getRootCls());
        }
        else if (evt.getNewValue().equals(OPTION_PROPERTIES)){
          multi.setRoot(getHelper().getRootSlots());
        }
        else{
          System.err.println("Cannot yet choose individuals");
        }
      }
    });

    AbstractList options = new ArrayList();
    options.add(OPTION_CLASSES);
    options.add(OPTION_PROPERTIES);
    //options.add(OPTION_INDIVIDUALS);

    addPage(new OptionsPage("0", resourceTypeProp, options));

    // the root below must match the default (in this case Cls)
    multi = new SwitchableMultiSelectComponent(getHelper().getRootCls(), false);
    addPage(new ArbitraryComponentPage("1", resourcesProp, multi));
  }

  protected boolean handleFinished(Console output) // could return errors
  {
    boolean result = true;

    Collection selectedResources = (Collection) getProperty(resourcesProp);

    getReadyToHandleResourceOperations(output);

    if (selectedResources != null) {
      for (Iterator i = selectedResources.iterator(); i.hasNext() && (result == true);) {
        result = handleResourceOperation((Frame) i.next(), output);
      }
    }

    return result;
  }

  protected abstract void getReadyToHandleResourceOperations(Console output);

  protected abstract boolean handleResourceOperation(Frame resource, Console output);
}
