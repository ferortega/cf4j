package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import es.upm.etsisi.cf4j.data.types.Pair;
import es.upm.etsisi.cf4j.data.types.SortedArrayList;

/**
 * <p>Defines an item. An item is composed by:</p>
 * <ul>
 *  <li>Item code</li>
 *  <li>Array of users who have rated the item</li>
 *  <li>Array of ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega, Jesús Mayor
 */
public class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 20200314L;

	//Stored metrics
	protected double min = Double.MAX_VALUE;
	protected double max = Double.MIN_VALUE;
	protected double average = 0.0;
	protected int totalRatings = 0;

	/**
	 * Item code
	 */
	protected String itemCode;
	
	/**
	 * Map of the item
	 */
	//protected DataBank dataBank;

	/**
	 * Users that have rated this item
	 */
	protected SortedArrayList<Pair<String, Double>> usersRatings;

	/**
	 * Creates a new instance of an item. This constructor should not be users by developers.
	 * @param itemCode Item code
	 */
	public Item (String itemCode) {
		this.itemCode = itemCode;
		//this.dataBank = new DataBank();
		this.usersRatings = new SortedArrayList<Pair<String, Double>>();
	}

	//public DataBank getDataBank(){
	//	return dataBank;
	//}

	/**
	 * Return the item code.
	 * @return Item code
	 */
	public String getItemCode() {
		return this.itemCode;
	}
	
	/**
	 * Returns the user code at local index position.
	 * @param userLocalIndex Index.
	 * @return User code at localIndex.
	 */
	public String getUser(int userLocalIndex) {
		if (userLocalIndex < 0 || userLocalIndex > this.usersRatings.size())
			return null;

		return this.usersRatings.get(userLocalIndex).key;
	}

	/**
	 * Returns the rating at index position. 
	 * @param userLocalIndex Index.
	 * @return Rating at localIndex.
	 */
	public Double getRating(int userLocalIndex) {
		if (userLocalIndex < 0 || userLocalIndex > this.usersRatings.size())
			return null;

		return this.usersRatings.get(userLocalIndex).value;
	}
	
	/**
	 * Get the index of an user code at the user's item array.
	 * @param userCode User code
	 * @return User local index in the user's item array if the user has rated the item or -1 if not
	 */
	public int findUserRating (String userCode) {
		//We need create a aux Pair to get the real one localIndex
		Pair<String,Double> aux = new Pair<String, Double>(userCode,0.0);
		return usersRatings.find(aux);
	}
	
	/**
	 * Get the number of ratings that the item have received.
	 * @return Number of ratings received
	 */
	public int getNumberOfRatings () {
		return this.usersRatings.size();
	}

	/**
	 * Add/Modify a new rating to the item, associated to a user.
	 * @param userCode userCode which identify the specific user.
	 * @param rating rated value by user, refering this item.
	 */
	public void addRating(String userCode, double rating){
		if (this.usersRatings.add(new Pair<String, Double>(userCode, rating)))
			totalRatings++;

		min = Math.min(rating, min);
		max = Math.max(rating, max);
		average = (totalRatings <= 1) ? rating : ((average * (totalRatings-1)) + rating) / totalRatings;
	}

	public double getMin(){ return min; }
	public double getMax(){ return max; }
	public double getAverage(){ return average; }

	/**
	 * This methods implements the Comparable interface. It allows to be ordered by dynamicSortedArray.
	 * @param o Other item
	 * @return 1 0 or -1. If the other element si greater, equal or lesser.
	 */
	@Override
	public int compareTo(Item o) {
		return this.itemCode.compareTo(o.itemCode);
	}
}
