package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.test;

import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.MatrixCalculator;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.MatrixEditor;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public class TestMatrixCalculator implements MatrixCalculator {

  public Object getValue(Object column, Object row) {
    return "empty";
  }

  public MatrixEditor getEditor(Object column, Object row) {

    return null;  // @@TODO change default
  }
}
