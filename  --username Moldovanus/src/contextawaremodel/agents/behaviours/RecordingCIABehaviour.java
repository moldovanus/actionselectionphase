package contextawaremodel.agents.behaviours;

import com.hp.hpl.jena.util.FileUtils;
import contextawaremodel.agents.CIAgent;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import jade.core.behaviours.TickerBehaviour;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecordingCIABehaviour extends TickerBehaviour {

    private CIAgent ag;

    // constructor takes CIAgent reference and recording time interval in miliseconds
    public RecordingCIABehaviour(CIAgent ag, int mlsRecTimeInterval) {
        super(ag, mlsRecTimeInterval);
        this.ag = ag;
    }

    @Override
    protected void onTick() {
        // Create the directory path for recording
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        java.util.Date date = new java.util.Date();
        String pathName = "./logs/" + dateFormat.format(date);
        new File(pathName).mkdirs();

        // Save the file to the correct path
        String fileName = pathName + "/ontology.owl";
        ArrayList errors = new ArrayList();
        ((JenaOWLModel) this.ag.getOwlModel()).save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);

        System.out.println("[CIA] A new log was saved to \"" + pathName + "\".");
    }
}
