package cf4j.data;

import cf4j.data.types.DynamicSortedArray;

import java.io.Serializable;
import java.util.Iterator;

/**
 * <p>Class that manages all information of the Collaborative Filtering kernel. Contains the users and
 * items sets imported from the database.</p>
 *
 * <p>This class can not be instantiated. It implements the singleton pattern, so, when we want to use
 * it, we must use the getInstance() method.</p>
 * @author Fernando Ortega
 */
public class DataModel implements Serializable {

    private static final long serialVersionUID = 20190503L;

    public static final String AVERAGERATING_KEY = "average_rating";
    public static final String MAXRATING_KEY = "max_rating";
    public static final String MINRATING_KEY = "min_rating";

    private DynamicSortedArray<User> users;
    private DynamicSortedArray<TestUser> testUsers;

    private DynamicSortedArray<Item> items;
    private DynamicSortedArray<TestItem> testItems;

    private DataBank dataBank;

    /**
     * Default constructor. It doesn't contains any information by itself. You need use loadDataset.
     */
    public DataModel (){
        this.users = new DynamicSortedArray<User>();
        this.testUsers = new DynamicSortedArray<TestUser>();
        this.items = new DynamicSortedArray<Item>();
        this.testItems = new DynamicSortedArray<TestItem>();
        this.dataBank = new DataBank();
    }

    /**
     * This constructor initializes the DataModel with the contents the given DataSet.
     * @param dataset DataSet to be added to the DataModel.
     */
    public DataModel (DataSet dataset){
        //Initializing the arrays to the estimated initial size (taking into account the DataSet entries)
        this.users = new DynamicSortedArray<User>(dataset.getRatingsSize()/40);
        this.testUsers = new DynamicSortedArray<TestUser>(dataset.getTestRatingsSize()/40);
        this.items = new DynamicSortedArray<Item>(dataset.getRatingsSize()/40);
        this.testItems = new DynamicSortedArray<TestItem>(dataset.getTestRatingsSize()/40);
        this.dataBank = new DataBank();
        this.loadDataset(dataset);
    }

    /**
     * This method load inside the data model the registers found in the DataSet.
     * @param dataset Dataset eith the information to be added
     */
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

    /**
     * This method adds a single rating to the DataSet.
     * @param userCode UserCode as string, of the rating.
     * @param itemCode ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public void addRating (String userCode, String itemCode, double rating) {

        //Also to testUsers
        //Getting User with that id.
        User user = this.getUser(userCode);
        if(user == null) { //If don't exist, create new and add it.
            user = new User(userCode);
            this.users.add(user);
        }
        //Getting Item with that id.
        Item item = this.getItem(itemCode);
        if(item == null) {//If don't exist, create new and add it.
            item = new Item(itemCode);
            this.items.add(item);
        }

        user.addRating(itemCode, rating);
        item.addRating(userCode, rating);
    }

    /**
     * This method adds a single test rating to the DataSet
     * @param userCode UserCode as string, of the rating.
     * @param itemCode ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public void addTestRating (String userCode, String itemCode, double rating) {

        TestUser testUser = this.getTestUser(userCode);
        if(testUser == null) { //If don't exist, create new and add it to the arrays.
            testUser = new TestUser(userCode); //<-
            this.users.add(testUser);
            this.testUsers.add(testUser);
        }
        //Getting Item with that id.
        TestItem testItem = this.getTestItem(itemCode);
        if(testItem == null) {//If don't exist, create new and add it to the arrays..
            testItem = new TestItem(itemCode); //<-
            this.items.add(testItem);
            this.testItems.add(testItem);
        }

        testUser.addTestRating(itemCode, rating);
        testItem.addTestRating(userCode, rating);
    }

    public void calculateMetrics(){
        //TODO: Its made only of users, is it right?.
        double minRating = Double.MAX_VALUE;
        double maxRating = Double.MIN_VALUE;
        double sumRatigns = 0;
        int numRatings = 0;

        for (int i = 0; i < this.getNumberOfUsers(); i++){
            this.getUserAt(i).calculateMetrics();
        }
        for (int i = 0; i < this.getNumberOfTestUsers(); i++){
            this.getTestUserAt(i).calculateMetrics();
        }
        for (int i = 0; i < this.getNumberOfItems(); i++){
            this.getItemAt(i).calculateMetrics();
        }
        for (int i = 0; i < this.getNumberOfTestItems(); i++){
            this.getTestItemAt(i).calculateMetrics();
        }

        for (int i = 0; i < this.getNumberOfUsers(); i++){
            User user = this.getUserAt(i);
            for (int j = 0; j < user.getNumberOfRatings(); j++){
                if (user.getRatingAt(j) < minRating) minRating = user.getRatingAt(j);
                if (user.getRatingAt(j) > maxRating) maxRating = user.getRatingAt(j);
            }
            if (user.getNumberOfRatings() != 0)
                sumRatigns += user.getDataBank().getDouble(User.AVERAGERATING_KEY) * user.getNumberOfRatings();
            numRatings += user.getNumberOfRatings();
        }

        dataBank.setDouble( MINRATING_KEY, minRating );
        dataBank.setDouble( MAXRATING_KEY, maxRating );
        dataBank.setDouble( AVERAGERATING_KEY, sumRatigns / numRatings );
    }

    /**
     * Getter of the stored data. This data allows you to store general calculation data inside the DataModel.
     * @return The databank who stores general information.
     */
    public DataBank getDataBank(){
        return dataBank;
    }

    /**
     * Get an user by his code
     * @param userCode Code of the user to retrieve
     * @return User or null
     */
    public User getUser(String userCode) {

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
    public User getUserAt(int userIndex) {
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
    public TestUser getTestUser(String testUserCode) {
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
    public TestUser getTestUserAt(int testUserIndex) {
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
    public Item getItemAt(int itemIndex) {
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
    public TestItem getTestItemAt(int testItemIndex) {
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

        calculateMetrics();

        int numRatings = 0;
        for (int i = 0; i < this.users.size(); i++)
            numRatings += this.users.get(i).getNumberOfRatings();

        int numTestRatings = 0;
        for (int i = 0; i < this.testUsers.size(); i++)
            numTestRatings += this.testUsers.get(i).getNumberOfTestRatings();

        return "\nNumber of users: " + this.users.size() +
                "\nNumber of test users: " + this.testUsers.size() +
                "\nNumber of items: " + this.items.size() +
                "\nNumber of test items: " + this.testItems.size() +
                "\nNumber of ratings: " + numRatings +
                "\nNumber of test ratings: " + numTestRatings +
                "\nMin rating: " + this.dataBank.getDouble(MINRATING_KEY) +
                "\nMax rating: " + this.dataBank.getDouble(MAXRATING_KEY) +
                "\nAverage rating: " + this.dataBank.getDouble(AVERAGERATING_KEY);
    }

}
