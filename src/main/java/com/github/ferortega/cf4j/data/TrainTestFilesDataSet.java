package com.github.ferortega.cf4j.data;

import com.github.ferortega.cf4j.data.types.DataSetEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>This class implements the DataSet interface by loading training and test ratings from separated text files.
 * Each line of the ratings files must have the following format:</p>
 * <pre>&lt;userId&gt;&lt;separator&gt;&lt;itemId&gt;&lt;separator&gt;&lt;rating&gt;</pre>
 * <p>Where &lt;separator&gt; is an special character that delimits ratings fields (semicolon by default).</p>
 */
public class TrainTestFilesDataSet implements DataSet {

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
     * Generates a DataSet form training and test ratings files. Semicolon is used as separator.
     * @param fileName File with the (training) ratings.
     * @param testFileName File with the test ratings.
     * @throws IOException When the file is not accessible by the system with read permissions.
     */
    public TrainTestFilesDataSet(String fileName, String testFileName) throws IOException {
        this(fileName, testFileName, DEFAULT_SEPARATOR);
    }

    /**
     * Generates a DataSet form training and test ratings files.
     * @param fileName File with the (training) ratings.
     * @param testFileName File with the test ratings.
     * @param separator Separator char between ratings fields.
     * @throws IOException When the file is not accessible by the system with read permissions.
     */
    public TrainTestFilesDataSet(String fileName, String testFileName, String separator) throws IOException {
        System.out.println("\nLoading dataset...");

        ratings = new ArrayList<>();
        testRatings = new ArrayList<>();

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
        while ((line = testDatasetFile.readLine()) != null) {

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

    @Override
    public Iterator<DataSetEntry> getRatingsIterator(){
        return ratings.iterator();
    }

    @Override
    public Iterator<DataSetEntry> getTestRatingsIterator(){
        return testRatings.iterator();
    }

    @Override
    public int getNumberOfRatings(){
        return ratings.size();
    }

    @Override
    public int getNumberOfTestRatings(){
        return testRatings.size();
    }
}
