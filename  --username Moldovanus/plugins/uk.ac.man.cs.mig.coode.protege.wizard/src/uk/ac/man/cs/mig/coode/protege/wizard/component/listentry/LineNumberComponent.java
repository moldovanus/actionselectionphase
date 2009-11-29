package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Mar-2005
 */
public class LineNumberComponent extends JTextArea{

  private UniqueEntryListEditor editor;

  public LineNumberComponent(UniqueEntryListEditor editor) {
    super();
    this.editor = editor;

    setBackground(Color.lightGray);
    setFont(editor.getFont());
    setEditable(false);
    setColumns(3);

    refresh();

    editor.getDocument().addDocumentListener(new DocumentListener(){

      public void changedUpdate(DocumentEvent e) {
      }

      public void insertUpdate(DocumentEvent e) {
        refresh();
      }

      public void removeUpdate(DocumentEvent e) {
      }
    });

  }

  private void refresh() {
    //System.out.println("LineNumberComponent.refresh");
    int editorLines = LineNumberComponent.this.editor.getLineCount();
    //System.out.println("editorLines = " + editorLines);

      for (int i=getLineCount(); i<=editorLines; i++){
        append(i + "\n");
      }    
  }
}
