package ses.main.logic;


public interface SesLogicMBean {

	String addNewDriver(String pathToSensorML);

	String getDriversInfo();

	String getDriverInfo(String uniqueId);

	void startSampling(String uniqueId);

	void stopSampling(String uniqueId);

	void removeDriver(String uniqueId);

}
