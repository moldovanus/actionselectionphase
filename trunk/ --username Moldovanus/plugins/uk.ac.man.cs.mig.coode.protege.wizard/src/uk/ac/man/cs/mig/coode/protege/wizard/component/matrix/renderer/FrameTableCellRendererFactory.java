package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.FrameRenderer;
import uk.ac.man.cs.mig.coode.protege.wizard.renderer.MultiFrameTableCellRenderer;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.table.TableCellRenderer;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Nov-2004
 */
public class FrameTableCellRendererFactory extends DefaultTableCellRendererFactory {

  private static final boolean debug = Trace.renderer;

  private static FrameRenderer frameRendererInstance = null;

  public TableCellRenderer getRendererForClass(Class expectedType) {
    TableCellRenderer ren = null;

    if (Cls.class.isAssignableFrom(expectedType)) {
      if (debug) System.out.println("RENDERER: Cls");
      ren = getFrameRenderer();
    }
    else if (Slot.class.isAssignableFrom(expectedType)) {
      if (debug) System.out.println("RENDERER: Slot");
      ren = getFrameRenderer();
    }
    else if (Frame.class.isAssignableFrom(expectedType)) {
      if (debug) System.out.println("RENDERER: Frame");
      ren = getFrameRenderer();
    }
    else if (Collection.class.isAssignableFrom(expectedType)) {
      if (debug) System.out.println("RENDERER: COLLECTION");
      ren = new MultiFrameTableCellRenderer(getFrameRenderer());
    }
    else {
      if (debug) System.err.println("CAN'T FIND RENDERER FOR " + expectedType + ", trying parent");
      ren = super.getRendererForClass(expectedType);
    }

    return ren;
  }

  private static FrameRenderer getFrameRenderer() {
    if (frameRendererInstance == null) {
      frameRendererInstance = FrameRenderer.createInstance();
    }
    return frameRendererInstance;
  }
}
