package cf4j;

/**
 * <p>This interface has three methods:</p>
 * <ol>
 * 		<li>beforeRun: is executed once before execute the method 'run'. It can be
 * 		used to initialize resources.</li>
 * 		<li>run: is executed once for each test element. Depending on how the 
 * 		class is called from Processor, test elements will be users or items.</li>
 * 		<li>afterRun: is executed once after execute the method run. It can be
 * 		used to close resources.</li>
 * </ol>
 * 
 * @see TestUsersPartible
 * @see TestItemsPartible
 * 
 * @author Fernando Ortega
 */
public abstract interface Partible {

	/**
	 * Is executed once before execute the method 'run'. It can be used to initialize 
	 * resources.
	 */
	public void beforeRun ();

	/**
	 * Is executed once for each test element.
	 * @param index Index of the test element.
	 */
	public abstract void run (int index);

	/**
	 * Is executed once after execute the method run. It can be used to close 
	 * resources.
	 */
	public void afterRun ();
}
