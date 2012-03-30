package ses.driver;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerialReaderThread extends Thread {

	private static Logger logger = Logger.getLogger(SerialReaderThread.class.toString());

	InputStream in;

	PacketManipulation parser;

	SerialReaderThread(InputStream in, PacketManipulation parser) {
		this.in = in;
		this.parser = parser;
	}

	public void run() {
		int c;
		String packet = "";
		try {
			while (true) {
				while ((c = this.in.read()) > -1) {
					packet += (char) c;
					if (c == 10) {
						parser.parse(packet);
						packet = "";
					}
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

}
