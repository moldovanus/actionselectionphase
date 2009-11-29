package uk.ac.man.cs.mig.coode.protege.wizard.event;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         07-Dec-2004
 */
public class ProtegeWizardEvent {

  public static final int COMMAND_CREATE_WIZARD = 0;
  public static final int COMMAND_WIZARD_VISIBLE = 1;
  public static final int COMMAND_ADD_PAGE = 2;
  public static final int COMMAND_ADD_PROP = 3;
  public static final int USER_COMMAND_LAUNCH = 4;
  public static final int COMMAND_PRIMARY_INIT_COMPLETE = 5;
  public static final int COMMAND_CUSTOM_INIT_COMPLETE = 6;
  public static final int COMMAND_ALL_INIT_COMPLETE = 7;
  public static final int COMMAND_SHUTDOWN_REQUEST = 8;
  public static final int COMMAND_SHUTDOWN_COMPLETE = 9;
  public static final int USER_COMMAND_PRESSED_FINISH = 10;
  public static final int COMMAND_FINISH_SUCCESS = 11;
  public static final int COMMAND_FINISH_FAILURE = 12;

  public static final ProtegeWizardEvent CREATE_WIZARD_EVENT = new ProtegeWizardEvent(COMMAND_CREATE_WIZARD, "CREATE_WIZARD_EVENT");
  public static final ProtegeWizardEvent WIZARD_VISIBLE_EVENT = new ProtegeWizardEvent(COMMAND_WIZARD_VISIBLE, "WIZARD_VISIBLE_EVENT");
  public static final ProtegeWizardEvent ADD_PAGE_EVENT = new ProtegeWizardEvent(COMMAND_ADD_PAGE, "ADD_PAGE_EVENT");
  public static final ProtegeWizardEvent ADD_PROP_EVENT = new ProtegeWizardEvent(COMMAND_ADD_PROP, "ADD_PROP_EVENT");
  public static final ProtegeWizardEvent USER_LAUNCH_EVENT = new ProtegeWizardEvent(USER_COMMAND_LAUNCH, "USER_LAUNCH_EVENT");
  public static final ProtegeWizardEvent PRIMARY_INIT_COMPLETE_EVENT = new ProtegeWizardEvent(COMMAND_PRIMARY_INIT_COMPLETE, "PRIMARY_INIT_COMPLETE_EVENT");
  public static final ProtegeWizardEvent END_CUSTOM_INIT_EVENT = new ProtegeWizardEvent(COMMAND_CUSTOM_INIT_COMPLETE, "END_CUSTOM_INIT_EVENT");
  public static final ProtegeWizardEvent ALL_INIT_COMPLETE_EVENT = new ProtegeWizardEvent(COMMAND_ALL_INIT_COMPLETE, "ALL_INIT_COMPLETE_EVENT");
  public static final ProtegeWizardEvent SHUTDOWN_REQUEST_EVENT = new ProtegeWizardEvent(COMMAND_SHUTDOWN_REQUEST, "SHUTDOWN_REQUEST_EVENT");
  public static final ProtegeWizardEvent SHUTDOWN_COMPLETE_EVENT = new ProtegeWizardEvent(COMMAND_SHUTDOWN_COMPLETE, "SHUTDOWN_COMPLETE_EVENT");
  public static final ProtegeWizardEvent USER_PRESSED_FINISH_EVENT = new ProtegeWizardEvent(USER_COMMAND_PRESSED_FINISH, "USER_PRESSED_FINISH_EVENT");
  public static final ProtegeWizardEvent FINISH_SUCCESS_EVENT = new ProtegeWizardEvent(COMMAND_FINISH_SUCCESS, "FINISH_SUCCESS_EVENT");
  public static final ProtegeWizardEvent FINISH_FAILURE_EVENT = new ProtegeWizardEvent(COMMAND_FINISH_FAILURE, "FINISH_FAILURE_EVENT");

  private int type;
  private String name;

  private ProtegeWizardEvent(int type, String name) {
    this.type = type;
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public String toString() {
    return name;
  }
}
