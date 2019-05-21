package cf4j.process;

import cf4j.data.DataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * <p><b>The final user must not use this class.</b></p>
 * 
 * <p>This class handles the threads. It assigns each thread between the 
 * available processor.</p> 
 *  
 * @author Fernando Ortega
 */
public class PartibleThread extends Thread {

	private Partible partible;
	private boolean verbose;

	private int threadIndex;
	private int indexesPerThread;

	public PartibleThread(Partible partible, int threadIndex, int indexesPerThread, boolean verbose){
	    this.partible = partible;
		this.threadIndex = threadIndex;
		this.setName("Thread-" + threadIndex); //Name of the thread.
        this.indexesPerThread = indexesPerThread;
		this.verbose = verbose;
		this.start();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		long time1 = (new Date()).getTime() / 1000;
		long time2 = 0;
		int iXt = this.indexesPerThread;
		
		// Last theard could have less users
		for (int index = (this.threadIndex) * iXt; (index < (this.threadIndex + 1) * iXt)
				&& (index < partible.getTotalIndexes()); index++) {
			if (this.threadIndex == 0 && this.verbose) {
				time2 = (new Date()).getTime() / 1000;
				if ((time2 - time1) > 10) {
					System.out.print("..." + ((index - this.threadIndex * iXt) * 100 / iXt) + "%");
					time1 = time2;
				}
			}

			partible.run(index);
		}
	}
}
