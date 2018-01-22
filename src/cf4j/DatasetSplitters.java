package cf4j;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * <p>This class contains different built-in lambda functions to split dataset into test and training sets</p>
 * @author Fernando Ortega
 */
public class DatasetSplitters {

	/**
	 * Divides the set based on a random probability. If a random is lower than the probability, the element
	 * will be defined as test. Otherwise, the element will be defined as training.
	 * @param probability Probability of the element to be defined as test.
	 * @return True if element is test; False otherwise
	 */
	public static BiFunction <Integer, Map <Integer, Double>, Boolean> random (double probability) {
		return (code, ratings) -> { return Math.random() <= probability; }; 
	}
	
	/**
	 * Divides the set based on the number of ratings of each element. It the element has at least minNumberOfRatings
	 * ratings it will be defined as test. Otherwise, the element will be defined as training.
	 * @param minNumberOfRatings Minimum number of ratings of an element to be defined as test.
	 * @return True if element is test; False otherwise
	 */
	public static BiFunction <Integer, Map <Integer, Double>, Boolean> minNumberOfRatings (int minNumberOfRatings) {
		return (code, ratings) -> { return ratings.size() >= minNumberOfRatings; }; 
	}
	
	/**
	 * Divides the set based on the number of ratings of each element. It the element has equal or less than 
	 * maxNumberOfRatings it will be defined as test. Otherwise, the element will be defined as training. Useful
	 * for cold start situations.
	 * @param maxNumberOfRatings Maximum number of ratings of an element to be defined as test.
	 * @return True if element is test; False otherwise
	 */
	public static BiFunction <Integer, Map <Integer, Double>, Boolean> maxNumberOfRatings (int maxNumberOfRatings) {
		return (code, ratings) -> { return ratings.size() <= maxNumberOfRatings; }; 
	}
	
	/**
	 * Divides the set based on an explicit list of codes. It the element code is included on the list, it will
	 * be defined as test. Otherwise, the element will be defined as training.
	 * @param codes List of test codes.
	 * @return True if element is test; False otherwise
	 */
	public static BiFunction <Integer, Map <Integer, Double>, Boolean> explicit (List <Integer> codes) {
		return (code, ratings) -> { return codes.contains(code); }; 
	}
}
