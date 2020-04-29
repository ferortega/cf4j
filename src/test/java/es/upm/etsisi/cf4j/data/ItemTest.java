package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

  private static User user;
  private static User wiredUser;
  private static TestUser testUser;
  private static Item item;

  @BeforeAll
  static void initAll() {
    user = new User("101", 101);
    testUser = new TestUser("202", 202, 202);
    wiredUser = new TestUser("303", 303, 303);
    item = new Item("010", 10);
  }

  @Test
  void itemInsertion() {
    item.addRating(111, 1.3);
    item.addRating(user.getUserIndex(), 1.0);
    item.addRating(222, 2.4);
    item.addRating(testUser.getTestUserIndex(), 2.0);
    item.addRating(333, 3.5);
    item.addRating(wiredUser.getUserIndex(), 3.0);

    assertEquals(0, item.findUser(user.getUserIndex()));
    assertEquals(user.getUserIndex(), item.getUserAt(0));
    assertEquals(1.0, item.getRatingAt(0));
    assertEquals(111, item.getUserAt(1));
    assertEquals(1.3, item.getRatingAt(1));
    assertEquals(2, item.findUser(testUser.getTestUserIndex()));
    assertEquals(testUser.getTestUserIndex(), item.getUserAt(2));
    assertEquals(2.0, item.getRatingAt(2));
    assertEquals(222, item.getUserAt(3));
    assertEquals(2.4, item.getRatingAt(3));
    assertEquals(4, item.findUser(wiredUser.getUserIndex()));
    assertEquals(wiredUser.getUserIndex(), item.getUserAt(4));
    assertEquals(3.0, item.getRatingAt(4));
    assertEquals(333, item.getUserAt(5));
    assertEquals(3.5, item.getRatingAt(5));

    assertEquals("010", item.getId());
    assertEquals(10, item.getItemIndex());
    assertEquals(6, item.getNumberOfRatings());
    assertEquals(1, item.getMinRating());
    assertEquals(3.5, item.getMaxRating());
    assertTrue(Math.abs(item.getRatingAverage() - 2.2) <= Math.ulp(2.2));
  }
}
