package de.oliverprobst.tdk.navi.i2c;

import java.io.UnsupportedEncodingException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class I2CExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		byte b[] = { 84, 68, 75, 98, 49, 50, 51, 52, 53, 54, 48, 49, 50, 54,
				55, 56, 42 , 52};

		I2CPackage p = new I2CPackage(b);
		try {
			System.out.println(new String(b, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		System.out.println(p.getType());
		System.out.println(p.getPayload());
		System.out.println(p.isValid());
		System.out.println(p.getCalculatedCheckSum());
		System.out.println(p.getReceivedChecksum());

		if (true)
			return;

		System.out.println("I2C Sender, parmeter [Paket size] [Loop count]");
		int size = Integer.parseInt(args[0]);
		int loops = Integer.parseInt(args[1]);
		System.out.println("Starting, i2c size: " + size + ", loops: " + loops);

		System.out.println("get bus 1");
		// get I2C bus instance
		final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

		System.out.println("get device with id 4");
		I2CDevice arduino = bus.getDevice(0x04);
		byte[] buffer = new byte[size];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) i;
		}

		for (int i = 0; i < loops; i++) {
			System.out.println("send buffer now");

			long l = System.currentTimeMillis();
			// write(int address, byte[] buffer, int offset, int size) throws
			// IOException
			arduino.write(buffer, 0, buffer.length);
			long needed = System.currentTimeMillis() - l;
			// arduino.write((byte)65);

			System.out.println("done in " + needed + "ms");
		}
	}
}