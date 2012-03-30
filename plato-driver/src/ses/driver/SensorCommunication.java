package ses.driver;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.util.ArrayList;

public class SensorCommunication {

	String port;

	int baudrate;

	int num_bits;

	boolean parity;

	CommPort commPort;

	SerialReader reader;

	PacketManipulation parser;

	SensorCommunication(DriverDataBuffer buffer, ArrayList<ValueFormat> interfaces, ArrayList<ValueFormat> outputsFormat) {

		for (int i = 0; i < interfaces.size(); i++) {
			if (interfaces.get(i).name.equals("port")) {
				port = interfaces.get(i).value;
			} else if (interfaces.get(i).name.equals("baudrate")) {
				baudrate = (int) Double.parseDouble(interfaces.get(i).value);
			} else if (interfaces.get(i).name.equals("num-bits")) {
				num_bits = (int) Double.parseDouble(interfaces.get(i).value);
			} else if (interfaces.get(i).name.equals("parity")) {
				parity = Boolean.parseBoolean(interfaces.get(i).value);
			}
		}

		parser = new PacketManipulation(buffer, outputsFormat);
	}

	void connect() throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudrate, /*
															* SerialPort.DATABITS_8
															*/num_bits, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				System.out.println("TEST");
				reader = new SerialReader(serialPort.getInputStream(), parser);
				reader.start();
			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	//SYNARTHSH GIA THN ENERGOPOIHSH TOY PARSER GIA TO DIABASMA TWN DEDOMENWN
	void startParser() {
		// Resume the thread
		synchronized (reader) {
			reader.waitFlag = false;
			reader.notify();
		}
	}

	//SYNARTHSH GIA TO STAMATHMA TOU PARSER. 
	//TO NHMA DEN KATASTREFETAI, APLA KOLAEI KAI PERIMENEI NA ALLAXEI H TIMH THS METABLHTHS WSTE NA XANAE3NERGOPOIHTEI.
	void stopParser() {
		// Pause the thread
		synchronized (reader) {
			reader.waitFlag = true;
		}
	}

	void disconnect() {
		parser.destroyPacketManipulation();
		parser = null;

		reader.destroyThread();
		reader = null;

		commPort.close();
		commPort = null;
	}
}
