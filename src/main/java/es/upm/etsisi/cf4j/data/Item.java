package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * <p>Defines an item. An item is composed by:</p>
 * <ul>
 *  <li>Item code</li>
 *  <li>Array of users who have rated the item</li>
 *  <li>Array of ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega, Jesús Mayor
 */
public class Item implements Serializable {

	private static final long serialVersionUID = 20200314L;

	//Stored metrics
	protected double min = Double.MAX_VALUE;
	protected double max = Double.MIN_VALUE;
	protected double average = 0.0;

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
	protected SortedRatingList usersRatings;

	/**
	 * Creates a new instance of an item. This constructor should not be users by developers.
	 * @param itemCode Item code
	 */
	public Item (String itemCode) {
		this.itemCode = itemCode;
		this.dataBank = new DataBank();
		this.usersRatings = new SortedRatingList();
	}

	public DataBank getDataBank(){
		return dataBank;
	}

	/**
	 * Return the item code.
	 * @return Item code
	 */
	public String getItemCode() {
		return this.itemCode;
	}
	
	/**
	 * Returns the user index at a local index position.
	 * @param userLocalIndex Index.
	 * @return userIndex in the datamodel. NULL: if received localIndex was out of bounds.
	 */
	public Integer getUser(int userLocalIndex) {
		if (userLocalIndex < 0 || userLocalIndex > this.usersRatings.size())
			return null;

		return this.usersRatings.get(userLocalIndex).getLeft();
	}

	/**
	 * Returns the rating at index position. 
	 * @param userLocalIndex Index.
	 * @return Rating at localIndex. Null if received localIndex was out of bounds.
	 */
	public Double getRating(int userLocalIndex) {
		if (userLocalIndex < 0 || userLocalIndex > this.usersRatings.size())
			return null;

		return this.usersRatings.get(userLocalIndex).getRight();
	}
	
	/**
	 * Get the index of an user code at the user's item array.
	 * @param userIndex User code
	 * @return User local index in the items's user array if the user has rated the item or -1 if don't
	 */
	public int findUserRating (int userIndex) {
		return usersRatings.find(userIndex);
	}
	
	/**
	 * Get the number of ratings that the item have received.
	 * @return Number of ratings received
	 */
	public int getNumberOfRatings () {
		return this.usersRatings.size();
	}

	/**
	 * Add a new rating to the item, associated to an user.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an exception.
	 * @param userIndex user index which identify the specific user in the datamodel.
	 * @param rating rated value by user, referencing this item.
	 */
	public void addRating(int userIndex, double rating){
		if (!this.usersRatings.add(userIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + itemCode);
		min = Math.min(rating, min);
		max = Math.max(rating, max);
		average = (this.usersRatings.size() <= 1) ? rating : ((average * (this.usersRatings.size()-1)) + rating) / this.usersRatings.size();
	}

	/**
	 * Get the minimum rating done
	 * @return minimum rating
	 */
	public double getMin(){ return min; }

	/**
	 * Get the maximum rating done
	 * @return maximum rating
	 */
	public double getMax(){ return max; }

	/**
	 * Get the average of ratings done
	 * @return average
	 */
	public double getAverage(){ return average; }
}
