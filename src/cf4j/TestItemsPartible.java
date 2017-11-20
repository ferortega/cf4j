package cf4j;

/**
 * <p>This interface must be implemented if you want to process a Partible over
 * the test items set.</p>
 * @see Partible
 * @author Fernando Ortega
 */
public interface TestItemsPartible extends Partible {

	/**
	 * Is executed once for each test item.
	 * @param testItemIndex Index of the test item.
	 */
	public void run (int testItemIndex);
}
