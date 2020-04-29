package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestItemTest {

  private static User user;
  private static User wiredUser;
  private static TestUser testUser;
  private static Item wiredItem;
  private static TestItem testItem;

  @BeforeAll
  static void initAll() {
    user = new User("101", 101);
    testUser = new TestUser("202", 202, 202);
    wiredUser = new TestUser("303", 303, 303);
    testItem = new TestItem("020", 20, 20);
    wiredItem = new TestItem("030", 30, 30);
  }

  @Test
  void testItemInsertion() {
    testItem.addRating(111, 1.3);
    testItem.addRating(user.getUserIndex(), 1.0);
    testItem.addRating(222, 2.4);
    testItem.addRating(testUser.getTestUserIndex(), 2.0);
    testItem.addRating(333, 3.5);
    testItem.addRating(wiredUser.getUserIndex(), 3.0);

    testItem.addTestRating(111, 1.3);
    testItem.addTestRating(user.getUserIndex(), 1.0);
    testItem.addTestRating(222, 2.4);
    testItem.addTestRating(testUser.getTestUserIndex(), 2.0);
    testItem.addTestRating(333, 3.5);
    testItem.addTestRating(wiredUser.getUserIndex(), 3.0);

    assertEquals(0, testItem.findUser(user.getUserIndex()));
    assertEquals(user.getUserIndex(), testItem.getUserAt(0));
    assertEquals(1.0, testItem.getRatingAt(0));
    assertEquals(111, testItem.getUserAt(1));
    assertEquals(1.3, testItem.getRatingAt(1));
    assertEquals(2, testItem.findUser(testUser.getTestUserIndex()));
    assertEquals(testUser.getTestUserIndex(), testItem.getUserAt(2));
    assertEquals(2.0, testItem.getRatingAt(2));
    assertEquals(222, testItem.getUserAt(3));
    assertEquals(2.4, testItem.getRatingAt(3));
    assertEquals(4, testItem.findUser(wiredUser.getUserIndex()));
    assertEquals(wiredUser.getUserIndex(), testItem.getUserAt(4));
    assertEquals(3.0, testItem.getRatingAt(4));
    assertEquals(333, testItem.getUserAt(5));
    assertEquals(3.5, testItem.getRatingAt(5));

    assertEquals(0, testItem.findUser(user.getUserIndex()));
    assertEquals(user.getUserIndex(), testItem.getTestUserAt(0));
    assertEquals(1.0, testItem.getTestRatingAt(0));
    assertEquals(111, testItem.getTestUserAt(1));
    assertEquals(1.3, testItem.getTestRatingAt(1));
    assertEquals(2, testItem.findUser(testUser.getTestUserIndex()));
    assertEquals(testUser.getTestUserIndex(), testItem.getTestUserAt(2));
    assertEquals(2.0, testItem.getTestRatingAt(2));
    assertEquals(222, testItem.getTestUserAt(3));
    assertEquals(2.4, testItem.getTestRatingAt(3));
    assertEquals(4, testItem.findUser(wiredUser.getUserIndex()));
    assertEquals(wiredUser.getUserIndex(), testItem.getTestUserAt(4));
    assertEquals(3.0, testItem.getTestRatingAt(4));
    assertEquals(333, testItem.getTestUserAt(5));
    assertEquals(3.5, testItem.getTestRatingAt(5));

    assertEquals("020", testItem.getId());
    assertEquals(20, testItem.getItemIndex());
    assertEquals(6, testItem.getNumberOfRatings());
    assertEquals(1, testItem.getMinRating());
    assertEquals(3.5, testItem.getMaxRating());
    assertTrue(Math.abs(testItem.getRatingAverage() - 2.2) <= Math.ulp(2.2));
    assertEquals(20, testItem.getTestItemIndex());
    assertEquals(6, testItem.getNumberOfTestRatings());
    assertEquals(1, testItem.getMinTestRating());
    assertEquals(3.5, testItem.getMaxTestRating());
    assertTrue(Math.abs(testItem.getTestRatingAverage() - 2.2) <= Math.ulp(2.2));
  }

  @Test
  void testItemCastedToItemInsertion() {
    wiredItem.addRating(111, 1.3);
    wiredItem.addRating(user.getUserIndex(), 1.0);
    wiredItem.addRating(222, 2.4);
    wiredItem.addRating(testUser.getTestUserIndex(), 2.0);
    wiredItem.addRating(333, 3.5);
    wiredItem.addRating(wiredUser.getUserIndex(), 3.0);

    assertEquals(0, wiredItem.findUser(user.getUserIndex()));
    assertEquals(user.getUserIndex(), wiredItem.getUserAt(0));
    assertEquals(1.0, wiredItem.getRatingAt(0));
    assertEquals(111, wiredItem.getUserAt(1));
    assertEquals(1.3, wiredItem.getRatingAt(1));
    assertEquals(2, wiredItem.findUser(testUser.getTestUserIndex()));
    assertEquals(testUser.getTestUserIndex(), wiredItem.getUserAt(2));
    assertEquals(2.0, wiredItem.getRatingAt(2));
    assertEquals(222, wiredItem.getUserAt(3));
    assertEquals(2.4, wiredItem.getRatingAt(3));
    assertEquals(4, wiredItem.findUser(wiredUser.getUserIndex()));
    assertEquals(wiredUser.getUserIndex(), wiredItem.getUserAt(4));
    assertEquals(3.0, wiredItem.getRatingAt(4));
    assertEquals(333, wiredItem.getUserAt(5));
    assertEquals(3.5, wiredItem.getRatingAt(5));

    assertEquals("030", wiredItem.getId());
    assertEquals(30, wiredItem.getItemIndex());
    assertEquals(6, wiredItem.getNumberOfRatings());
    assertEquals(1, wiredItem.getMinRating());
    assertEquals(3.5, wiredItem.getMaxRating());
    assertTrue(Math.abs(wiredItem.getRatingAverage() - 2.2) <= Math.ulp(2.2));
  }
}
