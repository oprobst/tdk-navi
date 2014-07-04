package de.oliverprobst.tdk.navi.i2c;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object represents a package send via the I2C. It shall be very simple
 * and not optimized for performance.
 * 
 * Following specification: <br/>
 * <ul>
 * <li>A package is terminated by a * or 0x2a</li>
 * <li>Max payload size is 120 bytes</li>
 * <li>First three byte are a header and always the bytes 0x54 0x44 0x4b
 * <li>The fourth byte defines the message type</li>
 * <ul>
 * <li>0x61 - NMEA String without $, * and checksum</li>
 * <li>0x62 - Depth: 6 bytes in Millimeter, 5 bytes in mbar</li>
 * </li></li>
 * </ul>
 * <li>Followed by a checksum byte similar calculated to the NMEA checksum.
 * Calculation does include the header, but not the termination byte. </ul>
 * 
 * Example:<br/>
 * <code>
 * <table border="1">
 * <tr>
 * <td> 0x54 0x44 0x4b </td><td>0x62 </td><td>0x31 0x32 0x33 0x34 0x35 0x36  </td> <td>0x31 0x32 0x36 0x37 0x38   </td> <td> 2a   </td><td> 34  </td>  
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
public class I2CPackage {

	private static Logger log = LoggerFactory.getLogger(I2CPackage.class);

	private final byte[] message;

	public I2CPackage(byte b[]) {
		if (b.length < 6) {
			throw new IllegalArgumentException("Message size < 6 byte (= "
					+ b.length + ")");
		} else if (b.length > 128) {
			throw new IllegalArgumentException(
					"Could not read received message, payload bigger than 120 bytes (="
							+ b.length + " bytes).");
		} else if (b[b.length - 2] != 0x2a) {
			throw new IllegalArgumentException(
					"Received byte did not contain any termination byte 0x2a.");
		}

		this.message = b;
	}

	public byte getReceivedChecksum() throws IllegalArgumentException {

		return message[message.length - 1];

	}

	public boolean isValid() {
		return getCalculatedCheckSum() == getReceivedChecksum();
	}

	public MessageType getType() {
		return MessageType.getMessageType(message[3]);
	}

	public String getPayload() {

		if (!isValid()) {
			return "";
		}

		int i = 0;
		for (i = message.length -1; i > 0; i--) {
			if (message[i] == 0x2a) {
				break;
			}
		}
		String out;
		try {
			out = new String(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		out = out.substring(4, i);

		return out;
	}

	private String getSum(String in) {
		int checksum = 0;
		if (in.startsWith("$")) {
			in = in.substring(1, in.length());
		}

		int end = in.indexOf('*');
		if (end == -1)
			end = in.length();
		for (int i = 0; i < end; i++) {
			checksum = checksum ^ in.charAt(i);
		}
		String hex = Integer.toHexString(checksum);
		if (hex.length() == 1)
			hex = "0" + hex;
		return hex.toUpperCase();
	}

	public byte getCalculatedCheckSum() {

		int pos = 0;
		int checksum = 0;

		boolean foundTermination = false;
		do {
			if (message[pos] == 0x2a) {

				foundTermination = true;
				break;

			}
			checksum = checksum ^ message[pos];

		} while (pos++ < message.length);

		if (!foundTermination) {
			throw new IllegalArgumentException(
					"Payload is not terminated by a 0x42 byte.");
		}
		return (byte) checksum;
	}
}
