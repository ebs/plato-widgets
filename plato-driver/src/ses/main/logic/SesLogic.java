package ses.main.logic;

import java.util.HashMap;
import java.util.Map;

public class SesLogic implements SesLogicMBean {

	//PINAKAS OPOU THA BRISKONTAI APOTHIKEYMENES OI PLHROFORIES GIA OLOUS TOUS EN ENERGEIA DRIVER TOU SES
	private Map<String, DriverConnection> drivers = new HashMap<String, DriverConnection>();

	public void start() {
	}

	public void stop() {
		for (String uniqueID : drivers.keySet()) {
			removeDriver(uniqueID);
		}
	}

	@Override
	public String addNewDriver(String pathToSensorML) {
		DriverConnection newDriver = new DriverConnection(pathToSensorML);
		String uniqueID = newDriver.init();
		drivers.put(uniqueID, newDriver);
		return uniqueID;

	}

	@Override
	public String getDriversInfo() {
		String result = "";

		for (DriverConnection driver : drivers.values()) {
			result += driver.getDriverInfo() + "\n";
		}

		return result;
	}

	@Override
	public String getDriverInfo(String uniqueId) {
		if (drivers.containsKey(uniqueId)) {
			return drivers.get(uniqueId).getDriverInfo();
		}
		return "Driver.UniqueID not found";
	}

	@Override
	public void startSampling(String uniqueId) {
		if (drivers.containsKey(uniqueId)) {
			drivers.get(uniqueId).startSampling();
		}
	}

	@Override
	public void stopSampling(String uniqueId) {
		if (drivers.containsKey(uniqueId)) {
			drivers.get(uniqueId).stopSampling();
		}
	}

	@Override
	public void removeDriver(String uniqueId) {
		if (drivers.containsKey(uniqueId)) {
			DriverConnection driver = drivers.get(uniqueId);
			driver.stopSampling();
			driver.destroyDriver();
			drivers.remove(uniqueId);
		}
	}

}
