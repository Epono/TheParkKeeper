package individual;

import java.io.Serializable;
import java.util.ArrayList;

import environnement.Coordinate;
import environnement.Element;
import environnement.ElementType;

/** Abstract superclass of <b>Guardian</b> and <b>Intrude</b>.</br>
 * Represents an individual on the <b>Grid</b>.
 */
public abstract class Individual implements Element, Serializable {
	private static final long serialVersionUID = 3151415643009038340L;
	private int ID;
	private Coordinate position;
	private boolean played;
	private ArrayList<Coordinate> visualField;

	/** Abstract constructor of the abstract class <b>Individual</b>, to ease the construction of its subclasses, <b>Guardian</b> and <b>Intrude</b>.
	 * 
	 * @param ID : the ID of the <b>Individual</b>.
	 * @param position : current position (<b>Coordinate</b>) of the <b>Individual</b>.
	 */
	public Individual(int ID, Coordinate position) {
		this.ID = ID;
		this.position = position;
		visualField = new ArrayList<Coordinate>();
		played = false;
	}
	
	public ElementType getElementType() {
		return ElementType.INDIVIDUAL;
	}

	/** Returns a boolean, indicating if the <b>Individual</b> has played during this turn.
	 * 
	 * @return <code>true</code> if the <b>Individual</b> has played during this turn, <code>false</code> otherwise.
	 */
	public boolean hasPlayed() {
		return played;
	}

	/** Sets if the <b>Individual</b> has played during this turn.
	 * 
	 * @param hasPlayed : <code>true</code> to indicate that the <b>Individual</b> has played during this turn, <code>false</code> if he has not.
	 */
	public void setPlayed(boolean hasPlayed) {
		this.played = hasPlayed;
	}

	/** Sets the ID of the <b>Individual</b>.
	 * 
	 * @param ID : the new ID of the <b>Individual</b>.
	 */
	public void setID(int ID) {
		this.ID = ID;
	}

	/** Returns the ID of the <b>Individual</b>.
	 * 
	 * @return the ID of the <b>Individual</b>.
	 */
	public int getID() {
		return ID;
	}

	/** Returns the current position (<b>Coordinate</b>) of the <b>Individual</b>.
	 * 
	 * @return a <b>Coordinate</b> representing the current position of the <b>Individual</b>.
	 */
	public Coordinate getPosition() {
		return position;
	}

	/** Sets the position of the <b>Individual</b>.
	 * 
	 * @param position : the new position (<b>Coordinate</b>) of the <b>Individual</b>.
	 */
	public void setPosition(Coordinate position) {
		this.position = position;
	}

	/** Returns the field of view of the <b>Individual</b>.
	 * 
	 * @return a list of <b>Coordinates</b> visible by the <b>Individual</b>.
	 */
	public ArrayList<Coordinate> getVisualField() {
		return visualField;
	}

	/** Sets the field of view of the <b>Individual</b>. 
	 * 
	 * @param visualField : the list of <b>Coordinates</b> visible by the <b>Individual</b>.
	 */
	public void setVisualField(ArrayList<Coordinate> visualField) {
		this.visualField = visualField;
	}
}
