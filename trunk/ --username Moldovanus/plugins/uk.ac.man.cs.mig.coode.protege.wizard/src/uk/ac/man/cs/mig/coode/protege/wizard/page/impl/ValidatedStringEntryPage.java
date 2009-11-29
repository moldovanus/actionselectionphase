package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.text.ValidatedTextField;
import uk.ac.man.cs.mig.coode.protege.wizard.component.text.ValidatedTextFieldCombo;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class ValidatedStringEntryPage extends ArbitraryComponentPage {

    AbstractWizardEditor editor;

    public ValidatedStringEntryPage(String pageName, String propName,
                                    StringValidator validator,
                                    Collection alternatives) {
        super(pageName, propName);

        if (validator == null) {
            KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
            validator = WizardAPI.getWizardManager().getDefaultValidator(kb, null);
        }

        if (alternatives != null) {
            editor = new ValidatedTextFieldCombo(validator);
            ((ValidatedTextFieldCombo) editor).setValues(alternatives);
        }
        else {
            editor = new ValidatedTextField(validator);
        }

//    editor.getComponent().addKeyListener(new KeyAdapter(){
//      public void keyReleased(KeyEvent e) {
//        if (e.getKeyCode() == KeyEvent.VK_ENTER){
//          getProtegeWizard().
//        }
//      }
//    });

        setComponent(editor);
    }

    public ValidatedStringEntryPage(String pageName, String propName,
                                    StringValidator validator) {
        this(pageName, propName, validator, null);
    }
}
