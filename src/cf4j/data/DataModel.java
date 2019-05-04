package cf4j.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Class that manages all information of the Collaborative Filtering kernel. Contains the users and
 * items sets imported from the database.</p>
 *
 * <p>This class can not be instantiated. It implements the singleton pattern, so, when we want to use
 * it, we must use the getInstance() method.</p>
 * @author Fernando Ortega, Jesús Mayor
 */
public class DataModel implements Serializable {

    private static final long serialVersionUID = 20190503L;

    /**
     * Stored arrays
     */
    private ArrayList<User> users;
    private ArrayList<TestUser> testUsers;

    private ArrayList<Item> items;
    private ArrayList<TestItem> testItems;

    public DataModel (){}

    public DataModel (DataSet dataset){
        this.loadDataset(dataset);
    }

    public void loadDataset (DataSet dataset){
        for (Iterator<DataSet.DataSetEntry> it = dataset.getRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();
            this.addRating(entry.first, entry.second, entry.third);
        }
        for (Iterator<DataSet.DataSetEntry> it = dataset.getTestRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();
            this.addTestRating(entry.first, entry.second, entry.third);
        }
    }

    public void addRating (int userCode, int itemCode, double rating) {

        //TODO: REVISAR, Insertar ordenado...
        //Add it to User
        //Getting User with that id.
        User user = this.getUserByCode(userCode);
        if(user == null) { //If don't exist, create new.
            user = new User(userCode,0); //<-
        }
        //Getting Item with that id.
        Item item = this.getItem(itemCode);
        if(item != null) {//If don't exist, create new.
            item = new Item(itemCode,0); //<-
        }

        user.addRating(itemCode, rating);
        item.addRating(userCode, rating);

        //Also to testUsers
        //Getting User with that id.
        TestUser testUser = this.getTestUserByCode(userCode);
        if(testUser == null) { //If don't exist, create new.
            testUser = new TestUser(userCode,0,0); //<-
        }
        //Getting Item with that id.
        TestItem testItem = this.getTestItem(itemCode);
        if(testItem != null) {//If don't exist, create new.
            testItem = new TestItem(itemCode,0,0); //<-
        }

        testUser.addTestRating(itemCode, rating);
        testItem.addTestRating(userCode, rating);
    }

    public void addTestRating (int userCode, int itemCode, double rating) {

        //TODO: REVISAR, Insertar ordenado...
        //Getting User with that id.
        TestUser testUser = this.getTestUserByCode(userCode);
        if(testUser == null) { //If don't exist, create new.
            testUser = new TestUser(userCode,0,0); //<-
        }
        //Getting Item with that id.
        TestItem testItem = this.getTestItem(itemCode);
        if(testItem != null) {//If don't exist, create new.
            testItem = new TestItem(itemCode,0,0); //<-
        }

        testUser.addTestRating(itemCode, rating);
        testItem.addTestRating(userCode, rating);
    }

    /**
     * Get an user by his code
     * @param userCode Code of the user to retrieve
     * @return User or null
     */
    public User getUserByCode (int userCode) {
        return users.get(userCode);
    }
    /**
     * Get an user by his index
     * @param userIndex Index of the user to retrieve
     * @return User or null
     */
    public User getUserByIndex (int userIndex) {
        try {
            return this.users.get(userIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    /**
     * Get the index of an user at the users array
     * @param userCode User code
     * @return Index if the user exists or -1 if not
     */
    public int getUserIndex (int userCode) {
        int min = 0, max = users.size() -1;
        while (min <= max) {
            int center = ((max - min) / 2) + min;
            if (users.get(center).getUserCode() == userCode) return center;
            if (userCode < users.get(center).getUserCode()) {
                max = center - 1;
            } else {
                min = center + 1;
            }
        }
        return -1;
    }
    /**
     * Get an user by his code
     * @param testUserCode Code of the user to retrieve
     * @return User or null
     */
    public TestUser getTestUserByCode (int testUserCode) {
        return testUsers.get(testUserCode);
    }
    /**
     * Get an user by his index
     * @param testUserIndex Index of the user to retrieve
     * @return User or null
     */
    public TestUser getTestUserByIndex (int testUserIndex) {
        try {
            return this.testUsers.get(testUserIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    /**
     * Get the index of a test user at the test users array
     * @param testUserCode User code
     * @return Index if the user exists or -1 if not
     */
    public int getTestUserIndex (int testUserCode) {
        int min = 0, max = testUsers.size() -1;
        while (min <= max) {
            int center = ((max - min) / 2) + min;
            if (testUsers.get(center).getUserCode() == testUserCode) return center;
            if (testUserCode < testUsers.get(center).getUserCode()) {
                max = center - 1;
            } else {
                min = center + 1;
            }
        }
        return -1;
    }
    /**
     * Get an item by his code
     * @param itemCode Code of the item to retrieve
     * @return Item or null
     */
    public Item getItem (int itemCode) {
        return items.get(itemCode);
    }
    /**
     * Get a item by his index
     * @param itemIndex Index of the item to retrieve
     * @return TestItem or null
     */
    public Item getItemByIndex (int itemIndex) {
        try {
            return this.items.get(itemIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    /**
     * Get the index of a item at the test items array
     * @param itemCode Item code
     * @return Index if the item exists or -1 if not
     */
    public int getItemIndex (int itemCode) {
        int min = 0, max = items.size() -1;
        while (min <= max) {
            int center = ((max - min) / 2) + min;
            if (items.get(center).getItemCode() == itemCode) return center;
            if (itemCode < items.get(center).getItemCode()) {
                max = center - 1;
            } else {
                min = center + 1;
            }
        }
        return -1;
    }
    /**
     * Get a test item by his code
     * @param itemCode Code of the test item to retrieve
     * @return TestItem or null
     */
    public TestItem getTestItem (int itemCode) {
        return testItems.get(itemCode);
    }
    /**
     * Get a test item by his index
     * @param testItemIndex Index of the test item to retrieve
     * @return TestItem or null
     */
    public TestItem getTestItemByIndex (int testItemIndex) {
        try {
            return this.testItems.get(testItemIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    /**
     * Get the index of a test item at the test items array
     * @param testItemCode Test item code
     * @return Index if the item exists or -1 if not
     */
    public int getTestItemIndex (int testItemCode) {
        int min = 0, max = testItems.size() -1;
        while (min <= max) {
            int center = ((max - min) / 2) + min;
            if (testItems.get(center).getItemCode() == testItemCode) return center;
            if (testItemCode < testItems.get(center).getItemCode()) {
                max = center - 1;
            } else {
                min = center + 1;
            }
        }
        return -1;
    }

    /**
     * Get the number of users
     * @return Number of users
     */
    public int getNumberOfUsers () {
        return this.users.size();
    }

    /**
     * Get the number of test users
     * @return Number of test users
     */
    public int getNumberOfTestUsers () {
        return this.testUsers.size();
    }

    /**
     * Get the number of items
     * @return Number of items
     */
    public int getNumberOfItems () {
        return this.items.size();
    }

    /**
     * Get the number of test items
     * @return Number of test items
     */
    public int getNumberOfTestItems () {
        return this.testItems.size();
    }

    public String toString() {
        //TODO: Write
        return "TODO: Write";
    }

}
