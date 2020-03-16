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
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private double average = 0.0;

    private DataBank dataBank;

    /**
     * This constructor initializes the DataModel with the contents the given DataSet.
     * @param dataset DataSet to be added to the DataModel.
     */
    public DataModel (DataSet dataset){

        this.dataBank = new DataBank();

        //1.- Initializing the auxiliary arrays to the estimated initial size (taking into account the DataSet entries)
        ArrayList<User> aListUsers = new ArrayList<User>(dataset.getRatingsSize()/40);
        ArrayList<TestUser> aListTestUsers = new ArrayList<TestUser>(dataset.getTestRatingsSize()/40);
        ArrayList<Item> aListItems = new ArrayList<Item>(dataset.getRatingsSize()/40);
        ArrayList<TestItem> aListTestItems = new ArrayList<TestItem>(dataset.getTestRatingsSize()/40);

        //2.1.- First: Adding test cases to our DataModel.
        for (Iterator<DataSet.DataSetEntry> it = dataset.getTestRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();

            //Getting TestUser with Index.
            int testUserIndex = this.findTestUser(entry.first);
            TestUser testUser = this.getTestUser(testUserIndex);
            if(testUser == null) { //If don't exist, create new and add it to the arrays.
                testUser = new TestUser(entry.first); //<-
                aListUsers.add(testUser);
                aListTestUsers.add(testUser);
                testUserIndex = aListTestUsers.size() - 1;
            }

            //Getting TestItem with Index.
            int testItemIndex = this.findTestItem(entry.second);
            TestItem testItem = this.getTestItem(testItemIndex);
            if(testItem == null) {//If don't exist, create new and add it to the arrays..
                testItem = new TestItem(entry.second); //<-
                aListItems.add(testItem);
                aListTestItems.add(testItem);
                testItemIndex = aListTestItems.size() - 1;
            }

            //Relating user with item.
            testUser.addTestRating(testItemIndex, entry.third);
            testItem.addTestRating(testUserIndex, entry.third);

            int sumEntries = aListUsers.size() + aListItems.size() + aListTestUsers.size() + aListTestItems.size();
            min = Math.min(entry.third, min);
            max = Math.max(entry.third, max);
            average = (sumEntries <= 1) ? entry.third : ((average * (sumEntries-1)) + entry.third) / sumEntries;
        }

        //2.2.- Second: Adding non-test cases to our data structure
        for (Iterator<DataSet.DataSetEntry> it = dataset.getRatingsIterator(); it.hasNext(); ){
            DataSet.DataSetEntry entry = it.next();

            //Also to testUsers
            //Getting User with that Index.
            int userIndex = this.findUser(entry.first);
            User user = this.getUser(userIndex);
            if(user == null) { //If don't exist, create new and add it.
                user = new User(entry.first);
                aListUsers.add(user);
                userIndex = aListUsers.size() - 1;
            }

            //Getting Item with that Index.
            int itemIndex = this.findItem(entry.second);
            Item item = this.getItem(itemIndex);
            if(item == null) {//If don't exist, create new and add it.
                item = new Item(entry.second);
                aListItems.add(item);
                itemIndex = aListItems.size() - 1;
            }

            //Relating user with item.
            user.addRating(itemIndex, entry.third);
            item.addRating(userIndex, entry.third);

            int sumEntries = aListUsers.size() + aListItems.size() + aListTestUsers.size() + aListTestItems.size();
            min = Math.min(entry.third, min);
            max = Math.max(entry.third, max);
            average = (sumEntries <= 1) ? entry.third : ((average * (sumEntries-1)) + entry.third) / sumEntries;
        }

        //3.- Storing raw data to respective arrays.
        this.users = aListUsers.toArray(new User[aListUsers.size()]);
        this.testUsers = aListTestUsers.toArray(new TestUser[aListTestUsers.size()]);
        this.items = aListItems.toArray(new Item[aListItems.size()]);
        this.testItems = aListTestItems.toArray(new TestItem[aListTestItems.size()]);
    }

    /**
     * This method adds a single test rating to the DataSet
     * @param testUserIndex UserCode as string, of the rating.
     * @param testItemIndex ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public boolean addTestRating (int testUserIndex, int testItemIndex, double rating) {

        if (0 < testUserIndex && testUserIndex < this.getNumberOfTestUsers() &&
                0 < testItemIndex && testItemIndex < this.getNumberOfTestItems())
        {

            TestUser testUser = this.getTestUser(testUserIndex);
            TestItem testItem = this.getTestItem(testItemIndex);

            testUser.addTestRating(testItemIndex, rating);
            testItem.addTestRating(testUserIndex, rating);

            return true;
        }
        else return false;
    }

    /**
     * This method adds a single rating to the DataSet.
     * @param userIndex UserCode as string, of the rating.
     * @param itemIndex ItemCode to be rated.
     * @param rating Rating of the item.
     */
    public boolean addRating (int userIndex, int itemIndex, double rating) {

        if (0 < userIndex && userIndex < this.getNumberOfUsers() &&
                0 < itemIndex && itemIndex < this.getNumberOfItems())
        {
            User user = this.getUser(userIndex);
            Item item = this.getItem(itemIndex);

            user.addRating(itemIndex, rating);
            item.addRating(userIndex, rating);

            return true;
        }

        return false;
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
     * Get an user by his  index
     * @param userIndex Index of the users array inside the datamodel
     * @return User or null if userIndex is outside bounds.
     */
    public User getUser(int userIndex) {
        if (userIndex < 0 || userIndex > users.length)
            return null;

        return users[userIndex];
    }

    /**
     * Get the  index of an user at the users array
     * @param userCode User code
     * @return  Index if the user exists or -1 if doesn't
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
     * Get an user by his  index
     * @param testUserIndex Index of the testUsers array inside the datamodel
     * @return TestUser or null if testUserIndex is outside bounds
     */
    public TestUser getTestUser(int testUserIndex) {
        if (testUserIndex < 0 || testUserIndex > testUsers.length)
            return null;

        return testUsers[testUserIndex];
    }

    /**
     * Get the index of a test user at the test users array
     * @param testUserCode User code
     * @return  Index if the user exists or -1 if doesn't
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
     * Get an item by his  index
     * @param itemIndex Index of the items array inside the datamodel
     * @return Item or null if itemIndex is outside bounds.
     */
    public Item getItem (int itemIndex) {
        if (itemIndex < 0 || itemIndex > items.length)
            return null;

        return this.items[itemIndex];
    }

    /**
     * Get the  index of a item at the test items array
     * @param itemCode Item code
     * @return  Index if the item exists or -1 if doesn't
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
     * Get a test item by his  index
     * @param testItemIndex Code of the test item to retrieve
     * @return TestItem or null if testItemIndex is outside bounds.
     */
    public TestItem getTestItem (int testItemIndex) {
        if (testItemIndex < 0 || testItemIndex > testItems.length)
            return null;

        return this.testItems[testItemIndex];
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

    /**
     * Get the minimum rating done
     * @return minimum rating
     */
    public double getMin(){ return min; }

    /**
     * Get the maximum rating done
     * @return maximum rating
     */
    public double getMax(){ return max; }

    /**
     * Get the average of ratings done
     * @return average
     */
    public double getAverage(){ return average; }

    public String toString() {

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
