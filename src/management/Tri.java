package management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import environnement.Coordinate;

/** Allows to sort a <b>Map</b> list (using personalized Comparator) depending to : </br>
 *  - their name </br>
 *  - their creation date</br>
 *  - their number or turns</br>
 *  - their duration</br>
 *  </br>
 *  
 *  Also includes a Comparator to sort <b>Coordinate</b> list.
 */
public class Tri {
	/** Sorts the specified list according to the order induced by the specified comparator (sortByName).
	 * 
	 * @param unsortedList : the list to be sorted
	 * @param way : reverse order or not
	 * @return the sorted list
	 */
	public static ArrayList<Map> sortByName(ArrayList<Map> unsortedList, boolean way) {
		ArrayList<Map> sortedList = new ArrayList<Map>(unsortedList);
		if(way)
			Collections.sort(sortedList, new SortByName());
		else
			Collections.sort(sortedList, Collections.reverseOrder(new SortByName()));
		return sortedList;
	}

	/** Sorts the specified list according to the order induced by the specified comparator (sortByDate).
	 * 
	 * @param unsortedList : the list to be sorted
	 * @param way : reverse order or not
	 * @return the sorted list
	 */
	public static ArrayList<Map> sortByDate(ArrayList<Map> unsortedList, boolean way) {
		ArrayList<Map> sortedList = new ArrayList<Map>(unsortedList);
		if(way)
			Collections.sort(sortedList, new SortByDate());
		else
			Collections.sort(sortedList, Collections.reverseOrder(new SortByDate()));
		return sortedList;
	}

	/** Sorts the specified list according to the order induced by the specified comparator (sortByDuration).
	 * 
	 * @param unsortedList : the list to be sorted
	 * @param way : reverse order or not
	 * @return the sorted list
	 */
	public static ArrayList<Map> sortByDuration(ArrayList<Map> unsortedList, boolean way) {
		ArrayList<Map> sortedList = new ArrayList<Map>(unsortedList);
		if(way)
			Collections.sort(sortedList, new SortByDuration());
		else
			Collections.sort(sortedList, Collections.reverseOrder(new SortByDuration()));
		return sortedList;
	}

	/** Sorts the specified list according to the order induced by the specified comparator (sortByTurnsNumber).
	 * 
	 * @param unsortedList : the list to be sorted
	 * @param way : reverse order or not
	 * @return the sorted list
	 */
	public static ArrayList<Map> sortByTurnsNumber(ArrayList<Map> unsortedList, boolean way) {
		ArrayList<Map> sortedList = new ArrayList<Map>(unsortedList);
		if(way)
			Collections.sort(sortedList, new SortByTurnsNumber());
		else
			Collections.sort(sortedList, Collections.reverseOrder(new SortByTurnsNumber()));
		return sortedList;
	}


	/** Personalized class implementing a <b>Coordinate</b> Comparator.</br>
	 * Compares two <b>Coordinate</b> depending on their hashCode() values.
	 */
	static class SortByCoordinate implements Comparator<Coordinate> {
		@Override
		public int compare(Coordinate coord1, Coordinate coord2) {
			return coord1.hashCode() - coord2.hashCode();
		}
	}
	
	/** Personalized class implementing a <b>Map</b> Comparator.</br>
	 * Compares two <b>Map</b> depending on their name.
	 * 
	 */
	static class SortByName implements Comparator<Map> {
		@Override
		public int compare(Map map1, Map map2) {
			return map1.getName().compareTo(map2.getName());
		}
	}
	
	/** Personalized class implementing a <b>Map</b> Comparator.</br>
	 * Compares two <b>Map</b> depending on their duration.
	 */
	static class SortByDuration implements Comparator<Map> {
		@Override
		public int compare(Map map1, Map map2) {
			return (int) (map1.getDuration() - map2.getDuration());
		}
	}
	
	/** Personalized class implementing a <b>Map</b> Comparator.</br>
	 * Compares two <b>Map</b> depending on their number of turns.
	 */
	static class SortByTurnsNumber implements Comparator<Map> {
		@Override
		public int compare(Map map1, Map map2) {
			return map1.getNumberOfTurns() - map2.getNumberOfTurns();
		}
	}
	
	/** Personalized class implementing a <b>Map</b> Comparator.</br>
	 * Compares two <b>Map</b> depending on their creation date.
	 */
	static class SortByDate implements Comparator<Map> {
		@Override
		public int compare(Map map1, Map map2) {
			return map1.getCreationDate().compareTo(map2.getCreationDate());
		}
	}
}