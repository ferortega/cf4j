package es.upm.etsisi.cf4j.util.optimization;

import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.IntStream;

/**
 * This class generates the development set for a grid search. You can add two kind of params:
 *
 * <ul>
 *   <li><b>Fixed params</b>: They take the same value in all entries of the development set.
 *   <li><b>Params</b>: Development set is generated with all the permutations defined by params
 *       valid values.
 * </ul>
 *
 * The development set is generated as an iterator of Map&lt;String, Object&gt; that contains the
 * parameter name (String) and value (Object) of each development set entry. The casting of the
 * values into the appropriate data type must be performed by the methods that uses the development
 * set entries.
 */
public class ParamsGrid {

  /** Grid parameters to generate the development set */
  private List<Pair<String, Object[]>> grid;

  /** Creates a new ParamsGrid */
  public ParamsGrid() {
    this.grid = new ArrayList<>();
  }

  /**
   * Adds a variable parameter
   *
   * @param name Name of the parameter
   * @param values Values to be evaluated
   */
  public void addParam(String name, Object[] values) {
    Pair<String, Object[]> param = new Pair<>(name, values);
    this.grid.add(param);
  }

  /**
   * Adds a fixed parameter
   *
   * @param name Name of the parameter
   * @param value Value
   */
  public void addFixedParam(String name, Object value) {
    Object[] values = {value};
    this.addParam(name, values);
  }

  /**
   * Adds a fixed parameter
   *
   * @param name Name of the parameter
   * @param value String value
   */
  public void addFixedParam(String name, String value) {
    this.addFixedParam(name, (Object) value);
  }

  /**
   * Adds a variable parameter
   *
   * @param name Name of the parameter
   * @param values String values to be evaluated
   */
  public void addParam(String name, String[] values) {
    this.addParam(name, (Object[]) values);
  }

  /**
   * Adds a fixed parameter
   *
   * @param name Name of the parameter
   * @param value double value
   */
  public void addFixedParam(String name, double value) {
    this.addFixedParam(name, new Double(value));
  }

  /**
   * Adds a variable parameter
   *
   * @param name Name of the parameter
   * @param values double values to be evaluated
   */
  public void addParam(String name, double[] values) {
    this.addParam(name, Arrays.stream(values).boxed().toArray(Double[]::new));
  }

  /**
   * Adds a fixed parameter
   *
   * @param name Name of the parameter
   * @param value int value
   */
  public void addFixedParam(String name, int value) {
    this.addFixedParam(name, new Integer(value));
  }

  /**
   * Adds a variable parameter
   *
   * @param name Name of the parameter
   * @param values int values to be evaluated
   */
  public void addParam(String name, int[] values) {
    this.addParam(name, Arrays.stream(values).boxed().toArray(Integer[]::new));
  }

  /**
   * Adds a fixed parameter
   *
   * @param name Name of the parameter
   * @param value long value
   */
  public void addFixedParam(String name, long value) {
    this.addFixedParam(name, new Long(value));
  }

  /**
   * Adds a variable parameter
   *
   * @param name Name of the parameter
   * @param values long values to be evaluated
   */
  public void addParam(String name, long[] values) {
    this.addParam(name, Arrays.stream(values).boxed().toArray(Long[]::new));
  }

  /**
   * Adds a fixed parameter
   *
   * @param name Name of the parameter
   * @param value boolean
   */
  public void addFixedParam(String name, boolean value) {
    this.addFixedParam(name, new Boolean(value));
  }

  /**
   * Adds a variable parameter
   *
   * @param name Name of the parameter
   * @param values boolean values to be evaluated
   */
  public void addParam(String name, boolean[] values) {
    this.addParam(
        name, IntStream.range(0, values.length).mapToObj(i -> values[i]).toArray(Boolean[]::new));
  }

  /**
   * Returns the development set created from the grid parameters
   *
   * @return Development set
   */
  public Iterator<Map<String, Object>> getDevelopmentSetIterator() {
    List<Map<String, Object>> permutations = getPermutations(this.grid);
    return permutations.iterator();
  }

  /**
   * Generates the permutations from a grid parameter configuration
   *
   * @param grid List of Pairs where the key is the parameter name and the value is the plausible
   *     values ot that parameter
   * @return List of Maps with all permutations
   */
  private List<Map<String, Object>> getPermutations(List<Pair<String, Object[]>> grid) {
    List<Map<String, Object>> result = new ArrayList<>();

    if (grid.size() == 1) {
      for (Object value : grid.get(0).getValue()) {
        Map<String, Object> params = new HashMap<>();
        params.put(grid.get(0).getKey(), value);
        result.add(params);
      }

    } else {
      List<Map<String, Object>> permutations = getPermutations(grid.subList(1, grid.size()));
      for (Object value : grid.get(0).getValue()) {
        for (Map<String, Object> permutation : permutations) {
          Map<String, Object> params = new HashMap<>(permutation);
          params.put(grid.get(0).getKey(), value);
          result.add(params);
        }
      }
    }

    return result;
  }
}
