package management;

import individual.Displacement;
import individual.Guardian;
import individual.Intrude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import environnement.Coordinate;
import environnement.Grid;

/** Ccontains the main algorithms used in Artificial Intelligence :</br>
 * - Calculation of field of view (using the algorithm of Bresenham path segment).</br>
 * - Calculation of a route from point A to point B (Dijkstra's algorithm)</br>
 * - Calculation of a patrol route.</br>
 * 
 * and methods used in these algorithms, or to manage <b>Individuals</b>.
 */
public class AI {
	private Grid grid;

	/** Constructs an AI for the provided <b>Grid</b>.
	 * 
	 * @param grid : the current game <b>Grid</b>.
	 */
	public AI(Grid grid) {
		this.grid = grid;
	}

	/** Indicates whether two <b>Coordinates</b> passed as arguments are adjacent or not.
	 * 
	 * @param coordA : the <b>Coordinate</b> to compare.
	 * @param coordB : the <b>Coordinate</b> to compare.
	 * @return <code>true</code> if the <b>Coordinates</b> are adjacent, <code>false</code> otherwise.
	 */
	public boolean areAdjacent(Coordinate coordA, Coordinate coordB) {
		int x = coordA.getCoordX();
		int y = coordA.getCoordY();
		int x2 = coordB.getCoordX();
		int y2 = coordB.getCoordY();

		return (Math.abs(x - x2) <= 1) && (Math.abs(y - y2) <= 1) && ((x != x2) ^ (y != y2));
	}

	/** Implementation of <b>Dijkstra's algorithm</b>.
	 * 
	 * @param sourceDisplacement : source <b>Coordinate</b>.
	 * @param destinationDisplacement : destination <b>Coordinate</b>.
	 * @param avoidGuardian :  a boolean which indicates whether to consider <b>Guardians</b> (<code>true</code>) or not (<code>false</code>) when calculating the route.
	 * (Used to calculate a route patrol regardless of the position of <b>Guardians</b>).
	 * 
	 * @return a list containing <b>Coordinates</b> steps to move from source <b>Coordinate</b> (included) to destination <b>Coordinate</b> (included).
	 */
	public ArrayList<Coordinate> routeCalculation(Coordinate sourceDisplacement, Coordinate destinationDisplacement, boolean avoidGuardian) {
		if(areAdjacent(sourceDisplacement, destinationDisplacement) && grid.isValidGuardian(destinationDisplacement)) {
			ArrayList<Coordinate> route = new ArrayList<Coordinate>();
			route.add(sourceDisplacement);
			route.add(destinationDisplacement);
			return route;
		}
		// INITIALISATION
		// toutes les coordonnes valables
		int dimension = grid.getDimension();
		Coordinate coordinates[][] = new Coordinate[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				coordinates[i][j] = new Coordinate(i, j);
			}
		}

		// Coordonnees : la coordonee (cle) dont on connait la plus petite
		// distance (valeur) a la source
		HashMap<Coordinate, Integer> distanceSource = new HashMap<Coordinate, Integer>();

		// Coordonnees : les coordonnes dont il faut verifier la distance
		ArrayList<Coordinate> coordinatesToCheck = new ArrayList<Coordinate>();

		// Coordonnees : les coordonnes dont on a verifié la distance
		ArrayList<Coordinate> coordinatesChecked = new ArrayList<Coordinate>();

		// tableau contenant l'ordre des cases pour le (un des) plus court
		// chemin pour chaque case
		// 1ere coordonnee (cle) : coordonnee dont on veut connaitre le
		// predecesseur
		// 2em coordonee (valeur) : predecesseur de la coordonnee cle
		HashMap<Coordinate, Coordinate> predecessorCoordinate = new HashMap<Coordinate, Coordinate>();

		// source du deplacement
		Coordinate source = coordinates[sourceDisplacement.getCoordX()][sourceDisplacement.getCoordY()];

		// initialisation/remplissage de parcours
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				predecessorCoordinate.put(coordinates[i][j], source);
				distanceSource.put(coordinates[i][j], Integer.MAX_VALUE);
			}
		}

		// ajout de la source dans la Hashmap avec une distance nulle
		distanceSource.put(source, 0);

		// ajout de la source a l'arraylist des cases verifiees, et le reste
		// dans l'arraylist des cases ï¿½ verifier
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if(avoidGuardian) {
					if (grid.isValidGuardian(coordinates[i][j]))
						coordinatesToCheck.add(coordinates[i][j]);
				} 
				else {
					if (grid.isValid(coordinates[i][j]))
						coordinatesToCheck.add(coordinates[i][j]);
				}
			}
		}

		coordinatesToCheck.remove(source);
		coordinatesChecked.add(source);
		// FIN INITIALISATION

		// pour chaque case valable de la grille
		for (int i = 0; i < coordinatesChecked.size(); i++) {
			Coordinate coordSource = coordinatesChecked.get(i);
			for (int j = 0; j < coordinatesToCheck.size(); j++) {
				Coordinate coordEnCoursDeVerif = coordinatesToCheck.get(j);
				if (areAdjacent(coordSource, coordEnCoursDeVerif)) {
					coordinatesToCheck.remove(coordEnCoursDeVerif);
					coordinatesChecked.add(coordEnCoursDeVerif);
					j--;
					if (distanceSource.get(coordEnCoursDeVerif) > distanceSource.get(predecessorCoordinate.get(coordSource)) + 1) {
						distanceSource.put(coordEnCoursDeVerif,	distanceSource.get(coordSource) + 1);
						predecessorCoordinate.put(coordEnCoursDeVerif, coordSource);
						//						System.out.println("Coordonnee : " + coordEnCoursDeVerif + " --- distance a la source "+ source +" : " +
						//								distanceSource.get(coordEnCoursDeVerif) + " --- predecesseur pour chemin le plus court : "+
						//								parcours.get(coordEnCoursDeVerif)+" destination "+destination);
						//						 Coordonnee : (10,17) --- distance a la source : 27	 --- predecesseur pour chemin le plus court : (9,17)
					}

				}
			}
		}

		// construire chemin
		ArrayList<Coordinate> reverseRoute = new ArrayList<Coordinate>();
		ArrayList<Coordinate> route = new ArrayList<Coordinate>();
		Coordinate coordTemp = coordinates[destinationDisplacement.getCoordX()][destinationDisplacement.getCoordY()];


		reverseRoute.add(destinationDisplacement);

		while (!(predecessorCoordinate.get(coordTemp).equals(source))) {
			reverseRoute.add(predecessorCoordinate.get(coordTemp));
			coordTemp = predecessorCoordinate.get((coordTemp));
		}

		reverseRoute.add(source);

		for (int i = 0; i < reverseRoute.size(); i++) {
			route.add(reverseRoute.get(reverseRoute.size() - i - 1));
		}

		//itineraire.get(0) = coordSource
		//itineraire.get(itineraire.size()-1) = destination
		return route;
	}

	/** Calculates the <b>Displacement</b> of the (dynamic) provided <b>Intrude</b>.</br>
	 * - If the <b>Intrude</b> has a <b>Guardian</b> in his field of vision, he tries to avoid it. </br>
	 * (If surrounded he "panics" and moves randomly). </br>
	 * - Otherwise, the <b>Intrude</b> moves randomly. </br>
	 * 
	 * @param intrude : the <b>Intrude</b> to move.
	 * @return the corresponding <b>Displacement</b>.
	 */
	public Displacement intrudeDisplacementCalculation(Intrude intrude) {
		if (intrude.getGuardiansSpot().size() == 0) { // deplacement random puisque aucun guardien a proximite
			return randomDisplacement();
		} 
		else { // eviter les intrus
			HashMap<Displacement, Boolean> safeZone = new HashMap<Displacement, Boolean>();
			ArrayList<Displacement> guardiansPositions = new ArrayList<Displacement>();

			for (Coordinate coordTemp : intrude.getGuardiansSpot()) {
				guardiansPositions.add(relativePosition(intrude.getPosition(), coordTemp));
			}

			if (guardiansPositions.contains(Displacement.NORTH)) {
				safeZone.put(Displacement.NORTH, false);
			} 
			else
				safeZone.put(Displacement.NORTH, true);

			if (guardiansPositions.contains(Displacement.SOUTH)) {
				safeZone.put(Displacement.SOUTH, false);
			} 
			else
				safeZone.put(Displacement.SOUTH, true);

			if (guardiansPositions.contains(Displacement.WEST)) {
				safeZone.put(Displacement.WEST, false);
			} 
			else
				safeZone.put(Displacement.WEST, true);

			if (guardiansPositions.contains(Displacement.EAST)) {
				safeZone.put(Displacement.EAST, false);
			} 
			else
				safeZone.put(Displacement.EAST, true);

			if (!(safeZone.get(Displacement.EAST)
					|| safeZone.get(Displacement.WEST)
					|| safeZone.get(Displacement.SOUTH) 
					|| safeZone.get(Displacement.NORTH))) {
				return randomDisplacement();
			} 
			else {
				if (safeZone.get(Displacement.EAST) && intrude.getPosition().getCoordX() < grid.getDimension() - 1)
					return Displacement.EAST;
				else if (safeZone.get(Displacement.WEST) && intrude.getPosition().getCoordX() > 0)
					return Displacement.WEST;
				else if (safeZone.get(Displacement.NORTH) && intrude.getPosition().getCoordY() > 0)
					return Displacement.NORTH;
				else if (safeZone.get(Displacement.SOUTH)	&& intrude.getPosition().getCoordY() < grid.getDimension() - 1)
					return Displacement.SOUTH;
				else
					return Displacement.NONE;
			}
		}
	}

	/** Returns the relative position of two <b> Coordinates </b>.
	 *  Used to determine the position of a  <b>Guardian </b> relative to an <b>Intrude</b>.  
	 *  
	 * @param coordSource : source <b>Coordinate</b>.
	 * @param coordDestination : destination <b>Coordinate</b>.
	 * @return the <b> Displacement</b> corresponding to the position of <b>coordDestination</b> relative to <b>coordSource </b>.
	 */
	public Displacement relativePosition(Coordinate coordSource, Coordinate coordDestination) {
		int sourceX = coordSource.getCoordX();
		int sourceY = coordSource.getCoordY();
		int destinationX = coordDestination.getCoordX();
		int destinationY = coordDestination.getCoordY();

		int distanceX = Math.abs(destinationX - sourceX);
		int distanceY = Math.abs(destinationY - sourceY);

		if (distanceX > distanceY) {
			if (destinationX > sourceX)
				return Displacement.EAST;
			else
				return Displacement.WEST;
		} 
		else {
			if (destinationY > sourceY)
				return Displacement.SOUTH;
			else
				return Displacement.NORTH;
		}
	}

	/** Determines the type of <b>Displacement</b> based on provided <b>Coordinates</b>. </br>
	 * <b>Displacement</b> from <b>coordSource</b> to <b>coordTest</b>.
	 * 
	 * @param coordSource : source <b>Coordinate</b>.
	 * @param coordTest : destination <b>Coordinate</b>.
	 * @return the <b> Displacement</b> corresponding to the movement from <b>coordSource</b> to <b>coordTest</b>.
	 */
	public Displacement displacementCalculation(Coordinate coordSource,	Coordinate coordTest) {
		int sourceX = coordSource.getCoordX();
		int sourceY = coordSource.getCoordY();
		int testX = coordTest.getCoordX();
		int testY = coordTest.getCoordY();

		if (testX == sourceX + 1)
			return Displacement.EAST;
		else if (testX == sourceX - 1)
			return Displacement.WEST;
		else if (testY == sourceY + 1)
			return Displacement.SOUTH;
		else if (testY == sourceY - 1)
			return Displacement.NORTH;
		return Displacement.NONE;
	}

	/** Returns a random <b>Displacement</b>.
	 * 
	 * @return a random <b>Displacement</b>.
	 */
	public Displacement randomDisplacement() {
		double direction = Math.random();
		if (direction > 0.8)
			return Displacement.NORTH;
		else if (direction > 0.6)
			return Displacement.SOUTH;
		else if (direction > 0.4)
			return Displacement.EAST;
		else if (direction > 0.2)
			return Displacement.WEST;
		else
			return Displacement.NONE;
	}


	/** Patrol mode of a <b>Guardian</b> : </br>
	 * - If the provided <b>Gardian</b> doesn't have a patrol route, he moves randomly.</br>
	 * - If the provided <b>Gardian</b> has a patrol route and is currently patrolling, he continues his patrol.</br>
	 * - If the provided <b>Gardian</b> has a patrol route, but isn't doing it, he moves toward the nearest patrol <b>Coordinate</b>.</br>
	 * 
	 * @param guard : the patrolling <b>Gardian</b>.
	 * @return the corresponding <b>Displacement</b>.
	 */
	public Displacement patrolMode(Guardian guard) {
		if(guard.getPatrolPosition() == -2) {
			//pas de coord de patrouille, deplacement aleatoire
			return randomDisplacement();
		}
		else if(guard.getPatrolPosition() != -1 && guard.getPatrol().get(guard.getPatrolPosition()).equals(guard.getPosition())) {
			//aller au point suivant
			guard.incrPatrolPosition();
			return displacementCalculation(guard.getPosition(), guard.getPatrol().get(guard.getPatrolPosition()));
		}
		else {
			//aller vers le point unique le plus proche
			ArrayList<Coordinate> patrolCoordinates = new ArrayList<Coordinate>(guard.getPatrol().values());
			ArrayList<Coordinate> routeTemp = new ArrayList<Coordinate>();
			ArrayList<Coordinate> routeFinal = new ArrayList<Coordinate>();
			Coordinate coordDestination = null;
			//tirage de la coord la plus proche
			for(Coordinate coord : patrolCoordinates) {
				if(patrolCoordinates.lastIndexOf(coord) == patrolCoordinates.indexOf(coord)) { //si passage unique
					routeTemp = routeCalculation(guard.getPosition(), coord, true);
					if (routeFinal.size() == 0 || (routeTemp.size()!=0 && routeTemp.size() < routeFinal.size())) {
						routeFinal = new ArrayList<Coordinate>(routeTemp);
						coordDestination = coord.copy();
					}
				}
			}

			if(coordDestination == null) {//passage 2 fois partout, pas de bol, go au debut de la patrouille
				if(areAdjacent(guard.getPosition(), guard.getPatrol().get(0)))
					guard.setPatrolPosition(0);
				return displacementCalculation(guard.getPosition(), routeFinal.get(1));
			}
			else { //aller a itineraire final (proche et unique) et si on y arrive, mettre la bonne valeur de positionPatrouille
				if(areAdjacent(guard.getPosition(), guard.getPatrol().get(patrolCoordinates.indexOf(coordDestination))))
					guard.setPatrolPosition(patrolCoordinates.indexOf(coordDestination));
				return displacementCalculation(guard.getPosition(), routeFinal.get(1));
			}	
		}
	}

	/** Calculates a field of view of a lenght of 5 (5 squares in straight line).
	 * 
	 * @param coordSource : position from which to calculate the field of view.
	 * @return a list of <b>Coordinates</b> composing the field of view.
	 */
	public ArrayList<Coordinate> visualFieldCalculation(Coordinate coordSource) {
		int champDeVision = 5;
		int coordX = coordSource.getCoordX();
		int coordY = coordSource.getCoordY();
		ArrayList<Coordinate> unsortedVisible = new ArrayList<Coordinate>();
		for (int i = -champDeVision; i <= champDeVision; i++) {
			for (int j = -champDeVision; j <= champDeVision; j++) {
				if (Math.abs(i) + Math.abs(j) < champDeVision + 3 
						&& coordX + i >= 0 && coordX + i < grid.getDimension()
						&& coordY + j >= 0 && coordY + j < grid.getDimension()) {
					unsortedVisible.addAll(drawSegment(coordSource, new Coordinate(coordX + i, coordY + j)));
				}
			}
		}
		unsortedVisible.add(coordSource);
		
		Set<Coordinate> mySet = new HashSet<Coordinate>(unsortedVisible);
		ArrayList<Coordinate> sortedVisible = new ArrayList<Coordinate>(mySet);
		return sortedVisible;
	}

	/** Bresenham's line algorithm. </ Br>
	 * Used to calculate the field of view.</br>
	 * 
	 * @param coordA : starting point of the segment.
	 * @param coordB : arrival point of the segment.
	 * @return a list of <b>Coordinates</b> composing the segment.
	 */
	public ArrayList<Coordinate> drawSegment(Coordinate coordA, Coordinate coordB) {
		// details des coordonnÃ©es
		int xA = coordA.getCoordX();
		int yA = coordA.getCoordY();
		int xB = coordB.getCoordX();
		int yB = coordB.getCoordY();
		// tockage des coordonnÃ©es intermediaires
		ArrayList<Coordinate> segmentAToB = new ArrayList<Coordinate>();

		int dx, dy, i, xinc, yinc, cumul, x, y;

		x = xA;
		y = yA;
		dx = xB - xA;
		dy = yB - yA;
		xinc = (dx > 0) ? 1 : -1;
		yinc = (dy > 0) ? 1 : -1;
		dx = Math.abs(dx);
		dy = Math.abs(dy);

		if (dx > dy) {
			cumul = dx / 2;
			for (i = 1; i <= dx; i++) {
				if (grid.isOpaque(new Coordinate(x, y)))
					break;
				x = x + xinc;
				cumul += dy;
				if (cumul >= dx) {
					cumul = cumul - dx;
					y = y + yinc;
				}
				segmentAToB.add(new Coordinate(x, y));
			}
		}
		else {
			cumul = dy / 2;
			for (i = 1; i <= dy; i++) {
				if (grid.isOpaque(new Coordinate(x, y)))
					break;
				y = y + yinc;
				cumul = cumul + dx;
				if (cumul >= dy) {
					cumul -= dy;
					x = x + xinc;
				}
				segmentAToB.add(new Coordinate(x, y));
			}
		}
		return segmentAToB;
	}

	/** Calculates patrol routes (depending on reachable <b>Coordinates</b> by <b>Guardian</b>) to see all the <b>Grid</b>.</br>
	 *  A patrol route is then assigned to each <b>Guardian</b> (If several <b>Guardians</b> have access to the same <b>Coordinates</b>, their patrol will be the same).
	 * 
	 * @param guardians : a list of <b>Guardians</b> in order to directly assign their patrol route.
	 * @return  a list containing all the <b>Coordinates</b> of all the patrols routes. (Debug)
	 */
	public ArrayList<Coordinate> patrolRouteCalculation(ArrayList<Guardian> guardians) {
		//long debut = System.currentTimeMillis();
		int dimension = grid.getDimension();
		//ArrayList de coordonnees pour l'itineraire de patrouille
		ArrayList<Coordinate> validCoordinates = new ArrayList<Coordinate>();
		//ArrayList de coordonnees pour l'itineraire de patrouille
		//HashMap de coordonnees, et de l'ArrayList contenant le champ de vision a partir de cette coordoneee
		HashMap<Coordinate, ArrayList<Coordinate>> visible = new HashMap<Coordinate, ArrayList<Coordinate>>();

		//init ArrayList, tableau et HashMap
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				Coordinate coordTemp = new Coordinate(i, j);
				//				System.out.println(grid);
				if(grid.isValid(coordTemp) && grid.isDirectlyAccessible(coordTemp)) {
					validCoordinates.add(coordTemp);
					visible.put(coordTemp, visualFieldCalculation(coordTemp));
				}
			}
		}

		//Remplissage d'une HashMap avec les gardians pour Cle, et une ArrayList de coordonnees atteignables en Valeur
		HashMap<Guardian, ArrayList<Coordinate>> guardianToCoordinates = new HashMap<Guardian, ArrayList<Coordinate>>();
		for(Guardian guard : guardians) {
			ArrayList<Coordinate> accessibleCoordinatesGuardian = new ArrayList<Coordinate>();
			for(Coordinate coord : validCoordinates) {
				ArrayList<Coordinate> itineraireTemp = routeCalculation(guard.getPosition(), coord, false);
				if(itineraireTemp.size() > 2 || areAdjacent(guard.getPosition(), coord)) //Coord accessible
					accessibleCoordinatesGuardian.add(coord);
			}
			accessibleCoordinatesGuardian.add(guard.getPosition());
			Collections.sort(accessibleCoordinatesGuardian, new Tri.SortByCoordinate());
			guardianToCoordinates.put(guard, accessibleCoordinatesGuardian);
		}
		//		System.out.println(guardianVersCoordinates);
		//		System.out.println();
		//		System.out.println();


		//Fusion en ArrayList si plusieurs gardiens ont les mêmes coordonnees atteignables
		HashMap<ArrayList<Coordinate>, ArrayList<Guardian>> coordinatesToGuardians = new HashMap<ArrayList<Coordinate>, ArrayList<Guardian>>();
		for(Guardian guard : guardians) {
			ArrayList<Guardian> guardiansCoord = coordinatesToGuardians.get(guardianToCoordinates.get(guard));
			if(guardiansCoord == null) {
				guardiansCoord = new ArrayList<Guardian>();
			}
			if(!guardiansCoord.contains(guard))
				guardiansCoord.add(guard);
			coordinatesToGuardians.put(guardianToCoordinates.get(guard), guardiansCoord);
		}
		//		System.out.println(coordinatesVersGuardian);
		//		A ce moment, coordinatesVersGuardian contient des ArrayList de coordonnees atteignables comme clé, 
		//		et des ArrayList des guardiens qui peuvent les atteindre comme valeur

		//HashMap inverse de CoordinatesVersGuardians
		HashMap<ArrayList<Guardian>, ArrayList<Coordinate>> guardiansToCoordinates = new HashMap<ArrayList<Guardian>, ArrayList<Coordinate>>();
		for(ArrayList<Coordinate> alCoord : coordinatesToGuardians.keySet()){
			guardiansToCoordinates.put(coordinatesToGuardians.get(alCoord), alCoord);
		}


		//initialisation des coordonnes a visiter pour effectuer la patrouille (checkpoint)
		//ne represente pas l'itineraire
		//ne garde que quelques coordonnes cles
		ArrayList<Coordinate> globalPatrolCoordinates = new ArrayList<Coordinate>(validCoordinates);

		//calculer si un ensemble d'ArrayList de coordonnees ne contiendrait pas par hasard toutes les coordonnees visibles depuis une certaine coordonnee
		//calculer si le fait de supprimer une coordonnee enleve des cases visibles a l'ArrayList
		for (int i = 0; i < globalPatrolCoordinates.size(); i++) {
			ArrayList<Coordinate> coordinatesMinusOne = new ArrayList<Coordinate>();
			ArrayList<Coordinate> unsortedVisibleCoordinatesMinusOne = new ArrayList<Coordinate>();
			ArrayList<Coordinate> sortedVisibleCoordinatesMinusOne = new ArrayList<Coordinate>();

			for (Coordinate coordTest : globalPatrolCoordinates) {
				coordinatesMinusOne.add(coordTest);
			}

			Coordinate coordReference = globalPatrolCoordinates.get(i);
			coordinatesMinusOne.remove(coordReference);
			for(Coordinate coordTemp : coordinatesMinusOne) {
				unsortedVisibleCoordinatesMinusOne.addAll(visible.get(coordTemp));
			}

			Set<Coordinate> mySet = new HashSet<Coordinate>(unsortedVisibleCoordinatesMinusOne);
			sortedVisibleCoordinatesMinusOne.addAll(mySet);

			if(sortedVisibleCoordinatesMinusOne.containsAll(validCoordinates)) {
				globalPatrolCoordinates.remove(coordReference);
				i--;
			}
		}

		ArrayList<Coordinate> allPatrolsRoutes = new ArrayList<Coordinate>();

		//debut boucle
		for(ArrayList<Guardian> alGuardian : coordinatesToGuardians.values()) {
			//On ne garde que les coordonnees atteignables par le groupe de gardiens
			ArrayList<Coordinate> groupPatrolCoordinates = new ArrayList<Coordinate>(globalPatrolCoordinates);

			//			for(Coordinates coord : coordinatesPatrouilleGroupe) {
			//				if(!guardiansVersCoordinates.get(alGuardian).contains(coord))
			//					coordinatesPatrouilleGroupe.remove(coord);
			//			}
			for(int i = 0; i < groupPatrolCoordinates.size(); i++) {
				Coordinate coord = groupPatrolCoordinates.get(i);
				if(!guardiansToCoordinates.get(alGuardian).contains(coord)) {
					groupPatrolCoordinates.remove(coord);
					i--;
				}
			}

			TreeMap<Integer, Coordinate> patrolRoute = new TreeMap<Integer, Coordinate>();

			//Si aucun checkpoint accessible
			if(groupPatrolCoordinates.size() == 0) {
				for(Guardian guardTemp : alGuardian) {
					guardTemp.setPatrolPosition(-2);
				}
			}
			else {

				ArrayList<Coordinate> routeFinal = new ArrayList<Coordinate>();
				ArrayList<Coordinate> routeTemp = new ArrayList<Coordinate>();

				Coordinate coordSource = groupPatrolCoordinates.get(0);
				Coordinate coordSourceTemp = coordSource;
				Coordinate coordTemp = null;

				int index = 0;

				while(groupPatrolCoordinates.size() > 0) {
					groupPatrolCoordinates.remove(coordSourceTemp);
					routeFinal.clear();
					routeTemp.clear();

					for (Coordinate coordDestinationTemp : groupPatrolCoordinates) {
						routeTemp = routeCalculation(coordSourceTemp, coordDestinationTemp, false);
						//System.out.println("Itineraire " + coordSourceTemp + " vers " + coordDestinationTemp + " : " + itineraireTemp);
						if (routeFinal.size() == 0 || (routeTemp.size()!=0 && routeTemp.size() < routeFinal.size())) {
							routeFinal = new ArrayList<Coordinate>(routeTemp);
							coordTemp = coordDestinationTemp;
						}
					}
					//System.out.println(itineraireFinal);
					coordSourceTemp = coordTemp;
					for(int i=0; i<routeFinal.size()-1; i++) {
						patrolRoute.put(index, routeFinal.get(i));
						index++;
					}
				}

				//Ajout de l'itineraire pour aller e la fin de la patrouille vers le debut
				routeFinal = routeCalculation(coordTemp, coordSource, false);
				for(int i=0; i<routeFinal.size()-1; i++) {
					patrolRoute.put(index, routeFinal.get(i));
					index++;
				}

				//affectation de la patrouille a tous les gardiens, a modifier 
				for(Guardian guardTemp : alGuardian) {
					guardTemp.setPatrol(patrolRoute);
					guardTemp.setReachableCoordinates(guardiansToCoordinates.get(alGuardian));
				}
				allPatrolsRoutes.addAll(patrolRoute.values());


			}
			//fin boucle
		}
		//entre 100 et 1000ms en fonction de la taille (complexite lineaire)
		//System.out.println(System.currentTimeMillis() - debut);
		//System.out.println(itinerairePatrouille);

		return allPatrolsRoutes;
	}
	
	/**  Calculates patrol's <b>Coordinates</b>.</br>
	 * 
	 * @param guardians : a list of <b>Guardians</b> to share the patrol.
	 * @return a list containing the <b>Coordinates</b> checkpoints of the patrol. (Debug)
	 */
	public ArrayList<Coordinate> calculCoordinatesPatrouille(ArrayList<Guardian> guardians) {		
		//long debut = System.currentTimeMillis();
		int dimension = grid.getDimension();
		//ArrayList de coordonnees pour l'itineraire de patrouille
		ArrayList<Coordinate> validCoordinates = new ArrayList<Coordinate>();
		//ArrayList de coordonnees pour l'itineraire de patrouille
		//HashMap de coordonnees, et de l'ArrayList contenant le champ de vision a partir de cette coordoneee
		HashMap<Coordinate, ArrayList<Coordinate>> visible = new HashMap<Coordinate, ArrayList<Coordinate>>();

		//init ArrayList, tableau et HashMap
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				Coordinate coordTemp = new Coordinate(i, j);
				//				System.out.println(grid);
				if(grid.isValid(coordTemp) && grid.isDirectlyAccessible(coordTemp)) {
					validCoordinates.add(coordTemp);
					visible.put(coordTemp, visualFieldCalculation(coordTemp));
				}
			}
		}

		//Remplissage d'une HashMap avec les gardians pour Cle, et une ArrayList de coordonnees atteignables en Valeur
		HashMap<Guardian, ArrayList<Coordinate>> guardianToCoordinates = new HashMap<Guardian, ArrayList<Coordinate>>();
		for(Guardian guard : guardians) {
			ArrayList<Coordinate> accessibleCoordinatesGuardian = new ArrayList<Coordinate>();
			for(Coordinate coord : validCoordinates) {
				ArrayList<Coordinate> itineraireTemp = routeCalculation(guard.getPosition(), coord, false);
				if(itineraireTemp.size() > 2 || areAdjacent(guard.getPosition(), coord)) //Coord accessible
					accessibleCoordinatesGuardian.add(coord);
			}
			accessibleCoordinatesGuardian.add(guard.getPosition());
			Collections.sort(accessibleCoordinatesGuardian, new Tri.SortByCoordinate());
			guardianToCoordinates.put(guard, accessibleCoordinatesGuardian);
		}
		//		System.out.println(guardianVersCoordinates);
		//		System.out.println();
		//		System.out.println();


		//Fusion en ArrayList si plusieurs gardiens ont les mêmes coordonnees atteignables
		HashMap<ArrayList<Coordinate>, ArrayList<Guardian>> coordinatesToGuardians = new HashMap<ArrayList<Coordinate>, ArrayList<Guardian>>();
		for(Guardian guard : guardians) {
			ArrayList<Guardian> guardiansCoord = coordinatesToGuardians.get(guardianToCoordinates.get(guard));
			if(guardiansCoord == null) {
				guardiansCoord = new ArrayList<Guardian>();
			}
			if(!guardiansCoord.contains(guard))
				guardiansCoord.add(guard);
			coordinatesToGuardians.put(guardianToCoordinates.get(guard), guardiansCoord);
		}
		//		System.out.println(coordinatesVersGuardian);
		//		A ce moment, coordinatesVersGuardian contient des ArrayList de coordonnees atteignables comme clé, 
		//		et des ArrayList des guardiens qui peuvent les atteindre comme valeur

		//HashMap inverse de CoordinatesVersGuardians
		HashMap<ArrayList<Guardian>, ArrayList<Coordinate>> guardiansToCoordinates = new HashMap<ArrayList<Guardian>, ArrayList<Coordinate>>();
		for(ArrayList<Coordinate> alCoord : coordinatesToGuardians.keySet()){
			guardiansToCoordinates.put(coordinatesToGuardians.get(alCoord), alCoord);
		}


		//initialisation des coordonnes a visiter pour effectuer la patrouille (checkpoint)
		//ne represente pas l'itineraire
		//ne garde que quelques coordonnes cles
		ArrayList<Coordinate> patrolCoordinates = new ArrayList<Coordinate>(validCoordinates);

		//calculer si un ensemble d'ArrayList de coordonnees ne contiendrait pas par hasard toutes les coordonnees visibles depuis une certaine coordonnee
		//calculer si le fait de supprimer une coordonnee enleve des cases visibles a l'ArrayList
		for (int i = 0; i < patrolCoordinates.size(); i++) {
			ArrayList<Coordinate> coordinatesMinusOne = new ArrayList<Coordinate>();
			ArrayList<Coordinate> unsortedVisibleCoordinatesMinusOne = new ArrayList<Coordinate>();
			ArrayList<Coordinate> sortedVisibleCoordinatesMinusOne = new ArrayList<Coordinate>();

			for (Coordinate coordTest : patrolCoordinates) {
				coordinatesMinusOne.add(coordTest);
			}

			Coordinate coordReference = patrolCoordinates.get(i);
			coordinatesMinusOne.remove(coordReference);
			for(Coordinate coordTemp : coordinatesMinusOne) {
				unsortedVisibleCoordinatesMinusOne.addAll(visible.get(coordTemp));
			}

			Set<Coordinate> mySet = new HashSet<Coordinate>(unsortedVisibleCoordinatesMinusOne);
			sortedVisibleCoordinatesMinusOne.addAll(mySet);

			if(sortedVisibleCoordinatesMinusOne.containsAll(validCoordinates)) {
				patrolCoordinates.remove(coordReference);
				i--;
			}
		}

		return patrolCoordinates;
	}
}