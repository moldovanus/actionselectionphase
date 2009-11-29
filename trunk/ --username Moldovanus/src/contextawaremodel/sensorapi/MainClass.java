package contextawaremodel.sensorapi;

public class MainClass {
	public static void main(String[] args) {

        /*
		SensorWSReader swsr = new SensorWSReader("http://localhost:8080/RandomSensorWS/RandomSensorWSService");
		
		swsr.addListener( new SensorListener() {
			public void valueChanged(double newValue) {
				System.out.println(newValue + "\n"); 
			}
		});*/

        SensorAPI.addSensorListener("http://localhost:2056/TurnSensorWS.asmx", new SensorListener() {

            public void valueChanged(double newValue) {
                System.out.println(newValue);
            }
        });
	}
}
