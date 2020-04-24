package es.upm.etsisi.cf4j.data.types;

import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.types.DataSetEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MockDataSet implements DataSet {

    private ArrayList<DataSetEntry> ratings;
    private ArrayList<DataSetEntry> testRatings;

    public MockDataSet(){
        ratings = new ArrayList<>(Arrays.asList(
            new DataSetEntry("Laurie","Potatoad",3.1),
            new DataSetEntry("Laurie","Yeah,IsWired",3.2),
            new DataSetEntry("Laurie","Milk",3.3),
            new DataSetEntry("Mike","Milk",4.1),
            new DataSetEntry("Mike","WiredThing",0),
            new DataSetEntry("Mike","Potatoad",4.3),
            new DataSetEntry("Mike","Yeah,IsWired",4.4),
            new DataSetEntry("Tim","WiredThing",1.3),
            new DataSetEntry("Tim","Yeah,IsWired",1.4),
            new DataSetEntry("Kim","WiredThing",2.3),
            new DataSetEntry("Kim","Yeah,IsWired",2.4)
        ));

        testRatings = new ArrayList<>(Arrays.asList(
            new DataSetEntry("Tim","Milk", 1.1),
            new DataSetEntry("Tim","Potatoad", 1.2),
            new DataSetEntry("Kim","Milk", 2.1),
            new DataSetEntry("Kim","Potatoad", 2.1)
        ));
    }

    @Override
    public Iterator<DataSetEntry> getRatingsIterator() {
        return ratings.iterator();
    }

    @Override
    public int getNumberOfRatings() {
        return ratings.size();
    }

    @Override
    public Iterator<DataSetEntry> getTestRatingsIterator() {
        return testRatings.iterator();
    }

    @Override
    public int getNumberOfTestRatings() {
        return testRatings.size();
    }
}
