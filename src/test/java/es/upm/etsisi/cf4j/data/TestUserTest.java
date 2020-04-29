package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestUserTest {

  private static TestUser testUser;
  private static User wiredUser;
  private static Item item;
  private static Item wiredItem;
  private static TestItem testItem;

  @BeforeAll
  static void initAll() {
    testUser = new TestUser("202", 202, 202);
    wiredUser = new TestUser("303", 303, 303);
    item = new Item("010", 10);
    testItem = new TestItem("020", 20, 20);
    wiredItem = new TestItem("030", 30, 30);
  }

  @Test
  void testUserInsertion() {
    testUser.addTestRating(111, 1.3);
    testUser.addTestRating(item.getItemIndex(), 1.0);
    testUser.addTestRating(222, 2.4);
    testUser.addTestRating(testItem.getTestItemIndex(), 2.0);
    testUser.addTestRating(333, 3.5);
    testUser.addTestRating(wiredItem.getItemIndex(), 3.0);

    testUser.addRating(111, 1.3);
    testUser.addRating(item.getItemIndex(), 1.0);
    testUser.addRating(222, 2.4);
    testUser.addRating(testItem.getTestItemIndex(), 2.0);
    testUser.addRating(333, 3.5);
    testUser.addRating(wiredItem.getItemIndex(), 3.0);

    assertEquals(0, testUser.findItem(item.getItemIndex()));
    assertEquals(item.getItemIndex(), testUser.getTestItemAt(0));
    assertEquals(1.0, testUser.getTestRatingAt(0));
    assertEquals(1, testUser.findItem(testItem.getTestItemIndex()));
    assertEquals(testItem.getTestItemIndex(), testUser.getTestItemAt(1));
    assertEquals(2.0, testUser.getTestRatingAt(1));
    assertEquals(2, testUser.findItem(wiredItem.getItemIndex()));
    assertEquals(wiredItem.getItemIndex(), testUser.getTestItemAt(2));
    assertEquals(3.0, testUser.getTestRatingAt(2));
    assertEquals(111, testUser.getTestItemAt(3));
    assertEquals(1.3, testUser.getTestRatingAt(3));
    assertEquals(222, testUser.getTestItemAt(4));
    assertEquals(2.4, testUser.getTestRatingAt(4));
    assertEquals(333, testUser.getTestItemAt(5));
    assertEquals(3.5, testUser.getTestRatingAt(5));

    assertEquals(0, testUser.findItem(item.getItemIndex()));
    assertEquals(item.getItemIndex(), testUser.getItemAt(0));
    assertEquals(1.0, testUser.getRatingAt(0));
    assertEquals(1, testUser.findItem(testItem.getTestItemIndex()));
    assertEquals(testItem.getTestItemIndex(), testUser.getItemAt(1));
    assertEquals(2.0, testUser.getRatingAt(1));
    assertEquals(2, testUser.findItem(wiredItem.getItemIndex()));
    assertEquals(wiredItem.getItemIndex(), testUser.getItemAt(2));
    assertEquals(3.0, testUser.getRatingAt(2));
    assertEquals(111, testUser.getItemAt(3));
    assertEquals(1.3, testUser.getRatingAt(3));
    assertEquals(222, testUser.getItemAt(4));
    assertEquals(2.4, testUser.getRatingAt(4));
    assertEquals(333, testUser.getItemAt(5));
    assertEquals(3.5, testUser.getRatingAt(5));

    assertEquals("202", testUser.getId());
    assertEquals(202, testUser.getUserIndex());
    assertEquals(6, testUser.getNumberOfRatings());
    assertEquals(1, testUser.getMinRating());
    assertEquals(3.5, testUser.getMaxRating());
    assertTrue(Math.abs(testUser.getRatingAverage() - 2.2) <= Math.ulp(2.2));
    assertEquals(202, testUser.getTestUserIndex());
    assertEquals(6, testUser.getNumberOfTestRatings());
    assertEquals(1, testUser.getMinTestRating());
    assertEquals(3.5, testUser.getMaxTestRating());
    assertTrue(Math.abs(testUser.getTestRatingAverage() - 2.2) <= Math.ulp(2.2));
  }

  @Test
  void testUserCastedToUserInsertion() {
    wiredUser.addRating(111, 1.3);
    wiredUser.addRating(item.getItemIndex(), 1.0);
    wiredUser.addRating(222, 2.4);
    wiredUser.addRating(testItem.getTestItemIndex(), 2.0);
    wiredUser.addRating(333, 3.5);
    wiredUser.addRating(wiredItem.getItemIndex(), 3.0);

    assertEquals(0, wiredUser.findItem(item.getItemIndex()));
    assertEquals(item.getItemIndex(), wiredUser.getItemAt(0));
    assertEquals(1.0, wiredUser.getRatingAt(0));
    assertEquals(1, wiredUser.findItem(testItem.getTestItemIndex()));
    assertEquals(testItem.getTestItemIndex(), wiredUser.getItemAt(1));
    assertEquals(2.0, wiredUser.getRatingAt(1));
    assertEquals(2, wiredUser.findItem(wiredItem.getItemIndex()));
    assertEquals(wiredItem.getItemIndex(), wiredUser.getItemAt(2));
    assertEquals(111, wiredUser.getItemAt(3));
    assertEquals(1.3, wiredUser.getRatingAt(3));
    assertEquals(3.0, wiredUser.getRatingAt(2));
    assertEquals(222, wiredUser.getItemAt(4));
    assertEquals(2.4, wiredUser.getRatingAt(4));
    assertEquals(333, wiredUser.getItemAt(5));
    assertEquals(3.5, wiredUser.getRatingAt(5));

    assertEquals("303", wiredUser.getId());
    assertEquals(303, wiredUser.getUserIndex());
    assertEquals(6, wiredUser.getNumberOfRatings());
    assertEquals(1, wiredUser.getMinRating());
    assertEquals(3.5, wiredUser.getMaxRating());
    assertTrue(Math.abs(wiredUser.getRatingAverage() - 2.2) <= Math.ulp(2.2));
  }
}
