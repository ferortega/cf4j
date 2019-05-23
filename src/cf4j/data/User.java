package cf4j.data;

import java.io.Serializable;
import java.util.ArrayList;

import cf4j.data.types.DynamicArray;

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

	private static final long serialVersionUID = 20190518L;

	public static final String AVERAGERATING_KEY = "average_rating";
	public static final String STANDARDDEVIATION_KEY = "standardDeviation_rating";

	/**
	 * User code
	 */
	protected String userCode;
	
	/**
	 * Map of the user
	 */
	protected DataBank dataBank;

	/**
	 * Items rated by the user
	 */
	protected DynamicArray<String> items;

	/**
	 * Ratings of the user to the items
	 */
	protected ArrayList<Double> ratings;

	/**
	 * Creates a new instance of an user. This constructor should not be used by developers.
	 * @param userCode User code
	 */
	public User (String userCode) {
		this.userCode = userCode;
		this.dataBank = new DataBank();
		this.items = new DynamicArray<String>();
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

		double standardDeviation = (this.getNumberOfRatings()<=1)? 0 : Math.sqrt(sumDesv / (this.getNumberOfRatings()-1));

		this.getDataBank().setDouble(AVERAGERATING_KEY, ratingAverage);
		this.getDataBank().setDouble(STANDARDDEVIATION_KEY, standardDeviation);
	}

	/**
	 * Returns the user code.
	 * @return User code
	 */
	public String getUserCode() {
		return this.userCode;
	}

	/**
	 * Returns the item code at index position. 
	 * @param index Index.
	 * @return Item code at index. 
	 */
	public String getItemAt(int index) {
		return this.items.get(index);
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
		int positionInArray = this.items.get(itemCode);

		if (positionInArray != -1){ //If element already exists.
			this.items.set(positionInArray, itemCode);
			this.ratings.set(positionInArray, rating);
		}else{ //If not exist.
			this.ratings.add(this.items.addOrdered(itemCode), rating);
		}
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
