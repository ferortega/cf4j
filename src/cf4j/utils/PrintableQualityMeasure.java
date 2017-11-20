package cf4j.utils;

/**
 * Class responsible for abstracting the storage and representation of the results. Used to save those
 * results in which test an integer parameter (number of neighbors, number of recommendations, etc..)
 * over different data series (similarity metrics, for example).
 *
 * @author Fernando Ortega
 */
public class PrintableQualityMeasure {

	/**
	 * Values taken by the parameter "variable"
	 */
	private int [] variables;

	/**
	 * Series of data included
	 */
	private String [] series;

	/**
	 * Errors values
	 */
	private double [][] errors;

	/**
	 * Error Name
	 */
	private String name;

	/**
	 * Class constructor. Initializes parameters.
	 * @param name Error Name
	 * @param variables Values taken by the parameter variable
	 * @param series Series of data included
	 */
	public PrintableQualityMeasure (String name, int [] variables, String [] series) {
		this.name = name;
		this.variables = variables;
		this.series = series;
		this.errors = new double [this.variables.length][this.series.length];
	}

	/**
	 * This method indicates the error for specific parameters
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @param error Error value
	 */
	public void putError (int variable, String serie, double error) {
		int vi = 0; while (variable != this.variables[vi]) vi++;
		int si = 0; while (!serie.equals(this.series[si])) si++;
		this.errors[vi][si] = error;
	}

	/**
	 * This method retrieves the error value that was made with specific parameters
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @return Valor Error value
	 */
	public double getError (int variable, String serie) {
		int vi = 0; while (variable != this.variables[vi]) vi++;
		int si = 0; while (!serie.equals(this.series[si])) si++;
		return this.errors[vi][si];
	}

	/**
	 * This method returns a formatted String with error
	 * @param separator Field Separator
	 * @return String with the results
	 */
	public String toString (String separator) {
		String s = "\n" + this.name;
		for (String serie : this.series) s += separator + serie;
		s += "\n";
		for (int i = 0; i < this.variables.length; i++) {
			s += this.variables[i];
			for (int j = 0; j < this.series.length; j++) {
				s += separator + this.errors[i][j];
			}
			s+= "\n";
		}
		return s.replace(".", ",");
	}

	/**
	 * Returns a formatted error String. Uses space as separator.
	 * @return String with the results
	 */
	public String toString () {
		return this.toString(" ");
	}

	/**
	 * This method print the error
	 * @param separator Field separator
	 */
	public void print (String separator) {
		System.out.println(this.toString(separator));
	}

	/**
	 * This method print the error
	 */
	public void print () {
		this.print(" ");
	}
}
