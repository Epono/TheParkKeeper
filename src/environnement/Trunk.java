package environnement;

import java.io.Serializable;

/** Represents a trunk of a tree in the <b>Grid</b>.</br>
 * Is opaque and an obstacle to movement.
 */
public class Trunk implements Obstacle, Serializable {
	private static final long serialVersionUID = 707509851534458166L;
	private static Trunk trunk;

	/** Constructs a new <b>Trunk</b>.
	 * Private to allow only one object <b>Trunk</b> to be created.
	 */
	private Trunk() {}

	/** Returns the only instance of <b>Trunk</b>.
	 * 
	 * @return the only instance of the object <b>Trunk</b>.
	 */
	public static Trunk getInstance() {
		if (trunk == null)
			trunk = new Trunk();
		return trunk;
	}
	
	public ElementType getElementType() {
		return ElementType.TRUNK;
	}

	public boolean isObstacle() {
		return true;
	}

	public boolean isOpaque() {
		return true;
	}

	public String toString() {
		return "Trunk";
	}
}
