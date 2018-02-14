package cf4j.utils;

/**
 * Class responsible for abstracting the storage and representation of the results. Used to save those
 * results in which test an integer parameter (number of neighbors, number of recommendations, etc..)
 * over different data series (similarity metrics, for example). This class also store results of
 * repeated random sub-sampling validation averaging the error values of N experiments.
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
	private double [][][] errors;

	/**
	 * Error Name
	 */
	private String name;
	
	/**
	 * Number of repetitions of the experiment
	 */
	private int N;

	/**
	 * Class constructor. Initializes parameters.
	 * @param name Error Name
	 * @param variables Values taken by the parameter variable
	 * @param series Series of data included
	 */
	public PrintableQualityMeasure (String name, int [] variables, String [] series) {
		this(name, variables, series, 1);
	}
	
	/**
	 * Class constructor. Initializes parameters.
	 * @param name Error Name
	 * @param variables Values taken by the parameter variable
	 * @param series Series of data included
	 * @param N Number of repetitions of the experiment
	 */
	public PrintableQualityMeasure (String name, int [] variables, String [] series, int N) {
		this.name = name;
		this.variables = variables;
		this.series = series;
		this.N = N;
		this.errors = new double [N][this.variables.length][this.series.length];
	}

	/**
	 * This method indicates the error for specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @param error Error value
	 */
	public void putError (int n, int variable, String serie, double error) {
		int ri = n - 1;
		int vi = 0; while (variable != this.variables[vi]) vi++;
		int si = 0; while (!serie.equals(this.series[si])) si++;
		this.errors[ri][vi][si] = error;
	}
	
	/**
	 * This method indicates the error for specific parameters
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @param error Error value
	 */
	public void putError (int variable, String serie, double error) {
		this.putError(1, variable, serie, error);	
	}

	/**
	 * This method retrieves the error value that was made with specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @return Valor Error value
	 */
	public double getError (int n, int variable, String serie) {
		int ri = n - 1;
		int vi = 0; while (variable != this.variables[vi]) vi++;
		int si = 0; while (!serie.equals(this.series[si])) si++;
		return this.errors[ri][vi][si];
	}
	
	/**
	 * This method retrieves the averaged error value that was made with specific 
	 * parameters across all repetitions.
	 * @param repetitions Number of repetitions to be analyzed (from 1 to N)
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @return Valor Error value
	 */
	public double getAveragedError (int repetitions, int variable, String serie) {
		int vi = 0; while (variable != this.variables[vi]) vi++;
		int si = 0; while (!serie.equals(this.series[si])) si++;
		
		double error = 0;
		for (int ri = 0; ri < repetitions; ri++) {
			error += this.errors[ri][vi][si];
		}
		error /= repetitions;
			
		return error;
	}
	
	/**
	 * This method retrieves the averaged error value that was made with specific 
	 * parameters across all repetitions
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @return Valor Error value
	 */
	public double getAveragedError (int variable, String serie) {
		return this.getAveragedError(this.N, variable, serie);
	}

	/**
	 * This method returns a formatted String with error
	 * @param repetitions Number of repetitions to be analyzed (from 1 to N)
	 * @param separator Field separator
	 * @return String with the results
	 */
	public String toString (int repetitions, String separator) {
		String s = "\n" + this.name;
		for (String serie : this.series) s += separator + serie;
		s += "\n";
		for (int variable : this.variables) {
			s += variable;
			for (String serie : this.series) {
				s += separator + this.getAveragedError(repetitions, variable, serie);
			}
			s+= "\n";
		}
		return s.replace(".", ",");
	}
	
	/**
	 * This method returns a formatted String with error
	 * @param separator Field separator
	 * @return String with the results
	 */
	public String toString (String separator) {
		return this.toString(this.N, separator);
	}

	/**
	 * Returns a formatted error String. Uses space as separator.
	 * @param repetitions Number of repetitions to be analyzed (from 1 to N)
	 * @return String with the results
	 */
	public String toString (int repetitions) {
		return this.toString(repetitions, " ");
	}
	
	/**
	 * Returns a formatted error String. Uses space as separator.
	 * @return String with the results
	 */
	public String toString () {
		return this.toString(this.N);
	}

	/**
	 * This method print the error
	 * @param repetitions Number of repetitions to be analyzed (from 1 to N)
	 * @param separator Field separator
	 */
	public void print (int repetitions, String separator) {
		System.out.println(this.toString(repetitions, separator));
	}
	
	/**
	 * This method print the error
	 * @param repetitions Number of repetitions to be analyzed (from 1 to N)
	 */
	public void print (int repetitions) {
		System.out.println(this.toString(repetitions));
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
		System.out.println(this.toString());
	}
}
