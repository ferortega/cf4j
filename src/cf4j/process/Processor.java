package cf4j.process;

import java.util.Date;

/**
 * <p>Class that manages the execution of processes. To use this class, you must have previously 
 * loaded a dataModel. This datamodel should be sent to the specific Partible algorithms</p>
 * 
 * <p>Its mains methods are:</p>
 * <ul>
 * 		<li>get/setThreads (...): indicates the number of threads to the Partible execution.</li>
 * 		<li>parallelExecute (...): execute a specific Partible implementation.</li>
 * </ul>
 * 
 * @author Fernando Ortega, Jesús Mayor
 */
public class Processor {

	/**
	 * Class instance (Singleton pattern)
	 */
	private static Processor instance = null;

	/**
	 * Number of thread to be used
	 */
	private int numThreads;

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
	 * Creates a new instance. The number of executions sets is set based on
	 * the available processors.
	 */
	private Processor () {
		this.setNumThreads(1);
	}

	/**
	 * Returns the number of thread used.
	 * @return Number of Threads
	 */
	public int getNumThreads () {
		return this.numThreads;
	}

	/**
	 * Set the number of thread to be used.
	 * @param threads Number of threads
	 */
	public void setNumThreads (int threads) {
		this.numThreads = threads;
	}

	/**
	 * Execute a Partible implementation for users.
	 * @see Partible
	 * @param partible partible implementation of the datamodel focused on a specific runnable algorithm.
	 */
	public synchronized void parallelExec (Partible partible) {
		this.parallelExec(partible, true);
	}

	/**
	 * Execute a Partible implementation for users.
	 * @see Partible
	 * @param partible partible implementation of the datamodel focused on a specific runnable algorithm.
	 */
	public synchronized void parallelExec (Partible partible, boolean verbose) {
		if (verbose) System.out.println("\nProcessing... " + partible.getClass().getName());

		// Error control
		if (partible.getTotalIndexes() < 1)
			throw new RuntimeException("Test array can not be empty");


		if (this.numThreads < 1)
			throw new RuntimeException("The number of threads must be one or more");

		if (this.numThreads == 1){
			monoExec(partible,verbose);
			return;
		}

		// We compute number of indexes per thread
		int indexesPerThread = partible.getTotalIndexes() / numThreads;
		if (partible.getTotalIndexes() % numThreads != 0)
			indexesPerThread++;

		// Do some stuff...
		partible.beforeRun();

		// Launch all threads
		int index;
		PartibleThread[] pt = new PartibleThread[numThreads];
		//Run is processed in threads.
		for (index = 0; index < this.numThreads && index < partible.getTotalIndexes(); index++) {
			pt[index] = new PartibleThread(partible,index,indexesPerThread,verbose);
		}

		// Wait until all threads end
		try {
			for (index = 0; index < this.numThreads && index < partible.getTotalIndexes(); index++) {
				pt[index].join();
			}
		} catch (InterruptedException ie) {
			System.out.println("ERROR: " + ie);
		}

		// Do some stuff...
		partible.afterRun();
	}

	/**
	 * Execute a inside the main thread, without not balance the processing.
	 * @see Partible
	 * @param partible partible implementation of the datamodel focused on a specific runnable algorithm.
	 */
	private void monoExec (Partible partible, boolean verbose) {

		// Do some stuff...
		partible.beforeRun();

		// Run for each index, displaying load feedback.
		long time1 = (new Date()).getTime() / 1000;
		long time2 = 0;
		int index;
		for (index = 0; index < partible.getTotalIndexes(); index++) {

			if (verbose) {
				time2 = (new Date()).getTime() / 1000;
				if ((time2 - time1) > 10) {
					System.out.print("..." + ((index / partible.getTotalIndexes())  * 100) + "%");
					time1 = time2;
				}
			}

			partible.run(index);
		}

		// Do some stuff...
		partible.afterRun();
	}
}
