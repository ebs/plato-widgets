package ses.driver;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.opengis.gml.v_3_1_1.StringOrRefType;
import net.opengis.gml.v_3_1_1.TimeInstantType;
import net.opengis.gml.v_3_1_1.TimePositionType;
import net.opengis.om.v_1_0_0.ObjectFactory;
import net.opengis.om.v_1_0_0.ObservationType;
import net.opengis.om.v_1_0_0.ProcessPropertyType;
import net.opengis.sensorml.v_1_0_1.Capabilities;
import net.opengis.sensorml.v_1_0_1.ComponentType;
import net.opengis.sensorml.v_1_0_1.Identification;
import net.opengis.sensorml.v_1_0_1.Identification.IdentifierList;
import net.opengis.sensorml.v_1_0_1.Identification.IdentifierList.Identifier;
import net.opengis.sensorml.v_1_0_1.Interface;
import net.opengis.sensorml.v_1_0_1.InterfaceDefinition;
import net.opengis.sensorml.v_1_0_1.Interfaces;
import net.opengis.sensorml.v_1_0_1.Interfaces.InterfaceList;
import net.opengis.sensorml.v_1_0_1.IoComponentPropertyType;
import net.opengis.sensorml.v_1_0_1.LayerPropertyType;
import net.opengis.sensorml.v_1_0_1.Outputs;
import net.opengis.sensorml.v_1_0_1.SensorML;
import net.opengis.swe.v_1_0_1.DataComponentPropertyType;
import net.opengis.swe.v_1_0_1.DataRecordType;
import net.opengis.swe.v_1_0_1.Quantity;
import net.opengis.swe.v_1_0_1.Text;
import net.opengis.swe.v_1_0_1.TimeObjectPropertyType;
import net.opengis.swe.v_1_0_1.UomPropertyType;

// import net.opengis.swe.v_1_0_1.*;

public class Libelium_acc implements DriverInterface {

	String id;

	String interfaceType;

	String outputsId;

	double measuringInterval;

	static int dataOutputId = 0;

	//SENSORML DATA
	ArrayList<ValueFormat> identificationData;

	ArrayList<ValueFormat> interfaceData;

	ArrayList<ValueFormat> outputData;

	ArrayList<ResultFormat> resultFormat;

	//NODELIST  PERIEXEI TA ID TWN AISTHITIRWN POU YPARXOUN STO DYKTIO.
	//AYTA TA ID DEN THA PROERXONTAI APO TO SENSORML.
	//THA GEMIZEI AYTOMATA KATHE FORA POU LAMBANETAI MHNYMA APO KAPOIO KAINOURGIO AISHITHRA
	ArrayList<String> nodeIdList;

	//O BUFFER GIA THN APOTHIKEYSH TVN DEDOMENVN
	DriverDataBuffer buffer;

	//TO EPIPEDO EPIKOINVNIAS KAI LHPSHS TVN DEDOMENVN TOU DRIVER
	SensorCommunication driverComm;

	//ARXIKOPOIOYME TO JAXBContent WSTE NA TO XRHSIMPOIOYME STHN EPISTROFH TVN DEDOMENVN STON SES.
	JAXBContext jaxbContext;

	public Libelium_acc() {

		identificationData = new ArrayList<ValueFormat>();
		interfaceData = new ArrayList<ValueFormat>();
		outputData = new ArrayList<ValueFormat>();

		resultFormat = new ArrayList<ResultFormat>();

		nodeIdList = new ArrayList<String>();
	}

	@Override
	public void initDriver(SensorML sensorMLDoc) {

		/**
		 * *******************************************************
		 * PARSARISMA SENSORML
		 * *******************************************************
		 */
		//OPEN SENSORML
		SensorML.Member m = sensorMLDoc.getMember().get(0);
		System.out.println(m.getProcess());
		JAXBElement proce = m.getProcess();
		ComponentType component = (ComponentType) proce.getValue();

		//CREATE NEW DRIVER WITH THE SENSORML COMPONENT ID
		id = component.getId();

		/////////////////////////////////
		////  SENSORML IDENTIFICATION  //
		/////////////////////////////////
		List<Identification> ident = component.getIdentification();
		Identification identList = ident.get(0);
		IdentifierList identsList = identList.getIdentifierList();
		List<Identifier> idents = identsList.getIdentifier();

		for (int i = 0; i < idents.size(); i++) {
			identificationData.add(new ValueFormat(idents.get(i).getName(), idents.get(i).getTerm().getDefinition(), idents.get(i).getTerm().getValue()));
		}

		/////////////////////////////
		////  SENSORML INTERFACES  //
		/////////////////////////////

		//PARSE INTERFACE SENSORML PARAMETERS
		Interfaces inter = component.getInterfaces();
		InterfaceList interList = inter.getInterfaceList();
		Interface interf = interList.getInterface().get(0);

		//EISAGWGH TOU TYPOY TOY INTERFACE
		interfaceType = interf.getName();

		InterfaceDefinition intDe = interf.getInterfaceDefinition();
		LayerPropertyType intLayer = intDe.getPhysicalLayer();
		JAXBElement tempDataRecord = intLayer.getAbstractDataRecord();
		DataRecordType dataRecord = (DataRecordType) tempDataRecord.getValue();
		List<DataComponentPropertyType> fields = dataRecord.getField();

		//PARSAREI OLES TIS PARAMETROUS KAI GIA KATHE PARAMETROU TOU INTERFACE TO TOPOTHETEI MESA STO
		//DRIVER POU DHMIOURGHTHIKE PARAPANW
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).isSetText()) {
				interfaceData.add(new ValueFormat(fields.get(i).getName(), "text", fields.get(i).getText().getValue()));
			} else if (fields.get(i).isSetQuantity()) {
				interfaceData.add(new ValueFormat(fields.get(i).getName(), "quantity", fields.get(i).getQuantity().getValue() + ""));
			} else if (fields.get(i).isSetBoolean()) {
				interfaceData.add(new ValueFormat(fields.get(i).getName(), "boolean", fields.get(i).getBoolean() + ""));
			}
		}

		/////////////////////////////
		////  SENSORML OUTPUTS     //
		/////////////////////////////
		Outputs outs = component.getOutputs();
		Outputs.OutputList outlist = outs.getOutputList();

		//EISAGOUME TO ID TWN OUPUTS TOU SENSORML
		outputsId = outlist.getId();

		List<IoComponentPropertyType> output = outlist.getOutput();

		//EISAGOUME TA OUTPUTS. GIA KATHE OUTPUT PAIRNOUME TO ONOMA, TON TYPO KAI TO ID.
		//MPOROUME NA PAROUME KAI TA YPOLOIPA , APLA MONO AYTA THELW GIA TON DRIVER
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).isSetText()) {
				outputData.add(new ValueFormat(output.get(i).getName(), "text", output.get(i).getText().getId()));
				resultFormat.add(new ResultFormat(output.get(i).getName(), "text", output.get(i).getText().getId(), ""));
			} else if (output.get(i).isSetQuantity()) {
				outputData.add(new ValueFormat(output.get(i).getName(), "quantity", output.get(i).getQuantity().getId() + ""));

				Quantity code = output.get(i).getQuantity();
				UomPropertyType uom = code.getUom();
				resultFormat.add(new ResultFormat(output.get(i).getName(), "quantity", output.get(i).getQuantity().getId() + "", uom.getCode()));
			} else if (output.get(i).isSetBoolean()) {
				outputData.add(new ValueFormat(output.get(i).getName(), "boolean", output.get(i).getBoolean().getId() + ""));
				resultFormat.add(new ResultFormat(output.get(i).getName(), "boolean", output.get(i).getBoolean().getId() + "", ""));
			}
		}

		//////////////////////////////
		////  SENSORML CAPABILITIES //
		////  MEASURING INTERVALS   //
		//////////////////////////////
		Capabilities capable = component.getCapabilities().get(0);
		JAXBElement tempCapableRecord = capable.getAbstractDataRecord();
		DataRecordType capableRecord = (DataRecordType) tempCapableRecord.getValue();
		List<DataComponentPropertyType> capableField = capableRecord.getField();
		for (int i = 0; i < capableField.size(); i++) {
			if (capableField.get(i).getName().equals("measuringInterval")) {
				measuringInterval = capableField.get(i).getQuantity().getValue();
			}
		}

		//        } catch (XmlException ex) {
		//            System.out.println("Driver SensorML Parsing ERROR");
		//        } catch (IOException ex) {
		//            System.out.println("Driver SensorML Parsing ERROR");
		//        }

		/**
		 * *******************************************************
		 * BUFFER *******************************************************
		 */
		//ARXIKOPOIHSH TOU BUFFER GIA THN APOTHIKEYSH TVN DEODMENVN
		buffer = new DriverDataBuffer();

		/**
		 * *******************************************************
		 * SERIAL COMMUNICATION
		 * *******************************************************
		 */
		//DHMIOURGEIA TOU EPIPEDOU EPIKOINVNIAS ME TON SENSOR
		driverComm = new SensorCommunication(buffer, interfaceData, outputData);
		try {
			driverComm.connect();
		} catch (Exception ex) {
			System.out.println("SENSOR CONNECTION ERROR");
		}

		/**
		 * **********************************************************8
		 * ETOIMASIA TOU JAXBContent GIA NA PARAGEI XML DEDOMENA SAN EPISTROFH
		 * STO SES. ***********************************************************
		 */
		try {
			jaxbContext = JAXBContext.newInstance("net.opengis.om.v_1_0_0:net.opengis.sensorml.v_1_0_1");
		} catch (JAXBException ex) {
			System.out.println("ERROR ON JAXBContext CREATION. PLEASE CHECK ogc LIBRARIES");
		}

	}

	@Override
	public void destroyDriver() {
		//DESTROY SensorCommunication
		driverComm.disconnect();
		driverComm = null;

		//DESTROY JAXB OBJECT
		jaxbContext = null;

		//DESTROY BUFFER
		buffer.destroyBuffer();
		buffer = null;

		//DESTROY ARRAYLISTs
		identificationData.clear();
		identificationData = null;
		interfaceData.clear();
		interfaceData = null;
		outputData.clear();
		outputData = null;
		resultFormat.clear();
		resultFormat = null;
		nodeIdList.clear();
		nodeIdList = null;
	}

	@Override
	//EPISTREFEI TO UNIQUEID MESA APO TA IDENTIFIATIONS TOY SENSORML
	public String getNetworkId() {

		for (int i = 0; i < identificationData.size(); i++) {
			if (identificationData.get(i).name.equals("uniqueID")) {
				return identificationData.get(i).value;
			}
		}
		return "";
	}

	@Override
	//EPISTREFEI TO ID MESA APO TO SENSORML
	public String getNetworkType() {
		return id;
	}

	@Override
	public ArrayList<String> getNodesList() {
		return nodeIdList;
	}

	@Override
	public double getSamplingRate(int node) {
		if (node == -1) {
		}
		return measuringInterval;
	}

	@Override
	public ArrayList<ValueFormat> getLastReading(int node) {
		//System.out.println("MESA"+buffer.getData());
		return buffer.getData();
	}

	@Override
	public String getLastXMLReading(int node) {
		String returnXML = "";

		try {
			ArrayList<ValueFormat> arrayData = buffer.getData();

			if (arrayData == null || arrayData.size() < outputData.size() + 1) {
				return "";
			}
			ObservationType data = packXMLData(arrayData);

			JAXBElement<ObservationType> bookingElement = (new ObjectFactory()).createObservation(data);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			//jaxbMarshaller.marshal(bookingElement, file);
			//jaxbMarshaller.marshal(bookingElement, System.out);

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(bookingElement, sw);
			returnXML = sw.toString();
			//System.out.println(st);

		} catch (JAXBException ex) {
			Logger.getLogger(Libelium_acc.class.getName()).log(Level.SEVERE, null, ex);
		}

		return returnXML;

	}

	private ObservationType packXMLData(ArrayList<ValueFormat> arrayData) {

		//DHMIOURGIA TOU OBSERVATION XML.
		ObservationType l = new ObservationType();

		//PERASMA TOU OBSERVATION_XML_ID
		l.setId(id + "_" + dataOutputId);
		dataOutputId++;

		//ANATHESH TOU OBSERVATION XML DESCRIPTION
		StringOrRefType s = new StringOrRefType();
		for (int i = 0; i < identificationData.size(); i++) {
			if (identificationData.get(i).name.equals("shortName")) {
				String shortName = identificationData.get(i).value;
				s.setValue(shortName + " Data");
			}
		}
		l.setDescription(s);

		TimeObjectPropertyType t = new TimeObjectPropertyType();

		///////////////////////////////om:samplingTime
		net.opengis.gml.v_3_1_1.ObjectFactory gmlOF = new net.opengis.gml.v_3_1_1.ObjectFactory(); //begin position
		TimePositionType timePosition = new TimePositionType();
		timePosition.getValue().add(getTime(arrayData));
		TimeInstantType timeInstant = new TimeInstantType();
		timeInstant.setTimePosition(timePosition);
		TimeObjectPropertyType timeObjectPropertyType = new TimeObjectPropertyType();
		timeObjectPropertyType.setTimeObject(gmlOF.createTimeInstant(timeInstant));

		l.setSamplingTime(timeObjectPropertyType);

		//om:Procedure
		ProcessPropertyType processPropertyType = new ProcessPropertyType();
		processPropertyType.setHref(id);
		l.setProcedure(processPropertyType);

		//om:Result
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			System.out.println("CREATE O&M RESULTS ERROR");
		}
		org.w3c.dom.Document doc = builder.newDocument();
		net.opengis.swe.v_1_0_1.ObjectFactory sweOF = new net.opengis.swe.v_1_0_1.ObjectFactory();
		DataRecordType dataRecord = sweOF.createDataRecordType();

		List<DataComponentPropertyType> dataList = new ArrayList<DataComponentPropertyType>();

		String dataName, dataType, dataUom, dataValue;
		for (int i = 0; i < resultFormat.size(); i++) {
			dataName = resultFormat.get(i).name;
			dataType = resultFormat.get(i).type;
			dataUom = resultFormat.get(i).code;
			dataValue = getDataValue(arrayData, resultFormat.get(i).id);

			DataComponentPropertyType data = new DataComponentPropertyType();
			data.setName(dataName);

			if (dataType.equals("quantity")) {
				Quantity q = new Quantity();
				q.setValue(Double.parseDouble(dataValue));
				UomPropertyType uom = new UomPropertyType();
				uom.setHref(dataUom);
				q.setUom(uom);
				data.setQuantity(q);
			} else if (dataType.equals("text")) {
				Text tVal = new Text();
				tVal.setValue(dataValue);
			} else if (dataType.equals("boolean")) {
				net.opengis.swe.v_1_0_1.Boolean boolVal = new net.opengis.swe.v_1_0_1.Boolean();
				if (dataValue.equals("true")) {
					boolVal.setValue(true);
				} else {
					boolVal.setValue(false);
				}
			}

			dataList.add(data);
		}
		dataRecord.setField(dataList);

		l.setResult(dataRecord);

		return l;

	}

	//PSAXNEI MESA STON PINAKA ME TA DATA
	//BRISKEI THN EGGRAFH ME THN WRA KAI THN EPISTREFEI.
	private String getTime(ArrayList<ValueFormat> arrayData) {
		String time = "";
		for (int i = 0; i < arrayData.size(); i++) {
			if (arrayData.get(i).name.equals("timestamp")) {
				time = arrayData.get(i).value;
			}
		}
		return time;
	}

	private String getDataValue(ArrayList<ValueFormat> arrayData, String id) {
		String value = "";
		for (int i = 0; i < arrayData.size(); i++) {
			if (arrayData.get(i).name.equals(id)) {
				value = arrayData.get(i).value;
			}
		}
		return value;
	}

	/*
	 * THETEI THN KATASTASH LEITOURGIAS TOU DIKTYOU GIA AN XEKINHSEI H
	 * DEIGMATOLHPSIA THA PREPEI NA KLEITHEI ME status=true. GIA NA STAMATHSEI H
	 * DEIGMATOLHPSIA THA PREPEI NA KLEITHEI ME status=false.
	 */
	@Override
	public void setNetworkStatus(boolean status, int time) {

		//ENERGOPOIHSH THW EPIKOINVNIAS DRIVER-DIKTYOY
		if (status) {
			driverComm.startParser();
		} else { //AP-ENERGOPOIHSH THW EPIKOINVNIAS DRIVER-DIKTYOY
			driverComm.stopParser();
		}
	}
}
