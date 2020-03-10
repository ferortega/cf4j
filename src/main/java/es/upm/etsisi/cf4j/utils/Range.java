package cf4j.utils;

public class Range {

	/**
	 * Creates a range of int values
	 * @param from Initial value
	 * @param step Step size
	 * @param numValues Number of values of the range
	 * @return Int array with the range
	 */
	public static int [] ofIntegers (int from, int step, int numValues) {
		int [] range = new int [numValues];		
		for (int i = 0; i < numValues; i++) {
			range[i] = from + i * step;
		}
		return range;
	}
	
	/**
	 * Creates a range of double values
	 * @param from Initial value
	 * @param step Step size
	 * @param numValues Number of values of the range
	 * @return Double array with the range
	 */
	public static double [] ofDoubles (double from, double step, int numValues) {
		double [] range = new double [numValues];		
		for (int i = 0; i < numValues; i++) {
			range[i] = from + i * step;
		}
		return range;
	}
}
