package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.Pair;
import es.upm.etsisi.cf4j.data.types.SortedArrayList;
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

	protected SortedArrayList<Pair<String, Double>> testItemsRatings;

	/**
	 * Creates a new instance of an user. This constructor should not be used by developers.
	 * @param userCode User code
	 */
	public TestUser (String userCode) {
		super(userCode);
		this.testItemsRatings = new SortedArrayList<Pair<String, Double>>();
	}

	/**
	 * Returns the test item code at index position. 
	 * @param testItemLocalIndex Index.
	 * @return Test item code at index. 
	 */
	public String getTestItem(int testItemLocalIndex) {
		if (testItemLocalIndex < 0 || testItemLocalIndex > this.testItemsRatings.size())
			return null;

		return this.testItemsRatings.get(testItemLocalIndex).key;
	}
	
	/**
	 * Returns the test rating at index position. 
	 * @param testItemLocalIndex Index.
	 * @return Test rating at index. 
	 */
	public Double getTestRating(int testItemLocalIndex) {
		if (testItemLocalIndex < 0 || testItemLocalIndex > this.testItemsRatings.size())
			return null;

		return this.testItemsRatings.get(testItemLocalIndex).value;
	}
	
	/**
	 * Get the index of an test item code at the test items array of the user.
	 * @param itemCode Item code
	 * @return Test item index if the user has rated the item or -1 if dont
	 */
	public int findTestUserRating (String itemCode) {
		//We need create a aux Pair to get the real one localIndex
		Pair<String,Double> aux = new Pair<String, Double>(itemCode,0.0);
		return this.testItemsRatings.find(aux);
	}
	
	/**
	 * Get the number of test ratings of the user.
	 * @return Number of test ratings made
	 */
	public int getNumberOfTestRatings () {
		return this.testItemsRatings.size();
	}

	/**
	 * Add/Modify a new test rating to the test user, associated to a item.
	 * @param itemCode itemCode which identify the specific item.
	 * @param rating rated value by user, refering this item.
	 */
	public void addTestRating(String itemCode, double rating){
		if (this.testItemsRatings.add(new Pair<String, Double>(itemCode, rating))) {
			totalRatings++;
			average = (totalRatings <= 1) ? rating : ((average * (totalRatings-1)) + rating) / totalRatings;
		}else{
			average = 0.0;
			for (Pair<String, Double> pair : this.testItemsRatings)
				average += pair.value;
			average /= totalRatings;
		}

		min = Math.min(rating, min);
		max = Math.max(rating, max);
	}
}
