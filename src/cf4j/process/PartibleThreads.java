package cf4j.process;

import cf4j.data.DataModel;

import java.util.Date;

/**
 * <p><b>The final user must not use this class.</b></p>
 * 
 * <p>This class handles the threads. It assigns each thread between the 
 * available processor.</p> 
 *  
 * @author Fernando Ortega
 */
public abstract class PartibleThreads implements Runnable {

	protected DataModel dataModel;
	protected boolean verbose;

	private Thread thread;
	private int threadIndex;

	private int numThreads;
	private int indexesPerThread;

	public PartibleThreads (DataModel dataModel){
		this.dataModel = dataModel;
	}

	/**
	 * This method should return the max number of the processable elements (from the datamodel.
	 * It depends on the type of the defined processor. If it is focused on Users or Items.
	 */
	public abstract int getTotalIndexes ();

	/**
	 * Is executed once before execute the method 'run'. It can be used to initialize
	 * resources.
	 */
	public abstract void beforeRun ();

	/**
	 * Is executed once for each test element. It can be user, item, testUser or testItem indexes.
	 * The child class should indicate in the name if it's for users, items, testUsers or testItems.
	 * @param userOrItemIndex Index of the test element.
	 */
	public abstract void run (int userOrItemIndex);

	/**
	 * Is executed once after execute the method run. It can be used to close
	 * resources.
	 */
	public abstract void afterRun ();

	/* (non-Javadoc)
     */
	public synchronized void runThreads (int numThreads, boolean verbose) {
		if (verbose) System.out.println("\nProcessing... " + this.getClass().getName());
		
		if (this.getTotalIndexes() < 1)
			throw new RuntimeException("Test array can not be empty");
		
		if (numThreads <= 0)
			throw new RuntimeException("The number of threads must be one or more");

		this.numThreads = numThreads;

		if (numThreads == 1) {
			this.indexesPerThread = this.getTotalIndexes();
			this.beforeRun();
			for (int index = 0; (index < this.getTotalIndexes()); index++) {
				this.run(index);
			}
			this.afterRun();
			
		} else {
			// We compute number of indexes per thread
			if (numThreads > this.getTotalIndexes()) {
				this.numThreads = this.getTotalIndexes();
				this.indexesPerThread = 1;
			} else if (this.getTotalIndexes() % numThreads == 0) {
				this.indexesPerThread = this.getTotalIndexes() / numThreads;
			} else {
				this.indexesPerThread = this.getTotalIndexes() / numThreads + 1;
			}
				
			// Do some stuff...
			this.beforeRun();
			
			// Launch all threads
			int index;
			PartibleThreads [] pt = new PartibleThreads[numThreads];
			for (index = 0; index < this.numThreads; index++) {
				pt[index] = this.cloneInAThread(index);
			}

			// Wait until all threads end
			try {
				for (index = 0; index < this.numThreads; index++) {
					pt[index].getThread().join();
				}
			} catch (InterruptedException ie) {
				System.out.println("ERROR: " + ie);
			}
			
			// Do some stuff...
			this.afterRun();
		}

	}

	/**
	 * @return the thread
	 */
	protected Thread getThread() {
		return thread;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long t1 = (new Date()).getTime() / 1000, t2, t3 = 0;
		int iXt = this.indexesPerThread;
		
		// Last theard could have less users
		for (int index = (this.threadIndex) * iXt; (index < (this.threadIndex + 1) * iXt)
				&& (index < this.getTotalIndexes()); index++) {
			if (this.threadIndex == 0 && this.verbose) {
				t2 = (new Date()).getTime() / 1000;
				if ((t2 - t1) > 5) {
					System.out.print(".");
					t1 = t2;
					t3++;
				}
				if (t3 > 20) {
					System.out.println(((index - this.threadIndex * iXt) * 100 / iXt) + "%");
					t3 = 0;
				}
			}

			this.run(index);
		}
	}

	protected PartibleThreads cloneInAThread(int threadIndex) {
		try {
			PartibleThreads clone = (PartibleThreads) super.clone();

			clone.dataModel = this.dataModel;
			clone.verbose = this.verbose;
			clone.threadIndex = threadIndex;
			clone.thread = new Thread (this, String.valueOf(threadIndex));
			clone.thread.start();

			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Error while we are creating a new thread of the partible data model", e);
		}
	}

}
