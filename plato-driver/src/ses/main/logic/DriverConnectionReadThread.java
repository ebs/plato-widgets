package ses.main.logic;

import java.util.TimerTask;

import ses.driver.DriverInterface;

public class DriverConnectionReadThread extends TimerTask {

	DriverInterface driver;

	DriverConnectionReadThread(DriverInterface driver) {
		this.driver = driver;
	}

	public void run() {
		String lastReading = driver.getLastXMLReading(-1);
		System.out.println(lastReading);
	}

}
