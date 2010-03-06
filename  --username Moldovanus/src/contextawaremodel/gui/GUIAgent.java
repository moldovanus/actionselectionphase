package contextawaremodel.gui;

import contextawaremodel.GlobalVars;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;

import java.util.Map;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class GUIAgent extends GuiAgent implements GUIAgentExternal {

    Boolean alternate = true;
    Boolean broken = true;
    Timer timer;
    public static final int ADD_SENSOR = 1000;
    public static final int SHUTDOWN_PLATFORM = 1001;
    RDFProperty hasAcceptableValue;
    private PropertyValueListener pvl = new PropertyValueAdapter() {

        @Override
        public synchronized void propertyValueChanged(final RDFResource resource, RDFProperty property, Collection oldValues) {
            if (!resource.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("sensor"))) {
                return;
            }
            if (!property.getName().equals("has-value-of-service")) {
                return;
            }
            Map<String, Map<String, String>> mapping = GlobalVars.getValueMapping();
            String tempValue = resource.getPropertyValue(property).toString();
            final String newValue;
            Map<String, String> valueMapping = mapping.get(resource.getName());

            if (valueMapping != null) {
                newValue = valueMapping.get(tempValue);
            } else {
                newValue = tempValue;
            }
        }
    };
    private OWLModel owlModel;
    private MainWindow mainWindow;
    private JFrame memoryMonitor;

    @Override
    protected void setup() {
        System.out.println("[GUIAgent] Hello!");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            owlModel = (OWLModel) args[0];
            hasAcceptableValue = owlModel.getRDFProperty("AcceptableSensorValue");
        } else {
            System.out.println("[GUIAgent] No OWLModel provided.");
            this.doDelete();
            return;
        }


        // Start the Swing GUI
        Runnable r = new Runnable() {

            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                }

                // Start the Main Window
                GUIAgent.this.mainWindow = new MainWindow(GUIAgent.this, owlModel);
                mainWindow.setLocationRelativeTo(null);
                mainWindow.setState(JFrame.MAXIMIZED_BOTH);
                mainWindow.setVisible(true);


                // Start the Memory Monitor window, but keep it hidden
                final MemoryMonitor demo = new MemoryMonitor();
                memoryMonitor = new JFrame("Memory Monitor");
                memoryMonitor.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                memoryMonitor.getContentPane().add("Center", demo);
                memoryMonitor.pack();
                memoryMonitor.setSize(new Dimension(400, 500));
                memoryMonitor.setVisible(false);
                WindowListener l = new WindowAdapter() {

                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }

                    public void windowDeiconified(WindowEvent e) {
                        demo.surf.start();
                    }

                    public void windowIconified(WindowEvent e) {
                        demo.surf.stop();
                    }
                };

                //memoryMonitor.addWindowListener(l);
                demo.surf.start();
            }
        };
        SwingUtilities.invokeLater(r);

        this.addBehaviour(new ReceiveChangesGUIBehaviour(this));
        owlModel.addPropertyValueListener(pvl);

    }

    public void shutdownPlatform() {
        System.out.println("[GUIAgent] Shutting down platform ... ");
        try {
            this.getContainerController().getPlatformController().kill();
            System.exit(0);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onGuiEvent(GuiEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void showMemoryMonitor() {
        System.out.println("[GUIAgent] Showing the Memory Monitor.");
        this.memoryMonitor.setVisible(true);
    }

    public void startJADEGUI() {
        System.out.println("[GUIAgent] Starting a new RMA agent/JADE GUI.");
        try {
            this.getContainerController().createNewAgent("RMA", "jade.tools.rma.rma", null).start();
        } catch (Exception ex) {
        }

    }

    public void addIndividual(final String name) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                mainWindow.addIndividual(name);

            }
        });
    }

    public void startRealTimePlot(long interval) {
        this.addBehaviour(new LiveGraphGUIBehaviour(this, owlModel, interval));

    }
}
