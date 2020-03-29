package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * <p>A TestItem extends an Item given it the following properties:</p>
 * <ul>
 *  <li>Item userIndex in the test items array</li>
 *  <li>Array of test users who have rated the item</li>
 *  <li>Array of test ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega, Jesús Mayor
 */
public class TestItem extends Item {

	private static final long serialVersionUID = 20200314L;

	//Stored metrics
	protected double minTest = Double.MAX_VALUE;
	protected double maxTest = Double.MIN_VALUE;
	protected double averageTest = 0.0;

	protected SortedRatingList testUsersRatings;

	protected int testItemIndex;

	/**
	 * Creates a new instance of a test item. This constructor should not be used by developers.
	 * @param id Item code
	 * @param itemIndex Index related with the datamodel array.
	 * @param testItemIndex Index related with the datamodel test array.
	 */
	public TestItem(String id, int itemIndex, int testItemIndex) {
		super(id, itemIndex);
		this.testItemIndex = testItemIndex;
		this.testUsersRatings = new SortedRatingList();
	}

	/**
	 * Returns the testItemIndex.
	 * @return testItemIndex inside the datamodel
	 */
	public int getTestItemIndex() {
		return this.testItemIndex;
	}
	
	/**
	 * Returns the test user code at userIndex position.
	 * @param pos Index inside the local array.
	 * @return Test user userIndex in the datamodel.
	 */
	public int getTestUserAt(int pos) {
		return this.testUsersRatings.get(pos).getIndex();
	}

	/**
	 * Returns the test rating at userIndex position.
 	 * @param pos Index inside the local array.
	 * @return Test rating at position inside the local array.
	 */
	public double getTestRatingAt(int pos) {
		return this.testUsersRatings.get(pos).getRating();
	}

	/**
	 * Get the number of test ratings that the item have received.
	 * @return Number of test ratings received
	 */
	public int getNumberOfTestRatings () {
		return this.testUsersRatings.size();
	}

	/**
	 * Add a new test rating to the testItem, associated to a user.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an exception.
	 * @param testUserIndex user global userIndex which identify the specific user in the datamodel.
	 * @param rating rated value of the user, refering this item.
	 */
	public void addTestRating(int testUserIndex, double rating){
		if (!this.testUsersRatings.add(testUserIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + id);

		minTest = Math.min(rating, minTest);
		maxTest = Math.max(rating, maxTest);
		averageTest = (this.usersRatings.size() <= 1) ? rating : ((averageTest * (this.usersRatings.size()-1)) + rating) / this.usersRatings.size();
	}

	/**
	 * Get the minimum rating done
	 * @return minimum rating
	 */
	public double getMinTestRating(){ return minTest; }

	/**
	 * Get the maximum rating done
	 * @return maximum rating
	 */
	public double getMaxTestRating(){ return maxTest; }

	/**
	 * Get the average of ratings done
	 * @return average
	 */
	public double getTestRatingAverage(){ return averageTest; }
}
