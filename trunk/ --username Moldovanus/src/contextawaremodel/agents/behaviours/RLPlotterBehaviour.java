package contextawaremodel.agents.behaviours;

import contextawaremodel.agents.ReinforcementLearningAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import org.LiveGraph.LiveGraph;
import org.LiveGraph.dataFile.common.PipeClosedByReaderException;
import org.LiveGraph.dataFile.write.DataStreamWriter;

public class RLPlotterBehaviour extends TickerBehaviour {

    private DataStreamWriter out;
    private ReinforcementLearningAgent agent;
    private LiveGraph lg;

    public RLPlotterBehaviour(Agent agent, long interval) {
        super(agent, interval);
        this.agent = (ReinforcementLearningAgent) agent;

        lg = LiveGraph.application();
        lg.execStandalone();
        lg.guiManager().setDisplayDataFileSettingsWindows(false);
        lg.guiManager().setDisplayGraphSettingsWindows(false);
        lg.guiManager().setDisplayMessageWindows(false);

        // Turn LiveGraph into memory mode:
        out = lg.updateInvoker().startMemoryStreamMode();
        if (null == out) {
            System.out.println("[RL] Could not switch LiveGraph into memory stream mode.");
            lg.disposeGUIAndExit();
            stop();
            return;
        }

        // Set a values separator:
        out.setSeparator(";");

        // Add a file description line:
        out.writeFileInfo("RL agent in-memory file.");

        // Set-up the data series:

        out.addDataSeries("executionTime");
        out.addDataSeries("executionTimeAverage");


    }

    @Override
    protected void onTick() {


        out.setDataValue("executionTime", agent.getRlTime());
        out.setDataValue("executionTimeAverage", agent.getRlAverageTime());

        out.writeDataSet();

        // If LiveGraph's main window was closed by user, we can finish the demo:
        if (out.hadIOException()) {
            if (out.getIOException() instanceof PipeClosedByReaderException) {
                System.out.println("[RL] LiveGraph window closed. No reason for more data. Finishing.");
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
