package contextawaremodel.sensorapi;

import java.util.Random;

public class WSReaderThread extends Thread {

    private long timeInterval;
    private SensorWSReader sensorWSReader;
    private String wsURL;
    private boolean enabled;
    private static Random random = new Random();

    public WSReaderThread(long timeInterval, SensorWSReader sensorWSReader, String wsURL) {
        this.timeInterval = timeInterval;
        this.sensorWSReader = sensorWSReader;
        this.enabled = true;
        this.wsURL = wsURL;
        //setDaemon(true);
    }

    public void setEnabled(boolean newValue) {
        this.enabled = newValue;
    }

    @Override
    public void run() {
        while (enabled) {
            // TODO: Solve the WS problem.
            WSRequestBuilder wsrb = new WSRequestBuilder(wsURL);
            sensorWSReader.newValueFromWS(wsrb.getSensorValue());
            //sensorWSReader.newValueFromWS(random.nextDouble());

            try {
                sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setInterval(long interval) {
        this.timeInterval = interval;
    }

    public long getInterval() {
        return this.timeInterval;
    }
}
