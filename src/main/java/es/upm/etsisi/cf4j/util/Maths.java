package es.upm.etsisi.cf4j.util;

import java.util.ArrayList;
import java.util.List;

/** This class contains useful math methods. */
public class Maths {

  /**
   * Calculates the average of an double array
   *
   * @param array Array of double from which to calculate the mean
   * @return Array mean
   */
  public static double arrayAverage(double[] array) {
    double average = 0f;
    for (double d : array) average += d;
    return average / (double) array.length;
  }

  /**
   * Calculates the standard deviation of an double array
   *
   * @param array Array of double from which to calculate the standard deviation
   * @return Standard deviation of the array
   */
  public static double arrayStandardDeviation(double[] array) {
    double average = Maths.arrayAverage(array);
    double standard_deviation = 0f;
    for (double d : array) standard_deviation += (d - average) * (d - average);
    return Math.sqrt(standard_deviation / (double) array.length);
  }

  /**
   * Calculates the average of an int array
   *
   * @param array Array of int from which to calculate the mean
   * @return Array mean
   */
  public static double arrayAverage(int[] array) {
    double average = 0f;
    for (int i : array) average += i;
    return average / (double) array.length;
  }

  /**
   * Calculate the standard deviation of an int array
   *
   * @param array Array of int from which to calculate the standard deviation
   * @return Standard deviation of the array
   */
  public static double arrayStandardDeviation(int[] array) {
    double average = Maths.arrayAverage(array);
    double standard_deviation = 0f;
    for (int i : array) standard_deviation += (i - average) * (i - average);
    return Math.sqrt(standard_deviation / (double) array.length);
  }

  /**
   * Dot product between two vectors
   *
   * @param a Vector A
   * @param b Vector B
   * @return dot_product(A, B)
   */
  public static double dotProduct(double[] a, double[] b) {
    double r = 0;
    for (int i = 0; i < a.length; i++) r += a[i] * b[i];
    return r;
  }

  /**
   * Returns the log in an specific base
   *
   * @param x Value
   * @param b Base
   * @return log of x in base b
   */
  public static double log(double x, double b) {
    return Math.log(x) / Math.log(b);
  }

  /**
   * Returns the logistic function g(x)
   *
   * @param x The given parameter x of the function g(x)
   * @return Value of the logistic function g(x)
   */
  public static double logistic(double x) {
    return 1.0 / (1.0 + Math.exp(-x));
  }

  /**
   * Returns an array of int values.
   * @param start An integer value specifying at which position to start. Default 0.
   * @param stop An integer value specifying at which position to stop.
   * @param step An integer value specifying the incrementation. Default 1.
   * @param endpoint If true, include the stop value. Otherwise, it is not included. Default true.
   * @return Values of the range.
   */
  public static int[] range(int start, int stop, int step, boolean endpoint) {
    List<Integer> l = new ArrayList<>();
    int v = start;
    while((!endpoint && v < stop) || (endpoint && v <= stop)) {
      l.add(v);
      v += step;
    }
    return l.stream().mapToInt(Integer::intValue).toArray();
  }

  /**
   * Returns an array of int values.
   * @param start An integer value specifying at which position to start. Default 0.
   * @param stop An integer value specifying at which position to stop.
   * @param step An integer value specifying the incrementation. Default 1.
   * @return Values of the range.
   */
  public static int[] range(int start, int stop, int step) {
    return range(start, stop, step, true);
  }

  /**
   * Returns an array of int values.
   * @param start An integer value specifying at which position to start. Default 0.
   * @param stop An integer value specifying at which position to stop.
   * @return Values of the range.
   */
  public static int[] range(int start, int stop) {
    return range(start, stop, 1);
  }

  /**
   * Returns an array of int values.
   * @param stop An integer value specifying at which position to stop.
   * @return Values of the range.
   */
  public static int[] range(int stop) {
    return range(0, stop);
  }

  /**
   * Returns num evenly spaced samples.
   * @param start The starting value. Default 0.0.
   * @param stop The stop value.
   * @param num Number of samples to generate.
   * @param endpoint If true, stop is the last sample. Otherwise, it is not included. Default true.
   * @return Double array with num evenly spaced samples.
   */
  public static double[] linespace(double start, double stop, int num, boolean endpoint) {
    double inc = (stop - start) / (endpoint ? num - 1 : num);
    double[] linespace = new double[num];
    for (int i = 0; i < num; i++) {
      linespace[i] = start + i * inc;
    }
    return linespace;
  }

  /**
   * Returns num evenly spaced samples.
   * @param start The starting value. Default 0.0.
   * @param stop The stop value.
   * @param num Number of samples to generate.
   * @return Double array with num evenly spaced samples.
   */
  public static double[] linespace(double start, double stop, int num) {
    return linespace(start, stop, num, true);
  }

  /**
   * Returns num evenly spaced samples.
   * @param stop The stop value.
   * @param num Number of samples to generate.
   * @return Double array with num evenly spaced samples.
   */
  public static double[] linespace(double stop, int num) {
    return linespace(0, stop, num);
  }

  /**
   * Return numbers spaced evenly on a log scale. The sequence starts at Math.pow(base, start).
   * @param start Math.pow(base, start) is the starting value of the sequence.
   * @param stop Math.pow(base, stop) is the last value of the sequence if endpoint is true.
   * @param num Number of samples to generate.
   * @param endpoint If true, stop is the last sample. Otherwise, it is not included. Default true.
   * @param base The base of the log space. Default 10.
   * @return Double array with num evenly spaced samples on a log scale.
   */
  public static double[] logspace(
      double start, double stop, int num, boolean endpoint, double base) {
    double[] exp = linespace(start, stop, num, endpoint);
    double[] logspace = new double[num];
    for (int i = 0; i < num; i++) {
      logspace[i] = Math.pow(base, exp[i]);
    }
    return logspace;
  }

  /**
   * Return numbers spaced evenly on a log scale. The sequence starts at Math.pow(base, start).
   * @param start Math.pow(base, start) is the starting value of the sequence.
   * @param stop Math.pow(base, stop) is the last value of the sequence if endpoint is true.
   * @param num Number of samples to generate.
   * @param endpoint If true, stop is the last sample. Otherwise, it is not included. Default true.
   * @return Double array with num evenly spaced samples on a log scale.
   */
  public static double[] logspace(double start, double stop, int num, boolean endpoint) {
    return logspace(start, stop, num, endpoint, 10);
  }

  /**
   * Return numbers spaced evenly on a log scale. The sequence starts at Math.pow(base, start).
   * @param start Math.pow(base, start) is the starting value of the sequence.
   * @param stop Math.pow(base, stop) is the last value of the sequence if endpoint is true.
   * @param num Number of samples to generate.
   * @param base The base of the log space. Default 10.
   * @return Double array with num evenly spaced samples on a log scale.
   */
  public static double[] logspace(double start, double stop, int num, double base) {
    return logspace(start, stop, num, true, base);
  }

  /**
   * Return numbers spaced evenly on a log scale. The sequence starts at Math.pow(base, start).
   * @param start Math.pow(base, start) is the starting value of the sequence.
   * @param stop Math.pow(base, stop) is the last value of the sequence if endpoint is true.
   * @param num Number of samples to generate.
   * @return Double array with num evenly spaced samples on a log scale.
   */
  public static double[] logspace(double start, double stop, int num) {
    return logspace(start, stop, num, true, 10);
  }
}
