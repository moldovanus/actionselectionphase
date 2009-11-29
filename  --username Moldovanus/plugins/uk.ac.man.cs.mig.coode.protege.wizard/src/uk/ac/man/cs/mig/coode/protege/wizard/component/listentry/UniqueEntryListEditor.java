package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry;

import uk.ac.man.cs.mig.coode.protege.wizard.component.Fixable;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jun-2004
 */
public class UniqueEntryListEditor extends HighlightLinesEditor
        implements Fixable {

  public static final boolean debug = false;

  private List uniqueEntries = new ArrayList();
  private List uniqueEntryLines = new ArrayList();

  private HashMap problemEntries = new HashMap();

  private StringValidator theValidator = null;
  protected String propName = null;

  // stores results of the last validation
  private HashMap errorMessages = new HashMap();
//  private ErrorListener errorListener = null;

  protected String prefix = null;
  protected String postfix = null;

  private Console messageConsole = null;

  public UniqueEntryListEditor(StringValidator v, Console messageConsole) {
    super();

    if (v != null) {
      theValidator = v;
      this.messageConsole = messageConsole;
//
//      addCaretListener(new CaretListener() {
//        public void caretUpdate(CaretEvent e) {
//          updateMessages();
//        }
//      });
    }

    getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        //System.err.println("CHANGED");
      }

      public void insertUpdate(DocumentEvent e) {
        handleUpdate();
      }

      public void removeUpdate(DocumentEvent e) {
        handleUpdate();
      }

      private void handleUpdate() {
        if (theValidator != null) {
          validateAllContents();
        }
      }
    });
  }

  private void updateMessages() {
    if (messageConsole != null) {
      messageConsole.clear();

      LinkedList lineNumbers = new LinkedList(errorMessages.keySet());
      Collections.sort(lineNumbers);
      for (Iterator i = lineNumbers.iterator(); i.hasNext();) {
        Integer lineNumber = (Integer) i.next();
        String msg = (String) errorMessages.get(lineNumber);
        int userReadableLineNumber = lineNumber.intValue() + 1;
        if (msg != null) {
          messageConsole.err("line " + userReadableLineNumber + ": " + msg);
        }
      }
      // @@TODO scroll to current error
    }
  }

  public final void setPropertyName(String newPropertyName) {
    propName = newPropertyName;
  }

  public final void setPrefix(String newPrefix) {
    prefix = newPrefix;
    validateAllContents();
  }

  public final void setPostfix(String newPostfix) {
    postfix = newPostfix;
    validateAllContents();
  }

  public Object getValue() {
    return getValidEntries();
  }

  protected synchronized List getValidEntries() {
    List entries = null;

    if (theValidator != null) // if we are validating
    {
      // this should already be populated
      entries = uniqueEntries;
    }
    else {
//      System.err.println("HELP -- -- - -- -  - - shouldn't be doing this");
      // get the entries from the lines of text
      uniqueEntries.clear();
      uniqueEntryLines.clear();

      // @@TODO use getLines - beware that this will throw empty lines away though
      int numLines = getLinesCount();

      try {
        for (int i = 0; i < numLines; i++) {

          String lineToTest = getLine(i);
          if (lineToTest != null) {
            String entry = toFullName(lineToTest);
            if (entry != null) {
              uniqueEntries.add(entry);
              uniqueEntryLines.add(new Integer(i));
            }
          }
        }
        entries = uniqueEntries;
      }
      catch (BadLocationException ble) {
        System.err.println("Some error in reading the entries in the list");
      }
    }
    return entries;
  }

  /**
   * Get the lines of text from the editor
   *
   * @return a list of strings that are not blank or full of spaces, or null
   */
  public List getRawLines() {
    List strings = new ArrayList();

    int numLines = getLinesCount();

    for (int i = 0; i < numLines; i++) {
      try {
        strings.add(getLine(i));
      }
      catch (BadLocationException e) {
        e.printStackTrace();  //@@TODO replace default stack trace
      }
    }
    if (strings.size() <= 0) {
      strings = null;
    }
    return strings;
  }

  private void validateAllContents() {
    clearAllLines();

    uniqueEntries.clear();
    uniqueEntryLines.clear();

    problemEntries.clear();
    errorMessages.clear();

    Element paragraph = getDocument().getDefaultRootElement();

    int numLines = paragraph.getElementCount();
    for (int i = 0; i < numLines; i++) {
      try {
        String lineToTest = getLine(i);
        String entry = toFullName(lineToTest);
        if (entry != null) {
          // could add the line number into the error messages
          Integer currentLine = new Integer(i);
//          int entryIndex = uniqueEntries.indexOf(entry);
//          if (entryIndex >= 0) {
//            problemEntries.put(entry, currentLine);
//            int previousLineNumber = ((Integer) uniqueEntryLines.get(entryIndex)).intValue() + 1;
//            errorMessages.put(currentLine, "Already used on line: " + previousLineNumber);
//            highlightLine(i, Color.RED);
//          }
//          else {
          List err = new ArrayList();
          if (!theValidator.isValid(entry, uniqueEntries, propName, err)) {
            problemEntries.put(entry, currentLine);

            // @@TODO currently only shows 1 error per line
            errorMessages.put(currentLine, err.get(0));
            highlightLine(i, Color.RED);
          }
          else {
            uniqueEntries.add(entry);
            uniqueEntryLines.add(currentLine);
          }
//          }
        }
      }
      catch (BadLocationException e) {
        e.printStackTrace();  //@@TODO replace default frameworkTrace
      }
    }
    updateMessages();
  }

  protected String getLine(int lineNumber) throws BadLocationException {
    String result = null;
    Element paragraph = getDocument().getDefaultRootElement();
    Element line = paragraph.getElement(lineNumber);
    int start = line.getStartOffset();
    int end = line.getEndOffset();
    result = getText(start, end - start);
    return result;
  }

  public Map getErrors() {
    return errorMessages;
  }
//
//  public String getError(int lineNumber) {
//    // as Integer is a different object this might not work
//    return (String) errorMessages.get(new Integer(lineNumber));
//  }

  public void fix() {
    List rawLines = getRawLines();
    List fullNames = new ArrayList(rawLines.size());
    List indents = new ArrayList(rawLines.size());

    for (Iterator i = rawLines.iterator(); i.hasNext();) {
      String rawLine = (String) i.next();
      String fullName = toFullName(rawLine);
      if (fullName != null) {
        String fixedName = theValidator.fix(fullName, fullNames, propName, null);
        fullNames.add(fixedName);
        indents.add(getIndentString(rawLine));
      }
    }

    // now sort out the pre and post if required
    // cannot do this in the same loop as above, as that requires the names to be full
    List literalStrings = new ArrayList(fullNames.size());
    for (int i = 0; i < fullNames.size(); i++) {
      String fullName = (String) fullNames.get(i);
      String indent = (String) indents.get(i);
      literalStrings.add(toLiteral(fullName, indent));
    }

    setText(literalStrings);
    validateAllContents();
  }

  protected String getIndentString(String rawLine) {
    String indentString = null;
    String value = rawLine.trim();
    if (!value.equals("")) {
      int valueStart = rawLine.indexOf(value);
      indentString = rawLine.substring(0, valueStart);
    }
    return indentString;
  }

  protected String toLiteral(String s, String indent) {
    if (debug) System.out.println(">>> fullname = " + s + "(" + prefix + ", " + postfix + ")");

    // remove the prefix
    if (prefix != null) {
      s = s.replaceFirst(prefix, "");
    }

    // add back in the optional indentation string (not normalised)
    if (indent != null) {
      s = indent + s;
    }

    // remove the postfix but attach any number at the end to the literal
    if (postfix != null) {
      int postlen = postfix.length();
      if (s.endsWith(postfix)) { // easy
        s = s.substring(0, s.length() - postlen);
      }
      else { // we've added a number to the end to handle dupls
        int posOfPostfix = s.lastIndexOf(postfix);
        String number = s.substring(posOfPostfix + postlen);

        // this will be represented as a number after a space
        s = s.substring(0, posOfPostfix) + " " + number;
      }
    }
    if (debug) System.out.println("<<<<<< literal = " + s + "(" + prefix + ", " + postfix + ")");
    return s;
  }

  protected String toFullName(String s) {
    if (debug) System.out.println(">>> literal = " + s + "(" + prefix + ", " + postfix + ")");
    // @@TODO what do we do with indents??
    s = s.trim();
    if (s.equals("")) {
      s = null;
    }
    else {
      // if there is a number after the literal with a space in between
      // this goes AFTER the postfix
      if (postfix != null) {
        String numerals = null;
        int space = s.lastIndexOf(' ');
        if (space > 0) {
          numerals = s.substring(space + 1);
          s = s.substring(0, space) + postfix + numerals;
        }
        else {
          s = s + postfix;
        }
      }

      if (prefix != null) {
        s = prefix + s;
      }
    }
    if (debug) System.out.println("<<<<<< fullname = " + s + "(" + prefix + ", " + postfix + ")");
    return s;
  }
}