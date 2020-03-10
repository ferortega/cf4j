package cf4j.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * <p>This class allows us to load the parameters of an experiment from a file. The file must follow 
 * the following format:</p>
 * <ul>
 *  <li>Each line contains the value of one parameter.</li>
 *  <li>The lines that start with '#' are ignored.</li>
 * 	<li>The integer and doubles must be defined as "parameter_name=value".</li>
 * 	<li>The arrays must be defined as "array_name=value_1,value_2,...,value_N".</li>
 * </ul>
 * 
 * @author Fernando Ortega
 */
public class Parameters {

	/**
	 * Object where the parameters are saved.
	 */
	private Properties properties;
	
	/**
	 * Creates a new Parameters instance from a file.
	 * @param filename File name
	 */
	public Parameters (String filename) {
		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(filename));
		} 
		catch (FileNotFoundException e) {
			System.out.println("Parameters file could not be loaded");
			e.printStackTrace();
			System.exit(101);
		} 
		catch (IOException e) {
			System.out.println("An error has occurred while loading parameters file");
			e.printStackTrace();
			System.exit(102);
		}
	}
	
	/**
	 * Get a String parameter from a key
	 * @param key Parameter name
	 * @return Parameter value
	 */
	public String getString (String key) {
		return properties.getProperty(key);
	}
	
	/**
	 * Get an Integer parameter from a key
	 * @param key Parameter name
	 * @return Parameter value
	 */
	public int getInteger (String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
	
	/**
	 * Get a Double parameter from a key
	 * @param key Parameter name
	 * @return Parameter value
	 */
	public double getDouble (String key) {
		return Double.parseDouble(properties.getProperty(key));
	}
	
	/**
	 * Get an Integer array parameter from a key
	 * @param key Parameter name
	 * @return Parameter value
	 */
	public int [] getIntegerArray (String key) {
		String [] s = properties.getProperty(key).split(",");
		int [] array = new int [s.length];
		for (int i  = 0; i < s.length; i++) array[i] = Integer.parseInt(s[i]);
		return array;
	}
	
	/**
	 * Get a Double array parameter from a key
	 * @param key Parameter name
	 * @return Parameter value
	 */
	public double [] getDoubleArray (String key) {
		String [] s = properties.getProperty(key).split(",");
		double [] array = new double [s.length];
		for (int i  = 0; i < s.length; i++) array[i] = Double.parseDouble(s[i]);
		return array;
	}
	
	/**
	 * Get a Boolean parameter from a key
	 * @param key Parameter name
	 * @return Parameter value
	 */
	public boolean getBoolean (String key) {
		return Boolean.parseBoolean(properties.getProperty(key));
	}
}
