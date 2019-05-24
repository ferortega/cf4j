package cf4j.data.types;

import cf4j.data.Item;
import cf4j.data.TestItem;
import cf4j.data.TestUser;
import cf4j.data.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsersAndItemsTest {

    static User user;
    static User wiredUser;
    static TestUser testUser;
    static Item item;
    static Item wiredItem;
    static TestItem testItem;

    @BeforeAll
    static void initAll() {
        user = new User("101");
        testUser = new TestUser("202");
        wiredUser = new TestUser("303");
        item = new Item("010");
        testItem = new TestItem("020");
        wiredItem = new TestItem("030");
    }

    @Test
    void userInsertion () {
        user.addRating(item.getItemCode(),1.0);
        user.addRating(testItem.getItemCode(),2.0);
        user.addRating(wiredItem.getItemCode(),3.0);
        user.addRating("otherFakeItem01",1.8);
        user.addRating("otherFakeItem01",1.3);
        user.addRating("other",2.8);
        user.addRating("other",2.4);
        user.addRating("otherFake",3.8);
        user.addRating("otherFake",3.5);

        wiredUser.addRating(item.getItemCode(),1.0);
        wiredUser.addRating(testItem.getItemCode(),2.0);
        wiredUser.addRating(wiredItem.getItemCode(),3.0);
        wiredUser.addRating("otherFakeItem01",1.8);
        wiredUser.addRating("otherFakeItem01",1.3);
        wiredUser.addRating("other",2.8);
        wiredUser.addRating("other",2.4);
        wiredUser.addRating("otherFake",3.8);
        wiredUser.addRating("otherFake",3.5);

        assertEquals(user.getNumberOfRatings(), 6);
        assertEquals(user.getItemIndex(item.getItemCode()), 0);
        assertEquals(user.getItemAt(0), item.getItemCode());
        assertEquals(user.getRatingAt(0), 1.0);
        assertEquals(user.getItemIndex(testItem.getItemCode()), 1);
        assertEquals(user.getItemAt(1), testItem.getItemCode());
        assertEquals(user.getRatingAt(1), 2.0);
        assertEquals(user.getItemIndex(wiredItem.getItemCode()), 2);
        assertEquals(user.getItemAt(2), wiredItem.getItemCode());
        assertEquals(user.getRatingAt(2), 3.0);
        assertEquals(user.getItemAt(3), "other");
        assertEquals(user.getRatingAt(3), 2.4);
        assertEquals(user.getItemAt(4), "otherFake");
        assertEquals(user.getRatingAt(4), 3.5);
        assertEquals(user.getItemAt(5), "otherFakeItem01");
        assertEquals(user.getRatingAt(5), 1.3);

        assertEquals(wiredUser.getNumberOfRatings(), 6);
        assertEquals(wiredUser.getItemIndex(item.getItemCode()), 0);
        assertEquals(wiredUser.getItemAt(0), item.getItemCode());
        assertEquals(wiredUser.getRatingAt(0), 1.0);
        assertEquals(wiredUser.getItemIndex(testItem.getItemCode()), 1);
        assertEquals(wiredUser.getItemAt(1), testItem.getItemCode());
        assertEquals(wiredUser.getRatingAt(1), 2.0);
        assertEquals(wiredUser.getItemIndex(wiredItem.getItemCode()), 2);
        assertEquals(wiredUser.getItemAt(2), wiredItem.getItemCode());
        assertEquals(wiredUser.getRatingAt(2), 3.0);
        assertEquals(wiredUser.getItemAt(3), "other");
        assertEquals(wiredUser.getRatingAt(3), 2.4);
        assertEquals(wiredUser.getItemAt(4), "otherFake");
        assertEquals(wiredUser.getRatingAt(4), 3.5);
        assertEquals(wiredUser.getItemAt(5), "otherFakeItem01");
        assertEquals(wiredUser.getRatingAt(5), 1.3);
    }

    @Test
    void testUserInsertion () {
        testUser.addTestRating(item.getItemCode(),1.0);
        testUser.addTestRating(testItem.getItemCode(),2.0);
        testUser.addTestRating(wiredItem.getItemCode(),3.0);
        testUser.addTestRating("otherFakeItem01",1.8);
        testUser.addTestRating("otherFakeItem01",1.3);
        testUser.addTestRating("other",2.8);
        testUser.addTestRating("other",2.4);
        testUser.addTestRating("otherFake",3.8);
        testUser.addTestRating("otherFake",3.5);

        testUser.addRating(item.getItemCode(),1.0);
        testUser.addRating(testItem.getItemCode(),2.0);
        testUser.addRating(wiredItem.getItemCode(),3.0);
        testUser.addRating("otherFakeItem01",1.8);
        testUser.addRating("otherFakeItem01",1.3);
        testUser.addRating("other",2.8);
        testUser.addRating("other",2.4);
        testUser.addRating("otherFake",3.8);
        testUser.addRating("otherFake",3.5);

        assertEquals(testUser.getNumberOfTestRatings(), 6);
        assertEquals(testUser.getTestItemIndex(item.getItemCode()), 0);
        assertEquals(testUser.getTestItemAt(0), item.getItemCode());
        assertEquals(testUser.getTestRatingAt(0), 1.0);
        assertEquals(testUser.getTestItemIndex(testItem.getItemCode()), 1);
        assertEquals(testUser.getTestItemAt(1), testItem.getItemCode());
        assertEquals(testUser.getTestRatingAt(1), 2.0);
        assertEquals(testUser.getTestItemIndex(wiredItem.getItemCode()), 2);
        assertEquals(testUser.getTestItemAt(2), wiredItem.getItemCode());
        assertEquals(testUser.getTestRatingAt(2), 3.0);
        assertEquals(testUser.getTestItemAt(3), "other");
        assertEquals(testUser.getTestRatingAt(3), 2.4);
        assertEquals(testUser.getTestItemAt(4), "otherFake");
        assertEquals(testUser.getTestRatingAt(4), 3.5);
        assertEquals(testUser.getTestItemAt(5), "otherFakeItem01");
        assertEquals(testUser.getTestRatingAt(5), 1.3);

        assertEquals(testUser.getNumberOfRatings(), 6);
        assertEquals(testUser.getItemIndex(item.getItemCode()), 0);
        assertEquals(testUser.getItemAt(0), item.getItemCode());
        assertEquals(testUser.getRatingAt(0), 1.0);
        assertEquals(testUser.getItemIndex(testItem.getItemCode()), 1);
        assertEquals(testUser.getItemAt(1), testItem.getItemCode());
        assertEquals(testUser.getRatingAt(1), 2.0);
        assertEquals(testUser.getItemIndex(wiredItem.getItemCode()), 2);
        assertEquals(testUser.getItemAt(2), wiredItem.getItemCode());
        assertEquals(testUser.getRatingAt(2), 3.0);
        assertEquals(testUser.getItemAt(3), "other");
        assertEquals(testUser.getRatingAt(3), 2.4);
        assertEquals(testUser.getItemAt(4), "otherFake");
        assertEquals(testUser.getRatingAt(4), 3.5);
        assertEquals(testUser.getItemAt(5), "otherFakeItem01");
        assertEquals(testUser.getRatingAt(5), 1.3);
    }

    @Test
    void itemInsertion () {
        item.addRating(user.getUserCode(),1.0);
        item.addRating(testUser.getUserCode(),2.0);
        item.addRating(wiredUser.getUserCode(),3.0);
        item.addRating("otherFakeUser01",1.8);
        item.addRating("otherFakeUser01",1.3);
        item.addRating("other",2.8);
        item.addRating("other",2.4);
        item.addRating("otherFake",3.8);
        item.addRating("otherFake",3.5);

        wiredItem.addRating(user.getUserCode(),1.0);
        wiredItem.addRating(testUser.getUserCode(),2.0);
        wiredItem.addRating(wiredUser.getUserCode(),3.0);
        wiredItem.addRating("otherFakeUser01",1.8);
        wiredItem.addRating("otherFakeUser01",1.3);
        wiredItem.addRating("other",2.8);
        wiredItem.addRating("other",2.4);
        wiredItem.addRating("otherFake",3.8);
        wiredItem.addRating("otherFake",3.5);

        assertEquals(item.getNumberOfRatings(), 6);
        assertEquals(item.getUserIndex(user.getUserCode()), 0);
        assertEquals(item.getUserAt(0), user.getUserCode());
        assertEquals(item.getRatingAt(0), 1.0);
        assertEquals(item.getUserIndex(testUser.getUserCode()), 1);
        assertEquals(item.getUserAt(1), testUser.getUserCode());
        assertEquals(item.getRatingAt(1), 2.0);
        assertEquals(item.getUserIndex(wiredUser.getUserCode()), 2);
        assertEquals(item.getUserAt(2), wiredUser.getUserCode());
        assertEquals(item.getRatingAt(2), 3.0);
        assertEquals(item.getUserAt(3), "other");
        assertEquals(item.getRatingAt(3), 2.4);
        assertEquals(item.getUserAt(4), "otherFake");
        assertEquals(item.getRatingAt(4), 3.5);
        assertEquals(item.getUserAt(5), "otherFakeUser01");
        assertEquals(item.getRatingAt(5), 1.3);

        assertEquals(wiredItem.getNumberOfRatings(), 6);
        assertEquals(wiredItem.getUserIndex(user.getUserCode()), 0);
        assertEquals(wiredItem.getUserAt(0), user.getUserCode());
        assertEquals(wiredItem.getRatingAt(0), 1.0);
        assertEquals(wiredItem.getUserIndex(testUser.getUserCode()), 1);
        assertEquals(wiredItem.getUserAt(1), testUser.getUserCode());
        assertEquals(wiredItem.getRatingAt(1), 2.0);
        assertEquals(wiredItem.getUserIndex(wiredUser.getUserCode()), 2);
        assertEquals(wiredItem.getUserAt(2), wiredUser.getUserCode());
        assertEquals(wiredItem.getRatingAt(2), 3.0);
        assertEquals(wiredItem.getUserAt(3), "other");
        assertEquals(wiredItem.getRatingAt(3), 2.4);
        assertEquals(wiredItem.getUserAt(4), "otherFake");
        assertEquals(wiredItem.getRatingAt(4), 3.5);
        assertEquals(wiredItem.getUserAt(5), "otherFakeUser01");
        assertEquals(wiredItem.getRatingAt(5), 1.3);
    }

    @Test
    void testItemInsertion () {
        testItem.addRating(user.getUserCode(),1.0);
        testItem.addRating(testUser.getUserCode(),2.0);
        testItem.addRating(wiredUser.getUserCode(),3.0);
        testItem.addRating("otherFakeUser01",1.8);
        testItem.addRating("otherFakeUser01",1.3);
        testItem.addRating("other",2.8);
        testItem.addRating("other",2.4);
        testItem.addRating("otherFake",3.8);
        testItem.addRating("otherFake",3.5);

        testItem.addTestRating(user.getUserCode(),1.0);
        testItem.addTestRating(testUser.getUserCode(),2.0);
        testItem.addTestRating(wiredUser.getUserCode(),3.0);
        testItem.addTestRating("otherFakeUser01",1.8);
        testItem.addTestRating("otherFakeUser01",1.3);
        testItem.addTestRating("other",2.8);
        testItem.addTestRating("other",2.4);
        testItem.addTestRating("otherFake",3.8);
        testItem.addTestRating("otherFake",3.5);

        assertEquals(testItem.getNumberOfRatings(), 6);
        assertEquals(testItem.getUserIndex(user.getUserCode()), 0);
        assertEquals(testItem.getUserAt(0), user.getUserCode());
        assertEquals(testItem.getRatingAt(0), 1.0);
        assertEquals(testItem.getUserIndex(testUser.getUserCode()), 1);
        assertEquals(testItem.getUserAt(1), testUser.getUserCode());
        assertEquals(testItem.getRatingAt(1), 2.0);
        assertEquals(testItem.getUserIndex(wiredUser.getUserCode()), 2);
        assertEquals(testItem.getUserAt(2), wiredUser.getUserCode());
        assertEquals(testItem.getRatingAt(2), 3.0);
        assertEquals(testItem.getUserAt(3), "other");
        assertEquals(testItem.getRatingAt(3), 2.4);
        assertEquals(testItem.getUserAt(4), "otherFake");
        assertEquals(testItem.getRatingAt(4), 3.5);
        assertEquals(testItem.getUserAt(5), "otherFakeUser01");
        assertEquals(testItem.getRatingAt(5), 1.3);

        assertEquals(testItem.getNumberOfTestRatings(), 6);
        assertEquals(testItem.getTestUserIndex(user.getUserCode()), 0);
        assertEquals(testItem.getTestUserAt(0), user.getUserCode());
        assertEquals(testItem.getTestRatingAt(0), 1.0);
        assertEquals(testItem.getTestUserIndex(testUser.getUserCode()), 1);
        assertEquals(testItem.getTestUserAt(1), testUser.getUserCode());
        assertEquals(testItem.getTestRatingAt(1), 2.0);
        assertEquals(testItem.getTestUserIndex(wiredUser.getUserCode()), 2);
        assertEquals(testItem.getTestUserAt(2), wiredUser.getUserCode());
        assertEquals(testItem.getTestRatingAt(2), 3.0);
        assertEquals(testItem.getTestUserAt(3), "other");
        assertEquals(testItem.getTestRatingAt(3), 2.4);
        assertEquals(testItem.getTestUserAt(4), "otherFake");
        assertEquals(testItem.getTestRatingAt(4), 3.5);
        assertEquals(testItem.getTestUserAt(5), "otherFakeUser01");
        assertEquals(testItem.getTestRatingAt(5), 1.3);
    }
}