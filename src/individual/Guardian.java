package individual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import environnement.Coordinate;
import environnement.ElementType;

/** Subclass of <b>Individual</b>.</br>
 * Represents the "good guy" who has to catch <b>Intrudes</b>.</br>
 * He can be controlled by the user or by the computer, which moves him automatically.</br>
 */
public class Guardian extends Individual implements Serializable {
	private static final long serialVersionUID = 4245537814753323714L;
	private boolean controled;
	private int patrolPosition;
	private ArrayList<Coordinate> destinations;
	private ArrayList<Coordinate> reachableCoordinates;
	private TreeMap<Integer, Coordinate> patrol;
	
	/** Constructs a <b>Guardian</b> with given arguments.
	 * 
	 * @param ID : the ID of the <b>Guardian</b>.
	 * @param position : current position (<b>Coordinate</b>) of the <b>Guardian</b>.
	 * @param isControlled : indicates whether the <b>Guardian</b> is controlled by the user (<code>true</code>) or by the computer (<code>false</code>).
	 */
	public Guardian(int ID, Coordinate position, boolean isControlled) {
		super(ID, position);
		this.controled = isControlled;
		destinations = new ArrayList<Coordinate>();
		patrol = new TreeMap<Integer, Coordinate>();
		reachableCoordinates = new ArrayList<Coordinate>();
		patrolPosition = -1;
	}
	
	/** Increments the patrol position.
	 */
	public void incrPatrolPosition() {
		if(patrolPosition < patrol.size() - 1)
			patrolPosition++;
		else
			patrolPosition = 0;
	}
	
	public String toString() {
		String strControled;
		if (controled)
			strControled = "user";
		else
			strControled = "computer";

		return "Guardian -- Position : " + getPosition() + " -- ID : " + getID()	+ " -- Controled by : " + strControled 
				+ " -- Patrol position : " + patrolPosition + "\n";
	}

	public ElementType getElementType() {
		return ElementType.GUARDIAN;
	}

	/** Returns <code>true</code> if the <b>Guardian</b> is controlled by the user, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the <b>Guardian</b> is controlled by the user, <code>false</code> otherwise.
	 */
	public boolean isControled() {
		return controled;
	}

	/** Sets the control of the <b>Guardian</b>.
	 * 
	 * @param isControled : <code>true</code> to give the control of the <b>Guardian</b> to the user, <code>false</code> to give the control to the computer.
	 */
	public void setControled(boolean isControled) {
		this.controled = isControled;
	}

	/** Returns the <b>Guardian</b>'s destinations.
	 * 
	 * @return the <b>Guardian</b>'s destinations.
	 */
	public ArrayList<Coordinate> getDestinations() {
		return destinations;
	}

	/** Add a destination (<b>Intrude</b> position) to the the list of destinations of the <b>Guardian</b>.
	 * 
	 * @param destination : the destination to add to the list.
	 */
	public void addDestination(Coordinate destination) {
		destinations.add(destination);
	}

	/** Remove a destination of the the list of destinations of the <b>Guardian</b>.
	 * 
	 * @param destination : the destination to remove from the list.
	 */
	public void removeDestinations(Coordinate destination) {
		destinations.remove(destination);
	}

	/** Returns the <b>Guardian</b>'s patrol.
	 * 
	 * @return the <b>Guardian</b>'s patrol.
	 */
	public TreeMap<Integer, Coordinate> getPatrol() {
		return patrol;
	}

	/** Sets the <b>Guardian</b>'s patrol.
	 * 
	 * @param patrol : the new patrol of the <b>Guardian</b>.
	 */
	public void setPatrol(TreeMap<Integer, Coordinate> patrol) {
		this.patrol = patrol;
	}

	/** Returns the position on the patrol of the <b>Guardian</b>.
	 * 
	 * @return the position on the patrol of the <b>Guardian</b>.
	 */
	public int getPatrolPosition() {
		return patrolPosition;
	}

	/** Sets the position on the patrol of the <b>Guardian</b>.
	 * 
	 * @param patrolPosition : the new position on the patrol.
	 */
	public void setPatrolPosition(int patrolPosition) {
		this.patrolPosition = patrolPosition;
	}

	/** Returns the <b>Coordinates</b> reachable by the <b>Guardian</b>.
	 * 
	 * @return a list of the <b>Coordinates</b> reachable by the <b>Guardian</b>.
	 */
	public ArrayList<Coordinate> getReachableCoordinates() {
		return reachableCoordinates;
	}

	/** Sets the <b>Coordinates</b> reachable by the <b>Guardian</b>.
	 * 
	 * @param reachableCoordinates : the list of <b>Coordinates</b> reachable by the <b>Guardian</b>.
	 */
	public void setReachableCoordinates(ArrayList<Coordinate> reachableCoordinates) {
		this.reachableCoordinates = reachableCoordinates;
	}
}