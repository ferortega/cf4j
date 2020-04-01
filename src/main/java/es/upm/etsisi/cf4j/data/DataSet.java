package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import java.util.Iterator;

/**
 * This interface work as a bridge between the raw file and the DataModel (defined memory structure to finally use the
 * recommendation system). This interface ensures data separated into two groups: no-test and test.
 * The data, obtained from the files, must be returned in DataSetEntries which structure is conformed by user, item and rating.
 */
public interface DataSet{
    /**
     * This method generates an iterator to navigate through the raw ratings stored in DataSetEntries.
     * @return Iterator of ratings
     */
    Iterator<DataSetEntry> getRatingsIterator();

    /**
     * This method indicates the size of the Ratings stored data.
     * @return Number of elements of the stored data.
     */
    int getNumberOfRatings();

    /**
     * This method generates an iterator to navigate through the raw test ratings stored in DataSetEntries.
     * @return Iterator of test ratings
     */
    Iterator<DataSetEntry> getTestRatingsIterator();

    /**
     * This method indicates the size of the Test Ratings stored data
     * @return Number of elements of the test stored data.
     */
    int getNumberOfTestRatings();
}

