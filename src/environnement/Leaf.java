package environnement;

import java.io.Serializable;

/** Represents a box of leaves in the <b>Grid</b>.</br>
 * Is opaque, but not an obstacle to movement.
 */
public class Leaf implements Obstacle, Serializable {
	private static final long serialVersionUID = -7450468339233368170L;
	private static Leaf leaf;

	/** Constructs a new <b>Leaf</b>.
	 * Private to allow only one object <b>Leaf</b> to be created.
	 */
	private Leaf() {}

	/** Returns the only instance of <b>Leaf</b>.
	 * 
	 * @return the only instance of the object <b>Leaf</b>.
	 */
	public static Leaf getInstance() {
		if (leaf == null)
			leaf = new Leaf();
		return leaf;
	}
	
	public ElementType getElementType() {
		return ElementType.LEAF;
	}

	public boolean isObstacle() {
		return false;
	}

	public boolean isOpaque() {
		return true;
	}

	public String toString() {
		return "Leaf";
	}
}
