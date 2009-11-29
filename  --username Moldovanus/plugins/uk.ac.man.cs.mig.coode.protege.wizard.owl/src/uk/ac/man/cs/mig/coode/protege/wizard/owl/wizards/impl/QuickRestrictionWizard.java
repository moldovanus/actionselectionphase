package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor.FrameTableCellEditorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer.FrameTableCellRendererFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix.ClassPropMatrixModel;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix.PropertyCloseModel;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix.PropertyDefaultModel;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.MatrixPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.MultiSelectPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public class QuickRestrictionWizard extends AbstractOWLWizard {

  protected static final String classesProp = "classesProp";
  protected static final String propertiesProp = "propertiesProp";
  protected static final String matrixModelProp = "matrixModelProp";
  protected static final String defLabelProp = "defLabelProp";
  protected static final String closeLabelProp = "closeLabelProp";
  protected static final String defaultsModelProp = "defaultsModelProp";
  protected static final String closureModelProp = "closureModelProp";

  protected ClassPropMatrixModel restrTableModel = null;
  protected PropertyDefaultModel defaultsTableModel = null;
  protected PropertyCloseModel closureTableModel = null;

  protected void initialise() throws NullPointerException, WizardInitException {

    restrTableModel = new ClassPropMatrixModel(this, propertiesProp, classesProp);
    defaultsTableModel = new PropertyDefaultModel(this, defLabelProp, propertiesProp);
    closureTableModel = new PropertyCloseModel(this, closeLabelProp, propertiesProp);

    addProperty(classesProp, null);
    addProperty(propertiesProp, null);
    addUniqueProperty(defLabelProp, CollectionUtilities.createCollection("default"));
    addUniqueProperty(closeLabelProp, CollectionUtilities.createCollection("close"));
    addProperty(defaultsModelProp, defaultsTableModel);
    addProperty(matrixModelProp, restrTableModel);
    addProperty(closureModelProp, closureTableModel);

    addPropertyChangeListener(defaultsModelProp, new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        restrTableModel.setDefaults(defaultsTableModel.getMap());
      }
    });

    addPage(new MultiSelectPage("0", classesProp, getKnowledgeBase().getRootCls()));
    addPage(new MultiSelectPage("1", propertiesProp, getOwlHelper().getRootObjectProperties()));

    addPage(new MatrixPage("2", defaultsModelProp,
                           new FrameTableCellRendererFactory(),
                           new FrameTableCellEditorFactory(getPageProperty("2", "editorlabel"))));

    addPage(new MatrixPage("3", matrixModelProp,
                           new FrameTableCellRendererFactory(),
                           new FrameTableCellEditorFactory(getPageProperty("3", "editorlabel"))));

    addPage(new MatrixPage("4", closureModelProp,
                           new FrameTableCellRendererFactory(),
                           new FrameTableCellEditorFactory(getPageProperty("4", "editorlabel"))));
  }

  protected boolean handleFinished(Console output) {

    ClassPropMatrixModel model = (ClassPropMatrixModel) getProperty(matrixModelProp);
    Collection classesAffected = (Collection) getProperty(classesProp);
    Collection propertiesAffected = (Collection) getProperty(propertiesProp);

    HashMap closureMap = closureTableModel.getMap();

    Iterator i = classesAffected.iterator();
    while (i.hasNext()) {
      OWLNamedClass cls = (OWLNamedClass) i.next();
      Iterator j = propertiesAffected.iterator();
      while (j.hasNext()) {
        DefaultOWLObjectProperty prop = (DefaultOWLObjectProperty) j.next();
        Collection values = model.getValue(cls, prop);
        Object closure = closureMap.get(prop);
        boolean close = (closure != null) && ((Boolean) closure).booleanValue() == true;
        getOwlHelper().updateSomeValuesFromRestrsForProp(cls, prop, values, close);
      }
    }

    return true;
  }

  public List canBeLaunched() {
    List feedback = null;
    OwlKbHelper h = getOwlHelper();
    if (getModel().getOWLThingClass().getSubclassCount() == 0){
      feedback = CollectionUtilities.createList(getWizardProperty("error.noclasses"));
    }
    if (h.getRootObjectProperties() == null){
      if (feedback ==null){
        feedback = new ArrayList();
      }
      feedback.add(getWizardProperty("error.noproperties"));
    }
    return feedback;
  }
}
