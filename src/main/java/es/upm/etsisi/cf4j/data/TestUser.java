package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * <p>A TestUser extends an User given it the following properties:</p>
 * <ul>
 *  <li>User index in the test users array</li>
 *  <li>Array of test items that the user have rated</li>
 *  <li>Array of test ratings hat the user have made</li>
 * </ul>
 * @author Fernando Ortega, Jesús Mayor
 */
public class TestUser extends User {

	private static final long serialVersionUID = 20200314L;

	//Stored metrics
	protected double minTest = Double.MAX_VALUE;
	protected double maxTest = Double.MIN_VALUE;
	protected double averageTest = 0.0;

	protected SortedRatingList testItemsRatings;

	/**
	 * Creates a new instance of a test user. This constructor should not be used by developers.
	 * @param userCode User code
	 */
	public TestUser (String userCode) {
		super(userCode);
		this.testItemsRatings = new SortedRatingList();
	}

	/**
	 * Returns the test item code at index position. 
	 * @param testItemLocalIndex Index.
	 * @return Test item code at index. NULL: if received localIndex was out of bounds.
	 */
	public Integer getTestItem(int testItemLocalIndex) {
		if (testItemLocalIndex < 0 || testItemLocalIndex > this.testItemsRatings.size())
			return null;

		return this.testItemsRatings.get(testItemLocalIndex).getLeft();
	}
	
	/**
	 * Returns the test rating at index position. 
	 * @param testItemLocalIndex Index.
	 * @return Test rating at index. NULL: if received localIndex was out of bounds.
	 */
	public Double getTestRating(int testItemLocalIndex) {
		if (testItemLocalIndex < 0 || testItemLocalIndex > this.testItemsRatings.size())
			return null;

		return this.testItemsRatings.get(testItemLocalIndex).getRight();
	}
	
	/**
	 * Get the index of an test item index at test user's items array.
	 * @param itemIndex Item code
	 * @return Test item index if the user has rated the item or -1 if don't
	 */
	public int findTestUserRating (int itemIndex) {
		return this.testItemsRatings.find(itemIndex);
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
	 * @param itemIndex item global index which identify the specific item in the datamodel.
	 * @param rating rated value by user, referencing this item.
	 */
	public void addTestRating(int itemIndex, double rating){
		if (!this.testItemsRatings.add(itemIndex, rating))
			throw new IllegalArgumentException("Provided rating already exist in item: " + itemIndex);

		minTest = Math.min(rating, minTest);
		maxTest = Math.max(rating, maxTest);
		averageTest = (this.testItemsRatings.size() <= 1) ? rating : ((averageTest * (this.testItemsRatings.size()-1)) + rating) / this.testItemsRatings.size();
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
