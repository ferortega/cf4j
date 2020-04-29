package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;

import java.io.*;
import java.util.*;

/**
 * This class manages all the information related with a collaborative filtering based recommender
 * system. A DataModel should be instantiated from a DataSet. This class also provides the
 * possibility to manage serialized files based on instances of this class.
 */
public class DataModel implements Serializable {

  private static final long serialVersionUID = 20200314L;

  /** Stored users instances */
  private User[] users;

  /** Stored items instances */
  private Item[] items;

  /** Stored test users instances */
  private TestUser[] testUsers;

  /** Stored test items instances */
  private TestItem[] testItems;

  /** Minimum (training) rating in the DataModel */
  private double minRating = Double.MAX_VALUE;

  /** Maximum (training) rating in the DataModel */
  private double maxRating = Double.MIN_VALUE;

  /** Number of (training) ratings */
  private int numberOfRatings = 0;

  /** Average (training) rating */
  private double ratingAverage = 0.0;

  /** Minimum test rating in the DataModel */
  private double minTestRating = Double.MAX_VALUE;

  /** Maximum test rating in the DataModel */
  private double maxTestRating = Double.MIN_VALUE;

  /** Number of test ratings */
  private int numberOfTestRatings = 0;

  /** Average test rating */
  private double testRatingAverage = 0.0;

  /** DataBank to store heterogeneous information */
  private DataBank dataBank;

  /**
   * This constructor initializes the DataModel with the contents of the given DataSet. Data
   * contained in the DataSet is stored in 4 different arrays: user, testUser, item and testItems.
   * Keeping the main behaviour of the collaborative filtering in mind, any TestUser could have
   * voted both Item or TestItem. In the same way, any testItem could have been voted by a User and
   * TestUser. In the other hand, Users and Items will not have a test part.
   *
   * @param dataset DataSet to be added to the DataModel.
   */
  public DataModel(DataSet dataset) {

    this.dataBank = new DataBank();

    // Initializing the auxiliary arrays to the estimated initial size (taking into account the
    // DataSet entries)
    List<User> usersList = new ArrayList<>();
    List<TestUser> testUsersList = new ArrayList<>();
    List<Item> itemsList = new ArrayList<>();
    List<TestItem> testItemsLists = new ArrayList<>();

    Map<String, Integer> id2userIndex = new HashMap<>();
    Map<String, Integer> id2testUserIndex = new HashMap<>();
    Map<String, Integer> id2itemIndex = new HashMap<>();
    Map<String, Integer> id2testItemIndex = new HashMap<>();

    // First: Adding test cases to our DataModel

    for (Iterator<DataSetEntry> it = dataset.getTestRatingsIterator(); it.hasNext(); ) {
      DataSetEntry entry = it.next();

      // Getting TestUser with Index
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

      // Getting TestItem with Index
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

      // Relating user with item
      testUser.addTestRating(testItem.getTestItemIndex(), entry.rating);
      testItem.addTestRating(testUser.getTestUserIndex(), entry.rating);

      this.minTestRating = Math.min(entry.rating, this.minTestRating);
      this.maxTestRating = Math.max(entry.rating, this.maxTestRating);

      this.numberOfTestRatings++;
      this.testRatingAverage =
          (this.testRatingAverage * (this.numberOfTestRatings - 1) + entry.rating)
              / this.numberOfTestRatings;
    }

    // Second: Adding non-test cases to our data structure
    for (Iterator<DataSetEntry> it = dataset.getRatingsIterator(); it.hasNext(); ) {
      DataSetEntry entry = it.next();

      // Getting User with that Index
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

      // Getting Item with that Index
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

      // Relating user with item
      user.addRating(item.getItemIndex(), entry.rating);
      item.addRating(user.getUserIndex(), entry.rating);

      this.minRating = Math.min(entry.rating, this.minRating);
      this.maxRating = Math.max(entry.rating, this.maxRating);

      this.numberOfRatings++;
      this.ratingAverage =
          (this.ratingAverage * (this.numberOfRatings - 1) + entry.rating) / this.numberOfRatings;
    }

    // Storing raw data to respective arrays
    this.users = usersList.toArray(new User[0]);
    this.testUsers = testUsersList.toArray(new TestUser[0]);
    this.items = itemsList.toArray(new Item[0]);
    this.testItems = testItemsLists.toArray(new TestItem[0]);
  }

  /**
   * Saves the content of the DataModel in a serialized file.
   *
   * @param filePath Path where the file will be stored, filename and extension should be included
   *     in the path.
   * @throws IOException When the file is not accessible by the system with write permissions.
   */
  public void save(String filePath) throws IOException {
    FileOutputStream fileOut = new FileOutputStream(filePath);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(this);
    out.close();
    fileOut.close();
    System.out.println("Serialized DataModel is saved in " + filePath + ".");
  }

  /**
   * Loads a DataModel from a previously serialized file (see save() method).
   *
   * @param filePath Path where the file will be loaded, filename and extension should be included
   *     in the path.
   * @return If te file was successfully loaded, this method returns the DataModel.
   * @throws IOException When the file is not accessible by the system with reading permissions.
   * @throws ClassNotFoundException When the file exist and is accessible, but it doesn't contains a
   *     valid instance.
   */
  public static DataModel load(String filePath) throws IOException, ClassNotFoundException {
    FileInputStream fileIn = new FileInputStream(filePath);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    DataModel dataModel = (DataModel) in.readObject();
    in.close();
    fileIn.close();
    return dataModel;
  }

  /**
   * Adds a single test rating to the DataModel.
   *
   * @param testUserIndex Index of the TestUser at the DataModel
   * @param testItemIndex Index of the TestItem at the DataModel
   * @param rating Rating value
   */
  public void addTestRating(int testUserIndex, int testItemIndex, double rating) {
    TestUser testUser = this.getTestUser(testUserIndex);
    TestItem testItem = this.getTestItem(testItemIndex);

    testUser.addTestRating(testItemIndex, rating);
    testItem.addTestRating(testUserIndex, rating);

    this.minTestRating = Math.min(rating, this.minTestRating);
    this.maxTestRating = Math.max(rating, this.maxTestRating);

    this.numberOfTestRatings++;
    this.testRatingAverage =
        (this.testRatingAverage * (this.numberOfTestRatings - 1) + rating)
            / this.numberOfTestRatings;
  }

  /**
   * Adds a single (training) rating to the DataModel.
   *
   * @param userIndex Index of the User at the DataModel
   * @param itemIndex Index of the Item at the DataModel
   * @param rating Rating value
   */
  public void addRating(int userIndex, int itemIndex, double rating) {
    User user = this.getUser(userIndex);
    Item item = this.getItem(itemIndex);

    user.addRating(itemIndex, rating);
    item.addRating(userIndex, rating);

    this.minRating = Math.min(rating, this.minRating);
    this.maxRating = Math.max(rating, this.maxRating);

    this.numberOfRatings++;
    this.ratingAverage =
        (this.ratingAverage * (this.numberOfRatings - 1) + rating) / this.numberOfRatings;
  }

  /**
   * Gets the array of Users. If you need a specific User it is recommended to use the getUser()
   * method.
   *
   * @return Array of Users
   */
  public User[] getUsers() {
    return this.users;
  }

  /**
   * Gets the array of TestUsers. If you need a specific TestUser it is recommended to use the
   * getTestUser() method.
   *
   * @return Array of TestUsers
   */
  public TestUser[] getTestUsers() {
    return this.testUsers;
  }

  /**
   * Gets the array of Items. If you need a specific Item it is recommended to use the getItem()
   * method.
   *
   * @return Array of Items
   */
  public Item[] getItems() {
    return this.items;
  }

  /**
   * Gets the array of TestItems. If you need a specific TestItem it is recommended to use the
   * getTestItem() method.
   *
   * @return Array of TestItems
   */
  public TestItem[] getTestItems() {
    return this.testItems;
  }

  /**
   * Gets the DataBank instance that stores heterogeneous information related to the DataModel.
   *
   * @return DataBank instance
   */
  public DataBank getDataBank() {
    return dataBank;
  }

  /**
   * Gets the number of users contained in the DataModel.
   *
   * @return Number of users
   */
  public int getNumberOfUsers() {
    return this.users.length;
  }

  /**
   * Gets an User by his/her index.
   *
   * @param userIndex Index of the User in the Users' array inside the DataModel.
   * @return User located at given userIndex
   */
  public User getUser(int userIndex) {
    return users[userIndex];
  }

  /**
   * Finds the userIndex of a User at the Users' array given his/her unique id.
   *
   * @param userId User id to be searched.
   * @return userIndex if the User exists or -1 if doesn't.
   */
  public int findUserIndex(String userId) {
    for (int i = 0; i < this.users.length; i++) {
      if (this.users[i].getId().equals(userId)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the number of test users contained in the DataModel.
   *
   * @return Number of test users
   */
  public int getNumberOfTestUsers() {
    return this.testUsers.length;
  }

  /**
   * Gets a TestUser by his/her test index.
   *
   * @param testUserIndex Index of the TestUser in the TestUsers' array inside the DataModel.
   * @return TestUser located at given testUserIndex
   */
  public TestUser getTestUser(int testUserIndex) {
    return testUsers[testUserIndex];
  }

  /**
   * Finds the testUserIndex of a TestUser at the TestUsers' array given his/her unique id.
   *
   * @param userId User id to be searched.
   * @return testUserIndex if the TestUser exists or -1 if doesn't.
   */
  public int findTestUserIndex(String userId) {
    for (int i = 0; i < this.testUsers.length; i++) {
      if (this.testUsers[i].getId().equals(userId)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the number of items contained in the DataModel.
   *
   * @return Number of items
   */
  public int getNumberOfItems() {
    return this.items.length;
  }

  /**
   * Gets an Item by its index.
   *
   * @param itemIndex Index of the Item in the Items' array inside the DataModel.
   * @return Item located at given itemIndex
   */
  public Item getItem(int itemIndex) {
    return this.items[itemIndex];
  }

  /**
   * Finds the itemIndex of an Item at the Items' array given its unique id.
   *
   * @param itemId Item id to be searched.
   * @return itemIndex if the Item exists or -1 if doesn't.
   */
  public int findItemIndex(String itemId) {
    for (int i = 0; i < this.items.length; i++) {
      if (this.items[i].getId().equals(itemId)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the number of test items contained in the DataModel.
   *
   * @return Number of test items
   */
  public int getNumberOfTestItems() {
    return this.testItems.length;
  }

  /**
   * Gets a TestItem by its index.
   *
   * @param testItemIndex Index of the TestItem in the TestItem' array inside the DataModel.
   * @return TestItem located at given testItemIndex
   */
  public TestItem getTestItem(int testItemIndex) {
    return this.testItems[testItemIndex];
  }

  /**
   * Finds the testItemIndex of a TestItem at the TestItem' array given its unique id.
   *
   * @param itemId Item id to be searched.
   * @return testItemIndex if the TestItem exists or -1 if doesn't.
   */
  public int findTestItemIndex(String itemId) {
    for (int i = 0; i < this.testItems.length; i++) {
      if (this.testItems[i].getId().equals(itemId)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the minimum (training) rating.
   *
   * @return Minimum rating
   */
  public double getMinRating() {
    return minRating;
  }

  /**
   * Gets the maximum (training) rating.
   *
   * @return Maximum rating
   */
  public double getMaxRating() {
    return maxRating;
  }

  /**
   * Gets the average of (training) ratings.
   *
   * @return Rating average value
   */
  public double getRatingAverage() {
    return this.ratingAverage;
  }

  /**
   * Gets the minimum test rating.
   *
   * @return Minimum test rating
   */
  public double getMinTestRating() {
    return this.minTestRating;
  }

  /**
   * Gets the maximum test rating.
   *
   * @return Maximum test rating
   */
  public double getMaxTestRating() {
    return this.maxTestRating;
  }

  /**
   * Gets the average of test ratings.
   *
   * @return Test rating average value
   */
  public double getTestRatingAverage() {
    return this.testRatingAverage;
  }

  /**
   * Return the number of ratings contained in the DataModel.
   *
   * @return Number of ratings
   */
  public int getNumberOfRatings() {
    return this.numberOfRatings;
  }

  /**
   * Return the number of test ratings contained in the DataModel.
   *
   * @return Number of test ratings
   */
  public int getNumberOfTestRatings() {
    return this.numberOfTestRatings;
  }

  @Override
  public String toString() {
    return "\nNumber of users: "
        + this.getNumberOfUsers()
        + "\nNumber of test users: "
        + this.getNumberOfTestUsers()
        + "\nNumber of items: "
        + this.getNumberOfItems()
        + "\nNumber of test items: "
        + this.getNumberOfTestItems()
        + "\nNumber of ratings: "
        + this.getNumberOfRatings()
        + "\nMin rating: "
        + this.getMinRating()
        + "\nMax rating: "
        + this.getMaxRating()
        + "\nAverage rating: "
        + this.getRatingAverage()
        + "\nNumber of test ratings: "
        + this.getNumberOfTestRatings()
        + "\nMin test rating: "
        + this.getMinTestRating()
        + "\nMax test rating: "
        + this.getMaxTestRating()
        + "\nAverage test rating: "
        + this.getTestRatingAverage();
  }
}
