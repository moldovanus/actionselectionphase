/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents;

import actionselection.command.DecrementCommand;
import actionselection.command.IncrementCommand;
import actionselection.command.SetCommand;
import com.hp.hpl.jena.ontology.OntModel;
import contextawaremodel.GlobalVars;
import contextawaremodel.agents.behaviours.BasicX3DABehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.web3d.x3d.sai.BrowserFactory;
import org.web3d.x3d.sai.ExternalBrowser;
import org.web3d.x3d.sai.MFString;
import org.web3d.x3d.sai.SFBool;
import org.web3d.x3d.sai.SFColor;
import org.web3d.x3d.sai.SFTime;
import org.web3d.x3d.sai.X3DComponent;
import org.web3d.x3d.sai.X3DFieldEvent;
import org.web3d.x3d.sai.X3DFieldEventListener;
import org.web3d.x3d.sai.X3DNode;
import org.web3d.x3d.sai.X3DScene;

/**
 *
 * @author Administrator
 */
public class X3DAgent extends Agent {

    private JFrame frame;
    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    private X3DScene mainScene;
    private ACLMessage message;
    private final Agent selfReference = this;

    public void flipComputerState(String value) {
        X3DNode computerMaterial = mainScene.getNamedNode("computerScreen_MAT");
        SFColor computerColor = (SFColor) computerMaterial.getField("diffuseColor");
        if (value.contains("ON")) {
            computerColor.setValue(new float[]{1f, 1.0f, 1.0f});
        } else {
            computerColor.setValue(new float[]{0.0f, 0.0f, 0.0f});
        }
    }

    @Override
    protected void setup() {

        Object[] args = getArguments();
        if (args == null) {
            this.policyConversionModel = null;
            System.out.println("[X3D] X3D Agent failed, owlModel arguments are null!");
            return;
        }
        message = new ACLMessage(ACLMessage.SUBSCRIBE);
        message.addReceiver(new AID(GlobalVars.RLAGENT_NAME + "@" + this.getContainerController().getPlatformName()));
        try {
            message.setContentObject(new Boolean(true));
            message.setLanguage("JavaSerialization");
        } catch (IOException ex) {
            Logger.getLogger(X3DAgent.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        this.policyConversionModel = (OntModel) args[0];
        System.out.println("[X3DAgent] : Hellooo ! ");
        frame = new JFrame("X3D vizualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();

        // Setup browser parameters
        HashMap requestedParameters = new HashMap();
        requestedParameters.put("Xj3D_ShowConsole", Boolean.FALSE);
        requestedParameters.put("Xj3D_FPSShown", Boolean.TRUE);
        requestedParameters.put("Xj3D_NavbarShown", Boolean.TRUE);

        // Create an SAI component
        X3DComponent x3dComp = BrowserFactory.createX3DComponent(requestedParameters);

        // Add the component to the UI
        JComponent x3dPanel = (JComponent) x3dComp.getImplementation();
        contentPane.add(x3dPanel, BorderLayout.CENTER);

        // Get an external browser
        ExternalBrowser x3dBrowser = x3dComp.getBrowser();

        boolean useXj3D = true;

        if (x3dBrowser.getName().indexOf("Xj3D") < 0) {
            System.out.println("Not running on Xj3D, extended functions disabled");
            useXj3D = false;
        }
        frame.setSize(500, 500);
        frame.setVisible(true);

        // Create an X3D scene by loading a file
        mainScene = x3dBrowser.createX3DFromURL(new String[]{"src/scene.x3d"});

        // Replace the current world with the new one
        x3dBrowser.replaceWorld(mainScene);

        if (!useXj3D) {
            return;
        }

        /**
         * <ROUTE fromNode="Timer" fromField="fraction_changed" toNode="YellowCI" toField="set_fraction"/>
         * <ROUTE fromNode="YellowCI" fromField="value_changed" toNode="YellowLight" toField="diffuseColor"/>
         */
        X3DNode lightSensor = mainScene.getNamedNode("LightTouchSensor");
        X3DNode computerSensor = mainScene.getNamedNode("ComputerStateSensor");
        X3DNode pressureSensor = mainScene.getNamedNode("PressureSensor");
        X3DNode airConditioningSensor = mainScene.getNamedNode("AirConditioningSensor");
        X3DNode heaterSensor = mainScene.getNamedNode("HeaterSensor");

        if (lightSensor == null) {
            System.out.println("Couldn't find TouchSensor named: TOUCH_SENSOR");
            return;
        }
        if (pressureSensor == null) {
            System.out.println("Couldn't find TouchSensor named: PressureSensor");
            return;
        }

        if (computerSensor == null) {
            System.out.println("Couldn't find TouchSensor named: ComputerStateSensor");
            return;
        }

        if (airConditioningSensor == null) {
            System.out.println("Couldn't find TouchSensor named: AirConditioningSensor");
            return;
        }

        if (heaterSensor == null) {
            System.out.println("Couldn't find TouchSensor named: HeaterSensor");
            return;
        }


        //final float[] f = new float[]{1, 1, 1};
        SFTime lightTouchTimeField = (SFTime) lightSensor.getField("touchTime");
        SFTime pressureTouchTimeField = (SFTime) pressureSensor.getField("touchTime");
        SFTime computerTouchTimeField = (SFTime) computerSensor.getField("touchTime");
        SFTime airConditioningTouchTimeField = (SFTime) airConditioningSensor.getField("touchTime");
        SFTime heaterTouchTimeField = (SFTime) heaterSensor.getField("touchTime");
        //X3DNode sphere = mainScene.getNamedNode("boxColor");
        //SFColor color = (SFColor) sphere.getField("diffuseColor");
        //color.setValue(f);

        heaterTouchTimeField.addX3DEventListener(new X3DFieldEventListener() {

            public void readableFieldChanged(X3DFieldEvent xdfe) {

                IncrementCommand command = new IncrementCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#TemperatureSensorI",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 5);
                command.execute();
                command.setOWLValue();
                selfReference.send(message);
                //mainScene.addRoute(timer, "fraction_changed", PI, "set_fraction");
                //mainScene.addRoute(PI, "value_changed", TS, "diffuseColor");

                System.out.println("Air Conditioning unit clicked");
            }
        });

        airConditioningTouchTimeField.addX3DEventListener(new X3DFieldEventListener() {

            public void readableFieldChanged(X3DFieldEvent xdfe) {

                DecrementCommand command = new DecrementCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#TemperatureSensorI",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 5);
                command.execute();
                command.setOWLValue();
                selfReference.send(message);
                //mainScene.addRoute(timer, "fraction_changed", PI, "set_fraction");
                //mainScene.addRoute(PI, "value_changed", TS, "diffuseColor");
                System.out.println("Air Conditioning unit clicked");
            }
        });

        computerTouchTimeField.addX3DEventListener(new X3DFieldEventListener() {

            public void readableFieldChanged(X3DFieldEvent xdfe) {
                
                X3DNode computerStateString = mainScene.getNamedNode("computerState_STRING");
                MFString computerStateSensorValue = (MFString) computerStateString.getField("string");
                System.err.println("Room state: " + computerStateSensorValue.get1Value(0));

                int computerState = 0;
                if (computerStateSensorValue.get1Value(0).equals("Computer state: OFF")) {
                    //computerStateSensorValue.set1Value(0,"Computer state: ON");
                    computerState = 1;

                } else {
                    //computerStateSensorValue.set1Value(0,"Computer state: OFF");
                    computerState = 0;

                }

                SetCommand command = new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#ComputerStateSensorI",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, computerState);
                command.execute();
                command.setOWLValue();
                selfReference.send(message);
                //mainScene.addRoute(timer, "fraction_changed", PI, "set_fraction");
                //mainScene.addRoute(PI, "value_changed", TS, "diffuseColor");
                System.out.println("Computer unit clicked");
            }
        });

        pressureTouchTimeField.addX3DEventListener(new X3DFieldEventListener() {

            public void readableFieldChanged(X3DFieldEvent xdfe) {
                X3DNode roomStateSensor = mainScene.getNamedNode("roomEmpty_STRING");
                MFString roomStateSensorValue = (MFString) roomStateSensor.getField("string");
                System.err.println("Room state: " + roomStateSensorValue.get1Value(0));
                int roomState = 0;
                if (roomStateSensorValue.get1Value(0).equals("Room empty: FALSE")) {
                    // roomStateSensorValue.set1Value(0,"Room empty: TRUE");
                    roomState = 0;
                } else {
                    // roomStateSensorValue.set1Value(0,"Room empty: FALSE");
                    roomState = 1;
                }

                SetCommand command = new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#RoomStateSensorI",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, roomState);
                command.execute();
                command.setOWLValue();
                selfReference.send(message);
                //mainScene.addRoute(timer, "fraction_changed", PI, "set_fraction");
                //mainScene.addRoute(PI, "value_changed", TS, "diffuseColor");
                System.out.println("Room pressure pad clicked");
            }
        });

        lightTouchTimeField.addX3DEventListener(new X3DFieldEventListener() {

            public void readableFieldChanged(X3DFieldEvent xdfe) {

                X3DNode light = mainScene.getNamedNode("Light");
                SFBool lightIntensity = (SFBool) light.getField("on");
                //ACLMessage message= new ACLMessage(ACLMessage.INFORM);
                int lightState = 0;
                if (lightIntensity.getValue()) {
                    lightIntensity.setValue(false);
                    lightState = 0;
                } else {
                    lightIntensity.setValue(true);
                    lightState = 1;
                }

                SetCommand command = new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#LightSensorI",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, lightState);
                command.execute();
                command.setOWLValue();

                //mainScene.addRoute(timer, "fraction_changed", PI, "set_fraction");
                //mainScene.addRoute(PI, "value_changed", TS, "diffuseColor");
                System.out.println("Light clicked");

            }
        });

        //float[] f = new float[3];
        //color.getValue(f);
        //System.out.println("" + f[0] + "   " + f[1] + "   " + f[2]);

        addBehaviour(new BasicX3DABehaviour(this));
    }

    public X3DScene getMainScene() {
        return mainScene;
    }
}
