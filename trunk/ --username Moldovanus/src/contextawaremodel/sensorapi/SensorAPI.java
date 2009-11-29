package contextawaremodel.sensorapi;

import java.util.Hashtable;

public class SensorAPI {

    private static Hashtable<String, SensorWSReader> readerThreads = new Hashtable<String, SensorWSReader>();
    
	public static double getSensorValue(String wsURL) {
		WSRequestBuilder wsrb = new WSRequestBuilder(wsURL); 
		return wsrb.getSensorValue();
	}

    public static void addSensorListener(String url, SensorListener listener, long timeInterval) {
        if (readerThreads.containsKey(url)) {
            SensorWSReader reader = readerThreads.get(url);
            reader.setInterval(Math.min(timeInterval, reader.getInterval()));
            reader.addListener(listener);
        } else {
            SensorWSReader reader = new SensorWSReader(url, timeInterval);
            reader.addListener(listener);
        }
    }

    public static void addSensorListener(String url, SensorListener listener) {
        addSensorListener(url, listener, 3000);
    }
}
