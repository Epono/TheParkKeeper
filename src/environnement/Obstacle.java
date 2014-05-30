package environnement;

import java.io.Serializable;

/** Interface implemented by all the not <b>INDIVIDUAL</b> <b>Elements</b>.
 */
public interface Obstacle extends Element, Serializable {
	
	/** Indicates whether the <b>Obstacle</b> is opaque or not.
	 * 
	 * @return <code>true</code> if the <b>Element</b> is opaque, <code>false</code> otherwise.
	 */
	public boolean isObstacle();
	
	/** Indicates whether the <b>Obstacle</b> is an obstacle to movement or not.
	 * 
	 * @return <code>true</code> if the <b>Element</b> is an obstacle to movement, <code>false</code> otherwise.
	 */
	public boolean isOpaque();
}
