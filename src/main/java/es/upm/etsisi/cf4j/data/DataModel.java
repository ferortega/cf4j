package es.upm.etsisi.cf4j.data;

import java.io.Serializable;
import java.util.ArrayList;
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

    private static final long serialVersionUID = 20200314L;

    private User[] users;
    private TestUser[] testUsers;

    private Item[] items;
    private TestItem[] testItems;

    //Stored metrics
    private double min = 0.0;
    private double max = 0.0;
    private double average = 0.0;

    //private DataBank dataBank;

    /**
     * This constructor initializes the DataModel with the contents the given DataSet.
     * @param dataset DataSet to be added to the DataModel.
     */
    public DataModel (DataSet dataset){

        //this.dataBank = new DataBank();

        //1.- Initializing the auxiliary arrays to the estimated initial size (taking into account the DataSet entries)
        ArrayList<User> aListUsers = new ArrayList<User>(dataset.getRatingsSize()/40);
        ArrayList<TestUser> aListTestUsers = new ArrayList<TestUser>(dataset.getTestRatingsSize()/40);
        ArrayList<Item> aListItems = new ArrayList<Item>(dataset.getRatingsSize()/40);
        ArrayList<TestItem> aListTestItems = new ArrayList<TestItem>(dataset.getTestRatingsSize()/40);

        //2.1.- First: Adding test cases to our DataModel.
        for (Iterator<DataSet.DataSetEntry> it = dataset.getTestRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();

            //Getting TestUser with globalIndex.
            int testUserGlobalIndex = this.findTestUser(entry.first);
            TestUser testUser = this.getTestUser(testUserGlobalIndex);
            if(testUser == null) { //If don't exist, create new and add it to the arrays.
                testUser = new TestUser(entry.first); //<-
                aListUsers.add(testUser);
                aListTestUsers.add(testUser);
            }

            //Getting TestItem with globalIndex.
            int testItemGlobalIndex = this.findTestItem(entry.second);
            TestItem testItem = this.getTestItem(testItemGlobalIndex);
            if(testItem == null) {//If don't exist, create new and add it to the arrays..
                testItem = new TestItem(entry.second); //<-
                aListItems.add(testItem);
                aListTestItems.add(testItem);
            }

            if (testUserGlobalIndex == -1) testUserGlobalIndex = aListTestUsers.size() - 1;
            if (testItemGlobalIndex == -1) testItemGlobalIndex = aListTestItems.size() - 1;

            //Relating user with item.
            testUser.addTestRating(entry.second, entry.third);
            testItem.addTestRating(entry.first, entry.third);
        }

        //2.2.- Second: Adding non-test cases to our data structure
        for (Iterator<DataSet.DataSetEntry> it = dataset.getRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();

            //Also to testUsers
            //Getting User with that globalIndex.
            int userGlobalIndex = this.findUser(entry.first);
            User user = this.getUser(userGlobalIndex);
            if(user == null) { //If don't exist, create new and add it.
                user = new User(entry.first);
                aListUsers.add(user);
            }

            //Getting Item with that globalIndex.
            int itemGlobalIndex = this.findItem(entry.second);
            Item item = this.getItem(itemGlobalIndex);
            if(item == null) {//If don't exist, create new and add it.
                item = new Item(entry.second);
                aListItems.add(item);
            }

            if (userGlobalIndex == -1) userGlobalIndex = aListUsers.size() - 1;
            if (itemGlobalIndex == -1) itemGlobalIndex = aListItems.size() - 1;

            //Relating user with item.
            user.addRating(entry.second, entry.third);
            item.addRating(entry.first, entry.third);
        }

        //3.- Storing raw data to respective arrays.
        this.users = aListUsers.toArray(new User[aListUsers.size()]);
        this.testUsers = aListTestUsers.toArray(new TestUser[aListTestUsers.size()]);
        this.items = aListItems.toArray(new Item[aListItems.size()]);
        this.testItems = aListTestItems.toArray(new TestItem[aListTestItems.size()]);

        //4.- Calculate global metrics
        calculateGlobalMetrics();
    }

    /**
     * This method adds a single test rating to the DataSet
     * @param testUserGlobalIndex UserCode as string, of the rating.
     * @param testItemGlobalIndex ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public boolean addTestRating (int testUserGlobalIndex, int testItemGlobalIndex, double rating) {

        if (0 < testUserGlobalIndex && testUserGlobalIndex < this.getNumberOfTestUsers() &&
                0 < testItemGlobalIndex && testItemGlobalIndex < this.getNumberOfTestItems())
        {

            TestUser testUser = this.getTestUser(testUserGlobalIndex);
            TestItem testItem = this.getTestItem(testItemGlobalIndex);

            testUser.addTestRating(testItem.itemCode, rating);
            testItem.addTestRating(testUser.userCode, rating);

            return true;
        }
        else return false;

    }

    /**
     * This method adds a single rating to the DataSet.
     * @param userGlobalIndex UserCode as string, of the rating.
     * @param itemGlobalIndex ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public boolean addRating (int userGlobalIndex, int itemGlobalIndex, double rating) {

        if (0 < userGlobalIndex && userGlobalIndex < this.getNumberOfUsers() &&
                0 < itemGlobalIndex && itemGlobalIndex < this.getNumberOfItems())
        {
            User user = this.getUser(userGlobalIndex);
            Item item = this.getItem(itemGlobalIndex);

            user.addRating(item.itemCode, rating);
            item.addRating(user.userCode, rating);

            return true;
        }

        return false;
    }

    private void calculateGlobalMetrics(){
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;

        double sumRatigns = 0;
        int numRatings = 0;

        for (int i = 0; i < this.getNumberOfUsers(); i++){
            User user = this.getUser(i);
            min = Math.min(user.getMin(), min);
            max = Math.max(user.getMax(), max);

            if (user.getNumberOfRatings() != 0)
                sumRatigns += user.getAverage() * user.getNumberOfRatings();
            numRatings += user.getNumberOfRatings();
        }

        average = sumRatigns / numRatings;
    }

    /**
     * Getter of the stored data. This data allows you to store general calculation data inside the DataModel.
     * @return The databank who stores general information.
     */
    //public DataBank getDataBank(){
    //    return dataBank;
    //}

    /**
     * Get the number of users
     * @return Number of users
     */
    public int getNumberOfUsers () {
        return this.users.length;
    }

    /**
     * Get an user by his global index
     * @param userGlobalIndex Index of the users array inside the datamodel
     * @return User or null if userGlobalIndex is outside bounds.
     */
    public User getUser(int userGlobalIndex) {
        if (userGlobalIndex < 0 || userGlobalIndex > users.length)
            return null;

        return users[userGlobalIndex];
    }

    /**
     * Get the global index of an user at the users array
     * @param userCode User code
     * @return Global Index if the user exists or -1 if doesn't
     */
    public int findUser(String userCode) {
        for ( int i = 0; i < this.users.length; i++)
            if ( this.users[i].userCode.equals(userCode))
                return i;

        return -1;
    }

    /**
     * Get the number of test users
     * @return Number of test users
     */
    public int getNumberOfTestUsers () { return this.testUsers.length; }

    /**
     * Get an user by his global index
     * @param testUserGlobalIndex Index of the testUsers array inside the datamodel
     * @return TestUser or null if testUserGlobalIndex is outside bounds
     */
    public TestUser getTestUser(int testUserGlobalIndex) {
        if (testUserGlobalIndex < 0 || testUserGlobalIndex > testUsers.length)
            return null;

        return testUsers[testUserGlobalIndex];
    }

    /**
     * Get the index of a test user at the test users array
     * @param testUserCode User code
     * @return Global Index if the user exists or -1 if doesn't
     */
    public int findTestUser(String testUserCode) {
        for ( int i = 0; i < this.testUsers.length; i++)
            if ( this.testUsers[i].userCode.equals(testUserCode))
                return i;

        return -1;
    }

    /**
     * Get the number of items
     * @return Number of items
     */
    public int getNumberOfItems () { return this.items.length; }

    /**
     * Get an item by his global index
     * @param itemGlobalIndex Index of the items array inside the datamodel
     * @return Item or null if itemGlobalIndex is outside bounds.
     */
    public Item getItem (int itemGlobalIndex) {
        if (itemGlobalIndex < 0 || itemGlobalIndex > items.length)
            return null;

        return this.items[itemGlobalIndex];
    }

    /**
     * Get the global index of a item at the test items array
     * @param itemCode Item code
     * @return Global Index if the item exists or -1 if doesn't
     */
    public int findItem(String itemCode) {
        for ( int i = 0; i < this.items.length; i++)
            if ( this.items[i].itemCode.equals(itemCode))
                return i;

        return -1;
    }

    /**
     * Get the number of test items
     * @return Number of test items
     */
    public int getNumberOfTestItems () { return this.testItems.length; }

    /**
     * Get a test item by his global index
     * @param testItemGlobalIndex Code of the test item to retrieve
     * @return TestItem or null if testItemGlobalIndex is outside bounds.
     */
    public TestItem getTestItem (int testItemGlobalIndex) {
        if (testItemGlobalIndex < 0 || testItemGlobalIndex > testItems.length)
            return null;

        return this.testItems[testItemGlobalIndex];
    }

    /**
     * Get the index of a test item at the test items array
     * @param testItemCode Test item code
     * @return Index if the item exists or -1 if doesn't
     */
    public int findTestItem(String testItemCode) {
        for ( int i = 0; i < this.testItems.length; i++)
            if ( this.testItems[i].itemCode.equals(testItemCode))
                return i;

        return -1;
    }

    public double getMin(){ return min; }
    public double getMax(){ return max; }
    public double getAverage(){ return average; }

    public String toString() {

        calculateGlobalMetrics();

        int numRatings = 0;
        for (int i = 0; i < this.users.length; i++)
            numRatings += this.users[i].getNumberOfRatings();

        int numTestRatings = 0;
        for (int i = 0; i < this.testUsers.length; i++)
            numTestRatings += this.testUsers[i].getNumberOfTestRatings();

        return "\nNumber of users: " + this.users.length +
                "\nNumber of test users: " + this.testUsers.length +
                "\nNumber of items: " + this.items.length +
                "\nNumber of test items: " + this.testItems.length +
                "\nNumber of ratings: " + numRatings +
                "\nNumber of test ratings: " + numTestRatings +
                "\nMin rating: " + min +
                "\nMax rating: " + max +
                "\nAverage rating: " + average;
    }

}
