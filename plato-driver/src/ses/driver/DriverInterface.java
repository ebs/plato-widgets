package ses.driver;

import java.util.ArrayList;

import net.opengis.sensorml.v_1_0_1.SensorML;

public interface DriverInterface {

	void initDriver(SensorML sensorMLDoc);

	void destroyDriver();

	String getNetworkId();

	String getNetworkType();

	ArrayList<String> getNodesList();

	double getSamplingRate(int node);

	//
	//setSamplingRate(int node, int time)
	//getNetworkStatus()

	/*
	 * Θέτει την κατάσταση λειτουργίας των κόμβων του δικτύου. Παίρνει σαν
	 * όρισμα την κατάσταση (On, Off) και την διάρκεια της συγκεκριμένης
	 * κατάστασης. Η παράμετρος time όταν πάρει την τιμή 0 , θα θέτει την
	 * διάρκεια της συγκεκριμένης κατάστασης στο «απεριόριστο» (μέχρι να έρθει
	 * νέα εντολή αλλαγής της κατάστασης). Aυτή η εναλλαγή της κατάστασης
	 * λειτουργίας των κόμβων του δικτύου είναι χρήσιμη για εξοικονόμηση
	 * ενέργειας όταν δεν είναι απαραίτητη η συνεχή λειτουργία των κόμβων. Π.χ.
	 * στην περίπτωση όπου υπάρχει ένα δίκτυο κόμβων πάνω σε φορτηγά, κατά την
	 * διάρκεια της ημέρας θέλουμε να παίρνουμε μετρήσεις για τα φορτηγά. Το
	 * βράδυ όμως που τα φορτηγά θα βρίσκονται παρκαρισμένα, δεν υπάρχει λόγος
	 * να παίρνουμε μετρήσεις. Οπότε και το δίκτυο μπορεί να είναι ανενεργό.
	 */
	void setNetworkStatus(boolean status, int time);

	//getLowPowerStatus()
	//setLowPowerStatus( boolean status)
	//getAttribute(int node, String attr)
	//setAttribute(int node, String attr, String attr_info)
	//
	//
	//getReadingsToTime(int time)
	ArrayList<ValueFormat> getLastReading(int node);

	String getLastXMLReading(int node);
	//getReadings(int node)
	//getStreamingData(int node)
}
