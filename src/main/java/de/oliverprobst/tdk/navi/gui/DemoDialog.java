package de.oliverprobst.tdk.navi.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class DemoDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1650240400924063147L;

	/**
	 * ctor
	 */
	public DemoDialog() {
		super();
		this.setPreferredSize(new Dimension(640, 500));
		this.setLocation(100, 600);
		this.setResizable(false);
		this.setVisible(true);
		this.setFocusable(false);
		createMainGridLayout();
		this.pack();
	}

	private void createMainGridLayout() {
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 2, 2);
		JLabel title = new JLabel();
		title.setText("Submarine Navigation System Demo");
		title.setFont(title.getFont().deriveFont(18.0f));
		this.add(title, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 2, 2);
		JLabel help = new JLabel();

		Font font = help.getFont();
		// same font but bold
		Font boldFont = new Font(font.getFontName(), Font.PLAIN, font.getSize());
		help.setFont(boldFont);
		help.setText(getHelpText());
		this.add(help, gbc);

	}

	private String getHelpText() {

		String helpText = "<html>"
				+ "<p><b>No sensor connected. Switched to test and demo mode. </b> </p>"
				+ "<table>"
				+ "<td><b>q</b> : </td><td>Quit </td><td></td><td> </td></tr>"
				+ "<td><b>w</b> : </td><td>Up </td><td> <b>s</b>: </td><td>Down </td></tr>"
				+ "<td><b>e</b> : </td><td>Left </td><td> <b>d:</b> </td><td>Right </td></tr>"
				+ "<td><b>r</b> : </td><td>check GPS antenna  </td><td></td><td></td></tr>"
				+ "<td><b>+</b> : </td><td>Speed + </td><td> <b>- :</b></td><td>Speed - </td></tr>"
				+ "<td><b>pgDown</b> : </td><td>pitch down </td><td> <b>pgUp</b>: </td><td>pitch up </td></tr>"
				+ "<td><b>home</b> : </td><td>pitch left </td><td> <b>end</b>: </td><td>pitch right </td></tr>"
				+ "<td><b>t</b> : </td><td>Stern leak </td><td><b>g</b>: </td><td>Bow leak</td></tr>"
				+ "<td><b>z</b> : </td><td>Hull pressure + </td><td>  <b>h</b>: </td><td>Hull pressure - </td></tr>"
				+ "<td><b>u</b> : </td><td>Voltage + </td><td>  <b>j</b>: </td><td>Voltage - </td></tr>"
				+ "<td><b>f</b> : </td><td>reset leak detection</td><td></td><td></td></tr></table></html>";
		
		return helpText;
	}
}
