package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.types.DataSetEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MockDataSet implements DataSet {

  private ArrayList<DataSetEntry> ratings;
  private ArrayList<DataSetEntry> testRatings;

  public MockDataSet() {
    ratings =
        new ArrayList<>(
            Arrays.asList(
                new DataSetEntry("Laurie", "Potatoad", 1.0),
                new DataSetEntry("Laurie", "Yeah,IsWired", 2.0),
                new DataSetEntry("Laurie", "Milk", 5.0),
                new DataSetEntry("Mike", "Milk", 4.0),
                new DataSetEntry("Mike", "WiredThing", 1.0),
                new DataSetEntry("Mike", "Potatoad", 2.0),
                new DataSetEntry("Mike", "Yeah,IsWired", 3.0),
                new DataSetEntry("Tim", "WiredThing", 1.0),
                new DataSetEntry("Tim", "Yeah,IsWired", 2.0),
                new DataSetEntry("Kim", "WiredThing", 3.0),
                new DataSetEntry("Kim", "Yeah,IsWired", 4.0)));

    testRatings =
        new ArrayList<>(
            Arrays.asList(
                new DataSetEntry("Tim", "Milk", 1.0),
                new DataSetEntry("Tim", "Potatoad", 2.0),
                new DataSetEntry("Kim", "Milk", 5.0),
                new DataSetEntry("Kim", "Potatoad", 1.0)));
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
