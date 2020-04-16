package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersAndItemsTest {
    private static User user;
    private static User wiredUser;
    private static TestUser testUser;
    private static Item item;
    private static Item wiredItem;
    private static TestItem testItem;

    @BeforeAll
    static void initAll() {
        user = new User("101",101);
        testUser = new TestUser("202",202,202);
        wiredUser = new TestUser("303",303,303);
        item = new Item("010",10);
        testItem = new TestItem("020", 20,20);
        wiredItem = new TestItem("030", 30, 30);
    }

    @Test
    void userInsertion () {
        user.addRating(111,1.3);
        user.addRating(item.getItemIndex(),1.0);
        user.addRating(222,2.4);
        user.addRating(testItem.getTestItemIndex(),2.0);
        user.addRating(333,3.5);
        user.addRating(wiredItem.getItemIndex(),3.0);

        wiredUser.addRating(111,1.3);
        wiredUser.addRating(item.getItemIndex(),1.0);
        wiredUser.addRating(222,2.4);
        wiredUser.addRating(testItem.getTestItemIndex(),2.0);
        wiredUser.addRating(333,3.5);
        wiredUser.addRating(wiredItem.getItemIndex(),3.0);

        assertEquals(user.findItem(item.getItemIndex()), 0);
        assertEquals(user.getItemAt(0), item.getItemIndex());
        assertEquals(user.getRatingAt(0), 1.0);
        assertEquals(user.findItem(testItem.getTestItemIndex()), 1);
        assertEquals(user.getItemAt(1), testItem.getTestItemIndex());
        assertEquals(user.getRatingAt(1), 2.0);
        assertEquals(user.findItem(wiredItem.getItemIndex()), 2);
        assertEquals(user.getItemAt(2), wiredItem.getItemIndex());
        assertEquals(user.getItemAt(3), 111);
        assertEquals(user.getRatingAt(3), 1.3);
        assertEquals(user.getRatingAt(2), 3.0);
        assertEquals(user.getItemAt(4), 222);
        assertEquals(user.getRatingAt(4), 2.4);
        assertEquals(user.getItemAt(5), 333);
        assertEquals(user.getRatingAt(5), 3.5);

        assertEquals(wiredUser.findItem(item.getItemIndex()), 0);
        assertEquals(wiredUser.getItemAt(0), item.getItemIndex());
        assertEquals(wiredUser.getRatingAt(0), 1.0);
        assertEquals(wiredUser.findItem(testItem.getTestItemIndex()), 1);
        assertEquals(wiredUser.getItemAt(1), testItem.getTestItemIndex());
        assertEquals(wiredUser.getRatingAt(1), 2.0);
        assertEquals(wiredUser.findItem(wiredItem.getItemIndex()), 2);
        assertEquals(wiredUser.getItemAt(2), wiredItem.getItemIndex());
        assertEquals(wiredUser.getItemAt(3), 111);
        assertEquals(wiredUser.getRatingAt(3), 1.3);
        assertEquals(wiredUser.getRatingAt(2), 3.0);
        assertEquals(wiredUser.getItemAt(4), 222);
        assertEquals(wiredUser.getRatingAt(4), 2.4);
        assertEquals(wiredUser.getItemAt(5), 333);
        assertEquals(wiredUser.getRatingAt(5), 3.5);

        assertEquals(user.getId(),"101");
        assertEquals(user.getUserIndex(),101);
        assertEquals(user.getNumberOfRatings(), 6);
        assertEquals(user.getMinRating(),1);
        assertEquals(user.getMaxRating(),3.5);
        assertTrue(Math.abs(user.getRatingAverage() - 2.2) <= Math.ulp(2.2) );

        assertEquals(wiredUser.getId(),"303");
        assertEquals(wiredUser.getUserIndex(),303);
        assertEquals(wiredUser.getNumberOfRatings(), 6);
        assertEquals(wiredUser.getMinRating(),1);
        assertEquals(wiredUser.getMaxRating(),3.5);
        assertTrue(Math.abs(wiredUser.getRatingAverage() - 2.2) <= Math.ulp(2.2) );
    }

    @Test
    void testUserInsertion () {
        testUser.addTestRating(111,1.3);
        testUser.addTestRating(item.getItemIndex(),1.0);
        testUser.addTestRating(222,2.4);
        testUser.addTestRating(testItem.getTestItemIndex(),2.0);
        testUser.addTestRating(333,3.5);
        testUser.addTestRating(wiredItem.getItemIndex(),3.0);

        testUser.addRating(111,1.3);
        testUser.addRating(item.getItemIndex(),1.0);
        testUser.addRating(222,2.4);
        testUser.addRating(testItem.getTestItemIndex(),2.0);
        testUser.addRating(333,3.5);
        testUser.addRating(wiredItem.getItemIndex(),3.0);

        assertEquals(testUser.findItem(item.getItemIndex()), 0);
        assertEquals(testUser.getTestItemAt(0), item.getItemIndex());
        assertEquals(testUser.getTestRatingAt(0), 1.0);
        assertEquals(testUser.findItem(testItem.getTestItemIndex()), 1);
        assertEquals(testUser.getTestItemAt(1), testItem.getTestItemIndex());
        assertEquals(testUser.getTestRatingAt(1), 2.0);
        assertEquals(testUser.findItem(wiredItem.getItemIndex()), 2);
        assertEquals(testUser.getTestItemAt(2), wiredItem.getItemIndex());
        assertEquals(testUser.getTestRatingAt(2), 3.0);
        assertEquals(testUser.getTestItemAt(3), 111);
        assertEquals(testUser.getTestRatingAt(3), 1.3);
        assertEquals(testUser.getTestItemAt(4), 222);
        assertEquals(testUser.getTestRatingAt(4), 2.4);
        assertEquals(testUser.getTestItemAt(5), 333);
        assertEquals(testUser.getTestRatingAt(5), 3.5);

        assertEquals(testUser.findItem(item.getItemIndex()), 0);
        assertEquals(testUser.getItemAt(0), item.getItemIndex());
        assertEquals(testUser.getRatingAt(0), 1.0);
        assertEquals(testUser.findItem(testItem.getTestItemIndex()), 1);
        assertEquals(testUser.getItemAt(1), testItem.getTestItemIndex());
        assertEquals(testUser.getRatingAt(1), 2.0);
        assertEquals(testUser.findItem(wiredItem.getItemIndex()), 2);
        assertEquals(testUser.getItemAt(2), wiredItem.getItemIndex());
        assertEquals(testUser.getRatingAt(2), 3.0);
        assertEquals(testUser.getItemAt(3), 111);
        assertEquals(testUser.getRatingAt(3), 1.3);
        assertEquals(testUser.getItemAt(4), 222);
        assertEquals(testUser.getRatingAt(4), 2.4);
        assertEquals(testUser.getItemAt(5), 333);
        assertEquals(testUser.getRatingAt(5), 3.5);

        assertEquals(testUser.getId(),"202");
        assertEquals(testUser.getUserIndex(),202);
        assertEquals(testUser.getNumberOfRatings(), 6);
        assertEquals(testUser.getMinRating(),1);
        assertEquals(testUser.getMaxRating(),3.5);
        assertTrue(Math.abs(testUser.getRatingAverage() - 2.2) <= Math.ulp(2.2) );
        assertEquals(testUser.getTestUserIndex(),202);
        assertEquals(testUser.getNumberOfTestRatings(), 6);
        assertEquals(testUser.getMinTestRating(),1);
        assertEquals(testUser.getMaxTestRating(),3.5);
        assertTrue(Math.abs(testUser.getTestRatingAverage() - 2.2) <= Math.ulp(2.2) );
    }

    @Test
    void itemInsertion () {
        item.addRating(111,1.3);
        item.addRating(user.getUserIndex(),1.0);
        item.addRating(222,2.4);
        item.addRating(testUser.getTestUserIndex(),2.0);
        item.addRating(333,3.5);
        item.addRating(wiredUser.getUserIndex(),3.0);

        wiredItem.addRating(111,1.3);
        wiredItem.addRating(user.getUserIndex(),1.0);
        wiredItem.addRating(222,2.4);
        wiredItem.addRating(testUser.getTestUserIndex(),2.0);
        wiredItem.addRating(333,3.5);
        wiredItem.addRating(wiredUser.getUserIndex(),3.0);

        assertEquals(item.findUser(user.getUserIndex()), 0);
        assertEquals(item.getUserAt(0), user.getUserIndex());
        assertEquals(item.getRatingAt(0), 1.0);
        assertEquals(item.getUserAt(1), 111);
        assertEquals(item.getRatingAt(1), 1.3);
        assertEquals(item.findUser(testUser.getTestUserIndex()), 2);
        assertEquals(item.getUserAt(2), testUser.getTestUserIndex());
        assertEquals(item.getRatingAt(2), 2.0);
        assertEquals(item.getUserAt(3), 222);
        assertEquals(item.getRatingAt(3), 2.4);
        assertEquals(item.findUser(wiredUser.getUserIndex()), 4);
        assertEquals(item.getUserAt(4), wiredUser.getUserIndex());
        assertEquals(item.getRatingAt(4), 3.0);
        assertEquals(item.getUserAt(5), 333);
        assertEquals(item.getRatingAt(5), 3.5);

        assertEquals(wiredItem.findUser(user.getUserIndex()), 0);
        assertEquals(wiredItem.getUserAt(0), user.getUserIndex());
        assertEquals(wiredItem.getRatingAt(0), 1.0);
        assertEquals(wiredItem.getUserAt(1), 111);
        assertEquals(wiredItem.getRatingAt(1), 1.3);
        assertEquals(wiredItem.findUser(testUser.getTestUserIndex()), 2);
        assertEquals(wiredItem.getUserAt(2), testUser.getTestUserIndex());
        assertEquals(wiredItem.getRatingAt(2), 2.0);
        assertEquals(wiredItem.getUserAt(3), 222);
        assertEquals(wiredItem.getRatingAt(3), 2.4);
        assertEquals(wiredItem.findUser(wiredUser.getUserIndex()), 4);
        assertEquals(wiredItem.getUserAt(4), wiredUser.getUserIndex());
        assertEquals(wiredItem.getRatingAt(4), 3.0);
        assertEquals(wiredItem.getUserAt(5), 333);
        assertEquals(wiredItem.getRatingAt(5), 3.5);

        assertEquals(item.getId(),"010");
        assertEquals(item.getItemIndex(),10);
        assertEquals(item.getNumberOfRatings(), 6);
        assertEquals(item.getMinRating(),1);
        assertEquals(item.getMaxRating(),3.5);
        assertTrue(Math.abs(item.getRatingAverage() - 2.2) <= Math.ulp(2.2) );

        assertEquals(wiredItem.getId(),"030");
        assertEquals(wiredItem.getItemIndex(),30);
        assertEquals(wiredItem.getNumberOfRatings(), 6);
        assertEquals(wiredItem.getMinRating(),1);
        assertEquals(wiredItem.getMaxRating(),3.5);
        assertTrue(Math.abs(wiredItem.getRatingAverage() - 2.2) <= Math.ulp(2.2) );
    }

    @Test
    void testItemInsertion () {
        testItem.addRating(111,1.3);
        testItem.addRating(user.getUserIndex(),1.0);
        testItem.addRating(222,2.4);
        testItem.addRating(testUser.getTestUserIndex(),2.0);
        testItem.addRating(333,3.5);
        testItem.addRating(wiredUser.getUserIndex(),3.0);

        testItem.addTestRating(111,1.3);
        testItem.addTestRating(user.getUserIndex(),1.0);
        testItem.addTestRating(222,2.4);
        testItem.addTestRating(testUser.getTestUserIndex(),2.0);
        testItem.addTestRating(333,3.5);
        testItem.addTestRating(wiredUser.getUserIndex(),3.0);

        assertEquals(testItem.findUser(user.getUserIndex()), 0);
        assertEquals(testItem.getUserAt(0), user.getUserIndex());
        assertEquals(testItem.getRatingAt(0), 1.0);
        assertEquals(testItem.getUserAt(1), 111);
        assertEquals(testItem.getRatingAt(1), 1.3);
        assertEquals(testItem.findUser(testUser.getTestUserIndex()), 2);
        assertEquals(testItem.getUserAt(2), testUser.getTestUserIndex());
        assertEquals(testItem.getRatingAt(2), 2.0);
        assertEquals(testItem.getUserAt(3), 222);
        assertEquals(testItem.getRatingAt(3), 2.4);
        assertEquals(testItem.findUser(wiredUser.getUserIndex()), 4);
        assertEquals(testItem.getUserAt(4), wiredUser.getUserIndex());
        assertEquals(testItem.getRatingAt(4), 3.0);
        assertEquals(testItem.getUserAt(5), 333);
        assertEquals(testItem.getRatingAt(5), 3.5);

        assertEquals(testItem.findUser(user.getUserIndex()), 0);
        assertEquals(testItem.getTestUserAt(0), user.getUserIndex());
        assertEquals(testItem.getTestRatingAt(0), 1.0);
        assertEquals(testItem.getTestUserAt(1), 111);
        assertEquals(testItem.getTestRatingAt(1), 1.3);
        assertEquals(testItem.findUser(testUser.getTestUserIndex()), 2);
        assertEquals(testItem.getTestUserAt(2), testUser.getTestUserIndex());
        assertEquals(testItem.getTestRatingAt(2), 2.0);
        assertEquals(testItem.getTestUserAt(3), 222);
        assertEquals(testItem.getTestRatingAt(3), 2.4);
        assertEquals(testItem.findUser(wiredUser.getUserIndex()), 4);
        assertEquals(testItem.getTestUserAt(4), wiredUser.getUserIndex());
        assertEquals(testItem.getTestRatingAt(4), 3.0);
        assertEquals(testItem.getTestUserAt(5), 333);
        assertEquals(testItem.getTestRatingAt(5), 3.5);

        assertEquals(testItem.getId(),"020");
        assertEquals(testItem.getItemIndex(),20);
        assertEquals(testItem.getNumberOfRatings(), 6);
        assertEquals(testItem.getMinRating(),1);
        assertEquals(testItem.getMaxRating(),3.5);
        assertTrue(Math.abs(testItem.getRatingAverage() - 2.2) <= Math.ulp(2.2) );
        assertEquals(testItem.getTestItemIndex(),20);
        assertEquals(testItem.getNumberOfTestRatings(), 6);
        assertEquals(testItem.getMinTestRating(),1);
        assertEquals(testItem.getMaxTestRating(),3.5);
        assertTrue(Math.abs(testItem.getTestRatingAverage() - 2.2) <= Math.ulp(2.2) );
    }
}