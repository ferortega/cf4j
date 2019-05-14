package cf4j.data;

import cf4j.data.types.DynamicArray;
import cf4j.data.types.DynamicSortedArray;
import cf4j.utils.Methods;
import java.util.ArrayList;

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

	public final static String SIMILARITIES_KEY = "similarities";
	public final static String NEIGHBORS_KEY = "neighbors";

	/**
	 * Test users that have rated this item
	 */
	protected DynamicSortedArray<String> testUsers;

	/**
	 * Test ratings of the users
	 */
	protected DynamicArray<Double> testRatings;
	
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
	 */
	public TestItem (String itemCode) {
		super(itemCode);
		this.testUsers = new DynamicSortedArray<String>();
		this.testRatings = new DynamicArray<Double>();
		//TODO: Metrics?
		//this.testRatingAverage = Methods.arrayAverage(testRatings);
		//this.testRatingStandardDeviation = Methods.arrayStandardDeviation(testRatings);
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
	 * Get the test users that have rated the item
	 * @return Test users codes sorted from low to high. 
	 */
	public DynamicSortedArray<String> getTestUsers() {
		return this.testUsers;
	}
	
	/**
	 * Returns the test user code at index position. 
	 * @param index Index.
	 * @return Test user code at index. 
	 */
	public String getTestUserAt(int index) {
		return this.getTestUsers().get(index);
	}

	/**
	 * Get the test ratings of the test users to the item. The indexes of this 
	 * array overlaps with indexes of the getTestUsers() array.
	 * @return Test users ratings
	 */
	public DynamicArray<Double> getTestRatings() {
		return this.testRatings;
	}
	
	/**
	 * Returns the test rating at index position. 
 	 * @param index Index.
	 * @return Test rating at index. 
	 */
	public double getTestRatingAt(int index) {
		return this.getTestRatings().get(index);
	}
	
	/**
	 * Get the index of an user code at the test user's item array.
	 * @param user_code User code
	 * @return Test user index in the test user's item array if the user has rated 
	 * 	the item or -1 if not
	 */
	public int getTestUserIndex (String user_code) {
		return this.testUsers.get(user_code);
	}
	
	/**
	 * Get the number of test ratings that the item have received.
	 * @return Number of test ratings received
	 */
	public int getNumberOfTestRatings () {
		return this.testRatings.size();
	}
	
	/**
	 * Add/Modify a new test rating to the testItem, associated to a user.
	 * @param userCode userCode which identify the specific user.
	 * @param rating rated value of the user, refering this item.
	 */
	public void addTestRating(String userCode, double rating){
		testRatings.add(testUsers.add(userCode), new Double(rating));
	}
}
