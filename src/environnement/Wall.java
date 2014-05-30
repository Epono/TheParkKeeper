package environnement;

import java.io.Serializable;

/** Represents a box of wall in the <b>Grid</b>.</br>
 * Is opaque and an obstacle to movement.
 */
public class Wall implements Obstacle, Serializable {
	private static final long serialVersionUID = 6265738514885232283L;
	private static Wall wall;

	/** Constructs a new <b>Wall</b>.
	 * Private to allow only one object <b>Wall</b> to be created.
	 */
	private Wall() {}

	/** Returns the only instance of <b>Wall</b>.
	 * 
	 * @return the only instance of the object <b>Wall</b>.
	 */
	public static Wall getInstance() {
		if (wall == null)
			wall = new Wall();
		return wall;
	}

	public ElementType getElementType() {
		return ElementType.WALL;
	}

	public boolean isObstacle() {
		return true;
	}

	public boolean isOpaque() {
		return true;
	}

	public String toString() {
		return "Wall";
	}
}
