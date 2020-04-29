package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * DataBank is focused on storing heterogeneous data as a support to the calculations made in the
 * recommendation process.
 *
 * <p>Only the following types are allowed to be stored: boolean, boolean[], int, int[], double,
 * double[], String and String[].
 */
public class DataBank implements Serializable {

  /**
   * Stored map of elements. With this map, you can store objects with his associated name or key.
   */
  private HashMap<String, Object> map = new HashMap<>();

  /** Deletes all content of this DataBank. */
  public void deleteAll() {
    map.clear();
  }

  /**
   * Deletes a value associated to a single element key.
   *
   * @param key Key where the data is stored
   * @return true if the key was found
   */
  public boolean delete(String key) {
    return map.remove(key) != null;
  }

  /**
   * Finds if an element exist inside the DataBank
   *
   * @param key Key to be searched
   * @return true if the key exist inside the DataBank
   */
  public boolean contains(String key) {
    return map.get(key) != null;
  }

  /**
   * Sets or stores a boolean inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value boolean value to be stored.
   */
  public void setBoolean(String key, boolean value) {
    this.map.put(key, value);
  }

  /**
   * Sets or stores a boolean array inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value boolean array to be stored.
   */
  public void setBooleanArray(String key, boolean[] value) {
    this.map.put(key, value);
  }

  /**
   * Sets or stores an int inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value int value to be stored.
   */
  public void setInt(String key, int value) {
    this.map.put(key, value);
  }

  /**
   * Sets or stores an int array inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value int array to be stored.
   */
  public void setIntArray(String key, int[] value) {
    this.map.put(key, value);
  }

  /**
   * Sets or stores a double inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value double value to be stored.
   */
  public void setDouble(String key, double value) {
    this.map.put(key, value);
  }

  /**
   * Sets or stores a double array inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value double array to be stored.
   */
  public void setDoubleArray(String key, double[] value) {
    this.map.put(key, value);
  }

  /**
   * Sets or stores an String inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value String value to be stored.
   */
  public void setString(String key, String value) {
    this.map.put(key, value);
  }

  /**
   * Sets or stores a double array inside the DataBank.
   *
   * @param key Key where the element will be stored.
   * @param value String array to be stored.
   */
  public void setStringArray(String key, String[] value) {
    this.map.put(key, value);
  }

  /**
   * Gets an stored boolean inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored boolean value or null if key does not exists
   */
  public boolean getBoolean(String key) {
    return (boolean) map.get(key);
  }

  /**
   * Gets an stored boolean array inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored boolean array value or null if key does not exists
   */
  public boolean[] getBooleanArray(String key) {
    return (boolean[]) map.get(key);
  }

  /**
   * Gets an stored int inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored int value or null if key does not exists
   */
  public int getInt(String key) {
    return (int) map.get(key);
  }

  /**
   * Gets an stored int array inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored int array value or null if key does not exists
   */
  public int[] getIntArray(String key) {
    return (int[]) map.get(key);
  }

  /**
   * Gets an stored double inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored double value or null if key does not exists
   */
  public double getDouble(String key) {
    return (double) map.get(key);
  }

  /**
   * Gets an stored double array inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored double array value or null if key does not exists
   */
  public double[] getDoubleArray(String key) {
    return (double[]) map.get(key);
  }

  /**
   * Gets an stored String inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored String value or null if key does not exists
   */
  public String getString(String key) {
    return (String) map.get(key);
  }

  /**
   * Gets an stored String array inside the DataBank.
   *
   * @param key Key where the element should be stored
   * @return Stored String array value or null if key does not exists
   */
  public String[] getStringArray(String key) {
    return (String[]) map.get(key);
  }
}
