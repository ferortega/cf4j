package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * A TestItem extends an Item given it the following properties:
 * <ul>
 *  <li>Item index (in the datamodel, where is stored).</li>
 *  <li>Array of test item ratings made by the users.</li>
 * </ul>
 * It is not recommended that developers generate new instances of this class since this is a memory-structural class.
 */
public class TestItem extends Item {

	private static final long serialVersionUID = 20200314L;

	/**
	 * TestItem index in datamodel.
	 */
	protected int testItemIndex;

	/**
	 * Minimum test rating in the DataModel.
	 */
	protected double minTest = Double.MAX_VALUE;

	/**
	 * Maximum test rating in the DataModel.
	 */

	protected double maxTest = Double.MIN_VALUE;

	/**
	 * Average test rating.
	 */
	protected double averageTest = 0.0;

	/**
	 * Array of test users that have rated this item.
	 */
	protected SortedRatingList testUsersRatings;

	/**
	 * Creates a new instance of a test item. This constructor should not be used by developers.
	 * @param id Item identification code.
	 * @param itemIndex Item index related with the datamodel array.
	 * @param testItemIndex TestItem index related with the datamodel test array.
	 */
	public TestItem(String id, int itemIndex, int testItemIndex) {
		super(id, itemIndex);
		this.testItemIndex = testItemIndex;
		this.testUsersRatings = new SortedRatingList();
	}

	/**
	 * Return the test item index inside the datamodel.
	 * @return testItemIndex inside the datamodel.
	 */
	public int getTestItemIndex() {
		return this.testItemIndex;
	}
	
	/**
	 * Returns the index of the TestUser whose rating is stored in the given position inside this TestItem.
	 * @param pos Position inside the local array.
	 * @return TestUser index in the datamodel.
	 */
	public int getTestUserAt(int pos) {
		return this.testUsersRatings.get(pos).getIndex();
	}

	/**
	 * Returns the test rating stored in the given position inside this TestItem.
 	 * @param pos Position inside the local array.
	 * @return Test rating at indicated position.
	 */
	public double getTestRatingAt(int pos) {
		return this.testUsersRatings.get(pos).getRating();
	}

	/**
	 * Get the number of test ratings that the item have received.
	 * @return Number of test ratings received.
	 */
	public int getNumberOfTestRatings () {
		return this.testUsersRatings.size();
	}

	/**
	 * Add a new rating to the test item, associated to a determined user who made this rating.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an IllegalArgumentException.
	 * @param testUserIndex TestUser index which identify the specific user in the datamodel.
	 * @param rating rated value of the test user, referencing this item.
	 */
	public void addTestRating(int testUserIndex, double rating){
		if (!this.testUsersRatings.add(testUserIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in test item: " + id);

		minTest = Math.min(rating, minTest);
		maxTest = Math.max(rating, maxTest);
		averageTest = (this.usersRatings.size() <= 1) ? rating : ((averageTest * (this.usersRatings.size()-1)) + rating) / this.usersRatings.size();
	}

	/**
	 * Get the minimum rating done.
	 * @return Minimum rating.
	 */
	public double getMinTestRating(){ return minTest; }

	/**
	 * Get the maximum rating done.
	 * @return Maximum rating.
	 */
	public double getMaxTestRating(){ return maxTest; }

	/**
	 * Get the average of ratings done.
	 * @return Average of ratings.
	 */
	public double getTestRatingAverage(){ return averageTest; }
}
