package cf4j.data;

import cf4j.utils.Triplet;
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

    //TODO: Permitir que los identificadores sean Strings en todo el código???
    class DataSetEntry extends Triplet<Integer,Integer,Double>{
        public DataSetEntry(Integer first, Integer second, Double third) {
            super(first, second, third);
        }
    };
}

