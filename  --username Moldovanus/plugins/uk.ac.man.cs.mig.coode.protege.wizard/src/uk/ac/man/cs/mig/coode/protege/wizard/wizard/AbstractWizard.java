package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ProtegeWizardEvent;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ConsolePage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.IntroPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

/**
 * Extension of the <code>SimpleAbstractWizard</code> which adds a navigation
 * bar to the left hand side of the wizard to allow the user to see the steps that
 * need to be taken
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jul-2004
 */
public abstract class AbstractWizard extends SimpleAbstractWizard {

  private static final String INTRO_URL_PROP = "INTRO_URL_PROP";

  // GUI definitions
  private static Dimension defaultWizardSize;

  private PageNavigationPanel leftPanel;

  // first page in wizard
  private IntroPage introPage;

  // final page in wizard
  private ConsolePage resultsPage;


  public AbstractWizard() {
    super();
  }

  protected AbstractWizard(String propertiesFileName) {
    super(propertiesFileName);
  }

  public AbstractWizard(String wizardName, String wizardDescription) {
    super(wizardName, wizardDescription);
  }

  void primaryWizardInit() {
    // check to see if there is an intro page set in the properties
    // if so, set the flag to make sure a page is visible for it
    String introUrlString = getWizardProperty("intro");
    if (introUrlString != null) {
      URL introUrl = getClass().getResource(introUrlString);
      addProperty(INTRO_URL_PROP, introUrl);
    }
    super.primaryWizardInit();
  }

  void finalWizardInit() throws WizardInitException {
    if (pages.size() > 0) {
      if ((getProperty(INTRO_URL_PROP) != null) && (WizardAPI.getIntroVisible())) {
        introPage = new IntroPage(INTRO_URL_PROP);
        addPage(0, introPage);
      }

      if (WizardAPI.getResultsVisible()) {
        resultsPage = new ConsolePage() {
          public void pageSelected(WizardDialog wizard) {
            super.pageSelected(wizard);
            stateMachine.sendEvent(ProtegeWizardEvent.USER_PRESSED_FINISH_EVENT);
          }
        };

        //@@ND remove if doesn't work
//        resultsPage.addComponentListener(new ComponentAdapter(){
//          public void componentShown(ComponentEvent e) {
//            doFinish();
//          }
//        });

        addPage(resultsPage);
        setDefaultConsole(resultsPage.getConsole());
      }
      else {
        resultsPage = null;
      }
    }

    if (WizardAPI.getNavVisible()) {
      leftPanel = new PageNavigationPanel();
      wizardDialog.getMainPanel().add(leftPanel, BorderLayout.WEST);
      wizardDialog.pack();
    }

    super.finalWizardInit();
  }

  void pageHasChanged(WizardPage currentPage) {
    super.pageHasChanged(currentPage);

    if (resultsPage != null) {
      int totalNumPages = pages.size();
      // if we're on the final "real" page
      if (pages.get(totalNumPages - 2) == currentPage) {
        wizardDialog.setNextButtonLabel(getWizardProperty(BUTTON_FINISH_LABEL));
      }
      else if (pages.get(totalNumPages - 1) == currentPage) {

      }
      else {
        wizardDialog.setNextButtonLabel(getWizardProperty(BUTTON_NEXT_LABEL));
      }
    }

    if (WizardAPI.getNavVisible()) {
      int pageIndex = pages.indexOf(currentPage);
      if (leftPanel != null) {
        leftPanel.setCurrentPageNavText(pageIndex);
      }
    }
  }

  SimpleAbstractWizard.WizardEventBehaviourAdapter getBehaviourAdapter() {
    return new SpecialBehaviourAdapter();
  }

  public String getMenuString() {
    return "Tools";
  }

  /////////////////////////////////////////////////////// Internal classes

  private class PageNavigationPanel extends JPanel {

    private final Color navBarColor = Color.WHITE;
    private final EmptyBorder paddedBorder = new EmptyBorder(6, 6, 6, 6);
    private final Dimension panelSize = new Dimension(150, 500);

    JList pageList;
    JLabel stepLabel;

    public PageNavigationPanel() {
      super(new BorderLayout(7, 7));

      setBackground(navBarColor);
      setBorder(BorderFactory.createLineBorder(Color.BLACK));
      setPreferredSize(panelSize);

      pageList = new JList(pages.toArray());
      pageList.setCellRenderer(new ListCellRenderer() {

        JLabel component = new JLabel();
        Font notSelectedFont = component.getFont();
        Font selectedFont = component.getFont().deriveFont(Font.BOLD);
        Color selectedColour = Color.BLUE;
        Color notSelectedColour = component.getBackground();

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
          String pageName = ((AbstractWizardPage) value).getName();
          component.setText(getPageLabel(pageName));
          if (isSelected) {
            component.setFont(selectedFont);
            component.setBackground(selectedColour);
          }
          else {
            component.setFont(notSelectedFont);
            component.setBackground(notSelectedColour);
          }
          return component;
        }
      });
      pageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      pageList.setEnabled(false);
      pageList.setBorder(paddedBorder);

      stepLabel = new JLabel();
      stepLabel.setBorder(paddedBorder);

      add(pageList, BorderLayout.NORTH);
      add(stepLabel, BorderLayout.SOUTH);
    }

    public void setCurrentPageNavText(int pageIndex) {
      pageList.setSelectedIndex(pageIndex);
      stepLabel.setText("Step " + (pageIndex + 1) + " of " + pages.size());
    }
  }

  class SpecialBehaviourAdapter extends SimpleAbstractWizard.WizardEventBehaviourAdapter {

    public void finishPressed(WizardEvent event) {
      super.finishPressed(event);
      if (resultsPage == null) {
        stateMachine.sendEvent(ProtegeWizardEvent.USER_PRESSED_FINISH_EVENT);
      }
    }
  }
}
