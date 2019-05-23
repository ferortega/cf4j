package cf4j.data;

import cf4j.data.types.DynamicArray;

import java.util.ArrayList;

/**
 * <p>A TestUser extends an User given it the following properties:</p>
 * <ul>
 *  <li>User index in the test users array</li>
 *  <li>Array of test items that the user have rated</li>
 *  <li>Array of test ratings hat the user have made</li>
 * </ul>
 * @author Fernando Ortega
 */
public class TestUser extends User {

	private static final long serialVersionUID = 20130403L;
	
	public final static String SIMILARITIES_KEY = "similarities";
	public final static String NEIGHBORS_KEY = "neighbors";
	public final static String PREDICTIONS_KEYS = "predictions";
	public static final String AVERAGETESTRATING_KEY = "averagetest_rating";
	public static final String STANDARDDEVIATIONTEST_KEY = "standardDeviation_rating";

	/**
	 * Test items that rated by the user
	 */
	protected DynamicArray<String> testItems;

	/**
	 * Test ratings of the items
	 */
	protected ArrayList<Double> testRatings;

	/**
	 * Creates a new instance of an user. This constructor should not be used by developers.
	 * @param userCode User code
	 */
	public TestUser (String userCode) {
		super(userCode);
		this.testItems = new DynamicArray<String>();
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
	 * Returns the test item code at index position. 
	 * @param index Index.
	 * @return Test item code at index. 
	 */
	public String getTestItemAt(int index) {
		return this.testItems.get(index);
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
	 * Get the index of an test item code at the test items array of the user.
	 * @param item_code Item code
	 * @return Test item index if the user has rated the item or -1 if not
	 */
	public int getTestItemIndex (String item_code) {
		return testItems.get(item_code);
	}
	
	/**
	 * Get the number of test ratings of the user.
	 * @return Number of test ratings made
	 */
	public int getNumberOfTestRatings () {
		return this.testRatings.size();
	}

	/**
	 * Add/Modify a new test rating to the test user, associated to a item.
	 * @param itemCode itemCode which identify the specific item.
	 * @param rating rated value by user, refering this item.
	 */
	public void addTestRating(String itemCode, double rating){
		int positionInArray = this.testItems.get(itemCode);

		if (positionInArray != -1){ //If element already exists.
			this.testItems.set(positionInArray, itemCode);
			this.testRatings.set(positionInArray, rating);
		}else{ //If not exist.
			this.testRatings.add(this.testItems.addOrdered(itemCode), rating);
		}
	}
}
