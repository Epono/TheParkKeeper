package environnement;

import java.io.Serializable;

/** Parent of everything placed on the <b>Grid</b>. 
 */
public interface Element extends Serializable {
	/** Returns the <b>ElementType</b> of the <b>Element</b>.
	 * 
	 * @return the <b>ElementType</b> of the <b>Element</b>.
	 */
	public ElementType getElementType();
}
