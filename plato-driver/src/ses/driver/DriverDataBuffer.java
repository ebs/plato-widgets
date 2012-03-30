package ses.driver;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DriverDataBuffer {

	//THEWRW SAN MEGISTO PLHTHOS APOTHIKEYMENVN TIMWN TO 100
	private BlockingQueue<ArrayList<ValueFormat>> itemsToLog;

	public DriverDataBuffer() {
		itemsToLog = new ArrayBlockingQueue<ArrayList<ValueFormat>>(100);
	}

	public void insertData(ArrayList<ValueFormat> data) {
		try {
			itemsToLog.put(data);
		} catch (InterruptedException ex) {
			System.out.println("Buffer Insertion Error");
		}
	}

	public ArrayList<ValueFormat> getData() {

		if (itemsToLog.size() > 0) {
			try {
				return itemsToLog.take();
			} catch (InterruptedException ex) {
				System.out.println("Buffer Remove Error");
			}
		}
		return null;
	}

	public void destroyBuffer() {
		itemsToLog.clear();
		itemsToLog = null;
	}
}
