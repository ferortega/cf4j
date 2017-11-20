package cf4j;

/**
 * <p>Class that manages the execution of processes. To use this class, you must have previously 
 * loaded kernel.</p>
 * 
 * <p>This class can not be instantiated. It implements the singleton pattern, so, when we want to use
 * it, we must use the getInstance() method.</p>
 * 
 * <p>Its mains methods are:</p>
 * <ul>
 * 		<li>usersProcess (...): execute a UserPartible implementation using users like test elements.</li>
 * 		<li>itemsProcess (...): execute a ItemPartible implementation using items like test elements.</li>
 * </ul>
 * 
 * @author Fernando Ortega
 */
public class Processor {
	
	/**
	 * Class instance (Singleton pattern)
	 */
	private static Processor instance = null;

	/**
	 * Number of thread to be used
	 */
	private int threads;

	/**
	 * Gets the single instance of the class.
	 * @return Single instance
	 */
	public static Processor getInstance() {
		if (instance == null) {
			instance = new Processor();
		}
		return instance;
	}
	
	/**
	 * Destroy the single instance of the class.
	 */
	public static void destroyInstance () {
		instance = null;
		System.gc();
	}
	
	/**
	 * Creates a new instance. The number of executions sets is set based on
	 * the available processors.
	 */
	private Processor () {
		this(Runtime.getRuntime().availableProcessors() * 2);
	}
	
	/**
	 * Creates a new instance setting the number of executions threads
	 */
	private Processor (int threads) {
		this.threads = threads;
	}

	/**
	 * Returns the number of thread used.
	 * @return Number of Threads
	 */
	public int getThreads () {
		return threads;
	}
	
	/**
	 * Set the number of thread to be used.
	 * @param threads Number of threads
	 */
	public void setThreads (int threads) {
		this.threads = threads;
	}
	
	/**
	 * Execute a Partible implementation for users.
	 * @see UsersPartible
	 * @param usersPartible usersPartible implementation instance.
	 */
	public void usersProcess (UsersPartible usersPartible) {	
		this.usersProcess(usersPartible, true);	
	}
	
	/**
	 * Execute a Partible implementation for users.
	 * @see UsersPartible
	 * @param usersPartible usersPartible implementation instance.
	 * @param verbose Print execution info
	 */
	public void usersProcess (UsersPartible usersPartible, boolean verbose) {	
		int numUsers = Kernel.getInstance().getNumberOfUsers();
		PartibleThreads.runThreads(usersPartible, this.threads, numUsers, verbose);	
	}
	
	/**
	 * Execute a Partible implementation for test users.
	 * @see TestUsersPartible
	 * @param testUsersPartible TestUsersPartible implementation instance.
	 */
	public void testUsersProcess (TestUsersPartible testUsersPartible) {	
		this.testUsersProcess(testUsersPartible, true);
	}
	
	/**
	 * Execute a Partible implementation for test users.
	 * @see TestUsersPartible
	 * @param testUsersPartible TestUsersPartible implementation instance.
	 * @param verbose Print execution info
	 */
	public void testUsersProcess (TestUsersPartible testUsersPartible, boolean verbose) {	
		int numTestUsers = Kernel.getInstance().getNumberOfTestUsers();
		PartibleThreads.runThreads(testUsersPartible, this.threads, numTestUsers, verbose);	
	}
	
	/**
	 * Execute a Partible implementation for items.
	 * @see ItemsPartible
	 * @param itemsPartible ItemsPartible implementation instance.
	 */
	public void itemsProcess (ItemsPartible itemsPartible) {	
		this.itemsProcess(itemsPartible, true);
	}
	
	/**
	 * Execute a Partible implementation for items.
	 * @see ItemsPartible
	 * @param itemsPartible ItemsPartible implementation instance.
	 * @param verbose Print execution info
	 */
	public void itemsProcess (ItemsPartible itemsPartible, boolean verbose) {	
		int numItems = Kernel.getInstance().getNumberOfItems();
		PartibleThreads.runThreads(itemsPartible, this.threads, numItems, verbose);	
	}
	
	/**
	 * Execute a Partible implementation for test items.
	 * @see TestItemsPartible
	 * @param testItemsPartible TestItemsPartible implementation instance.
	 */
	public void testItemsProcess (TestItemsPartible testItemsPartible) {	
		this.testItemsProcess(testItemsPartible, true);
	}
	
	/**
	 * Execute a Partible implementation for test items.
	 * @see TestItemsPartible
	 * @param testItemsPartible TestItemsPartible implementation instance.
	 * @param verbose Print execution info
	 */
	public void testItemsProcess (TestItemsPartible testItemsPartible, boolean verbose) {	
		int numTestItems = Kernel.getInstance().getNumberOfTestItems();
		PartibleThreads.runThreads(testItemsPartible, this.threads, numTestItems, verbose);	
	}
}
