package cf4j.process;

import cf4j.data.DataModel;

/**
 * <p>Class that manages the execution of processes. To use this class, you must have previously 
 * loaded a dataModel. This datamodel should be sent to the specific Partible algorithms</p>
 * 
 * <p>Its mains methods are:</p>
 * <ul>
 *      <li>get/setVerbose (...): indicates if the process will be verbose or not.</li>
 * 		<li>get/setThreads (...): indicates the number of threads to the Partible execution.</li>
 * 		<li>process (...): execute a specific Partible implementation.</li>
 * </ul>
 * 
 * @author Fernando Ortega
 */
public class Processor {

	/**
	 * Number of thread to be used
	 */
	private int threads;
	private boolean verbose;

	/**
	 * Creates a new instance. The number of executions sets is set based on
	 * the available processors.
	 */
	public Processor () {
		this(Runtime.getRuntime().availableProcessors() * 2, true);
	}

	/**
	 * Creates a new instance. The number of executions sets is set based on
	 * the available processors.
	 * @param verbose we should write some output feedback?
	 */
	public Processor (boolean verbose) {
		this(Runtime.getRuntime().availableProcessors() * 2, verbose);
	}

	/**
	 * Creates a new instance setting the number of executions threads
	 * @param threads number of threads to divide the processing.
	 */
	public Processor ( int threads) {
		this(threads,true);

	}

	/**
	 * Creates a new instance setting the number of executions threads
	 * @param threads number of threads to divide the processing.
	 * @param verbose we should write some output feedback?
	 */
	public Processor ( int threads, boolean verbose) {
		this.setThreads(threads);
		this.setVerbose(verbose);
	}

	/**
	 * Returns the number of thread used.
	 * @return Number of Threads
	 */
	public int getThreads () {
		return this.threads;
	}

	/**
	 * Set the number of thread to be used.
	 * @param threads Number of threads
	 */
	public void setThreads (int threads) {
		this.threads = threads;
	}

	/**
	 * Set if this processor should write some output.
	 * @param verbose we should write some output feedback?
	 */
	public void setVerbose (boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Returns if it's setted verbose mode or not.
	 * @return state of the verbose mode.
	 */
	public boolean getVerbose () {
		return this.verbose;
	}

	/**
	 * Execute a Partible implementation for users.
	 * @see PartibleThreads
	 * @param partible partible implementation of the datamodel focused on a specific runnable algorithm.
	 */
	public void process (PartibleThreads partible) {
		partible.runThreads(this.threads, verbose);
	}

}
