package contextawaremodel.gui;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.LiveGraph.LiveGraph;
import org.LiveGraph.dataFile.common.PipeClosedByReaderException;
import org.LiveGraph.dataFile.write.DataStreamWriter;

public class LiveGraphGUIBehaviour extends TickerBehaviour {

    private Collection<RDFResource> sensors;
    private RDFProperty valueProperty;
    private DataStreamWriter out;
    private Agent agent;
    private LiveGraph lg;

    public LiveGraphGUIBehaviour(Agent agent, OWLModel owlModel, long interval) {
        super(agent, interval);
        this.agent = agent;
        this.sensors = owlModel.getRDFIndividuals();
        
        RDFResource sensorClass = owlModel.getRDFSNamedClass("sensor");
        Collection<RDFResource> toBeDeleted = new LinkedList<RDFResource>();
        for ( Iterator<RDFResource> i = sensors.iterator(); i.hasNext(); ) {
            RDFResource r = i.next();
            if (!r.getProtegeType().getSuperclasses().contains(sensorClass)) {
                toBeDeleted.add(r);
            }
        }
        this.sensors.removeAll(toBeDeleted);

        this.valueProperty = owlModel.getRDFProperty("has-value-of-service");
        
        lg = LiveGraph.application();
    	lg.execStandalone();
        lg.guiManager().setDisplayDataFileSettingsWindows(false);
        lg.guiManager().setDisplayGraphSettingsWindows(false);
        lg.guiManager().setDisplayMessageWindows(false);

        // Turn LiveGraph into memory mode:
    	out = lg.updateInvoker().startMemoryStreamMode();
        if (null == out) {
            System.out.println("[CIA] Could not switch LiveGraph into memory stream mode.");
            lg.disposeGUIAndExit();
            stop();
            return;
        }

        // Set a values separator:
        out.setSeparator(";");

        // Add a file description line:
        out.writeFileInfo("GUIAgent sensor values in-memory file.");

        // Set-up the data series:
        for ( Iterator<RDFResource> it = sensors.iterator(); it.hasNext(); ) {
            out.addDataSeries(it.next().getName());
        }
    }

    @Override
    protected void onTick() {
        for ( Iterator<RDFResource> it = sensors.iterator(); it.hasNext(); ) {
            try {
                RDFResource sensor = it.next();
                String propertyValue = sensor.getPropertyValue(valueProperty).toString();
                double sensorValue = Double.parseDouble(propertyValue.split(" ")[0]);
                out.setDataValue(sensor.getName(), sensorValue); 
            } catch ( Exception e) {
                //out.setDataValue(" ");
            }
        }
        
        out.writeDataSet();

        // If LiveGraph's main window was closed by user, we can finish the demo:
        if (out.hadIOException()) {
            if (out.getIOException() instanceof PipeClosedByReaderException) {
                System.out.println("[CIA] LiveGraph window closed. No reason for more data. Finishing.");
                out.close();
                agent.removeBehaviour(this);
                stop();
                return;
            }
        }

        // Check for any other IOErrors and display:
        if (out.hadIOException()) {
            out.getIOException().printStackTrace();
            out.resetIOException();
            return;
        }

        lg.updateInvoker().requestUpdate();
    }

}
