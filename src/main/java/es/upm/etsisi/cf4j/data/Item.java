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
	protected String id;

	/**
	 * Item userIndex in datamodel
	 */
	protected int itemIndex;
	
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
	 * @param id Item code
	 * @param index Item Index
	 */
	public Item (String id, int index) {
		this.id = id;
		this.itemIndex = index;
		this.dataBank = new DataBank();
		this.usersRatings = new SortedRatingList();
	}

	public DataBank getDataBank(){
		return dataBank;
	}

	/**
	 * Return the item identification.
	 * @return Item identification
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return the item userIndex inside the datamodel.
	 * @return Item userIndex inside the datamodel
	 */
	public int getItemIndex() {
		return this.itemIndex;
	}
	
	/**
	 * Returns the user userIndex at a local userIndex pos.
	 * @param pos Index inside the local array.
	 * @return User userIndex in the datamodel.
	 */
	public int getUserAt(int pos) {
		return this.usersRatings.get(pos).getLeft();
	}

	/**
	 * Returns the rating at userIndex position.
	 * @param pos Index inside the local array.
	 * @return Rating at indicated position.
	 */
	public double getRatingAt(int pos) {
		return this.usersRatings.get(pos).getRight();
	}
	
	/**
	 * Get the userIndex of an user code at the user's item array.
	 * @param userIndex User userIndex.
	 * @return User position in the items's user array if the user has rated the item or -1 if don't
	 */
	public int findUser(int userIndex) {
		return this.usersRatings.find(userIndex);
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
	 * @param userIndex User userIndex which identify the specific user in the datamodel.
	 * @param rating Rated value by user, referencing this item.
	 */
	public void addRating(int userIndex, double rating){
		if (!this.usersRatings.add(userIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + id);

		min = Math.min(rating, min);
		max = Math.max(rating, max);
		average = (this.usersRatings.size() <= 1) ? rating : ((average * (this.usersRatings.size()-1)) + rating) / this.usersRatings.size();
	}

	/**
	 * Get the minimum rating done
	 * @return minimum rating
	 */
	public double getMinRating(){ return min; }

	/**
	 * Get the maximum rating done
	 * @return maximum rating
	 */
	public double getMaxRating(){ return max; }

	/**
	 * Get the average of ratings done
	 * @return average
	 */
	public double getRatingAverage(){ return average; }
}
