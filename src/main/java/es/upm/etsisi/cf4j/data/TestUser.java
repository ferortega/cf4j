package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * A TestUser extends an User given it the following properties:
 * <ul>
 *  <li>User userIndex (in the datamodel where is stored).</li>
 *  <li>Array of test items rated by the user.</li>
 * </ul>
 * It is not recommended that developers generate new instances of this class since this is a memory-structural class.
 */
public class TestUser extends User {

	private static final long serialVersionUID = 20200314L;

	/**
	 * TestItem index in datamodel.
	 */
	protected int testUserIndex;

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
	 * TestItems rated by the user.
	 */
	protected SortedRatingList testItemsRatings;

	/**
	 * Creates a new instance of a test item. This constructor should not be used by developers.
	 * @param id User identification code.
	 * @param userIndex User index related with the datamodel array.
	 * @param testUserIndex TestUser index related with the datamodel test array.
	 */
	public TestUser (String id, int userIndex, int testUserIndex) {
		super(id, userIndex);
		this.testUserIndex = testUserIndex;
		this.testItemsRatings = new SortedRatingList();
	}

	/**
	 * Return the test user index inside the datamodel.
	 * @return testUserIndex inside the datamodel
	 */
	public int getTestUserIndex() {
		return this.testUserIndex;
	}

	/**
	 Returns the index of the TestItem rated by the User and stored in the given position.
	 * @param pos Position inside the local array.
	 * @return TestItem index in the datamodel.
	 */
	public int getTestItemAt(int pos) {
		return this.testItemsRatings.get(pos).getIndex();
	}
	
	/**
	 * Returns the test rating stored in the given position inside this User.
	 * @param pos Position inside the local array.
	 * @return Test rating at indicated position.
	 */
	public double getTestRatingAt(int pos) {
		return this.testItemsRatings.get(pos).getRating();
	}

	/**
	 * Get the number of test ratings that the user have made.
	 * @return Number of test ratings made.
	 */
	public int getNumberOfTestRatings () {
		return this.testItemsRatings.size();
	}

	/**
	 * Add a new test rating to the test user who rated an specific test item.
	 * You cannot overwrite an existing relation, otherwise repeated relations will throw an IllegalArgumentException.
	 * @param testItemIndex TestItem index which identify the specific item in the datamodel.
	 * @param rating Rating value made by the test user, referencing this item.
	 */
	public void addTestRating(int testItemIndex, double rating){
		if (!this.testItemsRatings.add(testItemIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in user: " + id);

		minTest = Math.min(rating, minTest);
		maxTest = Math.max(rating, maxTest);
		averageTest = (this.testItemsRatings.size() <= 1) ? rating : ((averageTest * (this.testItemsRatings.size()-1)) + rating) / this.testItemsRatings.size();
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
