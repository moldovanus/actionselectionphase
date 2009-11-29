package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * A page that just displays text in a scrolling pane, in either HTML or plain text format.
 * The value can either be the text to show, or a URL, pointing to its location.
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         24-Aug-2004
 */
public class TextPage extends AbstractWizardPage {

  private static final boolean debug = Trace.Page.textPageTrace;

  protected JEditorPane textPane;
  protected static int htmlMaxSize = 500;

  public TextPage(String pageName, String propName) {
    super(pageName, propName);

    textPane = new JEditorPane();
    textPane.setEditable(false);

    // set the look and feel of the text
    HTMLEditorKit htmlKit = (HTMLEditorKit) textPane.getEditorKitForContentType("text/html");
    StyleSheet myStyleSheet = htmlKit.getStyleSheet();
    myStyleSheet.addRule("body {font: 11pt Verdana, Arial, Sans;}");
    myStyleSheet.addRule("h1 {font-size: 18px; font-style; italic;}");

    JScrollPane scroller = new JScrollPane(textPane);
    scroller.setPreferredSize(new Dimension(400, 300));

    add(scroller, BorderLayout.CENTER);
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);

    Object o = getPropertiesSource().getProperty(getPropName());

    if (o instanceof String) {
      textPane.setContentType("text/plain;");
      textPane.setText((String) o);
    }
    else if (o instanceof URL) {
      try {
        textPane.setContentType("text/html;");
        textPane.setPage((URL) o);
      }
      catch (Exception e) {
        textPane.setContentType("text/plain;");
        textPane.setText("Sorry, could not handle " + ((URL) o).getPath());
      }
    }
    else if (o instanceof FileReader) {
      FileReader reader = (FileReader) o;

      char[] html = new char[htmlMaxSize];

      try {
        reader.read(html);
        reader.close();
        String encoding = reader.getEncoding();
        if (debug) System.out.println("encoding = " + encoding);
        textPane.setContentType("text/html;");
        textPane.setText(new String(html));
      }
      catch (IOException e) {
        textPane.setContentType("text/plain;");
        textPane.setText("Sorry, could not read " + o);
      }
    }
  }
}
