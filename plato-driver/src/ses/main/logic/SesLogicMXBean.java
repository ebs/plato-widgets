package ses.main.logic;

import java.util.Map;

import javax.management.MXBean;

@MXBean
public interface SesLogicMXBean {

	String addNewDriver(String pathToSensorML);

	Map<String, String> getDriversInfo();

	String getDriverInfo(String uniqueId);

	void startSampling(String uniqueId);

	void stopSampling(String uniqueId);

	void removeDriver(String uniqueId);

	String getLastReading(String uniqueId);

}
