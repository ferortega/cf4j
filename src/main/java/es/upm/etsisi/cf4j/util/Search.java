package es.upm.etsisi.cf4j.util;

/** This class contains useful search methods. */
public class Search {

  /**
   * Gets efficiently the userIndex of an element in an array of integers
   *
   * @param array Array of integers sorted from lowest to highest
   * @param value Value which calculates the position
   * @return Index of the item or -1 if not found
   */
  public static int getIndex(int[] array, int value) {
    int min = 0, max = array.length - 1;
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
   * Returns the indexes of the biggest n elements of the values array. If the values arrays is
   * smaller than N, the returned array is completed with -1. The NaN values are ignored.
   *
   * @param values Array to search its top n elements
   * @param n Number of elements to obtain
   * @return Indexes of values sorted by higher to lower
   */
  public static int[] findTopN(double[] values, int n) {

    int[] indexes = new int[n];

    double[] aux = new double[n];

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
        for (; i < indexes.length; i++) indexes[i] = -1;
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
}
