package management;

import individual.Displacement;
import individual.Guardian;
import individual.Individual;
import individual.Intrude;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import environnement.Coordinate;
import environnement.Element;
import environnement.ElementType;
import environnement.Grass;
import environnement.Grid;
import environnement.Leaf;

/** Manages the current game (in a logical state, GridIHM managing the GUI state).</br>
 * Manages the movements of <b>Individuals</b> and field of view using the <b>AI</b>.
 */
public class Manager {
	private AI ai;
	private Grid grid;
	private ArrayList<Guardian> guardians;
	private ArrayList<Intrude> intrudes;
	private ArrayList<Individual> individuals;
	private ArrayList<Coordinate> visualField;
	private ArrayList<Coordinate> patrolRoutes;
	private ArrayList<Coordinate> patrolCoordinates;
	private String name;
	private Date creationDate;
	private int numberOfTurns;
	private long duration;
	private long startingTime;
	private long elapsedTime;
	private long temporaryTime;

	private boolean authorizedMovement;
	private boolean isPlaying;
	private Guardian movingGuardian;
	
	/** Constructs a <b>Manager</b> for a new game.
	 * 
	 * @param dimension : size of the <b>Grid</b>.
	 * @param nom : name of the game.      
	 */
	public Manager(int dimension, String nom) {
		grid = new Grid(dimension);
		ai = new AI(grid);
		guardians = new ArrayList<Guardian>();
		intrudes = new ArrayList<Intrude>();
		individuals = new ArrayList<Individual>();
		visualField = new ArrayList<Coordinate>();
		this.name = nom;
		creationDate = new Date();
		numberOfTurns = 0;
		duration = 0;
		authorizedMovement = false;
		isPlaying = true;
		startingTime = System.currentTimeMillis();
	}

	/** Constructs a <b>Manager</b> from an existing save (<b>Map</b>).
	 * 
	 * @param map : <b>Map</b> to load the game from.
	 */
	public Manager(Map map) {
		grid = map.getGrid();
		ai = new AI(grid);
		guardians = map.getGuardians();
		intrudes = map.getIntrudes();
		individuals = new ArrayList<Individual>();
		individuals.addAll(guardians);
		individuals.addAll(intrudes);
		visualField = new ArrayList<Coordinate>();
		visualField = calculVisualField();
		name = map.getName();
		creationDate = map.getCreationDate();
		numberOfTurns = map.getNumberOfTurns();
		duration = map.getDuration();
		patrolRoutes = map.getpatrolRoute();
		patrolCoordinates = map.getpatrolRoute();
		authorizedMovement = false;
		isPlaying = true;
		startingTime = System.currentTimeMillis();
	}

	/** Read through the <b>Grid</b> and fills <b>Intrude</b>, <b>Guardian</b> and <b>Individual</b> lists.
	 */
	public void initIndividuals() {
		Element element;
		Guardian guardian;
		Intrude intrude;
		int idIntrude = 0;
		int idGuardian  = 0;
		for (int row = 0; row < getGrid().getDimension(); row++) {
			for (int col = 0; col < getGrid().getDimension(); col++) {

				element = (Element) getGrid().getCell(new Coordinate(col, row));
				ElementType elementType = element.getElementType();

				switch (elementType) {
				case GUARDIAN:
					guardian = (Guardian) element;
					guardian.setID(idGuardian);
					guardians.add(guardian);
					idGuardian++;
					break;
				case INTRUDE:
					intrude = (Intrude) element;
					intrude.setID(100+idIntrude);
					intrudes.add(intrude);
					idIntrude++;
					break;
				default:
					break;
				}
			}
		}
		individuals.addAll(guardians);
		individuals.addAll(intrudes);
	}

	/** Automatically creates 2 controlled <b>Guardian</b> and density+2 <b>Intrude</b> (randomly dynamic or static).
	 */
	public void initAutoIndividuals() {
		Coordinate coord;
		for(int i = 0; i < 2 ; i++) {
			do {
				coord = new Coordinate(grid.getDimension());
			} while (grid.isObstacle(coord) || grid.getCell(coord).getElementType()==ElementType.INTRUDE 
					|| grid.getCell(coord).getElementType()==ElementType.GUARDIAN || !grid.isDirectlyAccessible(coord));
			Guardian guard = new Guardian(i, coord, true);
			grid.setCell(coord, guard);
			guardians.add(guard);
		}

		for (int i = 0; i < grid.getDensity() + 3; i++) {
			do {
				coord = new Coordinate(grid.getDimension());
			} while (grid.isObstacle(coord) || grid.getCell(coord).getElementType()==ElementType.INTRUDE 
					|| grid.getCell(coord).getElementType()==ElementType.GUARDIAN || !grid.isDirectlyAccessible(coord));
			Intrude intrude = new Intrude(100+i, coord, Math.random()<0.5);
			grid.setCell(coord, intrude);
			intrudes.add(intrude);
		}
		individuals.addAll(guardians);
		individuals.addAll(intrudes);
	}

	/** Initialization of patrols <b>Coordinates</b> and patrols routes.
	 */
	public void initPatrouille() {
		patrolRoutes = ai.patrolRouteCalculation(guardians);
		patrolCoordinates = ai.calculCoordinatesPatrouille(guardians);
	}


	/** Calculates fields of view for <b>Guardians</b> and <b>Intrudes</b>.</br>
	 * Calculates <b>Coordinates</b> to avoid for <b>Intrudes</b> and <b>Coordinates</b> to go for </b>Guardians</b> 
	 * 
	 * @return a list containing all <b>Coordinates</b> visible by <b>Guardians</b>.
	 */
	public ArrayList<Coordinate> calculVisualFieldAndDestinations() {		
		calculVisualField();
		for (Intrude intrude : intrudes) {
			//reset de ses coordonnes a eviter
			intrude.getGuardiansSpot().clear();
			//calcul champ de vision de chaque intrus
			intrude.setVisualField(ai.visualFieldCalculation(intrude.getPosition()));
			for (Guardian guard : guardians) {
				//si gardien dans le champ de vision et pas deja dans la liste des zone a eviter
				if (intrude.getVisualField().contains(guard.getPosition()) && !(intrude.getGuardiansSpot().contains(guard.getPosition()))) {
					intrude.getGuardiansSpot().add(guard.getPosition());
				}
			}
		}

		for (Guardian guardian : guardians) {
			for (Intrude intrude : intrudes) {
				if (guardian.getReachableCoordinates().contains(intrude.getPosition())	&& visualField.contains(intrude.getPosition())
						&& !(guardian.getDestinations().contains(intrude.getPosition()))) {
					guardian.addDestination(intrude.getPosition());
				}
				for (int i = 0; i < guardian.getDestinations().size(); i++) {
					Coordinate coordDestination = guardian.getDestinations().get(i);
					if (visualField.contains(coordDestination) && grid.getCell(coordDestination).getElementType() != ElementType.INTRUDE) {
						guardian.getDestinations().remove(coordDestination);
						i--;
					}
				}
			}
		}
		return visualField;
	}

	/** Calculates fields of view for <b>Guardians</b>.
	 * 
	 * @return a list containing all <b>Coordinates</b> visible by <b>Guardians</b>.
	 */
	public ArrayList<Coordinate> calculVisualField() {
		ArrayList<Coordinate> visualFieldNonTrie = new ArrayList<Coordinate>();
		visualField.clear();
		for (Guardian guardian : guardians) { // calcul champ de vision des gardiens
			visualFieldNonTrie.addAll(ai.visualFieldCalculation(guardian.getPosition()));
			guardian.setVisualField(ai.visualFieldCalculation(guardian.getPosition()));
		}
		Set<Coordinate> mySet = new HashSet<Coordinate>(visualFieldNonTrie);
		visualField.addAll(mySet);
		return visualField;
	}

	/** Moves automatically the given <b>Guardian</b>.</br>
	 * If he knows at least one position of an <b>Intrude</b>, he goes to the closest one.</br>
	 * Otherwise, he patrols.
	 * 
	 * @param guard : the <b>Guardian</b> to move.
	 */
	public void autoMove(Guardian guard) {
		ArrayList<Coordinate> destinations;
		try {
			destinations = guard.getDestinations();
			if (destinations.size() == 0) { 
				// patrouille
				moveIndividual(guard, ai.patrolMode(guard));
			} 
			else { 
				//aller chercher l'intrus le plus proche
				guard.setPatrolPosition(-1);
				ArrayList<Coordinate> itineraireFinal = new ArrayList<Coordinate>();
				ArrayList<Coordinate> itineraireTemp = new ArrayList<Coordinate>();
				for (Coordinate coordDestination : destinations) {
					itineraireTemp = ai.routeCalculation(guard.getPosition(), coordDestination, true);
					if (itineraireFinal.size() == 0 || (itineraireTemp.size()!=0 && itineraireTemp.size() < itineraireFinal.size() ) )
						itineraireFinal = new ArrayList<Coordinate>(itineraireTemp);
				}
				try {
					moveIndividual(guard, ai.displacementCalculation(guard.getPosition(),	itineraireFinal.get(1)));
				} catch (IndexOutOfBoundsException ioobe) {
					ioobe.printStackTrace();
				}
			}
		}
		catch (NullPointerException npe) { //on va trop vite
		}

	}


	/** Moves the given <b>Individual</b> on the <b>Grid</b>.
	 * 
	 * @param indi : the <b>Individual</b> to move.
	 * @param displacement : the <b>Displacement</b> to do.
	 */
	public void moveIndividual(Individual indi, Displacement displacement) {
		Coordinate coordSource = indi.getPosition().copy();
		Coordinate coordDestination = coordSource.copy();
		int coordX = indi.getPosition().getCoordX();
		int coordY = indi.getPosition().getCoordY();
		boolean deplacementEffectue = false;
		ElementType toSource;

		switch (displacement) {
		case NORTH:
			coordDestination.moveNorth();
			toSource = indi.getElementType();
			if(toSource == ElementType.INTRUDE) {
				if (coordY > 0	&& (grid.isValidIntrude(coordDestination))) {
					deplacementEffectue = true;
				}
			}
			else if(toSource == ElementType.GUARDIAN) {
				if (coordY > 0	&& (grid.isValidGuardian(coordDestination))) {
					if (grid.getCell(coordDestination).getElementType() == ElementType.INTRUDE) {
						intrudeCaught(coordDestination);
					}
					deplacementEffectue = true;
				}
			}
			break;

		case SOUTH:
			coordDestination.moveSouth(grid.getDimension());
			toSource = indi.getElementType();
			if(toSource == ElementType.INTRUDE) {
				if (coordY < grid.getDimension() - 1 && (grid.isValidIntrude(coordDestination))) {
					deplacementEffectue = true;
				}
			}
			else if(toSource == ElementType.GUARDIAN) {
				if (coordY < grid.getDimension() - 1 && (grid.isValidGuardian(coordDestination))) {
					if (grid.getCell(coordDestination).getElementType() == ElementType.INTRUDE) {
						intrudeCaught(coordDestination);
					}
					deplacementEffectue = true;
				}
			}
			break;

		case WEST:
			coordDestination.moveWest();
			toSource = indi.getElementType();
			if(toSource == ElementType.INTRUDE) {
				if (coordX > 0 && (grid.isValidIntrude(coordDestination))) {
					deplacementEffectue = true;
				}
			}
			else if(toSource == ElementType.GUARDIAN) {
				if (coordX > 0 && (grid.isValidGuardian(coordDestination))) {
					if (grid.getCell(coordDestination).getElementType() == ElementType.INTRUDE) {
						intrudeCaught(coordDestination);
					}
					deplacementEffectue = true;
				}
			}
			break;

		case EAST:
			coordDestination.moveEast(grid.getDimension());
			toSource = indi.getElementType();
			if(toSource == ElementType.INTRUDE) {
				if (coordX < grid.getDimension() - 1 && (grid.isValidIntrude(coordDestination))) {
					deplacementEffectue = true;
				}
			}
			else if(toSource == ElementType.GUARDIAN) {
				if (coordX < grid.getDimension() - 1 && (grid.isValidGuardian(coordDestination))) {
					if (grid.getCell(coordDestination).getElementType() == ElementType.INTRUDE) {
						intrudeCaught(coordDestination);
					}
					deplacementEffectue = true;
				}
			}
			break;
		case NONE:
			break;
		default:
			break;
		}

		if(deplacementEffectue) {
			indi.setPosition(coordDestination);
			grid.setCell(coordDestination, indi);
			if (grid.isNearTree(coordSource))
				grid.setCell(coordSource, Leaf.getInstance());
			else
				grid.setCell(coordSource, Grass.getInstance());
		}

		indi.setPlayed(true);
	}

	/** Deletes an <b>Intrude</b> if he's caught (a <b>Guardian</b> moves in the same <b>Coordinate</b>).
	 * 
	 * @param coordDestination : <b>Coordinate</b> where the <b>Intrude</b> has been caught.
	 */
	public void intrudeCaught (Coordinate coordDestination) {
		Intrude intruTemp = (Intrude) grid.getCell(coordDestination);
		for (Guardian guardTemp : guardians) {
			guardTemp.removeDestinations(intruTemp.getPosition());
		}
		intrudes.remove(intruTemp);
		individuals.remove(intruTemp);
	}

	/** Updates the duration of the game.
	 */
	public void updateDuree() {
		temporaryTime = System.currentTimeMillis();
		elapsedTime = temporaryTime - startingTime;
		startingTime = temporaryTime;
		duration += elapsedTime;
	}

	/** Saves the game by creating and serializing a <b>Map</b> with the name "*.savj", where * is the name of the game.
	 */
	public void saveMap() {
		updateDuree();
		Map map = new Map(grid, guardians, intrudes, name, creationDate, numberOfTurns, duration + elapsedTime, patrolRoutes);
		new File("saves").mkdir();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("saves/" + name + ".savj"));
			oos.writeObject(map);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Returns the <b>AI</b>.
	 * 
	 * @return the <b>AI</b>.
	 */
	public AI getAi() {
		return ai;
	}	

	/** Returns the <b>Grid</b> of the game.
	 * 
	 * @return the <b>Grid</b> of the game.
	 */
	public Grid getGrid() {
		return grid;
	}

	/** Returns the name of the game.
	 * 
	 * @return the name of the game.
	 */
	public String getName() {
		return name;
	}

	/** Returns the creation date of the game.
	 * 
	 * @return the creation date of the game.
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/** Returns the number of turns of the game.
	 * 
	 * @return the number of turns of the game.
	 */
	public int getNumberOfTurns() {
		return numberOfTurns;
	}

	/** Returns the duration of the game.
	 * 
	 * @return the duration of the game.
	 */
	public long getDuration() {
		return duration;
	}

	/** Returns the list of<b>Guardians</b> of the game.
	 * 
	 * @return the list of<b>Guardians</b> of the game.
	 */
	public ArrayList<Guardian> getGuardians() {
		return guardians;
	}

	/** Returns the list of<b>Intrudes</b> of the game.
	 * 
	 * @return the list of<b>Intrudes</b> of the game.
	 */
	public ArrayList<Intrude> getIntrudes() {
		return intrudes;
	}

	/** Returns the list of<b>Individuals</b> of the game.
	 * 
	 * @return the list of<b>Individuals</b> of the game.
	 */
	public ArrayList<Individual> getIndividuals() {
		return individuals;
	}

	/** Returns the list of<b>Coordinates</b> of all the patrols routes.
	 * 
	 * @return the list of<b>Coordinates</b> of all the patrols routes.
	 */
	public ArrayList<Coordinate> getPatrolsRoutes() {
		return patrolRoutes;
	}

	/** Returns the list of<b>Coordinates</b> of the patrol checkpoints.
	 * 
	 * @return the list of<b>Coordinates</b> of the patrol checkpoints.
	 */
	public ArrayList<Coordinate> getPatrolsCoordinates() {
		return patrolCoordinates;
	}

	/** Indicates whether the user can play or not. 
	 * 
	 * @return <code>true</code> if the user can play, <code>false</code> otherwise.
	 */
	public boolean isAuthorizedMovement() {
		return authorizedMovement;
	}

	/** Indicates whether the game is running or not. 
	 * 
	 * @return <code>true</code> if the game is running, <code>false</code> otherwise.
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/** Returns the <b>Guardian</b> which turn it is to move.
	 * 
	 * @return the <b>Guardian</b> which turn it is to move.
	 */
	public Guardian getGuardianMoving() {
		return movingGuardian;
	}

	/** Sets the <b>Guardian</b> which turn it is to move.
	 * 
	 * @param guardianMoving : the <b>Guardian</b> which turn it is to move.
	 */
	public void setGuardianMoving(Guardian guardianMoving) {
		this.movingGuardian = guardianMoving;
	}

	/** Sets the starting time of the game.
	 * 
	 * @param startingTime : the starting time of the game.
	 */
	public void setStartingTime(long startingTime) {
		this.startingTime = startingTime;
	}

	/** Sets the number of turns of the game.
	 * 
	 * @param numberOfTurns : the number of turns of the game.
	 */
	public void setNumberOfTurns(int numberOfTurns) {
		this.numberOfTurns = numberOfTurns;
	}

	/** Sets if the game is running or not.
	 * 
	 * @param isPlaying : <code>true</code> to indicate that the game is running, <code>false</code> to indicate that the game is paused.
	 */
	public void setIsPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	/** Sets if the user can play or not.
	 * 
	 * @param authorizedMovement : <code>true</code> to indicate that the user can play, <code>false</code> to indicate that the user can't play.
	 */
	public void setAuthorizedMovement(boolean authorizedMovement) {
		this.authorizedMovement = authorizedMovement;
	}
	
//	/** Plays the sound of a footstep.
//	*/
//	class playFootstep extends Thread {
//		public void run() {
//			java.applet.Applet.newAudioClip(getClass().getResource("/sons/grass1.wav")).play();
//		}
//	}
	
//	/** Plays the sound of a footstep.
//	*/
//	public void playFootstep() {
//		footstep.play();
//	}
	
}

//Test pour capture ecran
//class captureImage extends Thread {
//public void run() {
//	try {
//		Thread.sleep(500);
//		Robot robot = new Robot();
//		File fImage = new File("screenshot.jpg");
//		Rectangle rect = new Rectangle();
//		rect = gridihm.getJpGrid().getBounds();
//		rect.setLocation(gridihm.getJpGrid().getX() + 15, gridihm.getJpGrid().getY() + 34);
//		rect.grow(1, 1);
//		BufferedImage img = robot.createScreenCapture(rect);
//		ImageIO.write(img, "jpeg", fImage);
//	} catch (AWTException e) {
//		e.printStackTrace();
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//}

