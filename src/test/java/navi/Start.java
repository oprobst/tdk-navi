package navi;

import java.io.UnsupportedEncodingException;

import de.oliverprobst.tdk.navi.serial.SerialPackage;

public class Start {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			byte in[] = new byte[] { -80, 81, 82 - 12 };
			int checksum_A = 0;
			int checksum_B = 0;


			

			int nextByte = in[0] & 0xff;
			System.out.println("nextbyte " + nextByte);

			 nextByte = in[0] & 0xff;
			System.out.println("nextbyte " + nextByte);
			
			checksum_A = (checksum_A + nextByte) % 256;
			System.out.println("checksum_A " + checksum_A);
			
			checksum_B = (checksum_A + checksum_B) % 256;
			System.out.println("checksum_A " + checksum_A);
			
			String chksum = new String(new byte[] { (byte) checksum_A,
					(byte) checksum_B }, "UTF-8");
			System.out.println(chksum);

			System.out.println((int) (chksum.getBytes()[0] & (0xff)));
			System.out.println((int) (chksum.getBytes()[1] & (0xff)));

			final String[] samples = new String[] {
					"$b229,-18,-14,26.*k!",
					"$aGPVTG,,,,,,,,,N*Ã­w,$b229,-18,-13,26.*j",
					"$aGPGGA,80850.00,,,,,0,00,99.99,,,,,,*Â¿4",
					"$aGPGSA,A,1,,,,,,,,,,,,,99.99,99.99,99.99*ÂÂ,",
					"$b227,-17,-11,20.*_Ã",
					"$c0.00,0.00,2.00*!:",
					"$d0.00*L<",
					"$b284,0.9,-32,27.*nN",
					"$b281,0.3,-31,27.*dÃ",
					"$b282,0.1,-31,27.*cÂ¼",
					"$aGPRMC,162346.00,A,4846.13961,N,00819.92788,E,0.108,,011114,,,D*Yb",
					"$b282,0.0,-31,27.*bÂ¯" };

			for (String msg : samples) {
				SerialPackage sp = new SerialPackage(msg);

				System.out.println("'"+msg + "' --- "
						+ sp.isValid());

				System.out.println("R:" + sp.getReceivedChecksum());
				System.out.println("C:" + sp.getCalculatedCheckSum());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
