package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.MatrixComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor.TableCellEditorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer.TableCellRendererFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Oct-2004
 */
public class MatrixPage extends ArbitraryComponentPage {

  private MatrixComponent theMatrix;

  public MatrixPage(String pageName, String propName, TableCellRendererFactory rf,
                    TableCellEditorFactory ef) {

    super(pageName, propName);

//    AbstractMatrixModel model = (AbstractMatrixModel)getPropValue(getPropName());

    theMatrix = new MatrixComponent(rf, ef);

    setComponent(theMatrix);
  }
}
