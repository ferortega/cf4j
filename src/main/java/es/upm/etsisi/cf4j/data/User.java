package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import es.upm.etsisi.cf4j.data.types.Pair;
import es.upm.etsisi.cf4j.data.types.SortedArrayList;

/**
 * <p>Defines an user. An user is composed by:</p>
 * <ul>
 *  <li>User code</li>
 *  <li>Array of items who have rated the user</li>
 *  <li>Array of ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega, Jesús Mayor
 */
public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = 20200314L;

	//Stored metrics
	protected double min = Double.MAX_VALUE;
	protected double max = Double.MIN_VALUE;
	protected double average = 0.0;
	protected int totalRatings = 0;

	/**
	 * User code
	 */
	protected String userCode;
	
	/**
	 * Map of the user
	 */
	//protected DataBank dataBank;

	/**
	 * Items rated by the user
	 */
	protected SortedArrayList<Pair<String, Double>> itemsRatings;

	/**
	 * Creates a new instance of an user. This constructor should not be used by developers.
	 * @param userCode User code
	 */
	public User (String userCode) {
		this.userCode = userCode;
		//this.dataBank = new DataBank();
		this.itemsRatings = new SortedArrayList<Pair<String, Double>>();
	}

	//public DataBank getDataBank(){
	//	return dataBank;
	//}

	/**
	 * Returns the user code.
	 * @return User code
	 */
	public String getUserCode() {
		return this.userCode;
	}

	/**
	 * Returns the item code at local index position.
	 * @param itemLocalIndex Index.
	 * @return Item code at localIndex.
	 */
	public String getItem(int itemLocalIndex) {
		if (itemLocalIndex < 0 || itemLocalIndex > this.itemsRatings.size())
			return null;

		return this.itemsRatings.get(itemLocalIndex).key;
	}

	/**
	 * Returns the rating at index position. 
	 * @param itemLocalIndex Index.
	 * @return Rating at localIndex.
	 */
	public Double getRating(int itemLocalIndex) {
		if (itemLocalIndex < 0 || itemLocalIndex > this.itemsRatings.size())
			return null;

		return this.itemsRatings.get(itemLocalIndex).value;
	}
	
	/**
	 * Get the index of an item code at the items array of the user.
	 * @param itemCode Item code
	 * @return Item index if the user has rated the item or -1 if not
	 */
	public int findItemRating (String itemCode) {
		//We need create a aux Pair to get the real one localIndex
		Pair<String,Double> aux = new Pair<String, Double>(itemCode,0.0);
		return itemsRatings.find(aux);
	}

	/**
	 * Get the number of ratings that the user have made.
	 * @return Number of ratings
	 */
	public int getNumberOfRatings () {
		return this.itemsRatings.size();
	}

	/**
	 * Add/Modify a new rating to the user, associated to a item.
	 * @param itemCode itemCode which identify the specific item.
	 * @param rating rated value by user, refering this item.
	 */
	public void addRating(String itemCode, double rating){
		if (this.itemsRatings.add(new Pair<String, Double>(itemCode, rating)))
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
	 * @param o Other user
	 * @return 1 0 or -1. If the other element si greater, equal or lesser.
	 */
	@Override
	public int compareTo(User o) {
		return this.userCode.compareTo(o.userCode);
	}
}
