package management;

import individual.Guardian;
import individual.Intrude;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import environnement.Coordinate;
import environnement.Grid;

/** Represents a save of the game.
 */
public class Map implements Serializable {
	private static final long serialVersionUID = -2052146147510254656L;
	private Grid grid;
	private ArrayList<Guardian> guardians;
	private ArrayList<Intrude> intrudes;
	private ArrayList<Coordinate> patrolRoute;
	private String name;
	private Date creationDate;
	private int numberOfTurns;
	private long duration;

	/** Constructs a <b>Map</b> with given informations.
	 * 
	 * @param grid : current game <b>Grid</b>
	 * @param guardians : an list containing all the <b>Guardians</b>
	 * @param intrudes : an list containing all the <b>Intrudes</b>
	 * @param name : the name of the game
	 * @param creationDate : the creation date of the game
	 * @param numberOfTurns : the number of turns of the game
	 * @param duration : the game duration
	 * @param patrolRoute : a list containing the <b>Coordinate</b> of all the patrol's routes of the <b>Guardians</b> (debug)
	 */
	public Map(Grid grid, ArrayList<Guardian> guardians, ArrayList<Intrude> intrudes, String name, 
			Date creationDate, int numberOfTurns, long duration, ArrayList<Coordinate> patrolRoute) {
		this.grid = grid;
		this.guardians = guardians;
		this.intrudes = intrudes;
		this.name = name;
		this.creationDate = creationDate;
		this.numberOfTurns = numberOfTurns;
		this.duration = duration;
		this.patrolRoute = patrolRoute;
	}

	/** Returns the <b>Grid</b> of the <b>Map</b>.
	 * 
	 * @return the <b>Grid</b> of the <b>Map</b>.
	 */
	public Grid getGrid() {
		return grid;
	}

	/** Returns the list of <b>Guardians</b> of the <b>Map</b>.
	 * 
	 * @return the list of <b>Guardians</b> of the <b>Map</b>.
	 */
	public ArrayList<Guardian> getGuardians() {
		return guardians;
	}

	/** Returns the list of <b>Intrudes</b> of the <b>Map</b>.
	 * 
	 * @return the list of <b>Intrudes</b> of the <b>Map</b>.
	 */
	public ArrayList<Intrude> getIntrudes() {
		return intrudes;
	}

	/** Returns the list <b>Coordinates</b> forming all the patrols of <b>Guardians</b> of the <b>Map</b>.
	 * 
	 * @return the list <b>Coordinates</b> forming all the patrols of <b>Guardians</b> of the <b>Map</b>.
	 */
	public ArrayList<Coordinate> getpatrolRoute() {
		return patrolRoute;
	}

	/** Returns the name of the <b>Map</b>.
	 * 
	 * @return the name of the <b>Map</b>.
	 */
	public String getName() {
		return name;
	}

	/** Returns the creation date of the <b>Map</b>.
	 * 
	 * @return Returns the creation date of the <b>Map</b>.
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/** Returns the number of turns of the <b>Map</b>.
	 * 
	 * @return the number of turns of the <b>Map</b>.
	 */
	public int getNumberOfTurns() {
		return numberOfTurns;
	}

	/** Returns the duration of the <b>Map</b>.
	 * 
	 * @return the duration of the <b>Map</b>.
	 */
	public long getDuration() {
		return duration;
	}

	public String toString() {
		return name + "\n" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(creationDate);
	}
}
