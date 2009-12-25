/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.x3dCommand;

import org.web3d.x3d.sai.MFString;
import org.web3d.x3d.sai.X3DNode;
import org.web3d.x3d.sai.X3DScene;

/**
 *
 * @author Me
 */
public class X3DStringCommand extends X3DCommand {

    private String content;

    public String getContent() {
        return content;
    }

    public X3DStringCommand(String nodeName, String content) {
        super(nodeName);
        this.content = content;
    }

    @Override
    public void execute(X3DScene mainScene) {
        X3DNode sensor = mainScene.getNamedNode(nodeName);
        MFString sensorState = (MFString) sensor.getField("string");
        sensorState.set1Value(0, content);
    }
}
