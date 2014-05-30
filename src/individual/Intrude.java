package individual;

import java.io.Serializable;
import java.util.ArrayList;

import environnement.Coordinate;
import environnement.ElementType;

/** Subclass of <b>Individual</b>.</br>
 * Represents the "bad guy" that <b>Guardians</b> have to catch.</br>
 * He can be static (doesn't move) or dynamic (tries to avoid <b>Guardians</b> or moves randomly).
 */
public class Intrude extends Individual implements Serializable {
	private static final long serialVersionUID = -6117293614768692203L;
	private boolean dynamic;
	private ArrayList<Coordinate> guardiansSpot;

	/** Constructs an <b>Intrude</b> with given arguments.
	 * 
	 * @param ID : the ID of the <b>Intrude</b>.
	 * @param position : current position (<b>Coordinate</b>) of the <b>Intrude</b>.
	 * @param isDynamic : indicates whether the <b>Intrude</b> is dynamic (<code>true</code>) or static (<code>false</code>).
	 */
	public Intrude(int ID, Coordinate position, boolean isDynamic) {
		super(ID, position);
		this.dynamic = isDynamic;
		guardiansSpot = new ArrayList<Coordinate>();
	}
	
	public String toString() {
		String strDynamique;
		if (dynamic)
			strDynamique = "dynamic";
		else
			strDynamique = "static";
		return "Intrude -- Position : " + getPosition() + " -- ID : " + getID()	+ " -- Movement : " + strDynamique + "\n";
	}

	public ElementType getElementType() {
		return ElementType.INTRUDE;
	}

	/** Returns whether the <b>Intrude</b> is dynamic or not.
	 * 
	 * @return <code>true</code> if the <b>Intrude</b> is dynamic, <code>false</code> otherwise.
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/** Returns the list (of <b>Coordinates</b>) of known </b>Guardians</b> spots.
	 * 
	 * @return the list (of <b>Coordinates</b>) of known </b>Guardians</b> spots.
	 */
	public ArrayList<Coordinate> getGuardiansSpot() {
		return guardiansSpot;
	}

	/** Sets the list (of <b>Coordinates</b>) of known </b>Guardians</b> spots.
	 * 
	 * @param guardiansSpot : the list (of <b>Coordinates</b>) of known </b>Guardians</b> spots.
	 */
	public void setGuardiansSpot(ArrayList<Coordinate> guardiansSpot) {
		this.guardiansSpot = guardiansSpot;
	}
}
