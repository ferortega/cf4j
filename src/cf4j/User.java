package cf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
public class User implements Serializable {

	private static final long serialVersionUID = 20171018L;

	/**
	 * User code
	 */
	protected int userCode;
	
	/**
	 * User index
	 */
	protected int itemIndex;
	
	/**
	 * Map of the user
	 */
	private Map <String, Object> map;
	
	/**
	 * Items rated by the user
	 */
	protected int [] items;
	
	/**
	 * Ratings of the user to the items
	 */
	protected double [] ratings;
	
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
	 * @param userIndex User index
	 * @param items Items that the user have rated
	 * @param ratings Ratings of the user to the items
	 */
	public User (int userCode, int userIndex, int [] items, double [] ratings) {
		this.userCode = userCode;
		this.itemIndex = userIndex;
		this.map = new HashMap<String, Object>();
		this.items = items;
		this.ratings = ratings;
		this.ratingAverage = Methods.arrayAverage(ratings);
		this.ratingStandardDeviation = Methods.arrayStandardDeviation(ratings);
	}
	
	/**
	 * Write a data in the user map.
	 * @param key Key associated to the value
	 * @param value Value to be written in the map
	 * @return Previously value of the key if exists or null
	 */
	public synchronized Object put (String key, Object value) {		
		return map.put(key, value);
	}
	
	/**
	 * Retrieves a value from a key.
	 * @param key Key of the saved object
	 * @return The value associated to the key if exists or null
	 */
	public synchronized Object get(String key) {
		return map.get(key);
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
	public int getUserCode() {
		return this.userCode;
	}
	
	/**
	 * Return the user index.
	 * @return User index
	 */
	public int getUserIndex() {
		return this.itemIndex;
	}

	/**
	 * Get the map of the user. It is recommended using put(...) and get(...) instead of
	 * this method.
	 * @return Map of the user
	 */
	public Map<String, Object> getMap() {
		return map;
	}

	/**
	 * Returns the items codes rated by the user. 
	 * @return Items codes sorted from low to high. 
	 */
	public int[] getItems() {
		return this.items;
	}
	
	/**
	 * Returns the item code at index position. 
	 * @param index Index.
	 * @return Item code at index. 
	 */
	public int getItemAt(int index) {
		return this.getItems()[index];
	}

	/**
	 * Returns the ratings given by the user to the items. The indexes of the 
	 * array overlaps with indexes of the getItems() array.
	 * @return Items ratings.
	 */
	public double [] getRatings() {
		return this.ratings;
	}
	
	/**
	 * Returns the rating at index position. 
	 * @param index Index.
	 * @return Rating at index. 
	 */
	public double getRatingAt(int index) {
		return this.getRatings()[index];
	}
	
	/**
	 * Get the index of an item code at the items array of the user.
	 * @param item_code Item code
	 * @return Item index if the user has rated the item or -1 if not
	 */
	public int getItemIndex (int item_code) {
		return Methods.getIndex(this.items, item_code);
	}

	/**
	 * Get the number of ratings that the user have made.
	 * @return Number of ratings
	 */
	public int getNumberOfRatings () {
		return this.ratings.length;
	}
}
