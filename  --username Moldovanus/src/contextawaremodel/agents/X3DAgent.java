/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contextawaremodel.agents;

import contextawaremodel.agents.behaviours.BasicX3DABehaviour;
import jade.core.Agent;
import x3dtest.LoadTest;

/**
 *
 * @author Administrator
 */
public class X3DAgent extends Agent {
    private LoadTest loadTest;
    @Override
    protected void setup() {
        System.out.println("[X3DAgent] : Hellooo ! ");
      loadTest= new LoadTest();
      addBehaviour(new BasicX3DABehaviour(loadTest,this));

    }

}
