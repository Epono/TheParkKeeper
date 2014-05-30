package environnement;

import java.io.Serializable;

/** Represents a box of leaves above water in the <b>Grid</b>.</br>
 * Is opaque and an obstacle to movement.
 */
public class LeafWater implements Obstacle, Serializable {
	private static final long serialVersionUID = -7228280799277068634L;
	private static LeafWater leafWater;

	/** Constructs a new <b>LeafWater</b>.
	 * Private to allow only one object <b>LeafWater</b> to be created.
	 */
	private LeafWater() {}

	/** Returns the only instance of <b>LeafWater</b>.
	 * 
	 * @return the only instance of the object <b>LeafWater</b>.
	 */
	public static LeafWater getInstance() {
		if (leafWater == null)
			leafWater = new LeafWater();
		return leafWater;
	}

	public ElementType getElementType() {
		return ElementType.LEAFWATER;
	}

	public boolean isObstacle() {
		return true;
	}

	public boolean isOpaque() {
		return true;
	}

	public String toString() {
		return "LeafWater";
	}
}
