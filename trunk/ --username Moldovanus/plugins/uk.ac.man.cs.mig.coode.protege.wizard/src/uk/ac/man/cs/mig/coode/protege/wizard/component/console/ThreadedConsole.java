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
public class ThreadedConsole extends JComponent implements Console {

  private boolean alive;
  private List messageQueue;
  private JTextArea ta;
  private static final int flushLimit = 5;
  private boolean flushing = false;

  public ThreadedConsole() {
    super();
    setLayout(new BorderLayout());
    messageQueue = new LinkedList();

    ta = new JTextArea();
    ta.setPreferredSize(new Dimension(400, 300));
    JScrollPane s = new JScrollPane(ta);
    add(s, BorderLayout.WEST);
  }

  public void start() {
    Thread myThread = new FlushingThread();
    //myThread.setPriority(Thread.MAX_PRIORITY);
    alive = true;
    myThread.start();
  }

  public void stop() {
    alive = false;
  }

  public synchronized void out(String str) {
    while (flushing) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        e.printStackTrace();  //@@TODO replace default stack trace
      }
    }
    messageQueue.add(str);
    notifyAll();
  }

  public synchronized void err(String str) {
    while (flushing) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        e.printStackTrace();  //@@TODO replace default stack trace
      }
    }
    messageQueue.add("ERROR -> " + str);
    notifyAll();
  }

  public synchronized void warn(String str) {
    while (flushing) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        e.printStackTrace();  //@@TODO replace default stack trace
      }
    }
    messageQueue.add("WARNING -> " + str);
    notifyAll();
  }

  public void clear() {
    ta.setText("");
    messageQueue.clear();
  }

  private synchronized void flushBuffer() {
    while (messageQueue.size() == 0) {
      try {
        wait();
      }
      catch (InterruptedException e) {
        e.printStackTrace();  //@@TODO replace default stack trace
      }
    }

    flushing = true;
    // do flush
    while (messageQueue.size() > 0) {
      //System.out.println("will be last statement");
      //System.out.println("msg = " + (String)messageQueue.remove(0));
      String message = (String) messageQueue.remove(0);
      SwingUtilities.invokeLater(new MyRunnable(message));
      //System.out.println("messageQueue.size() = " + messageQueue.size());
    }
    flushing = false;
    notifyAll();
  }

  class FlushingThread extends Thread {

    public void run() {
      while (alive) {
        //System.out.println("running the console" + i++);
        flushBuffer();
      }
    }
  }

  class MyRunnable implements Runnable {

    private String message;

    public MyRunnable(String message) {
      this.message = message;
    }

    public void run() {
      ta.append(message + "\n");
    }
  }
}
