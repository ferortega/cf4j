package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.DataSetEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class implements the DataSet interface, this implies that it is in charge of reading a file in a certain way to
 * generate a series of iterators over the data read from the file.
 * Specifically, TrainTestFilesDataSet will read two files with the no-test and test raw data. This class will
 * store that information generating an itrator over the stored memory.
 */
public class TrainTestFilesDataSet implements DataSet
{
    protected static final String DEFAULT_SEPARATOR = ";";

    /**
     * Raw stored ratings
     */
    protected ArrayList<DataSetEntry> ratings;

    /**
     * Raw stored test ratings
     */
    protected ArrayList<DataSetEntry> testRatings;

    /**
     * Generates a DataSet form a text file, filling the dataModel attribute.
     * The lines of the file must have the format: userId;itemId;rating.
     * The dataset is loaded without test items and test users.
     * @param fileName File with the ratings
     * @param testFileName File with the ratings
     * @throws IOException When the file is not accessible by the system with write permissions.
     */
    public TrainTestFilesDataSet(String fileName, String testFileName) throws IOException {
        this(fileName, testFileName, DEFAULT_SEPARATOR);
    }

    /**
     * Generates a DataSet form a text file, filling the dataModel attribute.
     * The lines of the file must have the format: userId SEPARATOR itemId SEPARATOR rating.
     * @param fileName File with the ratings.
     * @param testFileName File with the ratings.
     * @param separator Separator char between file fields.
     * @throws IOException When the file is not accessible by the system with write permissions.
     */
    public TrainTestFilesDataSet(String fileName, String testFileName, String separator) throws IOException {
        System.out.println("\nLoading dataset...");

        // Dataset reader
        BufferedReader datasetFile = new BufferedReader(new FileReader(new File(fileName)));
        BufferedReader testDatasetFile = new BufferedReader(new FileReader(new File(testFileName)));

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
    }

    /**
     * Implementation of the interface DataSet.
     * @return An interator with the entries. (no test entries)
     */
    public Iterator<DataSetEntry> getRatingsIterator(){
        return ratings.iterator();
    }

    /**
     * Implementation of the interface DataSet.
     * @return An interator with the entries. (test entries)
     */
    public Iterator<DataSetEntry> getTestRatingsIterator(){
        return testRatings.iterator();
    }

    /**
     * Implementation of the interface DataSet.
     * @return Number of elements of the Rating array.
     */
    public int getNumberOfRatings(){
        return ratings.size();
    }

    /**
     * Implementation of the interface DataSet.
     * @return Number of elements of the Test Rating array.
     */
    public int getNumberOfTestRatings(){
        return testRatings.size();
    }

}
