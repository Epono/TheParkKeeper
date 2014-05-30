package environnement;

import java.io.Serializable;
import java.util.ArrayList;

/** Represents the grid of the game, made of a square of <b>Coordinates</b>.</br>
 * It's the support of the <b>Elements</b>.
 */
public class Grid implements Serializable {
	private static final long serialVersionUID = 1021095052181435068L;
	private ArrayList<ArrayList<Element>> grid;
	private int dimension;
	private int density;
//	public Water water = Water.getInstance();
//	public Wall wall = Wall.getInstance();
//	public Trunk trunk = Trunk.getInstance();
//	public Leaf leaf = Leaf.getInstance();
//	public LeafWater leafWater = LeafWater.getInstance();
//	public Grass grass = Grass.getInstance();

	/** Constructs a <b>Grid</b> of the specified dimension.</br>
	 * It's a <b>dimension*dimension</b> square of <b>Coordinates</b>, reprensented by a list of lists of <b>Elements</b>.
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public Grid(int dimension) {
		this.dimension = dimension;
		grid = new ArrayList<ArrayList<Element>>(dimension);
		for (int i = 0; i < dimension; i++) {
			grid.add(new ArrayList<Element>(dimension));
		}
		density = (dimension / 5) + 1;
	}

	/** Initializes the <b>Grid</b> with <b>Grass</b> in all the boxes.
	 */
	public void initGrid() {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				grid.get(i).add(Grass.getInstance());
			}
		}
	}

	/** Empties the <b>Grid</b>.
	 */
	public void emptyGrid() {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				grid.get(i).clear();
			}
		}
	}

	/** Automatically initializes the <b>Grid</b>, depending on its density.
	 */
	public void initAutoGrid() {
		switch (density) {
		case 1:
			initWall(new Coordinate(dimension));
			initLake(new Coordinate(dimension));
			initTree(new Coordinate(dimension));
			break;

		case 2:
			initWall(new Coordinate(dimension));
			initLake(new Coordinate(dimension));
			initTree(new Coordinate(dimension));

			break;

		case 3:
			for (int i = 0; i < 2; i++) {
				initWall(new Coordinate(dimension));
			}
			initLake(new Coordinate(dimension));
			initTree(new Coordinate(dimension));

			break;

		case 4:
			for (int i = 0; i < 2; i++) {
				initWall(new Coordinate(dimension));
			}
			initLake(new Coordinate(dimension));
			for (int i = 0; i < 2; i++) {
				initTree(new Coordinate(dimension));
			}
			break;

		case 5:
			for (int i = 0; i < 2; i++) {
				initWall(new Coordinate(dimension));
			}
			for (int i = 0; i < 2; i++) {
				initLake(new Coordinate(dimension));
			}
			for (int i = 0; i < 2; i++) {
				initTree(new Coordinate(dimension));
			}
			break;

		default:
			break;
		}

	}
	
	/** "Erase" the box (or area around the box) given by the <b>Coordinate</b>.</br>
	 * - if size = 1, erase only the selected box.</br>
	 * - if size = 2, erase a 3*3 square around the box.</br>
	 * - if size = 3, erase a 5*5 square around the box.</br></br>
	 * 
	 * Erase means replacing the <b>Element</b> in the box with <b>Grass</b>.
	 * 
	 * @param size : the size of the erasing.
	 * @param coord : the <b>Coordinate</b> center of the area to erase.
	 */
	public void expandGrass(int size, Coordinate coord) {
		if (size == 1)
			setCell(coord, Grass.getInstance());
		else if (size == 2) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (coord.getCoordX() - 1 + i < getDimension() && coord.getCoordX() - 1 + i >= 0
							&& coord.getCoordY() - 1 + j < getDimension() && coord.getCoordY() - 1 + j >= 0)
						setCell(new Coordinate(coord.getCoordX() - 1 + i, coord.getCoordY() - 1 + j), Grass.getInstance());
				}
			}
		} 
		else if (size == 3) {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					if (coord.getCoordX() - 2 + i < getDimension() && coord.getCoordX() - 2 + i >= 0
							&& coord.getCoordY() - 2 + j < getDimension() && coord.getCoordY() - 2 + j >= 0)
						setCell(new Coordinate(coord.getCoordX() - 2 + i, coord.getCoordY() - 2 + j), Grass.getInstance());
				}
			}
		}
	}
	
	/** Generates a lake at the specified <b>Coordinate</b>.
	 * Its size depends on the density of the <b>Grid</b>.
	 * 
	 * @param coord : the source <b>Coordinate</b> of the lake. 
	 */
	public void initLake(Coordinate coord) {
		Coordinate temp;
		Coordinate coordNorth = coord.copy();
		Coordinate coordEast = coord.copy();
		Coordinate coordSouth = coord.copy();
		Coordinate coordWest = coord.copy();
		setCell(coord, Water.getInstance());
		int direction;
		int emission;

		direction = (int) (Math.random()*(density + 2));
		for (int i = 0; i < direction; i++) {
			coordNorth.moveNorth();
			temp = coordNorth.copy();
			setCell(temp, Water.getInstance());
			emission = (int) (Math.random()*(density + 2));
			for (int j = 0; j < emission; j++) {
				temp.moveEast(dimension);
				setCell(temp, Water.getInstance());
			}
		}

		direction = (int) (Math.random()*(density + 2));
		for (int i = 0; i < direction; i++) {
			coordEast.moveEast(dimension);
			temp = coordEast.copy();
			setCell(temp, Water.getInstance());
			emission = (int) (Math.random()*(density + 2));
			for (int j = 0; j < emission; j++) {
				temp.moveSouth(dimension);
				setCell(temp, Water.getInstance());
			}
		}

		direction = (int) (Math.random()*(density + 2));
		for (int i = 0; i < direction; i++) {
			coordSouth.moveSouth(dimension);
			temp = coordSouth.copy();
			setCell(temp, Water.getInstance());
			emission = (int) (Math.random()*(density + 2));
			for (int j = 0; j < emission; j++) {
				temp.moveWest();
				setCell(temp, Water.getInstance());
			}
		}

		direction = (int) (Math.random()*(density + 2));
		for (int i = 0; i < direction; i++) {
			coordWest.moveWest();
			temp = coordWest.copy();
			setCell(temp, Water.getInstance());
			emission = (int) (Math.random()*(density + 2));
			for (int j = 0; j < emission; j++) {
				temp.moveNorth();

				setCell(temp, Water.getInstance());
			}
		}

	}
	
	/** Generates a straight wall (with a length of dimension/2), from the specified <b>Coordinate</b>.
	 * 
	 * @param coord : the source <b>Coordinate</b> of the generated wall. 
	 */
	public void initWall(Coordinate coord) {
		setCell(coord, Wall.getInstance());
		int direction = (int) (Math.random()*4);
		int lenght = (int) (Math.random()*(dimension/2));

		for (int i = 0; i < lenght; i++) {
			switch (direction) {
			case 0:
				coord.moveNorth();
				break;
			case 1:
				coord.moveEast(dimension);
				break;
			case 2:
				coord.moveSouth(dimension);
				break;
			case 3:
				coord.moveWest();
				break;
			default:
				break;
			}
			Element o = getCell(coord);
			if (o.getElementType() == ElementType.WATER) 
				break;
			else 
				setCell(coord, Wall.getInstance());
		}
	}

	/** Generates a forest around the specified <b>Coordinate</b>.
	 * Its size depends on the density of the <b>Grid</b>.
	 * 
	 * @param coord : the source <b>Coordinate</b> of the forest. 
	 */
	public void initTree(Coordinate coord) {
		Coordinate temp;
		Coordinate coordNorth = coord.copy();
		Coordinate coordEast = coord.copy();
		Coordinate coordSouth = coord.copy();
		Coordinate coordWest = coord.copy();
		setTree(coord);
		int direction;
		int emission;

		direction = (int) (Math.random()*density-1);
		for (int i = 0; i < direction; i++) {
			coordNorth.moveTreeNorth(dimension);
			temp = coordNorth.copy();
			setTree(temp);
			emission = (int) (Math.random()*density-1);
			for (int j = 0; j < emission; j++) {
				temp.moveTreeEast(dimension);
				setTree(temp);
			}
		}

		direction = (int) (Math.random()*density-1);
		for (int i = 0; i < direction; i++) {
			coordEast.moveTreeEast(dimension);
			temp = coordEast.copy();
			setTree(temp);
			emission = (int) (Math.random()*density-1);
			for (int j = 0; j < emission; j++) {
				temp.moveTreeSouth(dimension);
				setTree(temp);
			}
		}

		direction = (int) (Math.random()*density-1);
		for (int i = 0; i < direction; i++) {
			coordSouth.moveTreeSouth(dimension);
			temp = coordSouth.copy();
			setTree(temp);
			emission = (int) (Math.random()*density-1);
			for (int j = 0; j < emission; j++) {
				temp.moveTreeWest();
				setTree(temp);
			}
		}

		direction = (int) (Math.random()*density-1);
		for (int i = 0; i < direction; i++) {
			coordWest.moveTreeWest();
			temp = coordWest.copy();
			setTree(temp);
			emission = (int) (Math.random()*density-1);
			for (int j = 0; j < emission; j++) {
				temp.moveTreeNorth(dimension);
				setTree(temp);
			}
		}
	}

	/** Places a <b>Trunk</b> and expands its leaves if the <b>ElementType</b> at the specified <b>Coordinate</b> is neither a <b>WALL</b> nor a <b>WATER</b>,
	 * nor an <b>INTRUDE</b>, nor a <b>GUARDIAN</b>.
	 * 
	 * @param coord : <b>Coordinate</b> where to place the <b>Trunk</b>.
	 */
	public void setTree(Coordinate coord) {
		ElementType elementType = getCell(coord).getElementType();
		if (!(elementType == ElementType.WATER || elementType == ElementType.WALL
				|| elementType == ElementType.INTRUDE || elementType == ElementType.GUARDIAN)) {
			setCell(coord, Trunk.getInstance());
			expandsLeaf(coord);
		}
	}

	/** Expands the <b>Leaves</b> of the <b>Trunk</b> from the specified <b>Coordinate</b> in the 4 directions,
	 * if the <b>ElementType</b> is neither a <b>WALL</b> nor a <b>WATER</b>, nor an <b>INTRUDE</b>, nor a <b>GUARDIAN</b>.
	 * 
	 * @param coord : <b>Coordinate</b> of the <b>Trunk</b>.
	 */
	public void expandsLeaf(Coordinate coord) {
		Coordinate subCoord;
		for (int i = 0; i < 4; i++) {
			subCoord = coord.copy();
			switch (i) {
			case 0:
				subCoord.moveNorth();
				break;
			case 1:
				subCoord.moveEast(dimension);
				break;
			case 2:
				subCoord.moveSouth(dimension);
				break;
			case 3:
				subCoord.moveWest();
				break;
			default:
				break;
			}
			Element elem = getCell(subCoord);
			if (elem.getElementType() == ElementType.WATER) 
				setCell(subCoord, LeafWater.getInstance());
			else if (elem.getElementType() == ElementType.GRASS)
				setCell(subCoord, Leaf.getInstance());
		}
	}
	
	/** Tests whether the specified <b>Coordinate</b> is near a <b>Trunk</b> or not.
	 * 
	 * @param coord : the <b>Coordinate</b> to be tested.
	 * @return <code>true</code> if there is a <b>Trunk</b> adjacent to the <b>Coordinate</b>, <code>false</code> otherwise. 
	 */
	public boolean isNearTree(Coordinate coord) {
		ElementType to;
		for (int i = 0; i < 4; i++) {
			Coordinate coordTemp = coord.copy();

			switch (i) {
			case 0:
				coordTemp.moveNorth();
				break;
			case 1:
				coordTemp.moveEast(dimension);
				break;
			case 2:
				coordTemp.moveSouth(dimension);
				break;
			case 3:
				coordTemp.moveWest();
				break;
			default:
				break;
			}
			to = getCell(coordTemp).getElementType();
			if (to == ElementType.TRUNK)
				return true;
		}
		return false;
	}

	/** Indicates if the <b>Coordinate</b> is valid for the displacement of a <b>Guardian</b>.
	 * 
	 * @param coord : <b>Coordinate</b> to be tested.
	 * @return <code>true</code> if the <b>Element</b> at the specified <b>Coordinate</b> is not an obstacle or another <b>Guardian</b>, <code>false</code> otherwise.
	 */
	public boolean isValidGuardian(Coordinate coord) {
		if (getCell(coord).getElementType() == ElementType.GRASS
				|| getCell(coord).getElementType() == ElementType.LEAF
				|| getCell(coord).getElementType() == ElementType.INTRUDE)
			return true;
		else
			return false;
	}

	/** Indicates if the <b>Coordinate</b> is valid for the displacement of an <b>Intrude</b>.
	 * 
	 * @param coord : <b>Coordinate</b> to be tested.
	 * @return <code>true</code> if the <b>Element</b> at the specified <b>Coordinate</b> is not an obstacle or another <b>Individual</b>, <code>false</code> otherwise.
	 */
	public boolean isValidIntrude(Coordinate coord) {
		if (getCell(coord).getElementType() == ElementType.GRASS
				|| getCell(coord).getElementType() == ElementType.LEAF)
			return true;
		else
			return false;
	}

	/** Indicates if the <b>Coordinate</b> is valid for the displacement of a <b>Guardian</b>.</br>
	 * Used to calculate patrols routes, regardless of the <b>Guardians</b> positions.
	 * 
	 * @param coord : <b>Coordinate</b> to be tested.
	 * @return <code>true</code> if the <b>Element</b> at the specified <b>Coordinate</b> is not an obstacle, <code>false</code> otherwise.
	 */
	public boolean isValid(Coordinate coord) {
		if (getCell(coord).getElementType() == ElementType.GRASS
				|| getCell(coord).getElementType() == ElementType.LEAF
				|| getCell(coord).getElementType() == ElementType.INTRUDE
				|| getCell(coord).getElementType() == ElementType.GUARDIAN)
			return true;
		else
			return false;
	}
	
	/** Indicates if the <b>Element</b> at the specified <b>Coordinate</b>  is opaque.
	 * 
	 * @param coord : <b>Coordinate</b> of the <b>Element</b> to be tested.
	 * @return <code>true</code> if the <b>Element</b> is opaque, <code>false</code> otherwise.
	 */
	public boolean isOpaque(Coordinate coord) {
		if (getCell(coord).getElementType() == ElementType.LEAF
				|| getCell(coord).getElementType() == ElementType.LEAFWATER
				|| getCell(coord).getElementType() == ElementType.TRUNK
				|| getCell(coord).getElementType() == ElementType.WALL)
			return true;
		else
			return false;
	}

	/** Indicates if the <b>Element</b> at the specified <b>Coordinate</b>  is an obstacle.
	 * 
	 * @param coord : <b>Coordinate</b> of the <b>Element</b> to be tested.
	 * @return <code>true</code> if the <b>Element</b> is an obstacle, <code>false</code> otherwise.
	 */
	public boolean isObstacle(Coordinate coord) {
		if (getCell(coord).getElementType() == ElementType.WATER
				|| getCell(coord).getElementType() == ElementType.LEAFWATER
				|| getCell(coord).getElementType() == ElementType.TRUNK
				|| getCell(coord).getElementType() == ElementType.WALL)
			return true;
		else
			return false;
	}
	
	/** Indicates if the <b>Coordinate</b> is surrounded by obstacle <b>Elements</b>.
	 * 
	 * @param coord : the <b>Coordinate</b> to be tested.
	 * @return <code>true</code> if there is at least one not obstacle <b>Element</b> around the specified <b>Coordinate</b>, <code>false</code> otherwise.
	 */
	public boolean isDirectlyAccessible(Coordinate coord) {
		int x = coord.getCoordX();
		int y = coord.getCoordY();
		Coordinate coordTemp;

		if (y > 0) {
			coordTemp = coord.copy();
			coordTemp.moveNorth();
			if (!isObstacle(coordTemp)) {
				return true;
			}
		}
		if (x < dimension - 1) {
			coordTemp = coord.copy();
			coordTemp.moveEast(dimension);
			if (!isObstacle(coordTemp)) {
				return true;
			}
		}
		if (y < dimension - 1) {
			coordTemp = coord.copy();
			coordTemp.moveSouth(dimension);
			if (!isObstacle(coordTemp)) {
				return true;
			}
		}
		if (x > 0) {
			coordTemp = coord.copy();
			coordTemp.moveWest();
			if (!isObstacle(coordTemp)) {
				return true;
			}
		}
		return false;
	}
	
	/** Gets the <b>Element</b> at the specified <b>Coordinate</b>.
	 * 
	 * @param coord : the <b>Coordinate</b> to which fetch the <b>Element</b>.
	 * @return the <b>Element</b> at the specified <b>Coordinate</b>.
	 */
	public Element getCell(Coordinate coord) {
		return grid.get(coord.getCoordX()).get(coord.getCoordY());
	}

	/** Sets the specified <b>Element</b> at the specified <b>Coordinate</b>.
	 * 
	 * @param coord : the <b>Coordinate</b> to which place the <b>Element</b>.
	 * @param element : the <b>Element</b> to place.
	 */
	public void setCell(Coordinate coord, Element element) {
		grid.get(coord.getCoordX()).set(coord.getCoordY(), element);
	}

	/** Returns the dimension of the <b>Grid</b>.
	 * 
	 * @return the dimension of the <b>Grid</b>.
	 */
	public int getDimension() {
		return dimension;
	}

	/** Returns the density of the <b>Grid</b>.</br>
	 * density = (dimension / 5) + 1.
	 * 
	 * @return the density of the <b>Grid</b>.
	 */
	public int getDensity() {
		return density;
	}

	@Override
	public String toString() {
		String strReturn = "Gird : \n\n";
		for(int i = 0; i < dimension; i++) {
			for(int j = 0; j < dimension; j++) {
				strReturn += grid.get(i).get(j);
			}
			strReturn += "\n";
		}
		return strReturn;
	}
}
