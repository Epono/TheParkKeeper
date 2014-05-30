package environnement;

import java.io.Serializable;

/** Represents a box of the <b>Grid</b>, used as a getter.</br>
 * Contains an X coordinate and an Y coordinate.
 */
public class Coordinate implements Serializable {
	private static final long serialVersionUID = -596835483442423362L;
	private int coordX;
	private int coordY;

	/** Constructs a <b>Coordinate</b> with the given coordinates X and Y.
	 * 
	 * @param coordX : X coordinate.
	 * @param coordY : Y coordinate.
	 */
	public Coordinate(int coordX, int coordY) {
		this.coordX = coordX;
		this.coordY = coordY;
	}

	/** Constructs a random <b>Coordinate</b>, with X coordinate and Y coordinate less than <b>dimension</b>.
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public Coordinate(int dimension) {
		coordX = (int) (Math.random()*dimension);
		coordY = (int) (Math.random()*dimension);
	}

	/** Constructs a copy of the given <b>Coordinate</b>.
	 * 
	 * @return a copy of the <b>Coordinate</b>.
	 */
	public Coordinate copy() {
		return new Coordinate(this.coordX, this.coordY);
	}

	/** Decrements <b>coordY</b> coordinate if it is greater than 0.
	 */
	public void moveNorth() {
		if (coordY > 0) {
			coordY--;
		}
	}

	/** Increments <b>coordY</b> coordinate if it is less than (<b>dimension</b>-1).
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public void moveSouth(int dimension) {
		if (coordY < dimension - 1) {
			coordY++;
		}
	}

	/** Increments <b>coordX</b> coordinate if it is less than (<b>dimension</b>-1).
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public void moveEast(int dimension) {
		if (coordX < dimension - 1) {
			coordX++;
		}
	}

	/** Decrements <b>coordX</b> coordinate if it is greater than 0.
	 */
	public void moveWest() {
		if (coordX > 0) {
			coordX--;
		}
	}

	/** "Moves" a <b>Coordinate</b> two times <b>NORTH</b> and one time <b>EAST</b>.</br>
	 * Used to spread trees and generate a forest.
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public void moveTreeNorth(int dimension) {
		moveEast(dimension);
		moveNorth();
		moveNorth();
	}

	/** "Moves" a <b>Coordinate</b> two times <b>EAST</b> and one time <b>SOUTH</b>.</br>
	 * Used to spread trees and generate a forest.
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public void moveTreeEast(int dimension) {
		moveSouth(dimension);
		moveEast(dimension);
		moveEast(dimension);
	}

	/** "Moves" a <b>Coordinate</b> two times <b>SOUTH</b> and one time <b>WEST</b>.</br>
	 * Used to spread trees and generate a forest.
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public void moveTreeSouth(int dimension) {
		moveWest();
		moveSouth(dimension);
		moveSouth(dimension);
	}

	/** "Moves" a <b>Coordinate</b> two times <b>WEST</b> and one time <b>NORTH</b>.</br>
	 * Used to spread trees and generate a forest.
	 */
	public void moveTreeWest() {
		moveNorth();
		moveWest();
		moveWest();
	}

	/** Returns the X coordinate of the <b>Coordinate</b>.
	 * 
	 * @return the X coordinate of the <b>Coordinate</b>.
	 */
	public int getCoordX() {
		return coordX;
	}

	/** Returns the Y coordinate of the <b>Coordinate</b>.
	 * 
	 * @return the Y coordinate of the <b>Coordinate</b>.
	 */
	public int getCoordY() {
		return coordY;
	}

	@Override
	public String toString() {
		return "(" + coordX + "," + coordY + ")";
	}

	/** Override of the <b>hashCode()</b> method to indicate that 2 <b>Coordinate</b> are equals if they have the same X coordinate and Y coordinate.
	 */
	@Override
	public int hashCode() {
		return 100 * coordX + coordY;
	}

	/** Override of the <b>equals()</b> method to indicate that 2 <b>Coordinate</b> are equals if they have the same X coordinate and Y coordinate.
	 * 
	 * @param obj : the <b>Coordinate</b> to be tested.
	 * @return <code>true</code> if this <b>Coordinate</b> has the same X coordinate and Y coordinate, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (coordX != other.coordX)
			return false;
		if (coordY != other.coordY)
			return false;
		return true;
	}
}