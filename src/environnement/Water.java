package environnement;

import java.io.Serializable;

/** Represents a box of water in the <b>Grid</b>.</br>
 * Is not opaque, but is an obstacle to movement.
 */
public class Water implements Obstacle, Serializable {
	private static final long serialVersionUID = -1367668202823726648L;
	private static Water water;

	/** Constructs a new <b>Water</b>.
	 * Private to allow only one object <b>Water</b> to be created.
	 */
	private Water() {}

	/** Returns the only instance of <b>Water</b>.
	 * 
	 * @return the only instance of the object <b>Water</b>.
	 */
	public static Water getInstance() {
		if (water == null)
			water = new Water();
		return water;
	}

	public ElementType getElementType() {
		return ElementType.WATER;
	}

	public boolean isObstacle() {
		return true;
	}

	public boolean isOpaque() {
		return false;
	}

	public String toString() {
		return "Water";
	}
}
