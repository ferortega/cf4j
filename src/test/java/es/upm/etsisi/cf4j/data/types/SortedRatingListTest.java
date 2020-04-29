package es.upm.etsisi.cf4j.data.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SortedRatingListTest {

  @Test
  void add() {
    SortedRatingList testArray = new SortedRatingList();

    testArray.add(1, 1.234);
    testArray.add(0, 0.123);
    testArray.add(5, 5.678);
    testArray.add(3, 3.456);
    testArray.add(2, 2.345);
    testArray.add(4, 4.567);

    // Asserting everything is OK
    assertEquals(0.123, testArray.get(0).getRating());
    assertEquals(0, testArray.get(0).getIndex());
    assertEquals(1.234, testArray.get(1).getRating());
    assertEquals(1, testArray.get(1).getIndex());
    assertEquals(2.345, testArray.get(2).getRating());
    assertEquals(2, testArray.get(2).getIndex());
    assertEquals(3.456, testArray.get(3).getRating());
    assertEquals(3, testArray.get(3).getIndex());
    assertEquals(4.567, testArray.get(4).getRating());
    assertEquals(4, testArray.get(4).getIndex());
    assertEquals(5.678, testArray.get(5).getRating());
    assertEquals(5, testArray.get(5).getIndex());
  }

  @Test
  void find() {
    SortedRatingList testArray = new SortedRatingList();

    testArray.add(15, 15.678);
    testArray.add(0, 0.123);
    testArray.add(5, 5.678);
    testArray.add(20, 20.123);
    testArray.add(87, 87.890);

    assertEquals(testArray.get(testArray.find(0)).getRating(), 0.123);
    assertEquals(testArray.get(testArray.find(0)).getIndex(), 0);
    assertEquals(testArray.get(testArray.find(5)).getRating(), 5.678);
    assertEquals(testArray.get(testArray.find(5)).getIndex(), 5);
    assertEquals(testArray.get(testArray.find(15)).getRating(), 15.678);
    assertEquals(testArray.get(testArray.find(15)).getIndex(), 15);
    assertEquals(testArray.get(testArray.find(20)).getRating(), 20.123);
    assertEquals(testArray.get(testArray.find(20)).getIndex(), 20);
    assertEquals(testArray.get(testArray.find(87)).getRating(), 87.890);
    assertEquals(testArray.get(testArray.find(87)).getIndex(), 87);
  }
}
