package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;

import java.util.Iterator;

/**
 * This interface works as a bridge between the raw file and the DataModel. This interface ensures
 * collaborative filtering ratings separated into two groups: no-test and test. The ratings must be
 * returned in DataSetEntries which structure is conformed by user, item and rating.
 */
public interface DataSet {

  /**
   * This method generates an iterator to navigate through the raw ratings stored in DataSetEntries.
   *
   * @return Iterator of ratings
   */
  Iterator<DataSetEntry> getRatingsIterator();

  /**
   * This method indicates the number of (training) ratings.
   *
   * @return Number of (training) ratings
   */
  int getNumberOfRatings();

  /**
   * This method generates an iterator to navigate through the raw test ratings stored in
   * DataSetEntries.
   *
   * @return Iterator of test ratings
   */
  Iterator<DataSetEntry> getTestRatingsIterator();

  /**
   * This method indicates the number of test ratings.
   *
   * @return Number of test ratings
   */
  int getNumberOfTestRatings();
}
