package uk.ac.man.cs.mig.coode.protege.wizard.renderer;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.ui.FrameRenderer;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.util.AbstractList;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Oct-2004
 */
public class RendererFactory {

  private static final boolean debug = Trace.renderer;

  private static FrameRenderer frameRendererInstance = null;

  public static TableCellRenderer getTableCellRenderer(Class javaClass) {
    TableCellRenderer ren = null;

    if (javaClass.isAssignableFrom(Frame.class)) {
      if (debug) System.out.println("RENDERER: Frame");
      ren = getFrameRenderer();
    }
    else if (javaClass.isAssignableFrom(Cls.class)) {
      if (debug) System.out.println("RENDERER: Cls");
      ren = getFrameRenderer();
    }
    else if (javaClass.isAssignableFrom(AbstractList.class)) {
      if (debug) System.out.println("RENDERER: COLLECTION");
      ren = new MultiFrameTableCellRenderer(getFrameRenderer());
    }
    else {
      if (debug) System.err.println("CANNOT FIND RENDERER FOR " + javaClass);
      ren = new DefaultTableCellRenderer();
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
