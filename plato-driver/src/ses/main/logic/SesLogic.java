package ses.main.logic;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBException;

public class SesLogic implements SesLogicMXBean {

	//PINAKAS OPOU THA BRISKONTAI APOTHIKEYMENES OI PLHROFORIES GIA OLOUS TOUS EN ENERGEIA DRIVER TOU SES
	private Map<String, DriverConnection> drivers = new HashMap<String, DriverConnection>();

	private static boolean TESTING = Boolean.TRUE;

	public void start() {
	}

	public void stop() {
		for (String uniqueID : drivers.keySet()) {
			removeDriver(uniqueID);
		}
	}

	@Override
	public String addNewDriver(String sensorML) {
		if (TESTING) {
			return "test-driver";
		}
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
		if (TESTING) {
			result.put("test-driver", "TEST DRIVER INFO");
			return result;
		}
		for (DriverConnection driver : drivers.values()) {
			result.put(driver.uniqueID, driver.getDriverInfo());
		}
		return result;
	}

	@Override
	public String getDriverInfo(String uniqueId) {
		if (TESTING) {
			return "TEST DRIVER INFO";
		}
		if (drivers.containsKey(uniqueId)) {
			return drivers.get(uniqueId).getDriverInfo();
		}
		return "Driver.UniqueID not found";
	}

	@Override
	public void startSampling(String uniqueId) {
		if (TESTING) {
			return;
		}
		if (drivers.containsKey(uniqueId)) {
			drivers.get(uniqueId).startSampling();
		}
	}

	@Override
	public void stopSampling(String uniqueId) {
		if (TESTING) {
			return;
		}
		if (drivers.containsKey(uniqueId)) {
			drivers.get(uniqueId).stopSampling();
		}
	}

	@Override
	public void removeDriver(String uniqueId) {
		if (TESTING) {
			return;
		}
		if (drivers.containsKey(uniqueId)) {
			DriverConnection driver = drivers.get(uniqueId);
			driver.stopSampling();
			driver.destroyDriver();
			drivers.remove(uniqueId);
		}
	}

	@Override
	public String getLastReading(String uniqueId) {
		if (TESTING) {
			DecimalFormat df = new DecimalFormat("00.00");
			Random r = new Random();

			return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<ns6:Observation ns1:id=\"Libelium_acc_24\" xmlns:ns2=\"http://www.w3.org/1999/xlink\" xmlns:ns1=\"http://www.opengis.net/gml\" xmlns:ns4=\"urn:us:gov:ic:ism:v2\" xmlns:ns3=\"http://www.opengis.net/sensorML/1.0.1\" xmlns:ns5=\"http://www.opengis.net/swe/1.0.1\" xmlns:ns6=\"http://www.opengis.net/om/1.0\" xmlns:ns7=\"http://www.w3.org/2001/SMIL20/\" xmlns:ns8=\"http://www.w3.org/2001/SMIL20/Language\">" +
				"<ns1:description>Libelium Driver 1 Data</ns1:description>" +
				"<ns6:samplingTime ns2:type=\"simple\">" +
				"<ns1:TimeInstant>" +
				"<ns1:timePosition>" + System.currentTimeMillis() + "</ns1:timePosition>" +
				"</ns1:TimeInstant>" +
				"</ns6:samplingTime>" +
				"<ns6:procedure ns2:type=\"simple\" ns2:href=\"Libelium_acc\"/>" +
				"<ns6:result xsi:type=\"ns5:DataRecordType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"<ns5:field name=\"sensor_id\" ns2:type=\"simple\"/>" +
				"<ns5:field name=\"temperature\" ns2:type=\"simple\">" +
				"<ns5:Quantity>" +
				"<ns5:uom ns2:type=\"simple\" ns2:href=\"Celsius\"/>" +
				"<ns5:value>" + df.format(100 * r.nextFloat() + 1) + "</ns5:value>" +
				"</ns5:Quantity>" +
				"</ns5:field>" +
				"</ns6:result>" +
				"</ns6:Observation>";
		}
		if (drivers.containsKey(uniqueId)) {
			return drivers.get(uniqueId).getLastReading();
		}
		return "";
	}

}
