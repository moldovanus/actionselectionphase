package uk.ac.man.cs.mig.coode.protege.wizard.util;

import java.io.PrintStream;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         13-Aug-2004
 */
public class Trace {

  public static final boolean userOutput = true;

  public static final boolean overallTrace = false;

  public static final boolean frameworkTrace = true & overallTrace;
  public static final boolean menuStateTrace = false & frameworkTrace;
  public static final boolean wizardStateTrace = true & frameworkTrace;
  public static final boolean propertyTrace = false & frameworkTrace;

  public static final boolean keyTrace = false & overallTrace;
  public static final boolean renderer = false & overallTrace;

  private static PrintStream out = System.out;
  private static Console theConsole = null;

  private static PrintStream err = System.err;

  public class Util {

    public static final boolean general = false & overallTrace;
    public static final boolean kbUtil = false & general;
  }

  public class Page {

    public static final boolean general = true & overallTrace;
    public static final boolean ArbPageTrace = true & general;
    public static final boolean textPageTrace = true & general;
  }

  public class Component {

    public static final boolean general = true & overallTrace;
    public static final boolean treeSet = false & general;
    public static final boolean multiSelect = false & general;
    public static final boolean listEntry = false & general;
    public static final boolean annotationTable = false & general;

    public static final boolean matrix = true & general;
    public static final boolean matrixEditor = false & matrix;
  }
/*
  public static void setOut(PrintStream newOut){
    out = newOut;
  }

  public static void setOut(Console console){
    theConsole = console;
  }
*/
  public static void setErr(PrintStream newErr) {
    err = newErr;
  }

  public static void trace(String str) {
    if (userOutput) {
      if (theConsole != null) {
        theConsole.out(str);
      }
      else {
        out.println(str);
      }
    }
  }

  public static void error(String str) {
    if (userOutput) {
      if (theConsole != null) {
        theConsole.err(str);
      }
      else {
        err.println("ERROR " + str);
      }
    }
  }

  public static void warn(String str) {
    if (userOutput) {
      if (theConsole != null) {
        theConsole.warn(str);
      }
      else {
        err.println("WARNING: " + str);
      }
    }
  }

}
