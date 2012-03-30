package ses.main.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.opengis.sensorml.v_1_0_1.Capabilities;
import net.opengis.sensorml.v_1_0_1.ComponentType;
import net.opengis.sensorml.v_1_0_1.Identification;
import net.opengis.sensorml.v_1_0_1.Identification.IdentifierList;
import net.opengis.sensorml.v_1_0_1.Identification.IdentifierList.Identifier;
import net.opengis.sensorml.v_1_0_1.IoComponentPropertyType;
import net.opengis.sensorml.v_1_0_1.Outputs;
import net.opengis.sensorml.v_1_0_1.SensorML;
import net.opengis.sensorml.v_1_0_1.SensorML.Member;
import ses.driver.DriverInterface;

public class DriverConnection {

	public String uniqueID;

	private String driverType;

	private String pathToSensorML;

	private SensorML sensorML = null;

	private double measuringInterval;

	private ArrayList<ValueFormat> outputData;

	//H KLASH TOU DRIVER. EPEIDH DEN THA XEROUME TON TYPO,
	//TON ORIZOUME ENAN GENIKO  TYPO, KAI META THA KALOUME THN 
	//KATALLHLH KLASH ME THN SYNARTHSH Class.forName(...)
	//OPOU MESA STHN PARAMETRO THA MPAINEI TO ONOMA TOU DRIVER (0POTE KAI THS KLASHS TOU).
	//TO ONOMA AYTO THA TO XEROUME MESW TOU SENSORML. EINAI TO driverType;    
	//GIA NA MPOROUME NA TO XYRISTOUME APO EKEI KAI KATW KANOUME UPCASTING SE DriverInterface
	private DriverInterface driver;

	//TO THREAD POU PERIODIKA DIABAZEI TA DEDOMENA APO TON DRIVER
	private DriverConnectionReadThread tt = null;

	//O TIMER POU ANALAMBANEI PERIODIKA NA KALEI TO THREAD KAI NA PAIRNEI TA DEDOMENA APO TON DRIVER
	private java.util.Timer t1 = null;

	public DriverConnection(String pathToSensorML) {
		outputData = new ArrayList<ValueFormat>();
		this.pathToSensorML = pathToSensorML;
	}

	public String init() {
		//PARSARISMA TOU SENSORML WSTE NA PAROUME TA OUPUTS KAI TO ID
		parseSensorML();
		//DHMIOURGEIA TOU PINAKA STHN BASH
		//////////////////////////////////////////////// baseConn.createTable(prepareTableFormat());
		try {
			//DHMIOURGEIA TOU DRIVER
			//KALOUME THN KATALLHLH KLASH ME BASH TO ONOMA POU PHRAME APO TO SENSORML
			Class driverClass = Class.forName("ses.driver." + driverType);
			driver = (DriverInterface) driverClass.newInstance();
			driver.initDriver(sensorML);
		} catch (ClassNotFoundException ex) {
			System.out.println("Den brethike o Driver " + driverType);
		} catch (InstantiationException ex) {
			System.out.println("Lathos Drivera " + driverType);
		} catch (IllegalAccessException ex) {
			System.out.println("Lathos Drivers " + driverType);
		}

		return uniqueID;

	}

	private void parseSensorML() {

		JAXBContext jc = null;
		Unmarshaller u = null;

		// Regions regions = null;
		try {
			// Set the JAXB context to the package name we uses
			// when we created all the JAXB classes from the XSD
			//SensorML

			//  System.out.println(SensorML.class);
			jc = JAXBContext.newInstance(SensorML.class.getPackage().getName());//"net.opengis.sensorml.v_1_0_1");
		} catch (JAXBException e) {
			System.out.println("ASDF");
			e.printStackTrace();
		}

		try {
			// Create an unmarshaller (unmarshal from XML to Java objects)
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		try {
			sensorML = (SensorML) u.unmarshal(new File(pathToSensorML));
		} catch (JAXBException ex) {
			ex.printStackTrace();
		}

		Member m = sensorML.getMember().get(0);
		//System.out.println(m.getProcess());
		JAXBElement proce = m.getProcess();
		ComponentType component = (ComponentType) proce.getValue();

		//CREATE NEW DRIVER WITH THE SENSORML COMPONENT ID            
		driverType = component.getId();

		/////////////////////////////////
		////  SENSORML IDENTIFICATION  //
		/////////////////////////////////
		Identification ident = component.getIdentification().get(0);
		IdentifierList identList = ident.getIdentifierList();

		List<Identifier> idents = identList.getIdentifier();

		for (int i = 0; i < idents.size(); i++) {
			if (idents.get(i).getName().equals("uniqueID")) {
				uniqueID = idents.get(i).getTerm().getValue();
				//System.out.println(uniqueID);
			}
		}

		/////////////////////////////
		////  SENSORML OUTPUTS     //
		/////////////////////////////
		Outputs outs = component.getOutputs();
		Outputs.OutputList outlist = outs.getOutputList();

		//EISAGOUME TO ID TWN OUPUTS TOU SENSORML
		System.out.println(outlist.getId());

		List<IoComponentPropertyType> output = outlist.getOutput();

		//EISAGOUME TA OUTPUTS. GIA KATHE OUTPUT PAIRNOUME TO ONOMA, TON TYPO KAI TO ID.
		//MPOROUME NA PAROUME KAI TA YPOLOIPA , APLA MONO AYTA THELW GIA TON DRIVER
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).isSetText()) {
				outputData.add(new ValueFormat(output.get(i).getName(), "text", output.get(i).getText().getId()));
			} else if (output.get(i).isSetQuantity()) {
				outputData.add(new ValueFormat(output.get(i).getName(), "quantity", output.get(i).getQuantity().getId() + ""));
			} else if (output.get(i).isSetBoolean()) {
				outputData.add(new ValueFormat(output.get(i).getName(), "boolean", output.get(i).getBoolean().getId() + ""));
			}
		}

		//////////////////////////////
		////  SENSORML CAPABILITIES //
		////  MEASURING INTERVALS   //
		//////////////////////////////

		List<Capabilities> capables = component.getCapabilities();
		Capabilities capable = capables.get(0);
		JAXBElement tempElement = capable.getAbstractDataRecord();
		net.opengis.swe.v_1_0_1.DataRecordType capableRecord = (net.opengis.swe.v_1_0_1.DataRecordType) tempElement.getValue();
		List<net.opengis.swe.v_1_0_1.DataComponentPropertyType> capableField = capableRecord.getField();
		for (int i = 0; i < capableField.size(); i++) {
			if (capableField.get(i).getName().equals("measuringInterval")) {
				measuringInterval = capableField.get(i).getQuantity().getValue();
			}
		}
	}

	public String getDriverInfo() {
		String result = "-------------------------------------------------------\n";
		result += "Network ID\t=\t" + driver.getNetworkId() + "\n";
		result += "Network Type\t=\t" + driver.getNetworkType() + "\n";
		result += "Node Id List:\n";
		ArrayList<String> temp = driver.getNodesList();
		for (int i = 0; i < temp.size(); i++) {
			result += "\t" + temp.get(i) + "\n";
		}
		result += "Sampling Rate\t=\t" + driver.getSamplingRate(-1) + "\n";
		result += "-------------------------------------------------------\n";

		return result;
	}

	public void startSampling() {
		//THETOUME TO DIKTYO ENERGO GIA APERIORISTO XRONO, WSTE NA STELNEI DEDOMENA KAI NA TA DIABAZEI O DRIVER
		//KAI NA TA PROWTHEI STON SES
		driver.setNetworkStatus(true, 0);

		t1 = new java.util.Timer();
		tt = new DriverConnectionReadThread(driver);
		t1.schedule(tt, 0, (int) measuringInterval);
	}

	public void stopSampling() {
		if (driver != null) {
			//THETOUME TO DIKTYO ANENERGO GIA APERIORISTO XRONO, ETSI TO DIKTYO DEN TYHA STELNEI DEDOMENA
			//STON DRIVER 
			driver.setNetworkStatus(false, 0);
		}
		if (t1 != null) {
			tt.cancel();
			t1.cancel();
			t1.purge();
		}
	}

	public void destroyDriver() {
		driver.destroyDriver();
	}
}
