package uk.ac.man.cs.mig.coode.protege.wizard.util;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.ClsesTab;
import edu.stanford.smi.protege.widget.TabWidget;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.InterfaceNotReadyException;

import javax.swing.*;
import java.awt.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Nov-2004
 */
public class GuiUtils {

  private static final boolean debug = false;

  private static final String WIZARD_PATH_SPLITTER = "->";

  private static final String OWL_CLASSES_TAB_NAME = "edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab";

  private static Console out = new DefaultConsole();

  private static KnowledgeBase kb = null;

  public static void setConsole(Console output) {
    out = output;
  }

  public static Console getConsole() {
    return out;
  }

  public static void setKnowledgeBase(KnowledgeBase kb) {
    GuiUtils.kb = kb;
  }

  public static void setBrowserSlot(Slot newBrowserSlot) {
    KnowledgeBase kb = newBrowserSlot.getKnowledgeBase();
    Cls root = kb.getRootCls();
    Cls rootMeta = root.getDirectType();

    if (newBrowserSlot != null) {
      kb.setDirectBrowserSlotPattern(rootMeta, new BrowserSlotPattern(newBrowserSlot));
    }
    else {
      kb.setDirectBrowserSlot(rootMeta, kb.getSlot(Model.Slot.NAME));
    }
  }

  public static TabWidget getClassTab() throws InterfaceNotReadyException {
    TabWidget classTab = null;

    ProjectView view = ProjectManager.getProjectManager().getCurrentProjectView();
    classTab = view.getTabByClassName(ClsesTab.class.getName());

    if (classTab == null) {
      if (debug) System.out.println("Getting OWL Classes Tab");
      classTab = view.getTabByClassName(OWL_CLASSES_TAB_NAME);
      if (debug) System.out.println("classTab = " + classTab);
    }

    if (classTab == null) {
      if (debug) System.out.println("Cannot find a class tab");
      throw new InterfaceNotReadyException("No Class Tab is available");
    }
    return classTab;
  }

//  public static SelectableTree getClassTabTree() throws InterfaceNotReadyException {
//    return (SelectableTree) getClassTab().getClsTree();
//  }

  public static Cls getSelectedClassFromUI() throws Exception {
    return (Cls) CollectionUtilities.getFirstItem(getSelectedClassesFromUI());
  }


  public static Collection getSelectedClassesFromUI() throws InterfaceNotReadyException {
    //SelectableTree classTree = getClassTabTree();
    // Unfortunately, getSelection returns the path of the first item
    AbstractList selectedClasses = new ArrayList(getClassTab().getSelection());
    if (debug) System.out.println("selectedClasses = " + selectedClasses);
    // don't return owl:Thing in results
    Cls owlThing = kb.getRootCls();
    if (selectedClasses.contains(owlThing)) {
      selectedClasses.remove(owlThing);
    }
    // @@TODO sort out - currently just give back the top of the path
    return CollectionUtilities.createCollection(selectedClasses.get(selectedClasses.size() - 1));//selectedClasses;
  }

  public static void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(Application.getMainWindow(), message, null, JOptionPane.ERROR_MESSAGE);
  }

  public static JMenu getMenu(JMenuBar menuBar, String menuString) {
    String[] path = menuString.split(WIZARD_PATH_SPLITTER);
    JMenu menu = getSubMenu(menuBar, path[0]);
    for (int i = 1; i < path.length; i++) {
      menu = getSubMenu(menu, path[i]);
    }
    return menu;
  }

  public static JMenu getSubMenu(JMenuBar menuBar, String name) {
    JMenu menu = null;
    for (int i = 0; (i < menuBar.getMenuCount()) && (menu == null); i++) {
      JMenu current = menuBar.getMenu(i);
      String currentName = current.getText();
      if (currentName.equals(name)) {
        menu = current;
      }
    }
    if (menu == null) {
      menu = new JMenu(name);
      menuBar.add(menu);
    }
    return menu;
  }

  private static JMenu getComp(JComponent c, String s) {
    Component[] comps = c.getComponents();
    for (int i = 0; i < comps.length; i++) {
      if (comps[i] instanceof JMenu) {
        JMenu m = (JMenu) comps[i];
        if (m.getText().equals(s)) {
          return m;
        }
        getComp(m, s);
      }
    }
    return null;
  }

  public static JMenu getSubMenu(JMenu menu, String name) {
    JMenu submenu = getComp(menu, name);
    if (submenu == null) {
      submenu = new JMenu(name);
      menu.add(submenu);
    }
    return submenu;
  }


  /* Used by makeCompactGrid. */
  private static SpringLayout.Constraints getConstraintsForCell(int row, int col,
                                                                Container parent,
                                                                int cols) {
    SpringLayout layout = (SpringLayout) parent.getLayout();
    Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }

  /**
   * Aligns the first <code>rows</code> * <code>cols</code>
   * components of <code>parent</code> in
   * a grid. Each component in a column is as wide as the maximum
   * preferred width of the components in that column;
   * height is similarly determined for each row.
   * The parent is made just big enough to fit them all.
   *
   * @param rows     number of rows
   * @param cols     number of columns
   * @param initialX x location to start the grid at
   * @param initialY y location to start the grid at
   * @param xPad     x padding between cells
   * @param yPad     y padding between cells
   */
  public static void makeCompactGrid(Container parent,
                                     int rows, int cols,
                                     int initialX, int initialY,
                                     int xPad, int yPad) {
    SpringLayout layout;
    try {
      layout = (SpringLayout) parent.getLayout();
    }
    catch (ClassCastException exc) {
      System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
      return;
    }

    //Align all cells in each column and make them the same width.
    Spring x = Spring.constant(initialX);
    for (int c = 0; c < cols; c++) {
      Spring width = Spring.constant(0);
      for (int r = 0; r < rows; r++) {
        width = Spring.max(width,
                           getConstraintsForCell(r, c, parent, cols).
                           getWidth());
      }
      for (int r = 0; r < rows; r++) {
        SpringLayout.Constraints constraints =
                getConstraintsForCell(r, c, parent, cols);
        constraints.setX(x);
        constraints.setWidth(width);
      }
      x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    }

    //Align all cells in each row and make them the same height.
    Spring y = Spring.constant(initialY);
    for (int r = 0; r < rows; r++) {
      Spring height = Spring.constant(0);
      for (int c = 0; c < cols; c++) {
        height = Spring.max(height,
                            getConstraintsForCell(r, c, parent, cols).
                            getHeight());
      }
      for (int c = 0; c < cols; c++) {
        SpringLayout.Constraints constraints =
                getConstraintsForCell(r, c, parent, cols);
        constraints.setY(y);
        constraints.setHeight(height);
      }
      y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    }

    //Set the parent's size.
    SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint(SpringLayout.SOUTH, y);
    pCons.setConstraint(SpringLayout.EAST, x);
  }
}
