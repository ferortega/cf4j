package cf4j.data;

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
import java.util.Set;
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
public class DataModel implements Serializable {

	private static final long serialVersionUID = 20171018L;

	private static String DEFAULT_SPARATOR = ";";

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
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * <p>The dataset is loaded without test items and test users</p>
	 * @param filename File with the ratings
	 */
	public DataModel (String filename) {
		this(filename, DEFAULT_SPARATOR);
	}

	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode SEPARATOR itemCode SEPARATOR rating</p>
	 * <p>The dataset is loaded without test items and test users</p>
	 * @param filename File with the ratings
	 * @param separator Separator char between file fields
	 */
	public DataModel (String filename, String separator) {
		this(filename, 0.0, 0.0, separator);
	}

	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * @param filename File with the ratings
	 * @param testUsersPercent Percentage of users that will be of test
	 * @param testItemsPercent Percentage of items that will be of test
	 */
	public DataModel (String filename, double testUsersPercent, double testItemsPercent) {
		this(filename, testUsersPercent, testItemsPercent, DEFAULT_SPARATOR);
	}
	
	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * @param filename File with the ratings
	 * @param testUsersPercent Percentage of users that will be of test
	 * @param testItemsPercent Percentage of items that will be of test
	 * @param separator Separator char between file fields
	 */
	public DataModel (String filename, double testUsersPercent, double testItemsPercent, String separator) {
		this(filename, DataPartitioners.random(testUsersPercent), DataPartitioners.random(testItemsPercent), separator);
	}

	/**
	 * Kernel constructor.
	 */
	public DataModel (String filename, BiFunction <Integer, Map <Integer, Double>, Boolean> testUserFilter,
					   BiFunction <Integer, Map <Integer, Double>, Boolean> testItemFilter, String separator) {
		this.open(filename, testUserFilter, testItemFilter, separator);
	}
	
	/**
	 * <p>Generates a kernel form a text file. The lines of the file must have the following format:</p>
	 * <p>userCode::itemCode::rating</p>
	 * @param filename File with the ratings
	 * @param testUserFilter Lambda function that receives the user code and the user ratings and return true if the user is a test user and false otherwise
	 * @param testItemFilter Lambda function that receives the item code and the item ratings and return true if the item is a test item and false otherwise
	 * @param separator Separator char between file fields
	 * @see DataPartitioners
	 */
	private void open (String filename, BiFunction <Integer, Map <Integer, Double>, Boolean> testUserFilter,
			BiFunction <Integer, Map <Integer, Double>, Boolean> testItemFilter, String separator) {

		System.out.println("\nLoading dataset...");

		this.maxItemCode = Integer.MIN_VALUE;
		this.minItemCode = Integer.MAX_VALUE;
		this.maxUserCode = Integer.MIN_VALUE;
		this.minUserCode = Integer.MAX_VALUE;
		this.maxRating = Double.MIN_VALUE;
		this.minRating = Double.MAX_VALUE;

		TreeMap <Integer, TreeMap <Integer, Double>> userRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();
		TreeMap <Integer, TreeMap <Integer, Double>> itemRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();

		try {

			// Dataset reader
			BufferedReader dataset = new BufferedReader (new FileReader (new File (filename)));

			String line = ""; int numLines = 0;
			while ((line = dataset.readLine()) != null) {

				numLines++;
				if (numLines % 1000000  == 0) System.out.print(".");
				if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

				// Parse line
				String [] s = line.split(separator);
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
				if (!userRatings.containsKey(userCode)) userRatings.put(userCode, new TreeMap <Integer, Double> ());
				userRatings.get(userCode).put(itemCode, rating);
				
				if (!itemRatings.containsKey(itemCode)) itemRatings.put(itemCode, new TreeMap <Integer, Double> ());
				itemRatings.get(itemCode).put(userCode, rating);
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
		for (int userCode : userRatings.keySet()) {
			Map <Integer, Double> ratings = userRatings.get(userCode);
			if (testUserFilter.apply(userCode, ratings)) testUsersSet.add(userCode);
		}

		// Setting test items
		TreeSet <Integer> testItemsSet = new TreeSet <Integer> ();
		for (int itemCode : itemRatings.keySet()) {
			Map <Integer, Double> ratings = itemRatings.get(itemCode);
			if (testItemFilter.apply(itemCode, ratings)) testItemsSet.add(itemCode);
		}
		
		// Generate users test ratings
		TreeMap <Integer, TreeMap <Integer, Double>> userTestRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();
		for (int userCode : testUsersSet) {
			TreeMap <Integer, Double> itemsRated = userRatings.get(userCode);
			for (int itemCode : itemsRated.keySet()) {
				
				// Is test rating?
				if (testItemsSet.contains(itemCode)) {
					if (!userTestRatings.containsKey(userCode)) {
						userTestRatings.put(userCode, new TreeMap <Integer, Double> ());
					}
					
					double rating = itemsRated.get(itemCode);
					userTestRatings.get(userCode).put(itemCode, rating);
				}
			}
			
			// Remove test ratings
			if (userTestRatings.containsKey(userCode)) {
				for (int itemCode : userTestRatings.get(userCode).keySet()) {
					userRatings.get(userCode).remove(itemCode);
				}
			}		
		}
		
		// Generate items test ratings
		TreeMap <Integer, TreeMap <Integer, Double>> itemTestRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();
		for (int itemCode : testItemsSet) {
			TreeMap <Integer, Double> usersRated = itemRatings.get(itemCode);
			for (int userCode : usersRated.keySet()) {
				
				// Is test rating?
				if (testUsersSet.contains(userCode)) {
					if (!itemTestRatings.containsKey(itemCode)) {
						itemTestRatings.put(itemCode, new TreeMap <Integer, Double> ());
					}
					
					double rating = usersRated.get(userCode);
					itemTestRatings.get(itemCode).put(userCode, rating);
				}
			}
			
			// Remove test ratings
			if (itemTestRatings.containsKey(itemCode)) {
				for (int userCode : itemTestRatings.get(itemCode).keySet()) {
					itemRatings.get(itemCode).remove(userCode);
				}
			}		
		}
		
		// Generate arrays

		System.out.println("\nGenerating users sets...");
		this.createUsers(userRatings, userTestRatings);
		
		System.out.println("\nGenerating items sets...");
		this.createItems(itemRatings, itemTestRatings);

		System.out.println("\n'" + filename + "' dataset loaded succesfully");
	}
	
	/**
	 * <p>Generates a kernel form a training and test text file. The lines of the file must have the following format:</p>
	 * <p>userCode;itemCode;rating</p>
	 * @param trainingFile File with the training ratings
	 * @param testFile File with the test ratings
	 * @param separator Separator char between file fields
	 */
	public void open (String trainingFile, String testFile, String separator) {

		System.out.println("\nLoading training dataset...");

		this.maxItemCode = Integer.MIN_VALUE;
		this.minItemCode = Integer.MAX_VALUE;
		this.maxUserCode = Integer.MIN_VALUE;
		this.minUserCode = Integer.MAX_VALUE;
		this.maxRating = Double.MIN_VALUE;
		this.minRating = Double.MAX_VALUE;

		TreeMap <Integer, TreeMap <Integer, Double>> userRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();
		TreeMap <Integer, TreeMap <Integer, Double>> itemRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();

		try {

			// Dataset reader
			BufferedReader dataset = new BufferedReader (new FileReader (new File (trainingFile)));

			String line = ""; int numLines = 0;
			while ((line = dataset.readLine()) != null) {

				numLines++;
				if (numLines % 1000000  == 0) System.out.print(".");
				if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

				// Parse line
				String [] s = line.split(separator);
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
				if (!userRatings.containsKey(userCode)) userRatings.put(userCode, new TreeMap <Integer, Double> ());
				userRatings.get(userCode).put(itemCode, rating);
				
				if (!itemRatings.containsKey(itemCode)) itemRatings.put(itemCode, new TreeMap <Integer, Double> ());
				itemRatings.get(itemCode).put(userCode, rating);
			}

			dataset.close();

		} catch (Exception e) {
			System.out.println("An error has occurred while loading database");
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("\nLoading test dataset...");

		TreeMap <Integer, TreeMap <Integer, Double>> userTestRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();
		TreeMap <Integer, TreeMap <Integer, Double>> itemTestRatings = new TreeMap <Integer, TreeMap <Integer, Double>> ();

		try {

			// Dataset reader
			BufferedReader dataset = new BufferedReader (new FileReader (new File (testFile)));

			String line = ""; int numLines = 0;
			while ((line = dataset.readLine()) != null) {

				numLines++;
				if (numLines % 1000000  == 0) System.out.print(".");
				if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

				// Parse line
				String [] s = line.split(separator);
				int userCode = Integer.parseInt(s[0]);
				int itemCode = Integer.parseInt(s[1]);
				double rating = Double.parseDouble(s[2]);

				// Store rating
				if (!userTestRatings.containsKey(userCode)) userTestRatings.put(userCode, new TreeMap <Integer, Double> ());
				userTestRatings.get(userCode).put(itemCode, rating);
				
				if (!itemTestRatings.containsKey(itemCode)) itemTestRatings.put(itemCode, new TreeMap <Integer, Double> ());
				itemTestRatings.get(itemCode).put(userCode, rating);
			}

			dataset.close();

		} catch (Exception e) {
			System.out.println("An error has occurred while loading database");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Generate arrays

		System.out.println("\nGenerating users sets...");
		this.createUsers(userRatings, userTestRatings);
		
		System.out.println("\nGenerating items sets...");
		this.createItems(itemRatings, itemTestRatings);

		System.out.println("\n'" + trainingFile + " & " + testFile + "' datasets loaded succesfully");
	}
	
	/**
	 * Create users arrays
	 * @param userRatings Map containing user training ratings
	 * @param userTestRatings Map containing user test ratings
	 */
	private void createUsers (TreeMap <Integer, TreeMap <Integer, Double>> userRatings, 
			TreeMap <Integer, TreeMap <Integer, Double>> userTestRatings) {
		
		// Dateset average
		this.ratingAverage = 0;
		int averageCount = 0;		
		
		// Get all user codes
		Set <Integer> userCodes = new TreeSet <Integer> (); 
		userCodes.addAll(userRatings.keySet());
		userCodes.addAll(userTestRatings.keySet());
		
		// Create users array
		this.users = new User [userCodes.size()];
		int userIndex = 0;

		// Create test users array
		this.testUsers = new TestUser [userTestRatings.size()];
		int testUserIndex = 0;

		// Create all users
		for (int userCode : userCodes) {

			User user;

			// User has test ratings
			if (userTestRatings.containsKey(userCode)) {

				Set <Integer> training = userRatings.get(userCode).keySet();
				Set <Integer> test = userTestRatings.get(userCode).keySet();

				int [] itemsArray = new int [training.size()];
				double [] ratingsArray = new double [training.size()];
				int i = 0; 
				
				for (int itemCode : training) {
					itemsArray[i] = itemCode;
					ratingsArray[i] = userRatings.get(userCode).get(itemCode);
					i++;
				}

				int [] testItemsArray = new int [test.size()];
				double [] testRatingsArray = new double [test.size()];
				i = 0; 
				
				for (int itemCode : test) {
					testItemsArray[i] = itemCode;
					testRatingsArray[i] = userTestRatings.get(userCode).get(itemCode);
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

			// User does not has test ratings
			} else {
				
				Set <Integer> training = userRatings.get(userCode).keySet();

				int [] itemsArray = new int [training.size()];
				double [] ratingsArray = new double [training.size()];
				
				int i = 0; 
				for (int itemCode : training) {
					itemsArray[i] = itemCode;
					ratingsArray[i] = userRatings.get(userCode).get(itemCode);
					i++;
				}

				// New user instance
				user = new User(userCode, userIndex, itemsArray, ratingsArray);
			}

			// Add user to training users
			this.users[userIndex] = user;
			userIndex++;
			
			// Compute rating average
			if (user.getNumberOfRatings() > 0) {
				this.ratingAverage += user.getNumberOfRatings() * user.getRatingAverage();
				averageCount += user.getNumberOfRatings();
			}
		}
		
		// Compute rating average
		this.ratingAverage /= averageCount;
		
		// Remove gaps from testUser array
		TestUser [] testUsersTemp = new TestUser [testUserIndex];
		for (int i = 0; i < testUserIndex; i++) {
			testUsersTemp[i] = this.testUsers[i];
		}
		this.testUsers = testUsersTemp;
	}
	
	/**
	 * Create items arrays
	 * @param itemRatings Map containing item training ratings
	 * @param itemTestRatings Map containing item test ratings
	 */
	private void createItems (TreeMap <Integer, TreeMap <Integer, Double>> itemRatings, 
			TreeMap <Integer, TreeMap <Integer, Double>> itemTestRatings) {
		
		// Get all item codes
		Set <Integer> itemCodes = new TreeSet <Integer> (); 
		itemCodes.addAll(itemRatings.keySet());
		itemCodes.addAll(itemTestRatings.keySet());
		
		// Create items array
		this.items = new Item [itemCodes.size()];
		int itemIndex = 0;

		// Create test items array
		this.testItems = new TestItem [itemTestRatings.size()];
		int testItemIndex = 0;

		// Create all items
		for (int itemCode : itemCodes) {

			Item item;

			// Item has test ratings
			if (itemTestRatings.containsKey(itemCode)) {
				
				Set <Integer> training = itemRatings.get(itemCode).keySet();
				Set <Integer> test = itemTestRatings.get(itemCode).keySet();

				int [] usersArray = new int [training.size()];
				double [] ratingsArray = new double [training.size()];
				int i = 0; 
				
				for (int userCode : training) {
					usersArray[i] = userCode;
					ratingsArray[i] = itemRatings.get(itemCode).get(userCode);
					i++;
				}

				int [] testUsersArray = new int [test.size()];
				double [] testRatingsArray = new double [test.size()];
				i = 0; 
				
				for (int userCode : test) {
					testUsersArray[i] = userCode;
					testRatingsArray[i] = itemTestRatings.get(itemCode).get(userCode);
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
				
				Set <Integer> training = itemRatings.get(itemCode).keySet();

				int [] usersArrays = new int [training.size()];
				double [] ratingsArrays = new double [training.size()];
				
				int i = 0; 
				for (int userCode : training) {
					usersArrays[i] = userCode;
					ratingsArrays[i] = itemRatings.get(itemCode).get(userCode);
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
	public boolean write (String filename) {
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
	public static DataModel read (String filename) {
		System.out.println("\nLoading DataModel...");
		DataModel dm = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
			dm = (DataModel) ois.readObject();
			System.out.println("\nDataModel loaded successfully");
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error has occurred while loading DataModel");
		}
		return dm;
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
	public int getItemIndex (String itemCode) {
		return this.getIndex(this.items, itemCode);
	}

	/**
	 * Get the index of a test item at the test items array
	 * @param itemCode Test item code
	 * @return Index if the item exists or -1 if not
	 */
	public int getTestItemIndex (String itemCode) {
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
	public String getInfo () {
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
		try {
			return this.items[itemIndex];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
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
	 * @return TestItem or null
	 */
	public TestItem getTestItemByIndex (int testItemIndex) {
		try {
			return this.testItems[testItemIndex];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
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
		try {
			return this.getUsers()[userIndex];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
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
	 * @return TestUser or null
	 */
	public TestUser getTestUserByIndex (int testUserIndex) {
		try {
			return this.testUsers[testUserIndex];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}


	public void addRating (String userCode, String itemCode, double rating) {
		int userIndex = this.getUserIndex(userCode);
		int itemIndex = this.getItemIndex(itemCode);
		this.addRating(userIndex, itemIndex, rating);
	}

	public void addRating (int userIndex, int itemIndex, double rating) {
		if (rating > this.maxRating) this.maxRating = rating;
		if (rating < this.minRating) this.minRating = rating;

		User user = this.getUserByIndex(userIndex);
		Item item = this.getItemByIndex(itemIndex);

		user.addRating(item.getItemCode(), rating);
		item.addRating(user.getUserCode(), rating);
	}

	public void addTestRating (String userCode, String itemCode, double rating) {
		int testUserIndex = this.getTestUserIndex(userCode);
		int testItemIndex = this.getTestItemIndex(itemCode);
		this.addTestRating(testUserIndex, testItemIndex, rating);
	}

	public void addTestRating (int testUserIndex, int testItemIndex, double rating) {
		if (rating > this.maxRating) this.maxRating = rating;
		if (rating < this.minRating) this.minRating = rating;

		TestUser user = this.getTestUserByIndex(testUserIndex);
		TestItem item = this.getTestItemByIndex(testItemIndex);

		user.addTestRating(item.getItemCode(), rating);
		item.addTestRating(user.getUserCode(), rating);
	}
}
