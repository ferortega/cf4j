package cf4j.process;

import cf4j.data.DataModel;

import java.lang.reflect.InvocationTargetException;

/**
 * <p><b>The final user must not use this class.</b></p>
 * 
 * <p>This class handles the threads. It assigns each thread between the 
 * available processor.</p> 
 *  
 * @author Fernando Ortega
 */
public abstract class Partible {

	protected DataModel dataModel;

	public Partible(DataModel dataModel){
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
	 * @param index Index of the test element.
	 */
	public abstract void run (int index);

	/**
	 * Is executed once after execute the method run. It can be used to close
	 * resources.
	 */
	public abstract void afterRun ();

}
