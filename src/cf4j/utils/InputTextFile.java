package cf4j.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Util class to read text files line by line given the file name.
 * 
 * @author Fernando Ortega
 */
public class InputTextFile {
	
	/**
	 * Buffered reader of the file
	 */
	private BufferedReader f;
	
	public InputTextFile (String filename) throws FileNotFoundException {
		this.f = new BufferedReader (new FileReader (new File(filename)));
	}
	
	public String readLine () throws IOException {
		return this.f.readLine();
	}
	 
	public void close() throws IOException {
		this.f.close();
	}
}
