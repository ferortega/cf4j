package cf4j;

import cf4j.utils.Methods;

/**
 * <p>A TestItem extends an Item given it the following properties:</p>
 * <ul>
 *  <li>Item index in the test items array</li>
 *  <li>Array of test users who have rated the item</li>
 *  <li>Array of test ratings that the item have received</li>
 * </ul>
 * @author Fernando Ortega
 */
public class TestItem extends Item {

	private static final long serialVersionUID = 20171018L;
	
	private final static String SIMILARITIES_KEY = "similarities";
	private final static String NEIGHBORS_KEY = "neighbors";

	/**
	 * Test item index
	 */
	protected int testItemIndex;
	
	/**
	 * Test users that have rated this item
	 */
	protected int [] testUsers;
	
	/**
	 * Test ratings of the users
	 */
	protected double [] testRatings;
	
	/**
	 * Test rating average of the item
	 */
	protected double testRatingAverage;
	
	/**
	 * Test rating standard deviation of this item
	 */
	protected double testRatingStandardDeviation;
	
	/**
	 * Creates a new instance of a test item. This constructor should not be used by developers.
	 * @param itemCode Item code
	 * @param itemIndex Item index
	 * @param users Users that have rated this item
	 * @param ratings Ratings of the users
	 * @param testItemIndex Test item index
	 * @param testUsers Test users that have rated this item
	 * @param testRatings Test ratings of the test users
	 */
	public TestItem (int itemCode, int itemIndex, int [] users, double [] ratings, int testItemIndex, int [] testUsers, double [] testRatings) {
		super(itemCode, itemIndex, users, ratings);
		this.testItemIndex = testItemIndex;
		this.testUsers = testUsers;
		this.testRatings = testRatings;
		this.testRatingAverage = Methods.arrayAverage(testRatings);
		this.testRatingStandardDeviation = Methods.arrayStandardDeviation(testRatings);
	}

	/**
	 * Average of the test ratings
	 * @return Test ratings average
	 */
	public double getTestRatingAverage() {
		return this.testRatingAverage;
	}

	/**
	 * Standard deviation of the test ratings
	 * @return Test ratings standard deviation
	 */
	public double getTestRatingStandardDeviation() {
		return this.testRatingStandardDeviation;
	}
	
	/**
	 * Return the test item index
	 * @return Test item index
	 */
	public int getTestItemIndex() {
		return this.testItemIndex;
	}
	
	/**
	 * Get the test users that have rated the item
	 * @return Test users codes sorted from low to high. 
	 */
	public int [] getTestUsers() {
		return this.testUsers;
	}
	
	/**
	 * Returns the test user code at index position. 
	 * @param index Index.
	 * @return Test user code at index. 
	 */
	public int getTestUserAt(int index) {
		return this.getTestUsers()[index];
	}

	/**
	 * Get the test ratings of the test users to the item. The indexes of this 
	 * array overlaps with indexes of the getTestUsers() array.
	 * @return Test users ratings
	 */
	public double [] getTestRatings() {
		return this.testRatings;
	}
	
	/**
	 * Returns the test rating at index position. 
 	 * @param index Index.
	 * @return Test rating at index. 
	 */
	public double getTestRatingAt(int index) {
		return this.getTestRatings()[index];
	}
	
	/**
	 * Get the index of an user code at the test user's item array.
	 * @param user_code User code
	 * @return Test user index in the test user's item array if the user has rated 
	 * 	the item or -1 if not
	 */
	public int getTestUserIndex (int user_code) {
		return Methods.getIndex(this.testUsers, user_code);
	}
	
	/**
	 * Get the number of test ratings that the item have received.
	 * @return Number of test ratings received
	 */
	public int getNumberOfTestRatings () {
		return this.testRatings.length;
	}
	
	/**
	 * Return the similarities array of the test item. The similarity process must be 
	 * executed before use this method.
	 * @return Similarities array or null
	 */
	public double [] getSimilarities () {
		return (double []) this.get(SIMILARITIES_KEY);
	}
	
	/**
	 * Set the similarity of this item with the training items. The positions of the
	 * similarities arrays must overlaps with the positions of the items of the method
	 * Kernel.getItems().
	 * @param similarities Similarities array. Higher is more similar.
	 */
	public void setSimilarities (double [] similarities) {
		this.put(SIMILARITIES_KEY, similarities);
	}
	
	/**
	 * Return the neighbors array of the test item. The neighbors process must be executed
	 * before use this method.
	 * @return Neighbors array or null
	 */
	public int [] getNeighbors () {
		return (int []) this.get(NEIGHBORS_KEY);
	}
	
	/**
	 * Set the items indexes that are neighbors of the item. First positions of the neighbors
	 * array must be most similar items.
	 * @param neighbors Items indexes that are neighbors of this item sorted by similarity
	 */
	public void setNeighbors (int [] neighbors) {
		this.put(NEIGHBORS_KEY, neighbors);
	}
}
