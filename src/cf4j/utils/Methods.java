package cf4j.utils;

/**
 * This class contains useful methods to work with during the development of the implementations
 * with this API.
 * 
 * @author Fernando Ortega
 */
public class Methods {
	 
	 /**
	  * Calculates the average of an double array
	  * @param array Array of double from which to calculate the mean
	  * @return Array mean
	  */
	 public static double arrayAverage (double [] array) {
		 double average = 0f;
		 for (double d : array) average += d;
		 return average / (double) array.length;
	 }

	/**
	 * Calculates the standard deviation of an double array
	 * @param array Array of double from which to calculate the standard deviation
	 * @return Standard deviation of the array
	 */
	public static double arrayStandardDeviation (double [] array) {
		double average = Methods.arrayAverage(array);
		double standard_deviation = 0f;
		for (double d : array) standard_deviation += (d - average) * (d - average);
		return Math.sqrt(standard_deviation / (double) array.length);
	}
	
	 /**
	  * Calculates the average of an int array
	  * @param array Array of int from which to calculate the mean
	  * @return Array mean
	  */
	 public static double arrayAverage (int [] array) {
		 double average = 0f;
		 for (int i : array) average += i;
		 return average / (double) array.length;
	 }

	/**
	 * Calculate the standard deviation of an int array
	 * @param array Array of int from which to calculate the standard deviation
	 * @return Standard deviation of the array
	 */
	public static double arrayStandardDeviation (int [] array) {
		double average = Methods.arrayAverage(array);
		double standard_deviation = 0f;
		for (int i : array) standard_deviation += (i - average) * (i - average);
		return Math.sqrt(standard_deviation / (double) array.length);
	}

	
	/**
	 * Gets efficiently the index of an element in an array of integers
	 * @param array Array of integers sorted from lowest to highest
	 * @param value Value which calculates the position
	 * @return Index of the item or -1 if not found
	 */
	public static int getIndex (int [] array, int value) {
		int min = 0, max = array.length -1;
		while (min <= max) {
			int center = ((max - min) / 2) + min;
			if (array[center] == value) return center;
			if (value < array[center]) {
				max = center - 1;
			} else {
				min = center + 1;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the indexes of the biggest n elements of the values array. If 
	 * the values arrays is smaller than N, the returned array is completed 
	 * with -1. The NaN values are ignored.
	 * @param values Array to search its top n elements
	 * @param n Number of elements to obtain
	 * @return Indexes of values sorted by higher to lower
	 */
	public static int [] findTopN (double [] values, int n) {

		int [] indexes = new int [n];

		double [] aux = new double [n];

		for (int i = 0; i < n; i++) {

			// Search highest value
			double value = Double.NEGATIVE_INFINITY;
			int index = -1;
			for (int v = 0; v < values.length; v++) {
				if (!Double.isNaN(values[v]) && values[v] > value) {
					value = values[v];
					index = v;
				}
			}

			// If there is no value, fill with -1
			if (index == -1) {
				for (; i < indexes.length; i++)	indexes[i] = -1;
			}

			// If there is value, add to solution and continue
			else {
				aux[i] = values[index];
				values[index] = Double.NEGATIVE_INFINITY;
				indexes[i] = index;
			}
		}

		// Restore modified values
		for (int i = 0; i < n; i++) {
			if (indexes[i] == -1) break;
			values[indexes[i]] = aux[i];
		}

		return indexes;
	}

	/**
	 * Dot product between two vectors
	 * @param a Vector A
	 * @param b Vector B
	 * @return dot_product(A, B)
	 */
	public static double dotProduct (double [] a, double [] b) {
		double r = 0;
		for (int i = 0; i < a.length; i++) r += a[i] * b[i];
		return r;
	}
}
