package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.Pair;
import es.upm.etsisi.cf4j.data.types.SortedArrayList;

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

	protected SortedArrayList<Pair<String, Double>> testUsersRatings;

	/**
	 * Creates a new instance of a test item. This constructor should not be used by developers.
	 * @param itemCode Item code
	 */
	public TestItem (String itemCode) {
		super(itemCode);
		this.testUsersRatings = new SortedArrayList<Pair<String, Double>>();
	}
	
	/**
	 * Returns the test user code at index position. 
	 * @param testUserLocalIndex Index.
	 * @return Test user code at index. 
	 */
	public String getTestUser(int testUserLocalIndex) {
		if (testUserLocalIndex < 0 || testUserLocalIndex > this.testUsersRatings.size())
			return null;

		return this.testUsersRatings.get(testUserLocalIndex).key;
	}

	/**
	 * Returns the test rating at index position. 
 	 * @param testUserLocalIndex Index.
	 * @return Test rating at index. 
	 */
	public Double getTestRating(int testUserLocalIndex) {
		if (testUserLocalIndex < 0 || testUserLocalIndex > this.testUsersRatings.size())
			return null;

		return this.testUsersRatings.get(testUserLocalIndex).value;
	}
	
	/**
	 * Get the index of an user code at the test user's item array.
	 * @param userCode User code
	 * @return Test user index in the test user's item array if the user has rated the item or -1 if dont
	 */
	public int findTestUserRating (String userCode) {
		//We need create a aux Pair to get the real one localIndex
		Pair<String,Double> aux = new Pair<String, Double>(userCode,0.0);
		return this.testUsersRatings.find(aux);
	}
	
	/**
	 * Get the number of test ratings that the item have received.
	 * @return Number of test ratings received
	 */
	public int getNumberOfTestRatings () {
		return this.testUsersRatings.size();
	}

	/**
	 * Add/Modify a new test rating to the testItem, associated to a user.
	 * @param userCode userCode which identify the specific user.
	 * @param rating rated value of the user, refering this item.
	 */
	public void addTestRating(String userCode, double rating){
		if (this.testUsersRatings.add(new Pair<String, Double>(userCode, rating)))
			totalRatings++;

		min = Math.min(rating, min);
		max = Math.max(rating, max);
		average = (totalRatings <= 1) ? rating : ((average * (totalRatings-1)) + rating) / totalRatings;
	}
}
