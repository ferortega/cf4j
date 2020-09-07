package es.upm.etsisi.cf4j.util.plot;

import com.github.freva.asciitable.AsciiTable;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Abstract class that represents a CF4J plot. With a CF4J plot final users can:
 *
 * <ul>
 *   <li>Draw the plot.
 *   <li>Export the plot into a PNG image.
 *   <li>Print the plot data into the standard output.
 *   <li>Export the plot data into a CSV file.
 * </ul>
 */
public abstract class Plot {

  /**
   * Exports plot data into a CSV file
   *
   * @param filename File name
   * @throws IOException
   */
  public void exportData(String filename) throws IOException {
    this.exportData(filename, ",", true);
  }

  /**
   * Exports plot data into a CSV file
   *
   * @param filename File name
   * @param separator CSV separator field. By default: colon character (,)
   * @throws IOException
   */
  public void exportData(String filename, String separator) throws IOException {
    this.exportData(filename, separator, true);
  }

  /**
   * Exports plot data into a CSV file
   *
   * @param filename File name
   * @param includeHeader Include CSV header line. By default: true
   * @throws IOException
   */
  public void exportData(String filename, boolean includeHeader) throws IOException {
    this.exportData(filename, ",", includeHeader);
  }

  /**
   * Exports plot data into a CSV file
   *
   * @param filename File name
   * @param separator CSV separator field. By default: colon character (,)
   * @param includeHeader Include CSV header line. By default: true
   * @throws IOException
   */
  public void exportData(String filename, String separator, boolean includeHeader)
      throws IOException {
    File f = new File(filename);
    File parent = f.getAbsoluteFile().getParentFile();
    if (!parent.exists() && !parent.mkdirs()) {
      throw new IOException("Unable to create directory " + parent);
    }

    PrintWriter writer = new PrintWriter(f);

    if (includeHeader) {
      String[] headers = this.getDataHeaders();
      writer.println(StringUtils.join(headers, separator));
    }

    String[][] content = this.getDataContent("#.###########", "#.###########");
    for (String[] row : content) {
      writer.println(StringUtils.join(row, separator));
    }

    writer.close();
  }

  /** Prints the plot data into the standard output */
  public void printData() {
    this.printData("0.0000");
  }

  /**
   * Prints the plot data into the standard output
   *
   * @param axisTicksFormat Number format of the axis
   */
  public void printData(String axisTicksFormat) {
    this.printData(axisTicksFormat, axisTicksFormat);
  }

  /**
   * Prints the plot data into the standard output
   *
   * @param xAxisTicksFormat Number format of the x axis
   * @param yAxisTicksFormat Number format of the y axis
   */
  public void printData(String xAxisTicksFormat, String yAxisTicksFormat) {
    System.out.println("\n" + this.toString(xAxisTicksFormat, yAxisTicksFormat));
  }

  @Override
  public String toString() {
    return this.toString("0.0000");
  }

  /**
   * Stringify the plot data
   *
   * @param axisTicksFormat Number format for the values of the axis
   * @return Plot's data stringified
   */
  public String toString(String axisTicksFormat) {
    return this.toString(axisTicksFormat, axisTicksFormat);
  }

  /**
   * Stringify the plot data
   *
   * @param xAxisTicksFormat Number format for the values of the x axis
   * @param yAxisTicksFormat Number format for the values of the x axis
   * @return Plot's data stringified
   */
  public String toString(String xAxisTicksFormat, String yAxisTicksFormat) {
    String[] headers = this.getDataHeaders();
    String[][] content = this.getDataContent(xAxisTicksFormat, yAxisTicksFormat);
    return AsciiTable.getTable(headers, content);
  }

  /**
   * Returns an String array with the headers of the plot's data
   *
   * @return Headers of the plot's data
   */
  protected abstract String[] getDataHeaders();

  /**
   * Returns an String matrix with the content of the plot's data. First dimension denotes the row
   * and second dimension denotes the column that must be correlated with the header returned by
   * getDataHeaders().
   *
   * @param xAxisTicksFormat Number format for the values of the x axis
   * @param yAxisTicksFormat Number format for the values of the x axis
   * @return String matrix with the content of the plot's data
   */
  protected abstract String[][] getDataContent(String xAxisTicksFormat, String yAxisTicksFormat);

  /** Draws the plot into a JFrame */
  public void draw() {
    AbstractPlot plot = this.getGralPlot();
    PlotFrame frame = new PlotFrame(plot);
    frame.setTitle("CF4J " + this.getClass().getSimpleName());
    frame.setVisible(true);
  }

  /**
   * Exports the plot to a PNG file
   *
   * @param filename PNG file name
   * @throws IOException
   */
  public void exportPlot(String filename) throws IOException {
    AbstractPlot plot = this.getGralPlot();
    File f = new File(filename);
    File parent = f.getAbsoluteFile().getParentFile();
    if (!parent.exists() && !parent.mkdirs()) {
      throw new IOException("Unable to create directory " + parent);
    }
    DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
    FileOutputStream fos = new FileOutputStream(f);
    writer.write(plot, fos, PlotSettings.getWidth(), PlotSettings.getHeight());
    fos.close();
  }

  /**
   * Gets an AbstractPlot using GRAL
   *
   * @return GRAL's AbstractPlot
   */
  protected abstract AbstractPlot getGralPlot();

  /** Inner class used to transform an AbstractPlot into a JFrame */
  private class PlotFrame extends JFrame {
    public PlotFrame(AbstractPlot plot) {
      getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setMinimumSize(getContentPane().getMinimumSize());
      setSize(PlotSettings.getWidth(), PlotSettings.getHeight());
    }
  }
}
