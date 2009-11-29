package contextawaremodel.sensorapi;

import java.util.Iterator;
import java.util.LinkedList;

class SensorWSReader {

    private LinkedList<SensorListener> obsList;
    private WSReaderThread wsRT;

    public SensorWSReader(String wsURL) {
        this(wsURL, 1000);
    }

    public SensorWSReader(String wsURL, long timeInterval) {
        this.obsList = new LinkedList<SensorListener>();

        wsRT = new WSReaderThread(timeInterval, this, wsURL);
        wsRT.start();
    }

    public void pauseWSReadThread() {
        this.wsRT.setEnabled(false);
    }

    public void resumeWSReadThread() {
        this.wsRT.setEnabled(true);
    }

    public boolean addListener(SensorListener listener) {
        return this.obsList.add(listener);
    }

    public boolean removeListener(SensorListener listener) {
        return this.obsList.remove(listener);
    }

    public synchronized void newValueFromWS(double newValue) {
        Iterator<SensorListener> it = obsList.iterator();
        while (it.hasNext()) {
            it.next().valueChanged(newValue);
        }
    }

    public void setInterval(long interval) {
        wsRT.setInterval(interval);
    }

    public long getInterval() {
        return wsRT.getInterval();
    }
}
