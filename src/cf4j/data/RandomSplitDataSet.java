package cf4j.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class RandomSplitDataSet implements DataSet
{
    protected static final String DEFAULT_SPARATOR = ";";

    /**
     * Data storing variables
     */
    protected ArrayList<DataSetEntry> ratings;
    protected ArrayList<DataSetEntry> testRatings;

    /**
     * <p>Generates a DataSet form a text file, filling the dataModel attribute.</p>
     * <p> The lines of the file must have the format: userCode;itemCode;rating</p>
     * <p>The dataset is loaded without test items and test users</p>
     * @param fileName File with the ratings
     */
    public RandomSplitDataSet (String fileName) {
        this(fileName, 0.0 , 0.0, DEFAULT_SPARATOR);
    }

    /**
     * <p>Generates a DataSet form a text file, filling the dataModel attribute. </p>
     * <p>The lines of the file must have the format: userCode SEPARATOR item SEPARATOR Coderating</p>
     * @param fileName File with the ratings
     * @param testUsersPercent Percentage of users that will be of test
     * @param testItemsPercent Percentage of items that will be of test
     */
    public RandomSplitDataSet (String fileName, double testUsersPercent, double testItemsPercent) {
        this(fileName, testUsersPercent, testItemsPercent, DEFAULT_SPARATOR);
    }

    /**
     * <p>Generates a DataSet form a text file, filling the dataModel attribute.</p>
     * <p> The lines of the file must have the format: userCode SEPARATOR itemCode SEPARATOR rating</p>
     * <p>The dataset is loaded without test items and test users</p>
     * @param fileName File with the ratings
     * @param separator Separator char between file fields
     */
    public RandomSplitDataSet (String fileName, String separator) {
        this(fileName, 0.0 , 0.0, separator);
    }

    /**
     * <p>Generates a DataSet form a text file, filling the dataModel attribute. </p>
     * <p>The lines of the file must have the format: userCode;itemCode;rating</p>
     * @param fileName File with the ratings
     * @param testUsersPercent Percentage of users that will be of test
     * @param testItemsPercent Percentage of items that will be of test
     * @param separator Separator char between file fields
     */
    public RandomSplitDataSet (String fileName, double testUsersPercent, double testItemsPercent, String separator) {

        ratings = new ArrayList<DataSetEntry>();
        testRatings = new ArrayList<DataSetEntry>();

        System.out.println("\nLoading dataset...");

        try{
            // Dataset reader
            BufferedReader datasetFile = new BufferedReader (new FileReader(new File(fileName)));

            // Test selectors
            TreeMap<String, Boolean> testUsersFiltered = new TreeMap<String, Boolean> ();
            TreeMap<String, Boolean> testItemsFiltered = new TreeMap<String, Boolean> ();

            String line = ""; int numLines = 0;
            while ((line = datasetFile.readLine()) != null) {

                //Loading feedback
                numLines++;
                if (numLines % 1000000  == 0) System.out.print(".");
                if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

                // Parse line
                String [] s = line.split(separator);
                String userCode = s[0];
                String itemCode = s[1];
                double rating = Double.parseDouble(s[2]);

                //Filtering entries.
                if (!testUsersFiltered.containsKey(userCode))
                    testUsersFiltered.put(userCode, Math.random() <= testUsersPercent);
                if (!testItemsFiltered.containsKey(itemCode))
                    testItemsFiltered.put(itemCode, Math.random() <= testItemsPercent);

                // Store rating
                if (testUsersFiltered.get(userCode) == true && testItemsFiltered.get(itemCode) == true)
                    testRatings.add(new DataSetEntry(userCode, itemCode, rating));
                else
                    ratings.add(new DataSetEntry(userCode, itemCode, rating));
            }

            datasetFile.close();
        }catch (Exception e) {
            System.out.println("An error has occurred while loading database");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Implementation of the interface.
     * @return An interator with the entries. (no test entries)
     */
    public Iterator<DataSetEntry> getRatingsIterator(){
        return ratings.iterator();
    }

    /**
     * Implementation of the interface.
     * @return An interator with the entries. (test entries)
     */
    public Iterator<DataSetEntry> getTestRatingsIterator(){
        return testRatings.iterator();
    }


}
