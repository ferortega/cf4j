package cf4j.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Util class to write text files line by line (or char by char) 
 * given the file name.
 * 
 * @author Fernando Ortega
 */
public class OutputTextFile 
{
	private PrintWriter f;
	
	public OutputTextFile (String filename) throws IOException
	{
		this.f = new PrintWriter (new FileWriter(filename));
	}
	
	public void print (String s)
	{
		this.f.print(s);
	}
	
	public void println (String l)
	{
		this.f.println(l);
	}
	
	public void flush ()
	{
		this.f.flush();
	}
	
	public void close ()
	{
		this.f.close();
	}
}
