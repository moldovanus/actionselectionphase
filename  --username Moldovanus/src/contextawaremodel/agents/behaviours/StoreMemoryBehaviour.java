/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import actionselection.context.Memory;
import contextawaremodel.GlobalVars;
import contextawaremodel.agents.ReinforcementLearningAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class StoreMemoryBehaviour extends TickerBehaviour {

    private Memory memory;
    private ReinforcementLearningAgent agent;

    public StoreMemoryBehaviour(Agent a, long period, Memory memory) {
        super(a, period);
        agent = (ReinforcementLearningAgent) a;
        this.memory = memory;
    }

    @Override
    protected void onTick() {

 

        FileOutputStream fileOutputStream = null;
        {
            ObjectOutputStream outputStream = null;
            try {
                File memoryFile = new File(GlobalVars.MEMORY_FILE);
                fileOutputStream = new FileOutputStream(memoryFile);
                outputStream = new ObjectOutputStream(fileOutputStream);
                outputStream.writeObject(memory);
                System.out.println(" Memory has been saved to \"" + GlobalVars.MEMORY_FILE + "\".");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StoreMemoryBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(StoreMemoryBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(StoreMemoryBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(StoreMemoryBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
