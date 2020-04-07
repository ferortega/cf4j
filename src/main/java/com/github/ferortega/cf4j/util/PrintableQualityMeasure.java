package com.github.ferortega.cf4j.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class responsible for abstracting the storage and representation of the results reported by experiments that analyze
 * the incidence of an hyper-parameter over a Recommender. The hyper-parameter can be an integer, double or String
 * value. The Recommender must be identify by an unique name (serie). This class also store results of repeated random
 * sub-sampling validation averaging the error values of N experiments.
 */
public class PrintableQualityMeasure {

	/**
	 * Values for the analyzed variable
	 */
	private String[] variables;

	/**
	 * Quality measures scores
	 */
	private Map<String, double[][]> scores;

	/**
	 * Score name
	 */
	private String name;
	
	/**
	 * Number of repetitions of the experiment
	 */
	private int N;

	/**
	 * Class constructor. Fix the number of repetitions to 1.
	 * @param name Score name
	 * @param variables Values taken by the parameter variable
	 */
	public PrintableQualityMeasure(String name, int[] variables) {
		this(name, intsToStrings(variables));
	}
	
	/**
	 * Class constructor. Fix the number of repetitions to 1.
	 * @param name Score name
	 * @param variables Values taken by the parameter variable
	 */
	public PrintableQualityMeasure(String name, double[] variables) {
		this(name, doublesToStrings(variables));
	}
	
	/**
	 * Class constructor. Fix the number of repetitions to 1.
	 * @param name Score name
	 * @param variables Values taken by the parameter variable
	 */
	public PrintableQualityMeasure(String name, String[] variables) {
		this(name, variables, 1);
	}
	
	/**
	 * Class constructor.
	 * @param name Score name
	 * @param variables Values taken by the parameter variable
	 * @param N Number of repetitions of the experiment
	 */
	public PrintableQualityMeasure(String name, int[] variables, int N) {
		this(name, intsToStrings(variables), N);
	}
	
	/**
	 * Class constructor.
	 * @param name Score name
	 * @param variables Values taken by the parameter variable
	 * @param N Number of repetitions of the experiment
	 */
	public PrintableQualityMeasure(String name, double[] variables, int N) {
		this(name, doublesToStrings(variables), N);
	}
	
	/**
	 * Class constructor.
	 * @param name Score name
	 * @param variables Values taken by the parameter variable
	 * @param N Number of repetitions of the experiment
	 */
	public PrintableQualityMeasure(String name, String[] variables, int N) {
		this.name = name;
		this.variables = variables;
		this.N = N;
		this.scores = new HashMap<>();
	}
	
	/**
	 * Put the score error for specific parameters
	 * @param variable Variable value
	 * @param serie Serie name
	 * @param score Quality measure score
	 */
	public void putScore(int variable, String serie, double score) {
		this.putScore(Integer.toString(variable), serie, score);
	}

	/**
	 * Put the score error for specific parameters
	 * @param variable Variable value
	 * @param serie Serie name
	 * @param score Quality measure score
	 */
	public void putScore(double variable, String serie, double score) {
		this.putScore(Double.toString(variable), serie, score);
	}

	/**
	 * Put the score error for specific parameters
	 * @param variable Variable value
	 * @param serie Serie name
	 * @param score Quality measure score
	 */
	public void putScore(String variable, String serie, double score) {
		this.putScore(1, variable, serie, score);
	}

	/**
	 * Put the score error for specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @param score Quality measure score
	 */
	public void putScore(int n, int variable, String serie, double score) {
		this.putScore(n, Integer.toString(variable), serie, score);
	}

	/**
	 * Put the score error for specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @param score Quality measure score
	 */
	public void putScore(int n, double variable, String serie, double score) {
		this.putScore(n, Double.toString(variable), serie, score);
	}

	/**
	 * Put the score error for specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @param score Quality measure score
	 */
	public void putScore(int n, String variable, String serie, double score) {
		double[][] serieScores = this.scores.get(serie);
		if (serieScores == null) {
			serieScores = new double[this.N][this.variables.length];
		}

		int v = this.getVariableIndex(variable);
		serieScores[n-1][v] = score;

		this.scores.put(serie, serieScores);
	}
	
	/**
	 * Retrieves the score value that was made with specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @return Quality measure score
	 */
	public double getScore(int n, int variable, String serie) {
		return this.getScore(n, Integer.toString(variable), serie);
	}

	/**
	 * Retrieves the score value that was made with specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @return Quality measure score
	 */
	public double getScore(int n, double variable, String serie) {
		return this.getScore(n, Double.toString(variable), serie);
	}

	/**
	 * Retrieves the score value that was made with specific parameters
	 * @param n Repetition number (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @return Quality measure score
	 */
	public double getScore(int n, String variable, String serie) {
		double[][] serieScores = this.scores.get(serie);
		int v = this.getVariableIndex(variable);
		return serieScores[n-1][v];
	}
	
	/**
	 * Retrieves the averaged error value that was made with specific parameters across all repetitions.
	 * @param repetitions Number of repetitions to be averaged (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @return Quality measure score
	 */
	public double getAveragedScore(int repetitions, int variable, String serie) {
		return this.getAveragedScore(repetitions, Integer.toString(variable), serie);
	}
	
	/**
	 * Retrieves the averaged error value that was made with specific parameters across all repetitions.
	 * @param repetitions Number of repetitions to be averaged (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @return Quality measure score
	 */
	public double getAveragedScore(int repetitions, double variable, String serie) {
		return this.getAveragedScore(repetitions, Double.toString(variable), serie);
	}
	
	/**
	 * Retrieves the averaged error value that was made with specific parameters across all repetitions.
	 * @param repetitions Number of repetitions to be averaged (from 1 to N)
	 * @param variable Variable value
	 * @param serie Serie name
	 * @return Quality measure score
	 */
	public double getAveragedScore(int repetitions, String variable, String serie) {
		double[][] serieScores = this.scores.get(serie);
		int v = this.getVariableIndex(variable);
		
		double score = 0;
		for (int r = 0; r < repetitions; r++) {
			score += serieScores[r][v];
		}

		return score / repetitions;
	}
	
	/**
	 * Retrieves the averaged error value that was made with specific parameters across all repetitions.
	 * @param variable Variable value
	 * @param serie Serie name
	 * @return Quality measure score
	 */
	public double getAveragedScore(int variable, String serie) {
		return this.getAveragedScore(Integer.toString(variable), serie);
	}
	
	/**
	 * Retrieves the averaged error value that was made with specific parameters across all repetitions.
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @return Valor Error value
	 */
	public double getAveragedScore(double variable, String serie) {
		return this.getAveragedScore(Double.toString(variable), serie);
	}
	
	/**
	 * Retrieves the averaged error value that was made with specific parameters across all repetitions.
	 * @param variable Parameter variable value
	 * @param serie Data series in which the error occurred
	 * @return Valor Error value
	 */
	public double getAveragedScore(String variable, String serie) {
		return this.getAveragedScore(this.N, variable, serie);
	}

	/**
	 * Formats the averaged scores into a String
	 * @param repetitions Number of repetitions to be averaged (from 1 to N)
	 * @param separator Field separator
	 * @return String with the score
	 */
	public String toString(int repetitions, String separator) {
		StringBuilder str = new StringBuilder("\n").append(this.name);

		Set<String> series = this.scores.keySet();
		for (String serie : series) {
			str.append(separator).append(serie);
		}

		str.append("\n");

		for (String variable : variables) {
			str.append(variable);
			for (String serie : series) {
				double score = this.getAveragedScore(repetitions, variable, serie);
				str.append(separator).append(score);
			}
			str.append("\n");
		}

		return str.toString();
	}

	/**
	 * Formats the averaged scores into a String
	 * @param separator Field separator
	 * @return String with the score
	 */
	public String toString(String separator) {
		return this.toString(this.N, separator);
	}

	/**
	 * Formats the averaged scores into a String. Uses semicolon as separator.
	 * @param repetitions Number of repetitions to be averaged (from 1 to N)
	 * @return String with the score
	 */
	public String toString(int repetitions) {
		return this.toString(repetitions, ";");
	}
	
	@Override
	public String toString() {
		return this.toString(this.N);
	}

	/**
	 * Prints the score
	 * @param repetitions Number of repetitions to be averaged (from 1 to N)
	 * @param format Decimal number format
	 */
	public void print(int repetitions, String format) {
		DecimalFormat df = new DecimalFormat(format);
		Set<String> series = this.scores.keySet();

		int seriesWidth = format.length();
		for (String serie : series) {
			seriesWidth = Math.max(seriesWidth, serie.length());
		}

		int variablesWitdth = 0;
		for (String variable : variables) {
			variablesWitdth = Math.max(variablesWitdth, variable.length());
		}

		int separationWidth = 2;

		// Print score name
		System.out.println("\n" + this.name);

		// Print series names
		System.out.print(blankString(variablesWitdth));

		for (String serie: series) {
			System.out.print(blankString(seriesWidth - serie.length() + separationWidth) + serie);
		}

		System.out.println();

		// Print scores
		for (String variable : variables) {
			System.out.print(variable + blankString(variablesWitdth - variable.length()));
			for (String serie: series) {
				double score = this.getAveragedScore(repetitions, variable, serie);
				String strScore = df.format(score);
				System.out.print(blankString(seriesWidth - strScore.length() + separationWidth) + strScore);
			}
			System.out.println();
		}
	}

	/**
	 * Generates a blank String
	 * @param size Size of the string
	 * @return Blank String
	 */
	private String blankString(int size) {
		StringBuilder str = new StringBuilder();
		while (size > 0) {
			str.append(" ");
			size--;
		}
		return str.toString();
	}
	
	/**
	 * Prints the score. Scores are formated with 0.000000
	 * @param repetitions Number of repetitions to be averaged (from 1 to N)
	 */
	public void print(int repetitions) {
		this.print(repetitions, "0.000000");
	}
	
	/**
	 * Prints the score
	 * @param format Decimal number format
	 */
	public void print(String format) {
		this.print(this.N, format);
	}

	/**
	 * Prints the score
	 */
	public void print() {
		this.print(this.N);
	}
	
	/**
	 * Converts an array of ints to an array of strings
	 * @param ints Array of ints
	 * @return Array of strings
	 */
	private static String[] intsToStrings(int[] ints) {
		String [] strings = new String [ints.length];
		for (int i = 0; i < ints.length; i++) {
			strings[i] = Integer.toString(ints[i]);
		}
		return strings;
	}
	
	/**
	 * Converts an array of doubles to an array of strings
	 * @param doubles Array of doubles
	 * @return Array of strings
	 */
	private static String[] doublesToStrings(double[] doubles) {
		String [] strings = new String [doubles.length];
		for (int i = 0; i < doubles.length; i++) {
			strings[i] = Double.toString(doubles[i]);
		}
		return strings;
	}

	/**
	 * Find the index of a variable value
	 * @param value Variable value (must exists)
	 * @return Index of the variable value
	 */
	private int getVariableIndex(String value) {
		int index = 0;
		while (!value.equals(this.variables[index])) {
			index++;
		}
		return index;
	}
}
