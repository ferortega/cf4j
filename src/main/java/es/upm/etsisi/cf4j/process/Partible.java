package es.upm.etsisi.cf4j.process;


/**
 * <p><b>The final user must not use this class.</b></p>
 * 
 * <p>This class handles the threads. It assigns each thread between the 
 * available processor.</p> 
 *  
 * @author Fernando Ortega
 */
public interface Partible<T> {

	/**
	 * Is executed once before execute the method 'run'. It can be used to initialize
	 * resources.
	 */
	void beforeRun();

	/**
	 * Is executed once for each test element. It can be user, item, testUser or testItem indexes.
	 * The child class should indicate in the name if it's for users, items, testUsers or testItems.
	 * @param index Index of the test element.
	 */
	void run(T object);

	/**
	 * Is executed once after execute the method run. It can be used to close
	 * resources.
	 */
	void afterRun();

}
