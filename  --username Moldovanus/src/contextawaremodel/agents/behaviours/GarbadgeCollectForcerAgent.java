/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 *
 * @author Moldovanus
 */
public class GarbadgeCollectForcerAgent extends TickerBehaviour {

    public GarbadgeCollectForcerAgent(Agent a, long period) {
        super(a, period);
    }

    

    @Override
    protected void onTick() {
        Runtime r = Runtime.getRuntime();
        r.gc();

    }
}
