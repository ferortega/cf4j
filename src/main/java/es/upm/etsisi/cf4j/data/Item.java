package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * Defines a composition of an Item. An item is composed by:
 * <ul>
 *  <li>Item identification.</li>
 *  <li>Item index (in the datamodel where is stored).</li>
 *  <li>Array of ratings made by the users.</li>
 * </ul>
 * It is not recommended that developers generate new instances of this class since this is a memory-structural class.
 */
public class Item implements Serializable {

	private static final long serialVersionUID = 20200314L;

	/**
	 * Item identification code.
	 */
	protected String id;

	/**
	 * Item index in datamodel.
	 */
	protected int itemIndex;

	/**
	 * Minimum (training) rating in the DataModel.
	 */
	protected double min = Double.MAX_VALUE;

	/**
	 * Maximum (training) rating in the DataModel.
	 */
	protected double max = Double.MIN_VALUE;

	/**
	 * Average (training) rating.
	 */
	protected double average = 0.0;

	/**
	 * DataBank to store heterogeneous information
	 */
	protected DataBank dataBank;

	/**
	 * Array of users that have rated this item.
	 */
	protected SortedRatingList usersRatings;

	/**
	 * Creates a new instance of an item. This constructor should not be used by developers.
	 * @param id Item identification code.
	 * @param index Item index related with the datamodel array.
	 */
	public Item (String id, int index) {
		this.id = id;
		this.itemIndex = index;
		this.dataBank = new DataBank();
		this.usersRatings = new SortedRatingList();
	}

	/**
	 * Gets the DataBank instance that stores heterogeneous information related to the Item.
	 * @return DataBank instance.
	 */
	public DataBank getDataBank(){
		return dataBank;
	}

	/**
	 * Return the item identification code.
	 * @return Item identification.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return the item index inside the datamodel.
	 * @return Item index inside the datamodel.
	 */
	public int getItemIndex() {
		return this.itemIndex;
	}
	
	/**
	 * Returns the index of the User whose rating is stored in the given position inside this Item.
	 * @param pos Position inside the local array.
	 * @return User index in the datamodel stored at indicated position.
	 */
	public int getUserAt(int pos) {
		return this.usersRatings.get(pos).getIndex();
	}

	/**
	 * Returns the rating stored in the given position inside this Item.
	 * @param pos Position inside the local array.
	 * @return Rating at indicated position.
	 */
	public double getRatingAt(int pos) {
		return this.usersRatings.get(pos).getRating();
	}
	
	/**
	 * Find the rating position at the item's users array given an user index.
	 * @param userIndex User index associated to the datamodel.
	 * @return User position in the items's user array if the user has rated the item or -1 if don't.
	 */
	public int findUser(int userIndex) {
		return this.usersRatings.find(userIndex);
	}
	
	/**
	 * Get the number of ratings that the item have received.
	 * @return Number of ratings received.
	 */
	public int getNumberOfRatings () {
		return this.usersRatings.size();
	}

	/**
	 * Add a new rating to the item, associated to a determined user who made this rating.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an IllegalArgumentException.
	 * @param userIndex User index which identify the specific user in the datamodel.
	 * @param rating Rating value made by user, referencing this item.
	 */
	public void addRating(int userIndex, double rating){
		if (!this.usersRatings.add(userIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + id);

		min = Math.min(rating, min);
		max = Math.max(rating, max);
		average = (this.usersRatings.size() <= 1) ? rating : ((average * (this.usersRatings.size()-1)) + rating) / this.usersRatings.size();
	}

	/**
	 * Get the minimum rating done.
	 * @return Minimum rating.
	 */
	public double getMinRating(){
		return min;
	}

	/**
	 * Get the maximum rating done.
	 * @return Maximum rating.
	 */
	public double getMaxRating(){
		return max;
	}

	/**
	 * Get the average of ratings done.
	 * @return Average of ratings.
	 */
	public double getRatingAverage(){
		return average;
	}
}
