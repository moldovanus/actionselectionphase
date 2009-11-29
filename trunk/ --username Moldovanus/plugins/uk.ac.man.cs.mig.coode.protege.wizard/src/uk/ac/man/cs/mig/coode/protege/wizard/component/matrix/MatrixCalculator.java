package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public interface MatrixCalculator {

  public Object getValue(Object column, Object row);

  public MatrixEditor getEditor(Object column, Object row);
}
