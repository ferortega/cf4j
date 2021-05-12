package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import javax.annotation.Nullable;

public class ManualDataSet implements DataSet {

    /** Raw stored ratings */
    protected ArrayList<DataSetEntry> ratings;

    /** Raw stored test ratings */
    protected ArrayList<DataSetEntry> testRatings;

    /**
     * Method separates the given data set entries into train and test rating
     * depending on the trainPercentage.
     * This class is used when the manual import of data is required instead of using
     * csv or similar formats.
     * 
     * Randomized element is introduces by shuffling the given entries.
     * 
     * @param entries arraylist of datasetentries
     * @param trainPercentage set custom train data percentage
     */
    public ManualDataSet(ArrayList<DataSetEntry> entries, double trainPercentage, @Nullable Long seed){

        ratings = new ArrayList<>();
        testRatings = new ArrayList<>();

        if (seed != null)
            Collections.shuffle(entries, new Random(seed));
        else
            Collections.shuffle(entries, new Random());
        

        int cut = (int) (entries.size() * trainPercentage);
        int cnt = 0;
        System.out.println(entries.size());

        for (DataSetEntry entry : entries)
        {
            if (cnt >= cut)
            {
                testRatings.add(entry);
                continue;
            }
            else
                ratings.add(entry);
            cnt++;
        }
    }

    @Override
    public Iterator<DataSetEntry> getRatingsIterator() {
        return ratings.iterator();
    }

    @Override
    public Iterator<DataSetEntry> getTestRatingsIterator() {
        return testRatings.iterator();
    }

    @Override
    public int getNumberOfRatings() {
        return ratings.size();
    }

    @Override
    public int getNumberOfTestRatings() {
        return testRatings.size();
    }
}
