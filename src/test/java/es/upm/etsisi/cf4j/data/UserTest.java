package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

  private static User user;
  private static Item item;
  private static Item wiredItem;
  private static TestItem testItem;

  @BeforeAll
  static void initAll() {
    user = new User("101", 101);
    item = new Item("010", 10);
    testItem = new TestItem("020", 20, 20);
    wiredItem = new TestItem("030", 30, 30);
  }

  @Test
  void userInsertion() {
    user.addRating(111, 1.3);
    user.addRating(item.getItemIndex(), 1.0);
    user.addRating(222, 2.4);
    user.addRating(testItem.getTestItemIndex(), 2.0);
    user.addRating(333, 3.5);
    user.addRating(wiredItem.getItemIndex(), 3.0);

    assertEquals(0, user.findItem(item.getItemIndex()));
    assertEquals(item.getItemIndex(), user.getItemAt(0));
    assertEquals(1.0, user.getRatingAt(0));
    assertEquals(1, user.findItem(testItem.getTestItemIndex()));
    assertEquals(testItem.getTestItemIndex(), user.getItemAt(1));
    assertEquals(2.0, user.getRatingAt(1));
    assertEquals(2, user.findItem(wiredItem.getItemIndex()));
    assertEquals(wiredItem.getItemIndex(), user.getItemAt(2));
    assertEquals(111, user.getItemAt(3));
    assertEquals(1.3, user.getRatingAt(3));
    assertEquals(3.0, user.getRatingAt(2));
    assertEquals(222, user.getItemAt(4));
    assertEquals(2.4, user.getRatingAt(4));
    assertEquals(333, user.getItemAt(5));
    assertEquals(3.5, user.getRatingAt(5));

    assertEquals("101", user.getId());
    assertEquals(101, user.getUserIndex());
    assertEquals(6, user.getNumberOfRatings());
    assertEquals(1, user.getMinRating());
    assertEquals(3.5, user.getMaxRating());
    assertTrue(Math.abs(user.getRatingAverage() - 2.2) <= Math.ulp(2.2));
  }
}
