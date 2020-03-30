package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

public class TrainTestFilesDataSet implements DataSet
{
    protected static final String DEFAULT_SEPARATOR = ";";

    /**
     * Data storing variables
     */
    protected ArrayList<DataSetEntry> ratings;
    protected ArrayList<DataSetEntry> testRatings;

    /**
     * <p>Generates a DataSet form a text file, filling the dataModel attribute.</p>
     * <p> The lines of the file must have the format: userId;itemId;rating</p>
     * <p>The dataset is loaded without test items and test users</p>
     * @param fileName File with the ratings
     * @param testFileName File with the ratings
     */
    public TrainTestFilesDataSet(String fileName, String testFileName) {
        this(fileName, testFileName, DEFAULT_SEPARATOR);
    }

    /**
     * <p>Generates a DataSet form a text file, filling the dataModel attribute. </p>
     * <p>The lines of the file must have the format: userId SEPARATOR itemId SEPARATOR rating</p>
     * @param fileName File with the ratings
     * @param testFileName File with the ratings
     * @param separator Separator char between file fields
     */
    public TrainTestFilesDataSet(String fileName, String testFileName, String separator) {
        System.out.println("\nLoading dataset...");

        try {
            System.out.println("\nLoading dataset...");
            // Dataset reader
            BufferedReader datasetFile = new BufferedReader(new FileReader(new File(fileName)));
            BufferedReader testDatasetFile = new BufferedReader(new FileReader(new File(fileName)));

            String line;
            int numLines = 0;
            while ((line = datasetFile.readLine()) != null) {

                //Loading feedback
                numLines++;
                if (numLines % 1000000 == 0) System.out.print(".");
                if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

                // Parse line
                String[] s = line.split(separator);
                String userCode = s[0];
                String itemCode = s[1];
                double rating = Double.parseDouble(s[2]);

                // Store rating
                ratings.add(new DataSetEntry(userCode, itemCode, rating));
            }

            System.out.println("\nLoading test dataset...");
            numLines = 0;
            while ((line = datasetFile.readLine()) != null) {

                //Loading feedback
                numLines++;
                if (numLines % 1000000 == 0) System.out.print(".");
                if (numLines % 10000000 == 0) System.out.println(numLines + " ratings");

                // Parse line
                String[] s = line.split(separator);
                String userCode = s[0];
                String itemCode = s[1];
                double rating = Double.parseDouble(s[2]);

                // Store rating
                testRatings.add(new DataSetEntry(userCode, itemCode, rating));
            }

            datasetFile.close();
            testDatasetFile.close();

        } catch (Exception e) {
            System.out.println("An error has occurred while loading database");
            e.printStackTrace();
            System.exit(1);//TODO: Return status, don't close program.
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

    /**
     * Implementation of the interface.
     * @return Number of elements of the Rating array.
     */
    public int getRatingsSize(){
        return ratings.size();
    }

    /**
     * Implementation of the interface.
     * @return Number of elements of the Test Rating array.
     */
    public int getTestRatingsSize(){
        return testRatings.size();
    }

}