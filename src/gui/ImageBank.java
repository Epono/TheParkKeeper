package gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/** Class containing JLabels with images of the icons.
 */
public class ImageBank {

	/** Constructs the ImageBank.
	 */
	public ImageBank() {}

	/** Returns a <b>JLabel</b> containing the image of the <b>Grass</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel grassLabel() {
		JLabel grassLabel = new JLabel(new ImageIcon(getClass().getResource("/images/grass.png")));
		grassLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return grassLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the dark <b>Grass</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel darkGrassLabel() {
		JLabel darkGrassLabel = new JLabel(new ImageIcon(getClass().getResource("/images/darkgrass.png")));
		darkGrassLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return darkGrassLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>Water</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel waterLabel() {
		JLabel waterLabel = new JLabel(new ImageIcon(getClass().getResource("/images/water.png")));
		waterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return waterLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the dark <b>Water</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel darkWaterLabel() {
		JLabel darkWaterLabel = new JLabel(new ImageIcon(getClass().getResource("/images/darkwater.png")));
		darkWaterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return darkWaterLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>Wall</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel wallLabel() {
		JLabel wallLabel = new JLabel(new ImageIcon(getClass().getResource("/images/wall.png")));
		wallLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return wallLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the dark <b>Wall</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel darkWallLabel() {
		JLabel darkWallLabel = new JLabel(new ImageIcon(getClass().getResource("/images/darkwall.png")));
		darkWallLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return darkWallLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>Trunk</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel trunkLabel() {
		JLabel trunkLabel = new JLabel(new ImageIcon(getClass().getResource("/images/trunk.png")));
		trunkLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return trunkLabel;
	}

	/** JReturns a <b>JLabel</b> containing the image of the dark <b>Trunk</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel darkTrunkLabel() {
		JLabel darkTrunkLabel = new JLabel(new ImageIcon(getClass().getResource("/images/darktrunk.png")));
		darkTrunkLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return darkTrunkLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>Leaf</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel leafLabel() {
		JLabel leafLabel = new JLabel(new ImageIcon(getClass().getResource("/images/leaf.png")));
		leafLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return leafLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the dark <b>Leaf</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel darkLeafLabel() {
		JLabel darkLeafLabel = new JLabel(new ImageIcon(getClass().getResource("/images/darkleaf.png")));
		darkLeafLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return darkLeafLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>LeafWater</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel leafWaterLabel() {
		JLabel leafWaterLabel = new JLabel(new ImageIcon(getClass().getResource("/images/leafWater.png")));
		leafWaterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return leafWaterLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the dark <b>LeafWater</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel darkLeafWaterLabel() {
		JLabel darkLeafWaterLabel = new JLabel(new ImageIcon(getClass().getResource("/images/darkleafWater.png")));
		darkLeafWaterLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return darkLeafWaterLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>Guardian</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel guardianLabel() {
		JLabel guardianLabel = new JLabel(new ImageIcon(getClass().getResource("/images/guardian.png")));
		guardianLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return guardianLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>MovingGuardian</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel guardianMovingLabel() {
		JLabel guardianMovingLabel = new JLabel(new ImageIcon(getClass().getResource("/images/guardianMoving.png")));
		guardianMovingLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return guardianMovingLabel;
	}

	/** Returns a <b>JLabel</b> containing the image of the <b>Intrude</b>.
	 * 
	 * @return a <b>JLabel</b> containing the image.
	 */
	public JLabel intrudeLabel() {
		JLabel intrudeLabel = new JLabel(new ImageIcon(getClass().getResource("/images/intrude.png")));
		intrudeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return intrudeLabel;
	}
}
