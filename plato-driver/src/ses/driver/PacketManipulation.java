package ses.driver;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class PacketManipulation {

	ArrayList<ValueFormat> outputFormat;

	DriverDataBuffer buffer;

	PacketManipulation(DriverDataBuffer buffer, ArrayList<ValueFormat> outputs) {
		outputFormat = outputs;
		this.buffer = buffer;

	}

	void parse(String packet) {
		ArrayList<ValueFormat> outputValue = new ArrayList<ValueFormat>();

		StringTokenizer token = new StringTokenizer(packet, "#");

		//ADD DATA'S TIMESTAMP
		outputValue.add(new ValueFormat("timestamp", "", new Date() + ""));

		while (token.hasMoreTokens()) {
			String temp = token.nextToken();
			for (int i = 0; i < outputFormat.size(); i++) {
				if (temp.contains(outputFormat.get(i).value)) {

					outputValue.add(new ValueFormat(outputFormat.get(i).value, outputFormat.get(i).type, token.nextToken()));
				}
			}
			//            if (temp.contains("mac")) {
			//                //         System.out.println("MAC = " + token.nextToken());
			//            } else if (temp.contains("ACCX")) {
			//                valueX = Integer.parseInt(token.nextToken());
			//                //       System.out.println("ACCX = " + valueX);
			//            } else if (temp.contains("ACCY")) {
			//                valueY = Integer.parseInt(token.nextToken());
			//                //    System.out.println("ACCY = " + valueY);
			//            } else if (temp.contains("ACCZ")) {
			//                valueZ = Integer.parseInt(token.nextToken());
			//                //   System.out.println("ACCZ = " + valueZ);
			//            }
		}

		buffer.insertData(outputValue);

		//return outputValue;
	}

	void destroyPacketManipulation() {
		outputFormat.clear();
		outputFormat = null;
	}
}
