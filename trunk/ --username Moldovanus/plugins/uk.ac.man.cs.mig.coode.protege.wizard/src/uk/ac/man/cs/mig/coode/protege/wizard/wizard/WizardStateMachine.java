package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import uk.ac.man.cs.mig.coode.protege.wizard.event.ProtegeWizardEvent;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
class WizardStateMachine {

  private static final boolean debug = false;//Trace.wizardStateTrace;

  static final int STATE_IDLE = 0;       // before the wizard is launched
  static final int STATE_CUSTOM_INIT = 1;  // when the user can perform initialisation
  static final int STATE_RUNNING = 2;    // while the wizard is visible
  static final int STATE_PRIMARY_INIT = 3; // beginning of setup before init
  static final int STATE_FINAL_INIT = 4; // last phase of setup after init
  static final int STATE_SHUTTING_DOWN = 5;   // while releasing resources
  static final int STATE_READY_TO_DISPLAY = 6; // when all setup done
  static final int STATE_FINISHING = 7; // when user can operate with results
  static final int STATE_FINISHED = 8; // when user actions cease
  static final int STATE_INVALID = 9; // before the state machine has begun
  static final int STATE_ERROR = 10; // after error

  private int currentState = STATE_INVALID;

  private SimpleAbstractWizard theWizard = null;

  private Thread controllingThread;
  private List eventQueue = new LinkedList();

  private boolean running = false;

  protected WizardStateMachine(SimpleAbstractWizard w) {
    theWizard = w;
    controllingThread = Thread.currentThread();
    //controllingThread = new CheckQueueThread();
    //controllingThread.start();
  }

  public void sendEvent(ProtegeWizardEvent e) {
    if (debug) System.out.println("controllingThread posting event = " + Thread.currentThread().getName());

/*
if (Thread.currentThread() != controllingThread){

    }

    handleEvent(e);
*/
    // @@TODO make sure below works instead of passing straight in
    if (debug) System.out.println("state = " + printState(currentState) + ", event posted = " + e + " q size = " + eventQueue.size());

    if (eventQueue.size() > 0) {
      eventQueue.add(e);
      //if ((Thread.currentThread() == controllingThread) && (!running)) {
      if (!running) {
        flushQueue();
      }
    }
    else {
      handleEvent(e);
    }

  }

  private void flushQueue() {
    running = true;
    while (eventQueue.size() > 0) {
      //if (eventQueue.size() > 1) System.out.println(">>>>>>>>  events on queue: " + eventQueue.size());
      try {
        ProtegeWizardEvent event = (ProtegeWizardEvent) eventQueue.remove(0);
        handleEvent(event);
      }
      catch (EmptyStackException e) {
        System.err.println("empty queue");
      }
    }
    running = false;
  }

  private boolean handleEvent(ProtegeWizardEvent e) {
    if (debug) System.out.println("handle event [" + e + "] in state " + this);

    boolean handled = false;

    try {
      switch (e.getType()) {

        case ProtegeWizardEvent.COMMAND_CREATE_WIZARD:
          if (currentState == STATE_INVALID) {
            currentState = STATE_IDLE;
            handled = true;
          }
          break;

        case ProtegeWizardEvent.USER_COMMAND_LAUNCH:
          if (currentState == STATE_IDLE) {
            currentState = STATE_PRIMARY_INIT;
            theWizard.primaryWizardInit();
            sendEvent(ProtegeWizardEvent.PRIMARY_INIT_COMPLETE_EVENT);
            handled = true;
          }
          break;

        case ProtegeWizardEvent.COMMAND_PRIMARY_INIT_COMPLETE:
          if (currentState == STATE_PRIMARY_INIT) {
            currentState = STATE_CUSTOM_INIT;
            theWizard.initialise();
            sendEvent(ProtegeWizardEvent.END_CUSTOM_INIT_EVENT);
            handled = true;
          }
          break;

        case ProtegeWizardEvent.COMMAND_ADD_PAGE:
          if ((currentState == STATE_CUSTOM_INIT) || // can only add pages when in user init
              (currentState == STATE_FINAL_INIT)) { // or when completing (for log page)
            // state stays the same
            handled = true;
          }
          break;

        case ProtegeWizardEvent.COMMAND_ADD_PROP:
          if ((currentState == STATE_PRIMARY_INIT) || // OK for intro page
              (currentState == STATE_CUSTOM_INIT)) {
            // state stays the same
            handled = true;
          }
          break;

        case ProtegeWizardEvent.COMMAND_CUSTOM_INIT_COMPLETE:
          if (currentState == STATE_CUSTOM_INIT) {
            currentState = STATE_FINAL_INIT;
            theWizard.finalWizardInit();
            sendEvent(ProtegeWizardEvent.ALL_INIT_COMPLETE_EVENT);
            handled = true;
          }
          break;

        case ProtegeWizardEvent.COMMAND_ALL_INIT_COMPLETE:
          if (currentState == STATE_FINAL_INIT) {
            currentState = STATE_READY_TO_DISPLAY;
            //wizardDialog.showWizard();
            sendEvent(ProtegeWizardEvent.WIZARD_VISIBLE_EVENT);
            handled = true;
          }
          else {
            System.err.println("Cannot display wizard - startup not complete");
          }
          break;

        case ProtegeWizardEvent.COMMAND_WIZARD_VISIBLE:
          if (currentState == STATE_READY_TO_DISPLAY) {
            currentState = STATE_RUNNING;
            theWizard.showWizard();
            //System.err.println(">>>>>>>>>>>>>>>>>>        NOW THE WIZARD IS VISIBLE");
            handled = true;
          }
          break;

        case ProtegeWizardEvent.USER_COMMAND_PRESSED_FINISH:
          //System.out.println("handling USER PRESSED FINISH");
          if (currentState == STATE_RUNNING) {
            currentState = STATE_FINISHING;
            theWizard.doFinish();
            //wizardDialog.getFinisherThread().start();
            sendEvent(ProtegeWizardEvent.FINISH_SUCCESS_EVENT);
            handled = true;
          }
          break;

        case ProtegeWizardEvent.COMMAND_FINISH_SUCCESS:
          if (currentState == STATE_FINISHING) {
            currentState = STATE_FINISHED;
            //wizardDialog.completeTransaction(true);
            handled = true;
          }
          break;
//
//        case ProtegeWizardEvent.COMMAND_FINISH_FAILURE:
//          if (currentState == STATE_FINISHING){
//            currentState = STATE_FINISHED;
//            wizardDialog.completeTransaction(false);
//            handled = true;
//          }
//          break;

        case ProtegeWizardEvent.COMMAND_SHUTDOWN_REQUEST:
          if ((currentState == STATE_RUNNING) ||
              (currentState == STATE_FINISHED) ||
              (currentState == STATE_ERROR)) {
            currentState = STATE_SHUTTING_DOWN;
            handled = theWizard.shutDownWizard();
            sendEvent(ProtegeWizardEvent.SHUTDOWN_COMPLETE_EVENT);
          }
          break;

        case ProtegeWizardEvent.COMMAND_SHUTDOWN_COMPLETE:
          if (currentState == STATE_SHUTTING_DOWN) {
            currentState = STATE_IDLE;
            handled = true;
          }
          break;

        default:
          System.err.println("Event not recognized");
          // do nothing
      }

      if (handled == false) {
        throw new IllegalWizardStateException(currentState, e);
      }
    }
    catch (IllegalWizardStateException iwse) {
      System.err.println(iwse.getMessage());
      currentState = STATE_ERROR;
      sendEvent(ProtegeWizardEvent.SHUTDOWN_REQUEST_EVENT);
    }
    catch (WizardInitException wie) {
      wie.printStackTrace();
      currentState = STATE_ERROR;
      sendEvent(ProtegeWizardEvent.SHUTDOWN_REQUEST_EVENT);
    }

    return handled;
  }

  public String toString() {
    return "STATE: " + printState(currentState);
  }

  public static String printState(int state) {
    String string;
    switch (state) {
      case STATE_IDLE:
        string = "idle";
        break;
      case STATE_CUSTOM_INIT:
        string = "custom init";
        break;
      case STATE_RUNNING:
        string = "running";
        break;
      case STATE_PRIMARY_INIT:
        string = "primary init";
        break;
      case STATE_FINAL_INIT:
        string = "final init";
        break;
      case STATE_SHUTTING_DOWN:
        string = "shutdown";
        break;
      case STATE_READY_TO_DISPLAY:
        string = "ready to display";
        break;
      case STATE_FINISHING:
        string = "finishing";
        break;
      case STATE_FINISHED:
        string = "finished";
        break;
      case STATE_INVALID:
        string = "invalid";
        break;
      case STATE_ERROR:
        string = "error";
        break;
      default:
        string = "undefined state";
    }
    return string;
  }
}
