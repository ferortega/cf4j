package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * <p>A TestUser extends an User given it the following properties:</p>
 * <ul>
 *  <li>User userIndex in the test users array</li>
 *  <li>Array of test items that the user have rated</li>
 *  <li>Array of test ratings hat the user have made</li>
 * </ul>
 */
public class TestUser extends User {

	private static final long serialVersionUID = 20200314L;

	//Stored metrics
	protected double minTest = Double.MAX_VALUE;
	protected double maxTest = Double.MIN_VALUE;
	protected double averageTest = 0.0;

	protected SortedRatingList testItemsRatings;

	protected int testUserIndex;

	/**
	 * Creates a new instance of a test user. This constructor should not be used by developers.
	 * @param id User code
	 * @param userIndex Index related with the datamodel array.
	 * @param testUserIndex Index related with the datamodel test array.
	 */
	public TestUser (String id, int userIndex, int testUserIndex) {
		super(id, userIndex);
		this.testUserIndex = testUserIndex;
		this.testItemsRatings = new SortedRatingList();
	}

	/**
	 * Returns the testUserIndex.
	 * @return testUserIndex inside the datamodel
	 */
	public int getTestUserIndex() {
		return this.testUserIndex;
	}

	/**
	 * Returns the test item code at userIndex position.
	 * @param pos Index inside the local array.
	 * @return Test item userIndex in the datamodel.
	 */
	public int getTestItemAt(int pos) {
		return this.testItemsRatings.get(pos).getIndex();
	}
	
	/**
	 * Returns the test rating at userIndex position.
	 * @param pos Index inside the local array.
	 * @return Test rating in the datamodel.
	 */
	public double getTestRatingAt(int pos) {
		return this.testItemsRatings.get(pos).getRating();
	}

	/**
	 * Get the number of test ratings of the user.
	 * @return Number of test ratings made
	 */
	public int getNumberOfTestRatings () {
		return this.testItemsRatings.size();
	}

	/**
	 * Add a new test rating to the test user, associated to a item.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an exception.
	 * @param testItemIndex item global userIndex which identify the specific item in the datamodel.
	 * @param rating rated value by user, referencing this item.
	 */
	public void addTestRating(int testItemIndex, double rating){
		if (!this.testItemsRatings.add(testItemIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + testItemIndex);

		minTest = Math.min(rating, minTest);
		maxTest = Math.max(rating, maxTest);
		averageTest = (this.testItemsRatings.size() <= 1) ? rating : ((averageTest * (this.testItemsRatings.size()-1)) + rating) / this.testItemsRatings.size();
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
