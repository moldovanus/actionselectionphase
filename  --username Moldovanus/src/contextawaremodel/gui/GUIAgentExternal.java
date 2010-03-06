package contextawaremodel.gui;

public interface GUIAgentExternal {
    public void shutdownPlatform();
    public void showMemoryMonitor();
    public void startJADEGUI();
    public void addIndividual(String name);
    public void startRealTimePlot(long interval);
}
