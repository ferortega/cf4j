package es.upm.etsisi.cf4j.data;

import cf4j.data.types.DynamicArray;
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

	private static final long serialVersionUID = 20190518L;

	public final static String SIMILARITIES_KEY = "itemToItemMetrics";
	public final static String NEIGHBORS_KEY = "neighbors";
	public static final String AVERAGETESTRATING_KEY = "averagetest_rating";
	public static final String STANDARDDEVIATIONTEST_KEY = "standardDeviation_rating";

	/**
	 * Test users that have rated this item
	 */
	protected DynamicArray<String> testUsers;

	/**
	 * Test ratings of the users
	 */
	protected ArrayList<Double> testRatings;


	/**
	 * Creates a new instance of a test item. This constructor should not be used by developers.
	 * @param itemCode Item code
	 */
	public TestItem (String itemCode) {
		super(itemCode);
		this.testUsers = new DynamicArray<String>();
		this.testRatings = new ArrayList<Double>();
	}

	@Override
	public void calculateMetrics() {
		super.calculateMetrics();

		double sumRatings = 0;
		for (int i = 0; i < this.getNumberOfTestRatings();i++){
			sumRatings += this.testRatings.get(i);
		}

		double ratingAverage = sumRatings / this.getNumberOfTestRatings();
		double sumDesv = 0;

		for (int i = 0; i < this.getNumberOfTestRatings();i++){
			sumDesv += (this.testRatings.get(i) - ratingAverage) * (this.testRatings.get(i) - ratingAverage);
		}
		double standardDeviation = Math.sqrt(sumDesv / this.getNumberOfTestRatings()-1);

		this.getDataBank().setDouble(AVERAGETESTRATING_KEY, ratingAverage);
		this.getDataBank().setDouble(STANDARDDEVIATIONTEST_KEY, standardDeviation);
	}
	
	/**
	 * Returns the test user code at index position. 
	 * @param index Index.
	 * @return Test user code at index. 
	 */
	public String getTestUserAt(int index) {
		return this.testUsers.get(index);
	}

	/**
	 * Returns the test rating at index position. 
 	 * @param index Index.
	 * @return Test rating at index. 
	 */
	public double getTestRatingAt(int index) {
		return this.testRatings.get(index);
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
		int positionInArray = this.testUsers.get(userCode);

		if (positionInArray != -1){ //If element already exists.
			this.testUsers.set(positionInArray, userCode);
			this.testRatings.set(positionInArray, rating);
		}else{ //If not exist.
			this.testRatings.add(this.testUsers.addOrdered(userCode), rating);
		}
	}
}
