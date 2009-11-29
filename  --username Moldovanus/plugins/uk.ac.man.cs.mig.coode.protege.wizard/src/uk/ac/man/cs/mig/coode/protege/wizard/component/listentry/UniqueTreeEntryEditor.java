package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         30-Jun-2004
 */
public class UniqueTreeEntryEditor extends UniqueEntryListEditor {

  public UniqueTreeEntryEditor(StringValidator v, Console messageConsole) {
    super(v, messageConsole);
    setFont(new Font("Monospaced", Font.PLAIN, 12));
    setTabSize(2);
  }

  public Object getValue() {
    return getRoot();
  }
  

//  private int getIndentationLevelInSpaces(String lineToTest) {
//    int result = 0;
//    String value = lineToTest.trim();
//    if (!value.equals("")) {
//      int valueStart = lineToTest.indexOf(value);
//      String indentString = lineToTest.substring(0, valueStart);
//
//      // make sure the indents work with whatever tab size we have
//      char[] tabInSpaces = new char[getTabSize()];
//      for (int i = 0; i < tabInSpaces.length; i++) {
//        tabInSpaces[i] = ' ';
//      }
//      String tabString = String.copyValueOf(tabInSpaces);
//
//      indentString = indentString.replaceAll("\t", tabString);
//      result = indentString.length();
//
//      // @@TODO make sure the prev line checked in cases where we over/undershoot
//    }
//    return result;
//  }

  private int getIndentationLevelInSpaces(String lineToTest) {
    int result = 0;

    String indentString = getIndentString(lineToTest);
    if (indentString != null) {
      // make sure the indents work with whatever tab size we have
      char[] tabInSpaces = new char[getTabSize()];
      for (int i = 0; i < tabInSpaces.length; i++) {
        tabInSpaces[i] = ' ';
      }
      String tabString = String.copyValueOf(tabInSpaces);

      indentString = indentString.replaceAll("\t", tabString);
      result = indentString.length();
    }
    return result;
  }

  protected TreeNode getRoot() {

    String rawLine = null;
    MutableTreeNode currentNode = null;
    MutableTreeNode parentOfThisNode = null;
    int currentIndent = -1;

    // the stacks keeps track of how far "in" we are indented
    // as well as providing the most recent parent at a particular level
    Stack indentStack = new Stack();
    Stack parentStack = new Stack();

    // blank root node with indent -1 so we can have multiple entries at level "0"
    parentStack.push(new DefaultMutableTreeNode(null));
    indentStack.push(new Integer(-1));

    // iterate through the lines, creating a tree of Strings
    java.util.List rawLines = getRawLines();
    for (Iterator i = rawLines.iterator(); i.hasNext();) {
      rawLine = (String) i.next();
      String fullName = toFullName(rawLine);

      if (fullName != null) {

        // create a node to hold this entry
        currentNode = new DefaultMutableTreeNode(fullName);

        currentIndent = getIndentationLevelInSpaces(rawLine);

        // knock all higher or sibling nodes off the stack
        while (((Integer) indentStack.peek()).intValue() >= currentIndent) {
          indentStack.pop();
          parentStack.pop();
        }

        // add this node as a child of the entry on top of the stack
        parentOfThisNode = (DefaultMutableTreeNode) parentStack.lastElement();
        ((DefaultMutableTreeNode) parentOfThisNode).add(currentNode);

        // put this indent on the stack in case it has children
        indentStack.push(new Integer(currentIndent));
        parentStack.push(currentNode);
      }
    }
    return (TreeNode) parentStack.firstElement();
  }
}
