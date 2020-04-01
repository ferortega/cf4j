package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * Defines a composition of an User. An user is composed by:
 * <ul>
 *  <li>User identification.</li>
 *  <li>User index (in the datamodel where is stored).</li>
 *  <li>Array of items rated by the user.</li>
 * </ul>
 */
public class User implements Serializable {

	private static final long serialVersionUID = 20200314L;

	/**
	 * User identification code.
	 */
	protected String id;

	/**
	 * User index in datamodel.
	 */
	protected int userIndex;

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
	 * DataBank to store heterogeneous information.
	 */
	protected DataBank dataBank;

	/**
	 * Items rated by the user.
	 */
	protected SortedRatingList itemsRatings;

	/**
	 * Creates a new instance of an user. This constructor should not be used by developers.
	 * @param id User identification code.
	 * @param index index related with the datamodel array.
	 */
	public User (String id, int index) {
		this.id = id;
		this.userIndex = index;
		this.dataBank = new DataBank();
		this.itemsRatings = new SortedRatingList();
	}

	/**
	 * Gets the DataBank instance that stores heterogeneous information related to the User.
	 * @return DataBank instance.
	 */
	public DataBank getDataBank(){
		return dataBank;
	}

	/**
	 * Returns the user identification code.
	 * @return User identification.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return the user index inside the datamodel.
	 * @return User index inside the datamodel.
	 */
	public int getUserIndex() {
		return this.userIndex;
	}

	/**
	 * Returns the index of the Item rated by the User and stored in the given position.
	 * @param pos Position inside the local array.
	 * @return Item index in the datamodel stored at indicated position.
	 */
	public int getItemAt(int pos) {
		return this.itemsRatings.get(pos).getIndex();
	}

	/**
	 * Returns the Rating stored in the given position inside this User.
	 * @param pos Position inside the local array.
	 * @return Rating at indicated position.
	 */
	public double getRatingAt(int pos) {
		return this.itemsRatings.get(pos).getRating();
	}
	
	/**
	 * Find the rating position at the user's items array given an item index.
	 * @param itemIndex Item index associated to the datamodel.
	 * @return Item position in the user's items array if the item has rated the item or -1 if don't
	 */
	public int findItem(int itemIndex) {
		return itemsRatings.find(itemIndex);
	}

	/**
	 * Get the number of ratings that the user have made.
	 * @return Number of ratings received
	 */
	public int getNumberOfRatings () {
		return this.itemsRatings.size();
	}

	/**
	 * Add a new rating to the user who rated an specific item.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an IllegalArgumentException.
	 * @param itemIndex Item index which identify the specific item in the datamodel.
	 * @param rating Rating value made by user, referencing this item.
	 */
	public void addRating(int itemIndex, double rating){
		if (!this.itemsRatings.add(itemIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in user: " + id);

		min = Math.min(rating, min);
		max = Math.max(rating, max);
		average = (this.itemsRatings.size() <= 1) ? rating : ((average * (this.itemsRatings.size()-1)) + rating) / this.itemsRatings.size();
	}

	/**
	 * Get the minimum rating done.
	 * @return minimum rating.
	 */
	public double getMinRating(){
		return min;
	}

	/**
	 * Get the maximum rating done.
	 * @return maximum rating.
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
