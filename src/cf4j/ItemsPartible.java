package cf4j;

/**
 * <p>This interface must be implemented if you want to process a Partible over
 * the items set.</p>
 * @see Partible
 * @author Fernando Ortega
 */
public interface ItemsPartible extends Partible {

	/**
	 * Is executed once for each item.
	 * @param itemIndex Index of the item.
	 */
	public void run (int itemIndex);
}
