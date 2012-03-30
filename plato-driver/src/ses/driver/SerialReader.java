package ses.driver;

import java.io.IOException;
import java.io.InputStream;

public class SerialReader extends Thread {

	InputStream in;

	PacketManipulation parser;

	//EPEIDH OI SYNARTHSEIS STOP & SUSPEND GIA TON XEIRISMO TVN THREAD EINAI UNSAFE XRHSIMOPOIOYME THN PARAKATW METABLHTH
	boolean waitFlag = true;

	//METABLHTH GIA TON TERMATISMO TOU THREAD
	boolean destroyFlag = false;

	SerialReader(InputStream in, PacketManipulation parser) {
		this.in = in;
		this.parser = parser;
	}

	public void run() {
		byte[] buffer = new byte[1024];
		int len = -1;
		int c;
		String packet = "";
		//int value;

		int valueX = 0, valueY = 0, valueZ = 0;

		try {
			while (!destroyFlag) {
				// ELEGXO GIA TO AN THA PREPEI NA STAMATISEI TO NHMA 'H OXI TO DIABASMA TWN DEODMENWN APO TO DIKTYO
				synchronized (this) {
					while (waitFlag) {
						try {
							wait();
						} catch (Exception e) {
						}
					}
				}

				while ((c = this.in.read()) > -1) {
					// System.out.println(new String(buffer,0,len));
					//System.out.print((char)c);
					packet += (char) c;
					if (c == 10) {

						parser.parse(packet);
						//   System.out.println(packet);

						// String packet = new String(buffer,0,len);
						//   System.out.println(packet);

						packet = "";
						// System.out.println("==========================================================");
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void destroyThread() {

		//AN EINAI STAMATHMENO TO THREAD, TOTE TO XEKOLAME WSTE NA MPORESOUME META NA TO KATASTREPSOUME
		if (waitFlag) {
			synchronized (this) {
				this.waitFlag = true;
			}
		}

		destroyFlag = true;
	}
}
