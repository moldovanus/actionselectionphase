/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package x3dtest;

/**
 *
 * @author Me
 */
/*****************************************************************************
 *                        Web3d.org Copyright (c) 2004-2007
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.*;

import org.web3d.x3d.sai.*;

/**
 * An example of how to use Xj3D Specific SAI routines.
 *
 * @author Alan Hudson
 * @version
 */
public class LoadTest extends JFrame {

    /**
     * Constructor for the demo.
     */
    private final X3DScene mainScene;
    //private Map<String, X3DNode> runningTasks;
    private ArrayList<X3DNode> taskLabels;
    private Map<String, X3DNode> powerMeterLabels;
    static int server = 1;

    public LoadTest() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container contentPane = getContentPane();

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

        //runningTasks = new HashMap<String, X3DNode>();
        taskLabels = new ArrayList<X3DNode>();
        powerMeterLabels = new HashMap<String, X3DNode>();

        // Get an external browser
        final ExternalBrowser x3dBrowser = x3dComp.getBrowser();

        boolean useXj3D = true;

        if (x3dBrowser.getName().indexOf("Xj3D") < 0) {
            System.out.println("Not running on Xj3D, extended functions disabled");
            useXj3D = false;
        }
        setSize(500, 500);
        setVisible(true);

        // Create an X3D scene by loading a file
        mainScene = x3dBrowser.createX3DFromURL(new String[]{"src/datacenter.x3d"});

        // Replace the current world with the new one
        x3dBrowser.replaceWorld(mainScene);

        if (!useXj3D) {
            return;
        }


        /**
         * <ROUTE fromNode="Timer" fromField="fraction_changed" toNode="YellowCI" toField="set_fraction"/>
         * <ROUTE fromNode="YellowCI" fromField="value_changed" toNode="YellowLight" toField="diffuseColor"/>
         */
        ActionListener actionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                X3DNode tube = mainScene.getNamedNode("Server_0_tube_XFORM");
                X3DNode upperTubeOne = mainScene.getNamedNode("Server_0_tube_01_XFORM");
                X3DNode upperTubeTwo = mainScene.getNamedNode("Server_0_tube_02_XFORM");

                SFRotation rotation = (SFRotation) tube.getField("rotation");
                SFRotation rotation_1 = (SFRotation) upperTubeOne.getField("rotation");
                SFRotation rotation_2 = (SFRotation) upperTubeTwo.getField("rotation");
                float[] values = new float[4];
                rotation.getValue(values);
                values[ 3] += 0.1;

                float[] newValues = new float[]{1, 0, 0, 0};
                rotation_1.getValue(newValues);
                newValues[ 3] += 0.1;
                rotation.setValue(values);
                rotation_1.setValue(newValues);
                newValues[ 3] *= -1;
                rotation_2.setValue(newValues);
            }
        };

        Timer timer = new Timer(50, actionListener);
        timer.start();

        for (int i = 1; i <= 5; i++) {
            addLabelToPowerMeters("" + 1 * 10, "PowerMeterGroup_0" + i);
        }


        ActionListener simulationListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                for (X3DNode entry : taskLabels) {
                    mainScene.removeRootNode(entry);
                }

                for (Entry<String, X3DNode> entry : powerMeterLabels.entrySet()) {
                    mainScene.removeRootNode(entry.getValue());
                }

                if (server == 5) {
                    server = 1;
                }

                Random random = new Random();
                int index_1 = random.nextInt(5) + 1;
                int index_2 = random.nextInt(5) + 1;
                int index_3 = random.nextInt(5) + 1;

                createTask("Task_1", "Server_" + index_1, -0.1f);

                mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_1));
                mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_1 + "_Inverse"));
                addLabelToPowerMeters("10", "PowerMeterGroup_0" + index_1);
                if (index_2 == index_1) {
                    createTask("Task_2", "Server_" + index_1, 0);
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_1));
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_1 + "_Inverse"));
                    addLabelToPowerMeters("20", "PowerMeterGroup_0" + index_1);

                    if (index_3 == index_1) {
                        mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_1));
                        mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_1 + "_Inverse"));
                        addLabelToPowerMeters("30", "PowerMeterGroup_0" + index_1);
                        createTask("Task_3", "Server_" + index_1, 0.1f);
                    }
                } else {
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_2));
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_2 + "_Inverse"));
                    addLabelToPowerMeters("10", "PowerMeterGroup_0" + index_2);
                    createTask("Task_2", "Server_" + index_2, -0.1f);
                }

                if (index_3 == index_2) {
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_2));
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_2 + "_Inverse"));
                    addLabelToPowerMeters("20", "PowerMeterGroup_0" + index_2);
                    createTask("Task_3", "Server_" + index_2, 0);
                } else {
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_3));
                    mainScene.removeRootNode(powerMeterLabels.get("PowerMeterGroup_0" + index_3 + "_Inverse"));
                    addLabelToPowerMeters("10", "PowerMeterGroup_0" + index_3);
                    createTask("Task_3", "Server_" + index_3, -0.1f);
                }

                for (int i = 1; i <= 5; i++) {
                    if (i == index_1 || i == index_2 || i == index_3) {
                        continue;
                    }
                    addLabelToPowerMeters("" + 0, "PowerMeterGroup_0" + i);
                }

                //x3dBrowser.replaceWorld(mainScene);

            }
        };
        Timer simulationTimer = new Timer(1000, simulationListener);

        simulationTimer.start();
        //addLabelToPowerMeters();

    }

    /**
     * Main method.
     *
     * @param args None handled
     */
    public static void main(String[] args) {

        LoadTest demo = new LoadTest();
    }

    private void addInverseLabelToPowerMeters(String power, String powerMeterName) {
        //for ( int i = 1; i <= 5; i++){
        X3DNode transform = mainScene.createNode("Transform");

        X3DNode shape = mainScene.createNode("Shape");
        X3DNode label = mainScene.createNode("Text");
        X3DNode appearance = mainScene.createNode("Appearance");
        X3DNode material = mainScene.createNode("Material");
        X3DNode fontStyle = mainScene.createNode("FontStyle");

        MFString justify = (MFString) fontStyle.getField("justify");
        SFFloat size = (SFFloat) fontStyle.getField("size");

        justify.set1Value(0, "MIDDLE");
        justify.set1Value(1, "MIDDLE");
        size.setValue(0.15f);
        SFNode fontStyleAttribute = (SFNode) label.getField("fontStyle");
        fontStyleAttribute.setValue(fontStyle);

        SFColor diffuseColor = (SFColor) material.getField("diffuseColor");
        SFFloat ambientIntensity = (SFFloat) material.getField("ambientIntensity");

        diffuseColor.setValue(new float[]{0, 0, 1});
        ambientIntensity.setValue(1);

        SFNode appearanceMaterial = (SFNode) appearance.getField("material");
        appearanceMaterial.setValue(material);

        SFNode shapeAppearance = (SFNode) shape.getField("appearance");
        shapeAppearance.setValue(appearance);

        MFString string = (MFString) label.getField("string");
        string.clear();
        string.insertValue(0, "Watts: " + power);

        SFNode shapeGeometry = (SFNode) shape.getField("geometry");
        shapeGeometry.setValue(label);

        MFNode newTransformChildren = (MFNode) transform.getField("children");
        newTransformChildren.append(shape);

        SFVec3f serverTranslation = (SFVec3f) transform.getField("translation");
        float[] translationValues = new float[3];

        X3DNode powerMeter = mainScene.getNamedNode(powerMeterName + "_XFORM");
        SFVec3f powerMeterTranslation = (SFVec3f) powerMeter.getField("translation");
        powerMeterTranslation.getValue(translationValues);
        translationValues[1] += 0.2;
        translationValues[0] -= 0.4;

        serverTranslation.setValue(translationValues);

        SFRotation serverRotation = (SFRotation) transform.getField("rotation");
        float[] rotationValues = new float[]{0, 1, 0, 0f};
        serverRotation.setValue(rotationValues);
        mainScene.addRootNode(transform);
        powerMeterLabels.put(powerMeterName + "_Inverse", transform);
        //}
    }

    private void addLabelToPowerMeters(String power, String powerMeterName) {

        X3DNode transform = mainScene.createNode("Transform");
        X3DNode shape = mainScene.createNode("Shape");
        X3DNode label = mainScene.createNode("Text");
        X3DNode appearance = mainScene.createNode("Appearance");
        X3DNode material = mainScene.createNode("Material");
        X3DNode fontStyle = mainScene.createNode("FontStyle");

        MFString justify = (MFString) fontStyle.getField("justify");
        SFFloat size = (SFFloat) fontStyle.getField("size");

        justify.set1Value(0, "MIDDLE");
        justify.set1Value(1, "MIDDLE");
        size.setValue(0.15f);
        SFNode fontStyleAttribute = (SFNode) label.getField("fontStyle");
        fontStyleAttribute.setValue(fontStyle);

        SFColor diffuseColor = (SFColor) material.getField("diffuseColor");
        SFFloat ambientIntensity = (SFFloat) material.getField("ambientIntensity");

        diffuseColor.setValue(new float[]{0, 0, 1});
        ambientIntensity.setValue(1);

        SFNode appearanceMaterial = (SFNode) appearance.getField("material");
        appearanceMaterial.setValue(material);

        SFNode shapeAppearance = (SFNode) shape.getField("appearance");
        shapeAppearance.setValue(appearance);

        MFString string = (MFString) label.getField("string");
        string.clear();
        string.insertValue(0, "Watts: " + power);

        SFNode shapeGeometry = (SFNode) shape.getField("geometry");
        shapeGeometry.setValue(label);

        MFNode newTransformChildren = (MFNode) transform.getField("children");
        newTransformChildren.append(shape);

        SFVec3f serverTranslation = (SFVec3f) transform.getField("translation");
        float[] translationValues = new float[3];

        X3DNode powerMeter = mainScene.getNamedNode(powerMeterName + "_XFORM");
        SFVec3f powerMeterTranslation = (SFVec3f) powerMeter.getField("translation");
        powerMeterTranslation.getValue(translationValues);
        translationValues[1] += 0.2;
        serverTranslation.setValue(translationValues);

        SFRotation serverRotation = (SFRotation) transform.getField("rotation");
        float[] rotationValues = new float[]{0, 1, 0, 3f};
        serverRotation.setValue(rotationValues);

        mainScene.addRootNode(transform);
        powerMeterLabels.put(powerMeterName, transform);
        addInverseLabelToPowerMeters(power, powerMeterName);

    }

    private void addLabelToTask(String taskName, X3DNode taskTransform) {

        X3DNode transform = mainScene.createNode("Transform");
        X3DNode shape = mainScene.createNode("Shape");
        X3DNode label = mainScene.createNode("Text");
        X3DNode appearance = mainScene.createNode("Appearance");
        X3DNode material = mainScene.createNode("Material");
        X3DNode fontStyle = mainScene.createNode("FontStyle");

        MFString justify = (MFString) fontStyle.getField("justify");
        SFFloat size = (SFFloat) fontStyle.getField("size");

        justify.set1Value(0, "MIDDLE");
        justify.set1Value(1, "MIDDLE");
        size.setValue(0.15f);
        SFNode fontStyleAttribute = (SFNode) label.getField("fontStyle");
        fontStyleAttribute.setValue(fontStyle);

        SFColor diffuseColor = (SFColor) material.getField("diffuseColor");
        SFFloat ambientIntensity = (SFFloat) material.getField("ambientIntensity");

        diffuseColor.setValue(new float[]{0, 1, 0});
        ambientIntensity.setValue(1);

        SFNode appearanceMaterial = (SFNode) appearance.getField("material");
        appearanceMaterial.setValue(material);

        SFNode shapeAppearance = (SFNode) shape.getField("appearance");
        shapeAppearance.setValue(appearance);

        MFString string = (MFString) label.getField("string");
        string.clear();
        string.insertValue(0, taskName);

        SFNode shapeGeometry = (SFNode) shape.getField("geometry");
        shapeGeometry.setValue(label);

        MFNode newTransformChildren = (MFNode) transform.getField("children");
        newTransformChildren.append(shape);



        SFRotation serverRotation = (SFRotation) transform.getField("rotation");
        float[] rotationValues = new float[]{0, 1, 0, 3};
        serverRotation.setValue(rotationValues);

        SFVec3f serverTranslation = (SFVec3f) transform.getField("translation");
        float[] translationValues = new float[3];

        //SFVec3f powerMeterTranslation = (SFVec3f) taskTransform.getField("translation");
        //powerMeterTranslation.getValue(translationValues);
        //translationValues[1] += 0.2;
        translationValues[1] += 0.1;
        translationValues[0] += 0.15;
        translationValues[2] -= 0.15;
        serverTranslation.setValue(translationValues);

        MFNode taskTransformChildren = (MFNode) taskTransform.getField("children");
        taskTransformChildren.append(transform);
        mainScene.addRootNode(taskTransform);

        taskLabels.add(taskTransform);
    }

    public void createTask(String taskName, String serverName, float yVal) {

        X3DNode newShape = mainScene.createNode("Shape");
        X3DNode newBox = mainScene.createNode("Box");
        X3DNode newTransform = mainScene.createNode("Transform");
        X3DNode appearance = mainScene.createNode("Appearance");
        X3DNode material = mainScene.createNode("Material");

        SFNode appearanceField = (SFNode) newShape.getField("appearance");
        SFVec3f boxSize = (SFVec3f) newBox.getField("size");
        boxSize.setValue(new float[]{0.2699f, 0.08436f, 0.2801f});


        SFColor diffuseColor = (SFColor) material.getField("diffuseColor");
        SFFloat ambientIntensity = (SFFloat) material.getField("ambientIntensity");

        diffuseColor.setValue(new float[]{0.5529f, 0.02745f, 0.2275f});
        ambientIntensity.setValue(1);

        SFNode appearanceMaterial = (SFNode) appearance.getField("material");
        appearanceMaterial.setValue(material);
        appearanceField.setValue(appearance);

        SFNode shape_geometry = (SFNode) (newShape.getField("geometry"));
        shape_geometry.setValue(newBox);
        MFNode newTransformChildren = (MFNode) newTransform.getField("children");
        newTransformChildren.append(newShape);

        //SFVec3f scale = (SFVec3f) newTransform.getField("scale");
        //scale.setValue(new float[]{1.0f, 1.0f, 1.0f});

        SFVec3f translation = (SFVec3f) newTransform.getField("translation");

        X3DNode serverTransform = mainScene.getNamedNode(serverName + "_XFORM");
        SFVec3f serverTranslation = (SFVec3f) serverTransform.getField("translation");
        float[] translationValues = new float[3];

        serverTranslation.getValue(translationValues);
        translationValues[1] += yVal;
        translation.setValue(translationValues);

        SFRotation newTransformRotation = (SFRotation) newTransform.getField("rotation");
        SFRotation serverRotation = (SFRotation) serverTransform.getField("rotation");
        float[] rotationValues = new float[]{0, 1, 0, 3};
        serverRotation.getValue(rotationValues);
        newTransformRotation.getValue(rotationValues);

        //runningTasks.put(taskName, newTransform);

        addLabelToTask(taskName, newTransform);

    }
}
