package navi;

import de.oliverprobst.tdk.navi.serial.SerialPackage;

public class Start {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String msg = "$b284,0.9,-32,27.*nN";
		String msg2 = "$b281,0.3,-31,27.*dÃ";
		String msg3 = "$b282,0.1,-31,27.*cÂ¼";
		String msg4 = "$aGPRMC,162346.00,A,4846.13961,N,00819.92788,E,0.108,,011114,,,D*Yb";
		String msg5 = "$b282,0.0,-31,27.*bÂ¯";

		final String[] samples = new String[] { "$b229,-18,-14,26.*k!",
				"$aGPVTG,,,,,,,,,N*Ã­w,$b229,-18,-13,26.*j",
				"$aGPGGA,80850.00,,,,,0,00,99.99,,,,,,*Â¿4",
				"$aGPGSA,A,1,,,,,,,,,,,,,99.99,99.99,99.99*ÂÂ,",
				"$b227,-17,-11,20.*_Ã", "$c0.00,0.00,2.00*!:", "$d0.00*L<" };

		SerialPackage sp = new SerialPackage(msg);

		System.out.println("R:" + sp.getReceivedChecksum());
		System.out.println("C:" + sp.getCalculatedCheckSum());
	}
}
