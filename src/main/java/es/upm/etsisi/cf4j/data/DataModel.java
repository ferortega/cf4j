package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import java.util.*;

/**
 * <p>Class that manages all information of the Collaborative Filtering kernel. Contains the users and
 * items sets imported from the database.</p>
 *
 * <p>This class can not be instantiated. It implements the singleton pattern, so, when we want to use
 * it, we must use the getInstance() method.</p>
 * @author Fernando Ortega, Jesús Mayor
 */
public class DataModel implements Serializable {

    private static final long serialVersionUID = 20200314L;

    private User[] users;
    private TestUser[] testUsers;

    private Item[] items;
    private TestItem[] testItems;

    //Stored metrics
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private int numberOfRatings = 0;
    private double ratingAverage = 0.0;

    //Stored test metrics
    private double minTest = Double.MAX_VALUE;
    private double maxTest = Double.MIN_VALUE;
    private int numberOfTestRatings = 0;
    private double testRatingAverage = 0.0;




    private DataBank dataBank;

    /**
     * This constructor initializes the DataModel with the contents the given DataSet.
     * @param dataset DataSet to be added to the DataModel.
     */
    public DataModel (DataSet dataset){

        this.dataBank = new DataBank();

        //1.- Initializing the auxiliary arrays to the estimated initial size (taking into account the DataSet entries)
        List<User> usersList = new ArrayList<>(dataset.getRatingsSize()/40);
        List<TestUser> testUsersList = new ArrayList<>(dataset.getTestRatingsSize()/40);
        List<Item> itemsList = new ArrayList<>(dataset.getRatingsSize()/40);
        List<TestItem> testItemsLists = new ArrayList<>(dataset.getTestRatingsSize()/40);

        Map<String, Integer> id2userIndex = new HashMap<>();
        Map<String, Integer> id2testUserIndex = new HashMap<>();
        Map<String, Integer> id2itemIndex = new HashMap<>();
        Map<String, Integer> id2testItemIndex = new HashMap<>();

        //2.1.- First: Adding test cases to our DataModel.
        for (Iterator<DataSet.DataSetEntry> it = dataset.getTestRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();

            //To improve readability
            String testUserId = entry.first;
            String testItemId = entry.second;
            double testRating = entry.third;

            // Getting TestUser with Index.
            TestUser testUser;
            Integer testUserIndex = id2testUserIndex.get(testUserId);
            if ( testUserIndex != null )
                testUser =  testUsersList.get(testUserIndex);
            else {
                testUser = new TestUser(testUserId, usersList.size(),  testUsersList.size());
                id2userIndex.put(testUserId, usersList.size());
                id2testUserIndex.put(testUserId, testUsersList.size());
                usersList.add(testUser);
                testUsersList.add(testUser);
            }

            //Getting TestItem with Index.
            TestItem testItem;
            Integer testItemIndex = id2testItemIndex.get(testItemId);
            if ( testItemIndex != null)
                testItem = testItemsLists.get(testItemIndex);
            else {
                testItem = new TestItem(testItemId, itemsList.size(), testItemsLists.size());
                id2itemIndex.put (testItemId, itemsList.size());
                id2testItemIndex.put (testItemId, testItemsLists.size());
                itemsList.add(testItem);
                testItemsLists.add(testItem);
            }

            //Relating user with item.
            testUser.addTestRating(testItem.getTestItemIndex(), testRating);
            testItem.addTestRating(testUser.getTestUserIndex(), testRating);

            this.minTest = Math.min(testRating, this.minTest);
            this.maxTest = Math.max(testRating, this.maxTest);

            this.numberOfTestRatings++;
            this.testRatingAverage = (this.testRatingAverage * (this.numberOfTestRatings - 1) + testRating) / this.numberOfTestRatings;
        }

        //2.2.- Second: Adding non-test cases to our data structure
        for (Iterator<DataSet.DataSetEntry> it = dataset.getRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();

            String userId = entry.first;
            String itemId = entry.second;
            double rating = entry.third;

            //Getting User with that Index.
            User user;
            Integer userIndex = id2userIndex.get(userId);
            if ( userIndex != null)
                user = usersList.get(userIndex);
            else {
                user = new User(userId, usersList.size());
                id2userIndex.put(userId, usersList.size());
                usersList.add(user);
            }

            //Getting Item with that Index.
            Item item;
            Integer itemIndex = id2itemIndex.get(itemId);
            if ( itemIndex != null)
                item = itemsList.get(itemIndex);
            else {
                item = new Item(entry.second, itemsList.size());
                id2itemIndex.put(itemId, itemsList.size());
                itemsList.add(item);
            }

            //Relating user with item.
            user.addRating(item.getItemIndex(), rating);
            item.addRating(user.getUserIndex(), rating);

            this.min = Math.min(rating, this.min);
            this.max = Math.max(rating, this.max);

            this.numberOfRatings++;
            this.ratingAverage = (this.ratingAverage * (this.numberOfRatings - 1) + rating) / this.numberOfRatings;
        }

        //3.- Storing raw data to respective arrays.
        this.users = usersList.toArray(new User[usersList.size()]);
        this.testUsers = testUsersList.toArray(new TestUser[testUsersList.size()]);
        this.items = itemsList.toArray(new Item[itemsList.size()]);
        this.testItems = testItemsLists.toArray(new TestItem[testItemsLists.size()]);
    }

    /**
     * This method adds a single test rating to the DataSet
     * @param testUserIndex UserCode as string, of the rating.
     * @param testItemIndex ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public void addTestRating (int testUserIndex, int testItemIndex, double rating) {
        TestUser testUser = this.getTestUser(testUserIndex);
        TestItem testItem = this.getTestItem(testItemIndex);

        testUser.addTestRating(testItemIndex, rating);
        testItem.addTestRating(testUserIndex, rating);

        this.minTest = Math.min(rating, this.minTest);
        this.maxTest = Math.max(rating, this.maxTest);

        this.numberOfTestRatings++;
        this.testRatingAverage = (this.testRatingAverage * (this.numberOfTestRatings - 1) + rating) / this.numberOfTestRatings;
    }

    /**
     * This method adds a single rating to the DataSet.
     * @param userIndex UserCode as string, of the rating.
     * @param itemIndex ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public void addRating (int userIndex, int itemIndex, double rating) {
        User user = this.getUser(userIndex);
        Item item = this.getItem(itemIndex);

        user.addRating(itemIndex, rating);
        item.addRating(userIndex, rating);

        this.min = Math.min(rating, this.min);
        this.max = Math.max(rating, this.max);

        this.numberOfRatings++;
        this.ratingAverage = (this.ratingAverage * (this.numberOfRatings - 1) + rating) / this.numberOfRatings;
    }

    /**
     * Getter associated with the array of users
     * @return users array
     */
    public User[] getUsers() {
        return users;
    }

    /**
     * Getter associated with the array of test users
     * @return test users array
     */
    public  TestUser[] getTestUsers() {
        return testUsers;
    }

    /**
     * Getter associated with the array of items
     * @return items array
     */
    public  Item[] getItems() {
        return items;
    }

    /**
     * Getter associated with the array of test items
     * @return test items array
     */
    public  TestItem[] getTestItems() {
        return testItems;
    }

    /**
     * Getter of the stored data. This data allows you to store general calculation data inside the DataModel.
     * @return The databank who stores general information.
     */
    public DataBank getDataBank(){
        return dataBank;
    }

    /**
     * Get the number of users
     * @return Number of users
     */
    public int getNumberOfUsers () {
        return this.users.length;
    }

    /**
     * Get an user by his  userIndex
     * @param userIndex Index of the users array inside the datamodel
     * @return User located at userIndex
     */
    public User getUser(int userIndex) {
        return users[userIndex];
    }

    /**
     * Get the  userIndex of an user at the users array
     * @param userCode User code
     * @return  Index if the user exists or -1 if doesn't
     */
    public int findUserIndex(String userCode) {
        for ( int i = 0; i < this.users.length; i++)
            if ( this.users[i].getId().equals(userCode))
                return i;

        return -1;
    }

    /**
     * Get the number of test users
     * @return Number of test users
     */
    public int getNumberOfTestUsers() { return this.testUsers.length; }

    /**
     * Get an user by his  userIndex
     * @param testUserIndex Index of the testUsers array inside the datamodel
     * @return TestUser located at given userIndex
     */
    public TestUser getTestUser(int testUserIndex) {
        return testUsers[testUserIndex];
    }

    /**
     * Get the userIndex of a test user at the test users array
     * @param testUserCode User code
     * @return  Index if the user exists or -1 if doesn't
     */
    public int findTestUserIndex(String testUserCode) {
        for ( int i = 0; i < this.testUsers.length; i++)
            if ( this.testUsers[i].getId().equals(testUserCode))
                return i;

        return -1;
    }

    /**
     * Get the number of items
     * @return Number of items
     */
    public int getNumberOfItems() { return this.items.length; }

    /**
     * Get an item by his  userIndex
     * @param itemIndex Index of the items array inside the datamodel
     * @return Item located at given userIndex
     */
    public Item getItem(int itemIndex) {
        return this.items[itemIndex];
    }

    /**
     * Get the  userIndex of a item at the test items array
     * @param itemCode Item code
     * @return  Index if the item exists or -1 if doesn't
     */
    public int findItemIndex(String itemCode) {
        for ( int i = 0; i < this.items.length; i++)
            if ( this.items[i].getId().equals(itemCode))
                return i;

        return -1;
    }

    /**
     * Get the number of test items
     * @return Number of test items
     */
    public int getNumberOfTestItems() { return this.testItems.length; }

    /**
     * Get a test item by his  userIndex
     * @param testItemIndex Code of the test item to retrieve
     * @return TestItem located at given userIndex
     */
    public TestItem getTestItem(int testItemIndex) {
        return this.testItems[testItemIndex];
    }

    /**
     * Get the userIndex of a test item at the test items array
     * @param testItemCode Test item code
     * @return Index if the item exists or -1 if doesn't
     */
    public int findTestItemIndex(String testItemCode) {
        for ( int i = 0; i < this.testItems.length; i++)
            if ( this.testItems[i].getId().equals(testItemCode))
                return i;

        return -1;
    }

    /**
     * Get the minimum rating done
     * @return minimum rating
     */
    public double getMinRating(){
        return min;
    }

    /**
     * Get the maximum rating done
     * @return maximum rating
     */
    public double getMaxRating(){
        return max;
    }

    /**
     * Get the average of ratings
     * @return average
     */
    public double getRatingAverage(){
        return this.ratingAverage;
    }

    /**
     * Get the minimum rating done
     * @return minimum rating
     */
    public double getMinTestRating(){
        return this.minTest;
    }

    /**
     * Get the maximum rating done
     * @return maximum rating
     */
    public double getMaxTestRating(){
        return this.maxTest;
    }

    /**
     * Get the average of test ratings
     * @return average
     */
    public double getTestRatingAverage(){
        return this.testRatingAverage;
    }

    /**
     * Return the number of ratings
     * @return number of ratings
     */
    public int getNumberOfRatings() {
        return this.numberOfRatings;
    }

    /**
     * Return the number of test ratings
     * @return number of test ratings
     */
    public int getNumberOfTestRatings() {
        return this.numberOfTestRatings;
    }

    @Override
    public String toString() {
        return "\nNumber of users: " + this.users.length +
                "\nNumber of test users: " + this.testUsers.length +
                "\nNumber of items: " + this.items.length +
                "\nNumber of test items: " + this.testItems.length +
                "\nNumber of ratings: " + this.getNumberOfRatings() +
                "\nNumber of test ratings: " + this.getNumberOfTestRatings() +
                "\nMin rating: " + this.getMinRating() +
                "\nMax rating: " + this.getMaxRating() +
                "\nAverage rating: " + this.getRatingAverage() +
                "\nMin test rating: " + this.getMinTestRating() +
                "\nMax test rating: " + this.getMaxTestRating() +
                "\nAverage test rating: " + this.getTestRatingAverage();
    }
}
