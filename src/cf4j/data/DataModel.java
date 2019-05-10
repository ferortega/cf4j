package cf4j.data;

import cf4j.data.types.DynamicSortedArray;

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
    private DynamicSortedArray<User> users;
    private DynamicSortedArray<TestUser> testUsers;

    private DynamicSortedArray<Item> items;
    private DynamicSortedArray<TestItem> testItems;

    public DataModel (){
        this.users = new DynamicSortedArray<User>();
        this.testUsers = new DynamicSortedArray<TestUser>();
        this.items = new DynamicSortedArray<Item>();
        this.testItems = new DynamicSortedArray<TestItem>();
    }

    public DataModel (DataSet dataset){
        this();
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

    public void addRating (String userCode, String itemCode, double rating) {
        //Add it to User
        //Getting User with that id.
        User user = this.getUserByCode(userCode);
        if(user == null) { //If don't exist, create new.
            user = new User(userCode); //<-
        }
        //Getting Item with that id.
        Item item = this.getItem(itemCode);
        if(item != null) //If don't exist, create new.
            item = new Item(itemCode); //<-

        user.addRating(itemCode, rating);
        item.addRating(userCode, rating);

        //Also to testUsers
        //Getting User with that id.
        TestUser testUser = this.getTestUserByCode(userCode);
        if(testUser == null)  //If don't exist, create new.
            testUser = new TestUser(userCode); //<-

        //Getting Item with that id.
        TestItem testItem = this.getTestItem(itemCode);
        if(testItem != null) {//If don't exist, create new.
            testItem = new TestItem(itemCode); //<-
        }

        testUser.addTestRating(itemCode, rating);
        testItem.addTestRating(userCode, rating);
    }

    public void addTestRating (String userCode, String itemCode, double rating) {

        //TODO: REVISAR, Insertar ordenado...
        //Getting User with that id.
        TestUser testUser = this.getTestUserByCode(userCode);
        if(testUser == null) { //If don't exist, create new.
            testUser = new TestUser(userCode); //<-
        }
        //Getting Item with that id.
        TestItem testItem = this.getTestItem(itemCode);
        if(testItem != null) {//If don't exist, create new.
            testItem = new TestItem(itemCode); //<-
        }

        testUser.addTestRating(itemCode, rating);
        testItem.addTestRating(userCode, rating);
    }

    /**
     * Get an user by his code
     * @param userCode Code of the user to retrieve
     * @return User or null
     */
    public User getUserByCode (String userCode) {

        int index = users.get(new User(userCode));
        if (index == -1)
            return null;
        return users.get(index);
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
    public int getUserIndex (String userCode) {
        return users.get(new User(userCode));
    }
    /**
     * Get an user by his code
     * @param testUserCode Code of the user to retrieve
     * @return User or null
     */
    public TestUser getTestUserByCode (String testUserCode) {
        int index = testUsers.get(new TestUser(testUserCode));
        if (index == -1)
            return null;
        return testUsers.get(index);
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
    public int getTestUserIndex (String testUserCode) {
        return testUsers.get(new TestUser(testUserCode));
    }
    /**
     * Get an item by his code
     * @param itemCode Code of the item to retrieve
     * @return Item or null
     */
    public Item getItem (String itemCode) {

        int index = items.get(new Item(itemCode));
        if (index == -1)
            return null;
        return items.get(index);
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
    public int getItemIndex (String itemCode) {
        return items.get(new Item(itemCode));
    }
    /**
     * Get a test item by his code
     * @param itemCode Code of the test item to retrieve
     * @return TestItem or null
     */
    public TestItem getTestItem (String itemCode) {
        int index = testItems.get(new TestItem(itemCode));
        if (index == -1)
            return null;
        return testItems.get(index);
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
    public int getTestItemIndex (String testItemCode) {
        return testItems.get(new TestItem(testItemCode));
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
