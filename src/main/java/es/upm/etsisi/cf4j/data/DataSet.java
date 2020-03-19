package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import java.util.Iterator;

public interface DataSet{
    /**
     * <p>This method generates an iterator to get de current ratings</p>
     * @return Iterator of ratings
     */
    Iterator<DataSetEntry> getRatingsIterator();

    /**
     * <p>This method indicates the size of the Ratings stored data</p>
     * @return Number of elements of the stored data.
     */
    int getRatingsSize();

    /**
     * <p>This method generates an iterator to get de current test ratings</p>
     * @return Iterator of test ratings
     */
    Iterator<DataSetEntry> getTestRatingsIterator();

    /**
     * <p>This method indicates the size of the Test Ratings stored data</p>
     * @return Number of elements of the test stored data.
     */
    int getTestRatingsSize();
}

