package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import java.io.Serializable;
import java.util.*;

/**
 * This class manages all the information related with a collaborative filtering based recommender system.
 * A DataModel should be instantiated from a DataSet. This class also provides the possibility to manage serialized
 * files based on instances of this class.
 * @author Fernando Ortega, Jesús Mayor
 */
public class DataModel implements Serializable {

    private static final long serialVersionUID = 20200314L;

    //Stored users or items instances with his related ratings.
    private User[] users;
    private Item[] items;

    //Stored users or items instances with his related test ratings.
    private TestUser[] testUsers;
    private TestItem[] testItems;

    //Stored metrics.
    private double minRating = Double.MAX_VALUE;
    private double maxRating = Double.MIN_VALUE;
    private int numberOfRatings = 0;
    private double ratingAverage = 0.0;

    //Stored test metrics.
    private double minTestRating = Double.MAX_VALUE;
    private double maxTestRating = Double.MIN_VALUE;
    private int numberOfTestRatings = 0;
    private double testRatingAverage = 0.0;

    //Heterogeneous data storage.
    private DataBank dataBank;

    /**
     * This constructor initializes the DataModel with the contents of the given DataSet.
     * Data stored in the dataset is splitted in 4 different arrays: user, testUser, item and testItems. Keeping the
     * main behaviour of the Recommendation Systems in mind, a testUser could have voted a regular item or an TestItem.
     * Similarly, a testItem could have been voted by a regular user or a testUser.
     * In the other hand, regular users or items wont have a test part.
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

        for (Iterator<DataSetEntry> it = dataset.getTestRatingsIterator(); it.hasNext(); ){
            DataSetEntry entry = it.next();

            // Getting TestUser with Index.
            TestUser testUser;
            if (id2testUserIndex.containsKey(entry.userId)) {
                int testUserIndex = id2testUserIndex.get(entry.userId);
                testUser = testUsersList.get(testUserIndex);
            } else {
                int userIndex = usersList.size();
                int testUserIndex = testUsersList.size();
                testUser = new TestUser(entry.userId, userIndex, testUserIndex);

                usersList.add(testUser);
                testUsersList.add(testUser);

                id2userIndex.put(entry.userId, userIndex);
                id2testUserIndex.put(entry.userId, testUserIndex);
            }

            //Getting TestItem with Index.
            TestItem testItem;
            if (id2testItemIndex.containsKey(entry.itemId)) {
                int testItemIndex = id2testItemIndex.get(entry.itemId);
                testItem = testItemsLists.get(testItemIndex);
            } else {
                int itemIndex = itemsList.size();
                int testItemIndex = testItemsLists.size();

                testItem = new TestItem(entry.itemId, itemIndex, testItemIndex);

                itemsList.add(testItem);
                testItemsLists.add(testItem);

                id2itemIndex.put(entry.itemId, itemIndex);
                id2testItemIndex.put(entry.itemId, testItemIndex);
            }

            //Relating user with item.
            testUser.addTestRating(testItem.getTestItemIndex(), entry.rating);
            testItem.addTestRating(testUser.getTestUserIndex(), entry.rating);

            this.minTestRating = Math.min(entry.rating, this.minTestRating);
            this.maxTestRating = Math.max(entry.rating, this.maxTestRating);

            this.numberOfTestRatings++;
            this.testRatingAverage = (this.testRatingAverage * (this.numberOfTestRatings - 1) + entry.rating) / this.numberOfTestRatings;
        }

        //2.2.- Second: Adding non-test cases to our data structure
        for (Iterator<DataSetEntry> it = dataset.getRatingsIterator(); it.hasNext(); ){
            DataSetEntry entry = it.next();

            //Getting User with that Index.
            User user;
            if (id2userIndex.containsKey(entry.userId)) {
                int userIndex = id2userIndex.get(entry.userId);
                user = usersList.get(userIndex);
            } else {
                int userIndex = usersList.size();
                user = new User(entry.userId, userIndex);
                usersList.add(user);
                id2userIndex.put(entry.userId, userIndex);
            }

            //Getting Item with that Index.
            Item item;
            if (id2itemIndex.containsKey(entry.itemId)) {
                int itemIndex = id2itemIndex.get(entry.itemId);
                item = itemsList.get(itemIndex);
            } else {
                int itemIndex = itemsList.size();
                item = new Item(entry.itemId, itemIndex);
                itemsList.add(item);
                id2itemIndex.put(entry.itemId, itemIndex);
            }

            //Relating user with item.
            user.addRating(item.getItemIndex(), entry.rating);
            item.addRating(user.getUserIndex(), entry.rating);

            this.minRating = Math.min(entry.rating, this.minRating);
            this.maxRating = Math.max(entry.rating, this.maxRating);

            this.numberOfRatings++;
            this.ratingAverage = (this.ratingAverage * (this.numberOfRatings - 1) + entry.rating) / this.numberOfRatings;
        }

        //3.- Storing raw data to respective arrays.
        this.users = usersList.toArray(new User[0]);
        this.testUsers = testUsersList.toArray(new TestUser[0]);
        this.items = itemsList.toArray(new Item[0]);
        this.testItems = testItemsLists.toArray(new TestItem[0]);
    }

    /**
     * This method adds a single test rating to the DataModel.
     * This method is also in charge of recalculate the general test metrics: minTestRating, maxTestRating and ratingTestAverage.
     * @param testUserIndex UserCode as string, of the rating.
     * @param testItemIndex ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public void addTestRating (int testUserIndex, int testItemIndex, double rating) {
        TestUser testUser = this.getTestUser(testUserIndex);
        TestItem testItem = this.getTestItem(testItemIndex);

        testUser.addTestRating(testItemIndex, rating);
        testItem.addTestRating(testUserIndex, rating);

        this.minTestRating = Math.min(rating, this.minTestRating);
        this.maxTestRating = Math.max(rating, this.maxTestRating);

        this.numberOfTestRatings++;
        this.testRatingAverage = (this.testRatingAverage * (this.numberOfTestRatings - 1) + rating) / this.numberOfTestRatings;
    }

    /**
     * This method adds a single rating to the DataModel.
     * This method is also in charge of recalculate the general metrics: minRating, maxRating and ratingAverage.
     * @param userIndex User index as integer, of the rating.
     * @param itemIndex Item index to be rated.
     * @param rating Rating of the item.
     */
    public void addRating (int userIndex, int itemIndex, double rating) {
        User user = this.getUser(userIndex);
        Item item = this.getItem(itemIndex);

        user.addRating(itemIndex, rating);
        item.addRating(userIndex, rating);

        this.minRating = Math.min(rating, this.minRating);
        this.maxRating = Math.max(rating, this.maxRating);

        this.numberOfRatings++;
        this.ratingAverage = (this.ratingAverage * (this.numberOfRatings - 1) + rating) / this.numberOfRatings;
    }

    /**
     * Getter associated with the array of users
     * If you need a specific element it is recommended to use the getUser() method.
     * @return users array
     */
    public User[] getUsers() {
        return this.users;
    }

    /**
     * Getter associated with the array of test users
     * If you need a specific element it is recommended to use the getTestUser() method.
     * @return test users array
     */
    public TestUser[] getTestUsers() {
        return this.testUsers;
    }

    /**
     * Getter associated with the array of items
     * If you need a specific element it is recommended to use the getItem() method.
     * @return items array
     */
    public Item[] getItems() {
        return this.items;
    }

    /**
     * Getter associated with the array of test items
     * If you need a specific element it is recommended to use the getTestItem() method.
     * @return test items array
     */
    public TestItem[] getTestItems() {
        return this.testItems;
    }

    /**
     * Getter of the databank. This data allows you to store general calculation data inside the DataModel.
     * @return The databank who stores general information.
     */
    public DataBank getDataBank(){
        return dataBank;
    }

    /**
     * Get the number of users contained in the datamodel.
     * @return Number of users.
     */
    public int getNumberOfUsers () {
        return this.users.length;
    }

    /**
     * Get an user by his userIndex.
     * @param userIndex Index of the users' array inside the datamodel.
     * @return User located at given userIndex.
     */
    public User getUser(int userIndex) {
        return users[userIndex];
    }

    /**
     * Find the userIndex of a user at the users' array given an userCode.
     * @param userCode User code to be searched.
     * @return  Index if the user exists or -1 if doesn't.
     */
    public int findUserIndex(String userCode) {
        for ( int i = 0; i < this.users.length; i++)
            if ( this.users[i].getId().equals(userCode))
                return i;

        return -1;
    }

    /**
     * Get the number of test users contained in the datamodel.
     * @return Number of test users.
     */
    public int getNumberOfTestUsers() { return this.testUsers.length; }

    /**
     * Get a test user by his testUserIndex.
     * @param testUserIndex Index of the testUsers' array inside the datamodel.
     * @return TestUser located at given testUserIndex.
     */
    public TestUser getTestUser(int testUserIndex) {
        return testUsers[testUserIndex];
    }

    /**
     * Find the testUserIndex of a test user at the testUsers' array given a testUserCode.
     * @param testUserCode Test user code to be searched.
     * @return  Index if the user exists or -1 if doesn't.
     */
    public int findTestUserIndex(String testUserCode) {
        for ( int i = 0; i < this.testUsers.length; i++)
            if ( this.testUsers[i].getId().equals(testUserCode))
                return i;

        return -1;
    }

    /**
     * Get the number of items contained in the datamodel.
     * @return Number of items.
     */
    public int getNumberOfItems() { return this.items.length; }

    /**
     * Get an item by his itemIndex.
     * @param itemIndex Index of the items' array inside the datamodel.
     * @return Item located at given itemIndex.
     */
    public Item getItem(int itemIndex) {
        return this.items[itemIndex];
    }

    /**
     * Find the itemIndex of a item at the items' array given an itemCode.
     * @param itemCode Item code to be searched.
     * @return  Index if the item exists or -1 if doesn't.
     */
    public int findItemIndex(String itemCode) {
        for ( int i = 0; i < this.items.length; i++)
            if ( this.items[i].getId().equals(itemCode))
                return i;

        return -1;
    }

    /**
     * Get the number of test items contained in the datamodel.
     * @return Number of test items.
     */
    public int getNumberOfTestItems() { return this.testItems.length; }

    /**
     * Get a test item by his testItemIndex.
     * @param testItemIndex Index of the test items' array inside the datamodel.
     * @return TestItem located at given itemIndex.
     */
    public TestItem getTestItem(int testItemIndex) {
        return this.testItems[testItemIndex];
    }

    /**
     * Find the testItemIndex of a test item at the test items' array given a testItemCode.
     * @param testItemCode Test item code to be searched.
     * @return Index if the item exists or -1 if doesn't exist.
     */
    public int findTestItemIndex(String testItemCode) {
        for ( int i = 0; i < this.testItems.length; i++)
            if ( this.testItems[i].getId().equals(testItemCode))
                return i;

        return -1;
    }

    /**
     * Get the minimum rating done (test ratings are not included).
     * @return minimum rating.
     */
    public double getMinRating(){
        return minRating;
    }

    /**
     * Get the maximum rating done (test ratings are not included).
     * @return maximum rating.
     */
    public double getMaxRating(){
        return maxRating;
    }

    /**
     * Get the average of ratings (test ratings are not included).
     * @return average of ratings.
     */
    public double getRatingAverage(){
        return this.ratingAverage;
    }

    /**
     * Get the minimum test rating done.
     * @return minimum test rating.
     */
    public double getMinTestRating(){
        return this.minTestRating;
    }

    /**
     * Get the maximum test rating done.
     * @return maximum test rating.
     */
    public double getMaxTestRating(){
        return this.maxTestRating;
    }

    /**
     * Get the average of test ratings.
     * @return average of test ratings.
     */
    public double getTestRatingAverage(){
        return this.testRatingAverage;
    }

    /**
     * Return the number of ratings contained in the datamodel.
     * @return number of ratings.
     */
    public int getNumberOfRatings() {
        return this.numberOfRatings;
    }

    /**
     * Return the number of test ratings contained in the datamodel.
     * @return number of test ratings.
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
                "\nMin rating: " + this.getMinRating() +
                "\nMax rating: " + this.getMaxRating() +
                "\nAverage rating: " + this.getRatingAverage() +
                "\nNumber of test ratings: " + this.getNumberOfTestRatings() +
                "\nMin test rating: " + this.getMinTestRating() +
                "\nMax test rating: " + this.getMaxTestRating() +
                "\nAverage test rating: " + this.getTestRatingAverage();
    }
}
