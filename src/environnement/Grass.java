package environnement;

import java.io.Serializable;

/** Represents a box of grass in the <b>Grid</b>.</br>
 * Is neither opaque nor an obstacle to movement.
 */
public class Grass implements Obstacle, Serializable {
	private static final long serialVersionUID = -5130083865372407499L;
	private static Grass grass;

	/** Constructs a new <b>Grass</b>.
	 * Private to allow only one object <b>Grass</b> to be created.
	 */
	private Grass() {}

	/** Returns the only instance of <b>Grass</b>.
	 * 
	 * @return the only instance of the object <b>Grass</b>.
	 */
	public static Grass getInstance() {
		if (grass == null)
			grass = new Grass();
		return grass;
	}

	public ElementType getElementType() {
		return ElementType.GRASS;
	}

	public boolean isObstacle() {
		return false;
	}

	public boolean isOpaque() {
		return false;
	}

	public String toString() {
		return "Grass";
	}
}
