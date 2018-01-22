package cf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;


/**
 * <p>Class that manages all information of the Collaborative Filtering kernel. Contains the users and
 * items sets imported from the database.</p>
 *
 * <p>This class can not be instantiated. It implements the singleton pattern, so, when we want to use
 * it, we must use the getInstance() method.</p>
 * @author Fernando Ortega
 */
public class Kernel implements Serializable {

	private static final long serialVersionUID = 20171018L;

	private static String DEFAULT_SPARATOR = ";";

	/**
	 * Class instance (Singleton pattern)
	 */
	private static Kernel instance = null;

	/**
	 * Users array
	 */
	private User [] users;

	/**
	 * Items array
	 */
	private Item [] items;

	/**
	 * Test users array
	 */
	private TestUser [] testUsers;

	/**
	 * Test items array
	 */
	private TestItem [] testItems;

	/**
	 * Map of the item to write any type of data
	 */
	private Map <String, Object> map = new HashMap<String, Object>();

	/**
	 * Maximum user code
	 */
	private int maxUserCode;

	/**
	 * Minimum user code
	 */
	private int minUserCode;

	/**
	 * Maximum item code
	 */
	private int maxItemCode;

	/**
	 * Minimum item code
	 */
	private int minItemCode;

	/**
	 * Maximum rating
	 */
	private double maxRating;

	/**
	 * Minimum rating
	 */
	private double minRating;

	/**
	 * Rating average
	 */
	private double ratingAverage;

	/**
	 * Kernel constructor.
	 */
	private Kernel () { }

	/**
	 * Gets the single instance of the class.
	 * @return Single instance
	 */
	public static Kernel getInstance() {
		if (Kernel.instance == null)
			Kernel.instance = new Kernel();
		return Kernel.instance;
	}

	/**
	 * Gets the single instance of the class.
	 * @return Single instance
	 */
	public static Kernel gi () {
		return Kernel.getInstance();
	}

	/**
	 * Destroy the single instance of the class
	 */
	public static void destroyInstance() {
		Kernel.instance = null;
		System.gc();
	}

	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * <p>The dataset is loaded without test items and test users</p>
	 * @param filename File with the ratings
	 */
	public void open (String filename) {
		this.open(filename, DEFAULT_SPARATOR);
	}

	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode SEPARATOR itemCode SEPARATOR rating</p>
	 * <p>The dataset is loaded without test items and test users</p>
	 * @param filename File with the ratings
	 * @param separator Separator char between file fields
	 */
	public void open (String filename, String separator) {
		this.open(filename, 0.0, 0.0, separator);
	}

	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * @param filename File with the ratings
	 * @param testUsersPercent Percentage of users that will be of test
	 * @param testItemsPercent Percentage of items that will be of test
	 */
	public void open (String filename, double testUsersPercent, double testItemsPercent) {
		this.open(filename, testUsersPercent, testItemsPercent, DEFAULT_SPARATOR);
	}
	
	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * @param filename File with the ratings
	 * @param testUsersPercent Percentage of users that will be of test
	 * @param testItemsPercent Percentage of items that will be of test
	 * @param separator Separator char between file fields
	 */
	public void open (String filename, double testUsersPercent, double testItemsPercent, String separator) {
		this.open(filename, DatasetSplitters.random(testUsersPercent), DatasetSplitters.random(testItemsPercent), separator);
	}
	
	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * @param filename File with the ratings
	 * @param testUserFilter Lambda function that receives the user code and the user ratings and return true if the user is a test user and false otherwise
	 * @param testItemFilter Lambda function that receives the item code and the item ratings and return true if the item is a test item and false otherwise
	 * @param separator Separator char between file fields
	 * @see DatasetSplitters
	 */
	public void open (String filename, BiFunction <Integer, Map <Integer, Double>, Boolean> testUserFilter, 
			BiFunction <Integer, Map <Integer, Double>, Boolean> testItemFilter, String separator) {

		System.out.println("\nLoading dataset...");

		this.maxItemCode = Integer.MIN_VALUE;
		this.minItemCode = Integer.MAX_VALUE;
		this.maxUserCode = Integer.MIN_VALUE;
		this.minUserCode = Integer.MAX_VALUE;
		this.maxRating = Byte.MIN_VALUE;
		this.minRating = Byte.MAX_VALUE;

		TreeMap <Integer, TreeMap <Integer, Double>> usersRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();
		TreeMap <Integer, TreeMap <Integer, Double>> itemsRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();

		try {

			// Dataset reader
			BufferedReader dataset = new BufferedReader (new FileReader (new File (filename)));

			String line = ""; int numLines = 0;
			while ((line = dataset.readLine()) != null) {

				numLines++;
				if (numLines % 1000000  == 0) System.out.print(".");
				if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

				// Parse line
				String [] s = line.split("::");
				int userCode = Integer.parseInt(s[0]);
				int itemCode = Integer.parseInt(s[1]);
				double rating = Double.parseDouble(s[2]);

				// Update stats
				if (itemCode < this.minItemCode) this.minItemCode = itemCode;
				if (itemCode > this.maxItemCode) this.maxItemCode = itemCode;
				if (userCode < this.minUserCode) this.minUserCode = userCode;
				if (userCode > this.maxUserCode) this.maxUserCode = userCode;
				if (rating < this.minRating) this.minRating = rating;
				if (rating > this.maxRating) this.maxRating = rating;

				// Store rating
				if (!usersRatings.containsKey(userCode)) usersRatings.put(userCode, new TreeMap <Integer, Double> ());
				usersRatings.get(userCode).put(itemCode, rating);
				
				if (!itemsRatings.containsKey(itemCode)) itemsRatings.put(itemCode, new TreeMap <Integer, Double> ());
				itemsRatings.get(itemCode).put(userCode, rating);
			}

			dataset.close();

		} catch (Exception e) {
			System.out.println("An error has occurred while loading database");
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("\nSpliting users & items into training and test sets...");

		// Setting test users
		TreeSet <Integer> testUsersSet = new TreeSet <Integer> ();
		for (int userCode : usersRatings.keySet()) {
			Map <Integer, Double> ratings = usersRatings.get(userCode);
			if (testUserFilter.apply(userCode, ratings)) testUsersSet.add(userCode);
		}

		// Setting test items
		TreeSet <Integer> testItemsTest = new TreeSet <Integer> ();
		for (int itemCode : itemsRatings.keySet()) {
			Map <Integer, Double> ratings = itemsRatings.get(itemCode);
			if (testItemFilter.apply(itemCode, ratings)) testItemsTest.add(itemCode);
		}

		System.out.println("\nGenerating users sets...");

		int averageCount = 0;

		this.users = new User [usersRatings.size()];
		int userIndex = 0;

		this.testUsers = new TestUser [testUsersSet.size()];
		int testUserIndex = 0;

		for (int userCode : usersRatings.keySet()) {

			User user;

			// Is test user
			if (testUsersSet.contains(userCode)) {

				// Splitting ratings into test & training ratings
				TreeSet <Integer> training = new TreeSet <Integer> ();
				TreeSet <Integer> test = new TreeSet <Integer> ();
				for (int itemCode : usersRatings.get(userCode).keySet()) {
					if (testItemsTest.contains(itemCode)) test.add(itemCode);
					else training.add(itemCode);
				}

				// Setting training ratings arrays
				int [] itemsArray = new int [training.size()];
				double [] ratingsArray = new double [training.size()];
				int i = 0; for (int itemCode : training) {
					itemsArray[i] = itemCode;
					ratingsArray[i] = usersRatings.get(userCode).get(itemCode);
					i++;
				}

				// Settings test ratings arrays
				int [] testItemsArray = new int [test.size()];
				double [] testRatingsArray = new double [test.size()];
				i = 0; for (int itemCode : test) {
					testItemsArray[i] = itemCode;
					testRatingsArray[i] = usersRatings.get(userCode).get(itemCode);
					i++;
				}

				// If user did not have rated any test item, discard him
				if (test.size() == 0) {
					user = new User(userCode, userIndex, itemsArray, ratingsArray);
					
				// It not, create new testUser instance
				} else {
					user = new TestUser(userCode, userIndex, itemsArray, ratingsArray, testUserIndex, testItemsArray, testRatingsArray);

					// Add user to test users
					this.testUsers[testUserIndex] = (TestUser) user;
					testUserIndex++;
				}

			// Is training user
			} else {

				// Setting rating arrays
				int [] itemsArray = new int [usersRatings.get(userCode).keySet().size()];
				double [] ratingsArray = new double [usersRatings.get(userCode).keySet().size()];
				int i = 0; for (int item_code : usersRatings.get(userCode).keySet()) {
					itemsArray[i] = item_code;
					ratingsArray[i] = usersRatings.get(userCode).get(item_code);
					i++;
				}

				// New user instance
				user = new User(userCode, userIndex, itemsArray, ratingsArray);
			}

			// Add user to training users
			this.users[userIndex] = user;
			userIndex++;

			this.ratingAverage += user.getRatingAverage() * user.getNumberOfRatings();
			averageCount += user.getNumberOfRatings();
		}

		this.ratingAverage /= averageCount;
		
		// Remove gaps from testUser array
		TestUser [] testUsersTemp = new TestUser [testUserIndex];
		for (int i = 0; i < testUserIndex; i++) {
			testUsersTemp[i] = this.testUsers[i];
		}
		this.testUsers = testUsersTemp;
		

		System.out.println("\nGenerating items sets...");

		this.items = new Item [itemsRatings.size()];
		int itemIndex = 0;

		this.testItems = new TestItem [testItemsTest.size()];
		int testItemIndex = 0;

		for (int itemCode : itemsRatings.keySet()) {

			Item item;

			// Is test item
			if (testItemsTest.contains(itemCode)) {

				// Splitting ratings into test & training ratings
				TreeSet <Integer> training = new TreeSet <Integer> ();
				TreeSet <Integer> test = new TreeSet <Integer> ();
				for (int userCode : itemsRatings.get(itemCode).keySet()) {
					if (testUsersSet.contains(userCode)) test.add(userCode);
					else training.add(userCode);
				}

				// Setting training ratings arrays
				int [] usersArray = new int [training.size()];
				double [] ratingsArray = new double [training.size()];
				int i = 0; for (int userCode : training) {
					usersArray[i] = userCode;
					ratingsArray[i] = itemsRatings.get(itemCode).get(userCode);
					i++;
				}

				// Settings test ratings arrays
				int [] testUsersArray = new int [test.size()];
				double [] testRatingsArray = new double [test.size()];
				i = 0; for (int userCode : test) {
					testUsersArray[i] = userCode;
					testRatingsArray[i] = itemsRatings.get(itemCode).get(userCode);
					i++;
				}

				// If item did not have received any rating, discard it
				if (test.size() == 0) {
					item = new Item(itemCode, itemIndex, usersArray, ratingsArray);
					
				// It not, create new testUser instance
				} else {
					item = new TestItem(itemCode, itemIndex, usersArray, ratingsArray, testItemIndex, testUsersArray, testRatingsArray);

					// Add item to test items
					this.testItems[testItemIndex] = (TestItem) item;
					testItemIndex++;
				}

			// Is training item
			} else {

				// Setting rating arrays
				int [] usersArrays = new int [itemsRatings.get(itemCode).keySet().size()];
				double [] ratingsArrays = new double [itemsRatings.get(itemCode).keySet().size()];
				int i = 0; for (int userCode : itemsRatings.get(itemCode).keySet()) {
					usersArrays[i] = userCode;
					ratingsArrays[i] = itemsRatings.get(itemCode).get(userCode);
					i++;
				}

				// New item instance
				item = new Item(itemCode, itemIndex, usersArrays, ratingsArrays);
			}

			// Add item to training items
			this.items[itemIndex] = item;
			itemIndex++;
		}
		
		// Remove gaps from testItems array
		TestItem [] testItemsTemp = new TestItem [testItemIndex];
		for (int i = 0; i < testItemIndex; i++) {
			testItemsTemp[i] = this.testItems[i];
		}
		this.testItems = testItemsTemp;

		System.out.println("\n'" + filename + "' dataset loaded succesfully");
	}

	/**
	 * Retrieves a value from a key
	 * @param key Key of the saved object
	 * @return The value associated to the key if exists or null
	 */
	public Object get (String key) {
		return map.get(key);
	}

	/**
	 * Retrieve the value of a quality measure stored in the kernel map
	 * @param name Name of the quality measure
	 * @return Quality measure value
	 */
	public double getQualityMeasure (String name) {
		return (Double) this.get(name);
	}

	/**
	 * Write a data in the item map
	 * @param key Key associated to the value
	 * @param value Value to be written in the map
	 * @return Previously value of the key if exists or null
	 */
	public Object put (String key, Object value) {
		return map.put(key, value);
	}

	/**
	 * Save the Kernel on a binary file
	 * @param filename File name
	 * @return True if no error exits or False in other case
	 */
	public boolean writeKernel (String filename) {
		System.out.println("\nStoring kernel...");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			oos.writeObject(this);
			oos.flush();
			oos.close();
			System.out.println("\nKernel stored successfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error has occurred while storing kernel");
			return false;
		}
		return true;
	}

	/**
	 * Retrieve the Kernel from a binary file
	 * @param filename File name
	 * @return True if no error exits or False in other case
	 */
	public boolean readKernel (String filename) {
		System.out.println("\nLoading kernel...");
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
			Kernel.instance = (Kernel) ois.readObject();
			System.out.println("\nKernel loaded successfully");
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error has occurred while loading kernel");
			return false;
		}
		return true;
	}

	/**
	 * Writes on the Kernel map the average value of a key present in the users map. If one test
	 * user don not have the value, he will be ignored. The NaN values will be ignored.
	 * @param key Key on which calculate the average
	 */
	public void putUsersAverage (String key) {
		double summation = 0.0f;
		int numValues = 0;
		for (User user : this.users) {
			if (user.get(key) != null) {
				double userValue = (Double) user.get(key);
				if (!Double.isNaN(userValue)) {
					summation += userValue;
					numValues++;
				}
			}
		}

		if (numValues > 0) {
			this.put(key, new Double(summation / numValues));
		} else {
			this.put(key, Double.NaN);
		}
	}

	/**
	 * Returns the array of users
	 * @return Users sorted from low user code to high user code.
	 */
	public User [] getUsers() {
		return this.users;
	}

	/**
	 * Returns the array of test users
	 * @return Test users sorted from low user code to high user code.
	 */
	public TestUser [] getTestUsers() {
		return this.testUsers;
	}

	/**
	 * Returns the array of items
	 * @return Items sorted from low item code to high item code.
	 */
	public Item [] getItems() {
		return this.items;
	}

	/**
	 * Returns the array of test items
	 * @return Test items sorted from low item code to high item code.
	 */
	public TestItem [] getTestItems() {
		return this.testItems;
	}

	/**
	 * Returns the map of the Kernel. It is recommended using put(...) and get(...) instead of
	 * this method.
	 * @return Map of the kernel
	 */
	public Map <String, Object> getMap() {
		return this.map;
	}

	/**
	 * Returns the maximum user code
	 * @return Maximum user code
	 */
	public int getMaxUserCode() {
		return this.maxUserCode;
	}

	/**
	 * Returns the minimum user code
	 * @return Minimum user code
	 */
	public int getMinUserCode() {
		return this.minUserCode;
	}

	/**
	 * Returns the maximum item code
	 * @return Maximum item code
	 */
	public int getMaxItemCode() {
		return this.maxItemCode;
	}

	/**
	 * Returns the minimum item code
	 * @return Minimum item code
	 */
	public int getMinItemCode() {
		return this.minItemCode;
	}

	/**
	 * Returns the maximum rating
	 * @return Maximum rating
	 */
	public double getMaxRating() {
		return this.maxRating;
	}

	/**
	 * Returns the minimum rating
	 * @return Minimum rating
	 */
	public double getMinRating() {
		return this.minRating;
	}

	/**
	 * Returns the rating average
	 * @return Rating average
	 */
	public double getRatingAverage() {
		return this.ratingAverage;
	}

	/**
	 * Get the index of an item at the items array
	 * @param itemCode Item code
	 * @return Index if the item exists or -1 if not
	 */
	public int getItemIndex (int itemCode) {
		return this.getIndex(this.items, itemCode);
	}

	/**
	 * Get the index of a test item at the test items array
	 * @param itemCode Test item code
	 * @return Index if the item exists or -1 if not
	 */
	public int getTestItemIndex (int itemCode) {
		return this.getIndex(this.testItems, itemCode);
	}

	/**
	 * Search an item on an array
	 * @param items Items sorted
	 * @param itemCode Item code
	 * @return Index of the item or -1
	 */
	private int getIndex (Item [] items, int itemCode) {
		int min = 0, max = items.length -1;
		while (min <= max) {
			int center = ((max - min) / 2) + min;
			if (items[center].getItemCode() == itemCode) return center;
			if (itemCode < items[center].getItemCode()) {
				max = center - 1;
			} else {
				min = center + 1;
			}
		}
		return -1;
	}

	/**
	 * Get the index of an user at the users array
	 * @param userCode User code
	 * @return Index if the user exists or -1 if not
	 */
	public int getUserIndex (int userCode) {
		return this.getIndex(this.users, userCode);
	}

	/**
	 * Get the index of a test user at the test users array
	 * @param userCode User code
	 * @return Index if the user exists or -1 if not
	 */
	public int getTestUserIndex (int userCode) {
		return this.getIndex(this.testUsers, userCode);
	}

	/**
	 * Search an user on an array
	 * @param users Users sorted
	 * @param userCode User code
	 * @return Index of the user or -1
	 */
	private int getIndex (User [] users, int userCode) {
		int min = 0, max = users.length -1;
		while (min <= max) {
			int center = ((max - min) / 2) + min;
			if (users[center].getUserCode() == userCode) return center;
			if (userCode < users[center].getUserCode()) {
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
		return this.users.length;
	}

	/**
	 * Get the number of test users
	 * @return Number of test users
	 */
	public int getNumberOfTestUsers () {
		return this.testUsers.length;
	}

	/**
	 * Get the number of items
	 * @return Number of items
	 */
	public int getNumberOfItems () {
		return this.items.length;
	}

	/**
	 * Get the number of test items
	 * @return Number of test items
	 */
	public int getNumberOfTestItems () {
		return this.testItems.length;
	}

	/**
	 * Get information about the kernel loaded
	 * @return String with information about the kernel
	 */
	public String getKernelInfo () {
		int numRatings = 0;
		for (User user : this.users) numRatings += user.getNumberOfRatings();

		int numTestRatings = 0;
		for (TestUser user : this.testUsers) numTestRatings += user.getNumberOfTestRatings();

		return "\nNumber of users: " + this.users.length +
				"\nNumber of test users: " + this.testUsers.length +
				"\nNumber of items: " + this.items.length +
				"\nNumber of test items: " + this.testItems.length +
				"\nNumber of ratings: " + numRatings +
				"\nNumber of test ratings: " + numTestRatings +
				"\nMin rating: " + this.minRating +
				"\nMax rating: " + this.maxRating;
	}

	/**
	 * Get an item by his code
	 * @param itemCode Code of the item to retrieve
	 * @return Item or null
	 */
	public Item getItemByCode (int itemCode) {
		int index = this.getItemIndex(itemCode);
		if (index == -1) return null;
		else return this.items[index];
	}
	
	/**
	 * Get an item by his index
	 * @param itemIndex Index of the item to retrieve
	 * @return Item or null
	 */
	public Item getItemByIndex (int itemIndex) {
		if (itemIndex >= this.getNumberOfItems()) return null;
		else return this.items[itemIndex];
	}

	/**
	 * Get a test item by his code
	 * @param itemCode Code of the test item to retrieve
	 * @return TestItem or null
	 */
	public TestItem getTestItemByCode (int itemCode) {
		int index = this.getTestItemIndex(itemCode);
		if (index == -1) return null;
		else return this.testItems[index];
	}
	
	/**
	 * Get a test item by his index
	 * @param testItemIndex Index of the test item to retrieve
	 * @return Item or null
	 */
	public Item getTestItemByIndex (int testItemIndex) {
		if (testItemIndex >= this.getNumberOfTestItems()) return null;
		else return this.testItems[testItemIndex];
	}

	/**
	 * Get an user by his code
	 * @param userCode Code of the user to retrieve
	 * @return User or null
	 */
	public User getUserByCode (int userCode) {
		int index = this.getUserIndex(userCode);
		if (index == -1) return null;
		else return this.users[index];
	}
	
	/**
	 * Get an user by his index
	 * @param userIndex Index of the user to retrieve
	 * @return User or null
	 */
	public User getUserByIndex (int userIndex) {
		if (userIndex <= this.getNumberOfUsers()) return null;
		else return this.users[userIndex];
	}

	/**
	 * Get a test user by his code
	 * @param userCode Code of the test user to retrieve
	 * @return TestUser or null
	 */
	public TestUser getTestUserByCode (int userCode) {
		int index = this.getTestUserIndex(userCode);
		if (index == -1) return null;
		else return this.testUsers[index];
	}
	
	/**
	 * Get a test user by his index
	 * @param testUserIndex Index of the test user to retrieve
	 * @return User or null
	 */
	public User getTestUserByIndex (int testUserIndex) {
		if (testUserIndex <= this.getNumberOfTestUsers()) return null;
		else return this.testUsers[testUserIndex];
	}
}
