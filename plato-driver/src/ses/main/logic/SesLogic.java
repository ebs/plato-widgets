package ses.main.logic;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

public class SesLogic implements SesLogicMXBean {

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
	public String addNewDriver(String sensorML) {
		try {
			DriverConnection newDriver = new DriverConnection(sensorML);
			String uniqueID = newDriver.init();
			drivers.put(uniqueID, newDriver);
			return uniqueID;
		} catch (JAXBException e) {
			return null;
		}
	}

	@Override
	public Map<String, String> getDriversInfo() {
		Map<String, String> result = new HashMap<String, String>();
		for (DriverConnection driver : drivers.values()) {
			result.put(driver.uniqueID, driver.getDriverInfo());
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

	@Override
	public String getLastReading(String uniqueId) {
		if (drivers.containsKey(uniqueId)) {
			return drivers.get(uniqueId).getLastReading();
		}
		return "";
	}

}
