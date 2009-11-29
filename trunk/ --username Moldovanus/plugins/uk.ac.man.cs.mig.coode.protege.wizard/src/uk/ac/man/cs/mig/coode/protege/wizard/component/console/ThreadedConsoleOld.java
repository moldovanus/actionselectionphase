package uk.ac.man.cs.mig.coode.protege.wizard.component.console;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Jan-2005
 */
public class ThreadedConsoleOld extends JComponent implements Console {

  private boolean alive;
  private List messageQueue;
  private JTextArea ta;

  public ThreadedConsoleOld() {
    super();
    setLayout(new BorderLayout());
    messageQueue = new LinkedList();

    ta = new JTextArea();
    //ta.setPreferredSize(new Dimension(400, 300));
    JScrollPane s = new JScrollPane(ta);
    add(s, BorderLayout.WEST);
  }

  public void start() {
    Thread myThread = new MyThread();
    //myThread.setPriority(Thread.MAX_PRIORITY);
    alive = true;
    myThread.start();
  }

  public void stop() {
    alive = false;
  }

  public void out(String str) {
    messageQueue.add(str);
  }

  public void err(String str) {
    messageQueue.add("ERROR -> " + str);
  }

  public void warn(String str) {
    messageQueue.add("WARNING -> " + str);
  }

  public void clear() {
    ta.setText("");
    messageQueue.clear();
  }

  private void showMessage(String s) {
    ta.append(s + "\n");
  }

  class MyThread extends Thread {

    int i = 0;

    public void run() {
      while (alive) {
        //System.out.println("running the console" + i++);
        if (messageQueue.size() > 0) {
          showMessage((String) messageQueue.remove(0));
        }
        yield();
      }
    }
  }
}
