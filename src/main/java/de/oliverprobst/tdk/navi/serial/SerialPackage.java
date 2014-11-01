package de.oliverprobst.tdk.navi.serial;

/**
 * This object represents a package send via the serial connection.
 * 
 * Following specification: <br/>
 * <ul>
 * <li>A package is terminated by a * or 0x2a</li>
 * <li>Max payload size is 120 bytes</li>
 * <li>First three byte are a header and always the bytes 0x54 0x44 0x4b
 * <li>The fourth byte defines the message type. Examples:</li>
 * <ul>
 * *
 * <li>0x61 - NMEA String without $, * and checksum</li>
 * <li>0x62 - Depth: 6 bytes in Millimeter, 5 bytes in mbar</li>
 * <li>0x63 - Temperature in Celcius, 1 byte sign, 2byte digits before and 1
 * byte digit behind comma. Eg. TDKc+048* is + 4.8 Degree Celcius.</li>
 * <li>0x65 - Humidity: 2byte digits Eg. TDKe55* is 55 % Humidity.</li>
 * </li></li>
 * </ul>
 * <li>Followed by a checksum byte similar calculated to the NMEA checksum.
 * Calculation does include the header, but not the termination byte. </ul>
 * 
 * Example:<br/>
 * <code>
 * <table border="1">
 * <tr>
 * <td> 0x54 0x44 0x4b </td><td>0x62 </td><td>0x31 0x32 0x33 0x34 0x35 0x36  </td> <td>0x31 0x32 0x36 0x37 0x38   </td> <td> 0x2a   </td><td> 0x34  </td>  
 * </tr>
 * <tr>
 * <td> header</td><td>typ</td><td colspan = "2"> payload</td>  <td>End</td><td>ChkSum</td>
 *  </tr> 
 * <tr>
 * <td> TDK</td><td>b</td><td>123456</td><td>12678</td><td>*</td><td>OK</td>
 *  </tr>           
 * </table>
 * </code> is a Depth Message showing 123.456m with 12.678 mBar ambience
 * pressure
 * 
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class SerialPackage {

	// private static Logger log = LoggerFactory.getLogger(SerialPackage.class);

	private final byte[] messageBytes;

	private final String msg;

	private int terminatorIndex = 0;
	
	public SerialPackage(String message) {

		this.messageBytes = message.getBytes();
		terminatorIndex = message.indexOf('*');
		
		if (isValid()) {
			this.msg = message.substring(2, message.indexOf('*'));
		} else {
			this.msg = null;
		}
	}

	public int getCalculatedCheckSum() {
		if (messageBytes.length < 3) {
			return -1;
		}
		int pos = 1;
		int checksum_A = 0;
		int checksum_B = 0;

		do {

			checksum_A = (checksum_A + messageBytes[pos]) % 256;
			checksum_B = (checksum_B + checksum_A) % 256;

		} while (pos++ < terminatorIndex);

		return checksum_A * 10000; //+ checksum_B;
	}

	public String getPayload() {
		return msg;
	}

	public int getReceivedChecksum() throws IllegalArgumentException {
		if (messageBytes.length < 3) {
			return -2;
		}
		byte checksum_A = messageBytes[terminatorIndex + 1];
		byte checksum_B = messageBytes[terminatorIndex + 2];
		return ((int) checksum_A) * 10000;// + ((int) (256 + checksum_B)% 256);

	}

	public MessageType getType() {
		return MessageType.getMessageType(messageBytes[1]);
	}

	public boolean isValid() {
		return true;
		//return getCalculatedCheckSum() == getReceivedChecksum();
	}

	@Override
	public String toString() {
		return "SerialPackage [msg=" + msg + "]";
	}

	/*
	 * void calcChecksum(byte *checksumPayload, byte payloadSize) { byte CK_A =
	 * 0, CK_B = 0; for (int i = 0; i < payloadSize; i++) { CK_A = CK_A +
	 * *checksumPayload; CK_B = CK_B + CK_A; checksumPayload++; }checksumPayload
	 * = CK_A; checksumPayload++;checksumPayload = CK_B; }
	 */
}
