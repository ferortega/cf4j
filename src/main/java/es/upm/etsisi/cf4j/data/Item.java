package cf4j.data;

import java.io.Serializable;
import java.util.ArrayList;

import cf4j.data.types.DynamicArray;

/**
 * <p>Defines an item. An item is composed by:</p>
 * <ul>
 *  <li>Item code</li>
 *  <li>A databank where we can save any type of information</li>
 *  <li>Array of users who have rated the item</li>
 *  <li>Array of ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega, Jesús Mayor
 */
public class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 20190518L;

	public static final String AVERAGERATING_KEY = "average_rating";
	public static final String STANDARDDEVIATION_KEY = "standardDeviation_rating";

	/**
	 * Item code
	 */
	protected String itemCode;
	
	/**
	 * Map of the item
	 */
	protected DataBank dataBank;

	/**
	 * Users that have rated this item
	 */
	protected DynamicArray<String> users;

	/**
	 * Ratings of the users
	 */
	protected ArrayList<Double> ratings;

	/**
	 * Creates a new instance of an item. This constructor should not be users by developers.
	 * @param itemCode Item code
	 */
	public Item (String itemCode) {
		this.itemCode = itemCode;
		this.dataBank = new DataBank();
		this.users = new DynamicArray<String>();
		this.ratings = new ArrayList<Double>();
	}

	public DataBank getDataBank(){
		return dataBank;
	}

	public void calculateMetrics() {
		double sumRatings = 0;
		for (int i = 0; i < this.getNumberOfRatings();i++){
			sumRatings += this.ratings.get(i);
		}

		double ratingAverage = sumRatings / this.getNumberOfRatings();
		double sumDesv = 0;

		for (int i = 0; i < this.getNumberOfRatings();i++){
			sumDesv += (this.ratings.get(i) - ratingAverage) * (this.ratings.get(i) - ratingAverage);
		}
		double standardDeviation = Math.sqrt(sumDesv / this.getNumberOfRatings()-1);

		this.getDataBank().setDouble(AVERAGERATING_KEY, ratingAverage);
		this.getDataBank().setDouble(STANDARDDEVIATION_KEY, standardDeviation);
	}

	/**
	 * Return the item code.
	 * @return Item code
	 */
	public String getItemCode() {
		return this.itemCode;
	}
	
	/**
	 * Returns the user code at index position. 
	 * @param index Index.
	 * @return User code at index. 
	 */
	public String getUserAt(int index) {
		return this.users.get(index);
	}

	/**
	 * Returns the rating at index position. 
	 * @param index Index.
	 * @return Rating at index. 
	 */
	public double getRatingAt(int index) {
		return this.ratings.get(index);
	}
	
	/**
	 * Get the index of an user code at the user's item array.
	 * @param user_code User code
	 * @return User index in the user's item array if the user has rated the item or -1 if not
	 */
	public int getUserIndex (String user_code) {
		return users.get(user_code);
	}
	
	/**
	 * Get the number of ratings that the item have received.
	 * @return Number of ratings received
	 */
	public int getNumberOfRatings () {
		return this.ratings.size();
	}

	/**
	 * Add/Modify a new rating to the item, associated to a user.
	 * @param userCode userCode which identify the specific user.
	 * @param rating rated value by user, refering this item.
	 */
	public void addRating(String userCode, double rating){
		int positionInArray = this.users.get(userCode);

		if (positionInArray != -1){ //If element already exists.
			this.users.set(positionInArray, userCode);
			this.ratings.set(positionInArray, rating);
		}else{ //If not exist.
			this.ratings.add(this.users.addOrdered(userCode), rating);
		}
	}

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
