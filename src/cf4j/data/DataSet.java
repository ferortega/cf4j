package cf4j.data;

import cf4j.data.types.Triplet;
import java.util.Iterator;

public interface DataSet{
    /**
     * <p>This method generates an iterator to get de current ratings</p>
     * @return Iterator of ratings
     */
    public Iterator<DataSetEntry> getRatingsIterator();

    /**
     * <p>This method generates an iterator to get de current test ratings</p>
     * @return Iterator of test ratings
     */
    public Iterator<DataSetEntry> getTestRatingsIterator();

    class DataSetEntry extends Triplet<String,String,Double>{
        public DataSetEntry(String first, String second, Double third) {
            super(first, second, third);
        }
    };
}

