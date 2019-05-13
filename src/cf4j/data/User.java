package cf4j.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cf4j.data.types.DynamicArray;
import cf4j.data.types.DynamicSortedArray;
import cf4j.utils.Methods;

/**
 * <p>Defines an user. An user is composed by:</p>
 * <ul>
 *  <li>User code</li>
 *  <li>User index in the users array</li>
 *  <li>A map where we can save any type of information</li>
 *  <li>Array of items that the user have rated</li>
 *  <li>Array of ratings that the user have made</li>
 * </ul>
 * @author Fernando Ortega
 */
public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = 20171018L;

	/**
	 * User code
	 */
	protected String userCode;
	
	/**
	 * Map of the user
	 */
	protected DataBank storedData;

	/**
	 * Items rated by the user
	 */
	protected DynamicSortedArray<String> items;

	/**
	 * Ratings of the user to the items
	 */
	protected DynamicArray<Double> ratings;
	
	/**
	 * Rating average of the user ratings
	 */
	protected double ratingAverage;
	
	/**
	 * Standard deviation of the user ratings
	 */
	protected double ratingStandardDeviation;

	/**
	 * Creates a new instance of an user. This constructor should not be used by developers.
	 * @param userCode User code
	 */
	public User (String userCode) {
		this.userCode = userCode;
		this.storedData = new DataBank();
		this.items = new DynamicSortedArray<String>();
		this.ratings = new DynamicArray<Double>();
		//TODO: Metrics?
		//this.ratingAverage = Methods.arrayAverage(ratings);
		//this.ratingStandardDeviation = Methods.arrayStandardDeviation(ratings);
	}

	public DataBank GetStoredData (){
		return storedData;
	}

	/**
	 * Average of the user ratings.
	 * @return Rating average
	 */
	public double getRatingAverage() {
		return this.ratingAverage;
	}

	/**
	 * Standard deviation of the user ratings.
	 * @return Rating standard deviation
	 */
	public double getRatingStandardDeviation() {
		return this.ratingStandardDeviation;
	}

	/**
	 * Returns the user code.
	 * @return User code
	 */
	public String getUserCode() {
		return this.userCode;
	}

	/**
	 * Returns the items codes rated by the user. 
	 * @return Items codes sorted from low to high. 
	 */
	public DynamicSortedArray<String> getItems() {
		return this.items;
	}
	
	/**
	 * Returns the item code at index position. 
	 * @param index Index.
	 * @return Item code at index. 
	 */
	public String getItemAt(int index) {
		return this.getItems().get(index);
	}

	/**
	 * Returns the ratings given by the user to the items. The indexes of the 
	 * array overlaps with indexes of the getItems() array.
	 * @return Items ratings.
	 */
	public DynamicArray<Double> getRatings() {
		return this.ratings;
	}
	
	/**
	 * Returns the rating at index position. 
	 * @param index Index.
	 * @return Rating at index. 
	 */
	public double getRatingAt(int index) {
		return this.getRatings().get(index);
	}
	
	/**
	 * Get the index of an item code at the items array of the user.
	 * @param item_code Item code
	 * @return Item index if the user has rated the item or -1 if not
	 */
	public int getItemIndex (String item_code) {
		return items.get(item_code);
	}

	/**
	 * Get the number of ratings that the user have made.
	 * @return Number of ratings
	 */
	public int getNumberOfRatings () {
		return this.ratings.size();
	}

	/**
	 * Add/Modify a new rating to the user, associated to a item.
	 * @param itemCode itemCode which identify the specific item.
	 * @param rating rated value by user, refering this item.
	 */
	public void addRating(String itemCode, double rating){
		this.ratings.add(this.items.add(itemCode), new Double(rating));
	}

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
