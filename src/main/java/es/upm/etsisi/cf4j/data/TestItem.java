package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * <p>A TestItem extends an Item given it the following properties:</p>
 * <ul>
 *  <li>Item index in the test items array</li>
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

	/**
	 * Creates a new instance of a test item. This constructor should not be used by developers.
	 * @param itemCode Item code
	 */
	public TestItem (String itemCode) {
		super(itemCode);
		this.testUsersRatings = new SortedRatingList();
	}
	
	/**
	 * Returns the test user code at index position. 
	 * @param testUserLocalIndex Index.
	 * @return Test user code at index. NULL: if received localIndex was out of bounds.
	 */
	public Integer getTestUser(int testUserLocalIndex) {
		if (testUserLocalIndex < 0 || testUserLocalIndex > this.testUsersRatings.size())
			return null;

		return this.testUsersRatings.get(testUserLocalIndex).getLeft();
	}

	/**
	 * Returns the test rating at index position. 
 	 * @param testUserLocalIndex Index.
	 * @return Test rating at index. 
	 */
	public Double getTestRating(int testUserLocalIndex) {
		if (testUserLocalIndex < 0 || testUserLocalIndex > this.testUsersRatings.size())
			return null;

		return this.testUsersRatings.get(testUserLocalIndex).getRight();
	}
	
	/**
	 * Get the index of an user index a at the test user's item array.
	 * @param userIndex User code
	 * @return Test user index in the test testUser's item array if the user has rated the item or -1 if dont
	 */
	public int findTestUserRating (int userIndex) {
		return testUsersRatings.find(userIndex);
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
	 * @param userIndex user global index which identify the specific user in the datamodel.
	 * @param rating rated value of the user, refering this item.
	 */
	public void addTestRating(int userIndex, double rating){
		if (!this.testUsersRatings.add(userIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + itemCode);

		minTest = Math.min(rating, minTest);
		maxTest = Math.max(rating, maxTest);
		averageTest = (this.usersRatings.size() <= 1) ? rating : ((averageTest * (this.usersRatings.size()-1)) + rating) / this.usersRatings.size();
	}

	/**
	 * Get the minimum rating done
	 * @return minimum rating
	 */
	public double getMinTest(){ return min; }

	/**
	 * Get the maximum rating done
	 * @return maximum rating
	 */
	public double getMaxTest(){ return max; }

	/**
	 * Get the average of ratings done
	 * @return average
	 */
	public double getAverageTest(){ return average; }
}
