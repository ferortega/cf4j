package cf4j;

import java.util.Date;

/**
 * <p><b>The final user must not use this class.</b></p>
 * 
 * <p>This class handles the threads. It assigns each thread between the 
 * available processor.</p> 
 *  
 * @author Fernando Ortega
 */
public class PartibleThreads implements Runnable {

	private static Partible render;
	private static int numThreads;
	private static int numIndexes;
	private static int indexesPerThread;

	private Thread t;
	private int threadIndex;
	private boolean verbose;

	/* (non-Javadoc)
     */
	public static synchronized void runThreads (Partible partible, int numThreads, int numIndexes, boolean verbose) {
		if (verbose) System.out.println("\nProcessing... " + partible.getClass().getName());
		
		if (numIndexes < 1)
			throw new RuntimeException("Test array can not be empty");
		
		if (numThreads <= 0)
			throw new RuntimeException("The number of threads must be one or more");
		
		PartibleThreads.render = partible;
		PartibleThreads.numThreads = numThreads;
		PartibleThreads.numIndexes = numIndexes;

		if (numThreads == 1) {
			PartibleThreads.indexesPerThread = PartibleThreads.numIndexes;
			partible.beforeRun();
			for (int index = 0; (index < numIndexes); index++) {
				partible.run(index);
			}
			partible.afterRun();
			
		} else {
			// We compute number of indexes per thread
			if (numThreads > numIndexes) {
				PartibleThreads.numThreads = numIndexes;
				PartibleThreads.indexesPerThread = 1;
			} else if (numIndexes % numThreads == 0) {
				PartibleThreads.indexesPerThread = numIndexes / numThreads;
			} else {
				PartibleThreads.indexesPerThread = numIndexes / numThreads + 1;
			}
				
			// Do some stuff...
			partible.beforeRun();
			
			// Launch all threads
			int index;
			PartibleThreads [] pt = new PartibleThreads[numThreads];
			for (index = 0; index < PartibleThreads.numThreads; index++) {
				pt[index] = new PartibleThreads(index, verbose);
			}

			// Wait until all threads end
			try {
				for (index = 0; index < PartibleThreads.numThreads; index++) {
					pt[index].getT().join();
				}
			} catch (InterruptedException ie) {
				System.out.println("ERROR: " + ie);
			}
			
			// Do some stuff...
			partible.afterRun();
		}

	}

	/**
	 * @param threadIndex
	 */
	private PartibleThreads (int threadIndex, boolean verbose) {
		this.verbose = verbose;
		this.threadIndex = threadIndex;
		this.t = new Thread (this, String.valueOf(threadIndex));
		this.t.start();
	}

	/**
	 * @return the thread
	 */
	public Thread getT() {
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long t1 = (new Date()).getTime() / 1000, t2, t3 = 0;
		int iXt = PartibleThreads.indexesPerThread;
		
		// Last theard could have less users
		for (int index = (this.threadIndex) * iXt; (index < (this.threadIndex + 1) * iXt)
				&& (index < PartibleThreads.numIndexes); index++) {
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

			render.run(index);
		}
	}
}
