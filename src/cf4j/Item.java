package cf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cf4j.utils.Methods;

/**
 * <p>Defines an item. An item is composed by:</p>
 * <ul>
 *  <li>Item code</li>
 *  <li>Item index in the items array</li>
 *  <li>A map where we can save any type of information</li>
 *  <li>Array of users who have rated the item</li>
 *  <li>Array of ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega
 */
public class Item implements Serializable {

	private static final long serialVersionUID = 20171018L;

	/**
	 * Item code
	 */
	protected int itemCode;
	
	/**
	 * Item index
	 */
	protected int itemIndex;
	
	/**
	 * Map of the item
	 */
	protected Map <String, Object> map;
	
	/**
	 * Users that have rated this item
	 */
	protected int [] users;
	
	/**
	 * Ratings of the users
	 */
	protected double [] ratings;
	
	/**
	 * Rating average of the item
	 */
	protected double ratingAverage;
	
	/**
	 * Standard deviation of this item
	 */
	protected double ratingStandardDeviation;
	
	/**
	 * Creates a new instance of an item. This constructor should not be users by developers.
	 * @param itemCode Item code
	 * @param itemIndex Item index
	 * @param users Users that have rated this item
	 * @param ratings Ratings of the users
	 */
	public Item (int itemCode, int itemIndex, int [] users, double [] ratings) {
		this.itemCode = itemCode;
		this.itemIndex = itemIndex;
		this.map = new HashMap<String, Object>();
		this.users = users;
		this.ratings = ratings;
		this.ratingAverage = Methods.arrayAverage(ratings);
		this.ratingStandardDeviation = Methods.arrayStandardDeviation(ratings);
	}
	
	/**
	 * Write a data in the item map.
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
	 * Average of the ratings received by the item.
	 * @return Rating average
	 */
	public double getRatingAverage() {
		return this.ratingAverage;
	}

	/**
	 * Standard deviation of the ratings received by the item.
	 * @return Rating standard deviation
	 */
	public double getRatingStandardDeviation() {
		return this.ratingStandardDeviation;
	}

	/**
	 * Return the item code.
	 * @return Item code
	 */
	public int getItemCode() {
		return this.itemCode;
	}
	
	/**
	 * Return the item index.
	 * @return Item index
	 */
	public int getItemIndex() {
		return this.itemIndex;
	}
	
	/**
	 * Get the map of the item. It is recommended using put(...) and get(...) instead of
	 * this method.
	 * @return Map of the item
	 */
	public Map<String, Object> getMap() {
		return map;
	}
	
	/**
	 * Get the users that have rated the item.
	 * @return Test users codes sorted from low to high. 
	 */
	public int [] getUsers() {
		return this.users;
	}
	
	/**
	 * Returns the user code at index position. 
	 * @param index Index.
	 * @return User code at index. 
	 */
	public int getUserAt(int index) {
		return this.getUsers()[index];
	}

	/**
	 * Get the ratings of the users to the item. The indexes of the array overlaps
	 * with indexes of the getUsers() array.
	 * @return Training users ratings
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
	 * Get the index of an user code at the user's item array.
	 * @param user_code User code
	 * @return User index in the user's item array if the user has rated the item or -1 if not
	 */
	public int getUserIndex (int user_code) {
		return Methods.getIndex(this.users, user_code);
	}
	
	/**
	 * Get the number of ratings that the item have received.
	 * @return Number of ratings received
	 */
	public int getNumberOfRatings () {
		return this.ratings.length;
	}
}
