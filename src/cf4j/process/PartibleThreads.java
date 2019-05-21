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
public abstract class PartibleThreads extends Thread implements Cloneable {

	protected DataModel dataModel;
	protected boolean verbose;

	private int threadIndex;
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

	/**
	 * This methods is a substitute of the main Thread start function, if you want to specify what indexes process.
	 * @param threadIndex Thread number
	 * @param indexesPerThread Number of indexes to proces per thread.
	 */
	public synchronized void startPartible(int threadIndex,int indexesPerThread){
		this.startPartible(threadIndex,indexesPerThread, true);
	}

	/**
	 * This methods is a substitute of the main Thread start function, if you want to specify what indexes process.
	 * @param threadIndex Thread number
	 * @param indexesPerThread Number of indexes to proces per thread.
	 * @param verbose Should I write some output?
	 */
	public synchronized void startPartible(int threadIndex,int indexesPerThread, boolean verbose){
		this.indexesPerThread = indexesPerThread;
		this.threadIndex = threadIndex;
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
		long time3 = 0;
		int iXt = this.indexesPerThread;
		
		// Last theard could have less users
		for (int index = (this.threadIndex) * iXt; (index < (this.threadIndex + 1) * iXt)
				&& (index < this.getTotalIndexes()); index++) {
			if (this.threadIndex == 0 && this.verbose) {
				time2 = (new Date()).getTime() / 1000;
				if ((time2 - time1) > 5) {
					System.out.print(".");
					time1 = time2;
					time3++;
				}
				if (time3 > 20) {
					System.out.println(((index - this.threadIndex * iXt) * 100 / iXt) + "%");
					time3 = 0;
				}
			}

			this.run(index);
		}
	}

	@Override
	public Object clone() {
		try {
			PartibleThreads clone = this.getClass().getDeclaredConstructor(DataModel.class).newInstance(this.dataModel); //Reflection
			clone.verbose = this.verbose;
			clone.indexesPerThread = this.indexesPerThread;
			clone.threadIndex = this.threadIndex;
			return clone;
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Error while we are creating a new thread of the partible data model (IlegalAccesException)", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("Error while we are creating a new thread of the partible data model (InstantiationException)",e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Error while we are creating a new thread of the partible data model (NoSuchMethodException)",e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Error while we are creating a new thread of the partible data model (InvocationTargetException)", e);
		}
	}
}
