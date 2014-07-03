package de.oliverprobst.tdk.navi.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
 
 
public class I2CExample {
 
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("I2C Sender, parmeter [Paket size] [Loop count]");
        int size = Integer.parseInt(args[0]);
        int loops = Integer.parseInt(args[1]);
        System.out.println("Starting, i2c size: "+size+", loops: "+loops);
       
        System.out.println("get bus 1");
        // get I2C bus instance
        final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
       
        System.out.println("get device with id 4");
        I2CDevice arduino = bus.getDevice(0x04);
        byte[] buffer = new byte[size];
        for (int i=0; i<buffer.length; i++) {
            buffer[i] = (byte)i;
        }
       
        for (int i=0; i<loops; i++) {
            System.out.println("send buffer now");
           
            long l = System.currentTimeMillis();
            //write(int address, byte[] buffer, int offset, int size) throws IOException        
            arduino.write(buffer, 0, buffer.length);
            long needed = System.currentTimeMillis() - l;
            //arduino.write((byte)65);
           
            System.out.println("done in "+needed+"ms");        
        }
    }
}