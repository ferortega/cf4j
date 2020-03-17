package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * <p>Defines an user. An user is composed by:</p>
 * <ul>
 *  <li>User code</li>
 *  <li>Array of items who have rated the user</li>
 *  <li>Array of ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega, Jesús Mayor
 */
public class User implements Serializable {

	private static final long serialVersionUID = 20200314L;

	//Stored metrics
	protected double min = Double.MAX_VALUE;
	protected double max = Double.MIN_VALUE;
	protected double average = 0.0;

	/**
	 * User code
	 */
	protected String id;

	/**
	 * User code
	 */
	protected int userIndex;
	
	/**
	 * Map of the user
	 */
	protected DataBank dataBank;

	/**
	 * Items rated by the user
	 */
	protected SortedRatingList itemsRatings;

	/**
	 * Creates a new instance of an user. This constructor should not be used by developers.
	 * @param id User code
	 * @param index Index related with the datamodel array.
	 */
	public User (String id, int index) {
		this.id = id;
		this.userIndex = index;
		this.dataBank = new DataBank();
		this.itemsRatings = new SortedRatingList();
	}

	public DataBank getDataBank(){
		return dataBank;
	}

	/**
	 * Returns the user code.
	 * @return User code
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the user userIndex.
	 * @return User userIndex inside the datamodel
	 */
	public int getUserIndex() {
		return this.userIndex;
	}

	/**
	 * Returns the item userIndex at a local userIndex position.
	 * @param pos Index inside the local array.
	 * @return Item userIndex in the datamodel.
	 */
	public int getItemAt(int pos) {
		return this.itemsRatings.get(pos).getLeft();
	}

	/**
	 * Returns the rating at userIndex position.
	 * @param pos Index inside the local array.
	 * @return Rating at indicated position.
	 */
	public double getRatingAt(int pos) {
		return this.itemsRatings.get(pos).getRight();
	}
	
	/**
	 * Get the userIndex of an item code at the items array of the user.
	 * @param itemIndex Item code
	 * @return Item local position in the user's items array if the item has rated the item or -1 if don't
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
	 * Add a new rating to the user, associated to an item.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an exception.
	 * @param itemIndex item global code which identify the specific  in the datamodel.
	 * @param rating rated value by user, refering this item.
	 */
	public void addRating(int itemIndex, double rating){
		if (!this.itemsRatings.add(itemIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + itemIndex);

		min = Math.min(rating, min);
		max = Math.max(rating, max);
		average = (this.itemsRatings.size() <= 1) ? rating : ((average * (this.itemsRatings.size()-1)) + rating) / this.itemsRatings.size();
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
