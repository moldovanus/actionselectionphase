package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry;

import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jun-2004
 */
public class HighlightLinesEditor extends JTextArea {

  private Set valueChangeListeners = new TreeSet();

  public HighlightLinesEditor() {
    super();
    addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
          case '\n':
          case '\r':
            // make sure we stop the current highlight if we're moved to the next line
            Highlighter.Highlight lastHighlight = getHighlightForOffset(getCaretPosition() - 1);
            if (lastHighlight != null) {
              //System.out.println("reset the highlight before the return");
              Highlighter highLighter = getHighlighter();
              try {
                highLighter.changeHighlight(lastHighlight,
                                            lastHighlight.getStartOffset(),
                                            getCaretPosition() - 1);
              }
              catch (BadLocationException e1) {
              }
            }
            break;

          case '\b':
            //System.out.println("backspace");
            break;

          default:
            //System.out.println("other key pressed");
            break;
        }
      }
    });
    getDocument().addDocumentListener(new DocumentListener(){

      public void changedUpdate(DocumentEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
      }

      public void insertUpdate(DocumentEvent e) {
        notifyValueChanged();
      }

      public void removeUpdate(DocumentEvent e) {
        notifyValueChanged();
      }
    });
  }

  /**
   * Makes the given line in the editor highlighted
   *
   * @param lineNumber the index of the line to highlight
   * @param colour     the colour to highlight the line with
   * @throws BadLocationException if the line does not exist
   */
  public void highlightLine(int lineNumber, Color colour) throws BadLocationException {
    Highlighter highLighter = getHighlighter();

    Element paragraph = getDocument().getDefaultRootElement();
    Element line = paragraph.getElement(lineNumber);
    if (line != null) {
      // first remove existing highlight
      clearLine(lineNumber);

      // then add a new highlight
      int start = line.getStartOffset();
      int end = line.getEndOffset() - 1;
      highLighter.addHighlight(start, end, new MyHighlightPainter(colour));
    }
    else {
      throw new BadLocationException("Line does not exist - cannot highlight", lineNumber);
    }
  }

  /**
   * Sets a line to be clear from highlighting. Has no effect if the line does
   * not already have highlighting .
   *
   * @param lineNumber the index of the line to be cleared
   * @throws BadLocationException if the line passed in does not exist
   */
  public void clearLine(int lineNumber) throws BadLocationException {
    Highlighter.Highlight highLight = getHighlightForLine(lineNumber);
    if (highLight != null) {
      Highlighter highLighter = getHighlighter();
      highLighter.removeHighlight(highLight);
    }
  }

  public void clearAllLines() {
    Highlighter highLighter = getHighlighter();
    highLighter.removeAllHighlights();
  }

  public void setText(Collection newLines) {
    setText("");
    for (Iterator i = newLines.iterator(); i.hasNext();) {
      String line = (String) i.next();
      append(line + "\n");
    }
    notifyValueChanged();
  }

  public int getLinesCount() {
    Element paragraph = getDocument().getDefaultRootElement();
    return paragraph.getElementCount();
  }

  private Highlighter.Highlight getHighlightForOffset(int offset) {
    Highlighter.Highlight[] currentHighlights = getHighlighter().getHighlights();
    Highlighter.Highlight resultHighlight = null;

    for (int i = 0; ((i < currentHighlights.length) && (resultHighlight == null)); i++) {
      Highlighter.Highlight thisOne = currentHighlights[i];
      if ((thisOne.getStartOffset() <= offset) && (thisOne.getEndOffset() >= offset)) {
        if (thisOne.getPainter() instanceof MyHighlightPainter) {
          resultHighlight = thisOne;
        }
      }
    }
    return resultHighlight;
  }

  private Highlighter.Highlight getHighlightForLine(int lineNumber) throws BadLocationException {
    Highlighter.Highlight resultHighlight = null;

    Element paragraph = getDocument().getDefaultRootElement();
    Element line = paragraph.getElement(lineNumber);

    if (line != null) {
      int startOfLine = line.getStartOffset();
      int endOfLine = line.getEndOffset();

      Highlighter.Highlight[] currentHighlights = getHighlighter().getHighlights();

      for (int i = 0; ((i < currentHighlights.length) && (resultHighlight == null)); i++) {
        Highlighter.Highlight thisOne = currentHighlights[i];
        if ((thisOne.getStartOffset() >= startOfLine) && (thisOne.getEndOffset() <= endOfLine)) {
          if (thisOne.getPainter() instanceof MyHighlightPainter) {
            resultHighlight = thisOne;
          }
        }
      }
    }
    else {
      throw new BadLocationException("Line does not exist", lineNumber);
    }
    return resultHighlight;
  }

  public boolean isLineHighlighted(int lineNumber) throws BadLocationException {
    return getHighlightForLine(lineNumber) != null;
  }

  /**
   * A local implementation of the default highlight painter so that we can test
   * against this to make sure we only act on highlights applied by the parent and
   * not, for example, the cursor highlight.
   */
  class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

    public MyHighlightPainter(Color colour) {
      super(colour);
    }
  }

  public final boolean addValueChangeListener(ValueChangeListener valueChangeListener) {
    return valueChangeListeners.add(valueChangeListener);
  }

  public final boolean removeValueChangeListener(ValueChangeListener valueChangeListener) {
    return valueChangeListeners.remove(valueChangeListener);
  }

  private final void notifyValueChanged() {
    Iterator i = valueChangeListeners.iterator();
    while (i.hasNext()) {
      ((ValueChangeListener) i.next()).valueChanged();
    }
  }

  public Object getValue() {
    return getText();
  }
}
