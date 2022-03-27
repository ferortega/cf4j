package es.upm.etsisi.cf4j.util.plot;

import java.awt.*;

/** This class contains global plot settings. */
public class PlotSettings {

  /** Color palette */
  private static Color[] colorPalette;

  /** Plot's background color */
  private static Color backgroundColor;

  /** Inset for the legend */
  private static double legendInset;

  /** Inset for y axis */
  private static double yAxisInset;

  /** Inset for x axis */
  private static double xAxisInset;

  /** Inset for clear borders */
  private static double clearInset;

  /** Labels font */
  private static Font labelsFont;

  /** Ticks font */
  private static Font ticksFont;

  /** Legend font */
  private static Font legendFont;

  /** Points font */
  private static Font pointsFont;

  /** Label distance in the x axis */
  private static double xAxisLabelDistance;

  /** Label distance in the y axis */
  private static double yAxisLabelDistance;

  /** Legend distance */
  private static double legendDistance;

  /** Plot width in pixels */
  private static int width;

  /** Plot height in pixels */
  private static int height;

  static {
    colorPalette =
        new Color[] {
          new Color(0, 69, 134),
          new Color(255, 66, 14),
          new Color(255, 211, 32),
          new Color(87, 157, 28),
          new Color(126, 0, 33),
          new Color(131, 202, 205),
          new Color(49, 64, 4),
          new Color(174, 207, 0),
          new Color(75, 31, 111),
          new Color(255, 149, 14)
        };
    backgroundColor = Color.WHITE;
    legendInset = 70.0;
    yAxisInset = 102.0;
    xAxisInset = 85.0;
    clearInset = 20;
    labelsFont = new Font("Dialog", Font.PLAIN, 20);
    ticksFont = new Font("Dialog", Font.PLAIN, 17);
    legendFont = new Font("Dialog", Font.PLAIN, 18);
    pointsFont = new Font("Dialog", Font.PLAIN, 15);
    xAxisLabelDistance = 0.8;
    yAxisLabelDistance = 1.8;
    legendDistance = 0.5;
    width = 800;
    height = 600;
  }

  /**
   * Private constructor to avoid class instantiation.
   */
  private PlotSettings() {}

  /**
   * Gets the color palette
   *
   * @return Color palette
   */
  public static Color[] getColorPalette() {
    return colorPalette;
  }

  /**
   * Sets the color paletee
   *
   * @param colorPalette Color palette
   */
  public static void setColorPalette(Color[] colorPalette) {
    PlotSettings.colorPalette = colorPalette;
  }

  /**
   * Gets a color from the palette
   *
   * @param i Color index
   * @return Color
   */
  public static Color getColor(int i) {
    return colorPalette[i % colorPalette.length];
  }

  /**
   * Gets the plot's background color
   *
   * @return Plot's background color
   */
  public static Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Sets the plot's background color
   *
   * @param backgroundColor Plot's background color
   */
  public static void setBackgroundColor(Color backgroundColor) {
    PlotSettings.backgroundColor = backgroundColor;
  }

  /**
   * Gets the inset for the legend
   *
   * @return Inset for the legend
   */
  public static double getLegendInset() {
    return legendInset;
  }

  /**
   * Sets the inset for the legend
   *
   * @param legendInset Insets for the legend
   */
  public static void setLegendInset(double legendInset) {
    PlotSettings.legendInset = legendInset;
  }

  /**
   * Gets the inset for the y axis
   *
   * @return Inset for the y axis
   */
  public static double getyAxisInset() {
    return yAxisInset;
  }

  /**
   * Sets the inset for the y axis
   *
   * @param yAxisInset Inset for the y axis
   */
  public static void setyAxisInset(double yAxisInset) {
    PlotSettings.yAxisInset = yAxisInset;
  }

  /**
   * Gets the inset for the x axis
   *
   * @return Inset for the x axis
   */
  public static double getxAxisInset() {
    return xAxisInset;
  }

  /**
   * Sets the inset for the x axis
   *
   * @param xAxisInset Inset for the x axis
   */
  public static void setxAxisInset(double xAxisInset) {
    PlotSettings.xAxisInset = xAxisInset;
  }

  /**
   * Gets the inset for a clear border
   *
   * @return Inset for a clear border
   */
  public static double getClearInset() {
    return clearInset;
  }

  /**
   * Sets the inset for a clear border
   *
   * @param clearInset Inset for a clear border
   */
  public static void setClearInset(double clearInset) {
    PlotSettings.clearInset = clearInset;
  }

  /**
   * Gets the labels font
   *
   * @return Labels font
   */
  public static Font getLabelsFont() {
    return labelsFont;
  }

  /**
   * Sets the labels font
   *
   * @param labelsFont Labels font
   */
  public static void setPrimaryFont(Font labelsFont) {
    PlotSettings.labelsFont = labelsFont;
  }

  /**
   * Gets the ticks font
   *
   * @return Ticks font
   */
  public static Font getTicksFont() {
    return ticksFont;
  }

  /**
   * Sets the ticks font
   *
   * @param ticksFont Ticks font
   */
  public static void setTicksFont(Font ticksFont) {
    PlotSettings.ticksFont = ticksFont;
  }

  /**
   * Gets the legend font
   *
   * @return Legend font
   */
  public static Font getLegendFont() {
    return legendFont;
  }

  /**
   * Sets the legend font
   *
   * @param legendFont Legend font
   */
  public static void setLegendFont(Font legendFont) {
    PlotSettings.legendFont = legendFont;
  }

  /**
   * Gets the points font
   *
   * @return Points font
   */
  public static Font getPointsFont() {
    return pointsFont;
  }

  /**
   * Sets the points font
   *
   * @param pointsFont Points font
   */
  public static void setPointsFont(Font pointsFont) {
    PlotSettings.pointsFont = pointsFont;
  }

  /**
   * Gets the label distance in the x axis
   *
   * @return Label distance in the x axis
   */
  public static double getxAxisLabelDistance() {
    return xAxisLabelDistance;
  }

  /**
   * Sets the label distance in the x axis
   *
   * @param xAxisLabelDistance Label distance in the x axis
   */
  public static void setxAxisLabelDistance(double xAxisLabelDistance) {
    PlotSettings.xAxisLabelDistance = xAxisLabelDistance;
  }

  /**
   * Gets the label distance in the y axis
   *
   * @return Label distance in the y axis
   */
  public static double getyAxisLabelDistance() {
    return yAxisLabelDistance;
  }

  /**
   * Sets the label distance in the y axis
   *
   * @param yAxisLabelDistance Label distance in the y axis
   */
  public static void setyAxisLabelDistance(double yAxisLabelDistance) {
    PlotSettings.yAxisLabelDistance = yAxisLabelDistance;
  }

  /**
   * Gets the legend distance
   *
   * @return Legend distance
   */
  public static double getLegendDistance() {
    return legendDistance;
  }

  /**
   * Sets the legend distance
   *
   * @param legendDistance Legend distance
   */
  public static void setLegendDistance(double legendDistance) {
    PlotSettings.legendDistance = legendDistance;
  }

  /**
   * Get plot with in pixels
   *
   * @return Plot with in pixels
   */
  public static int getWidth() {
    return width;
  }

  /**
   * Sets the plot width in pixels
   *
   * @param width Plot width in pixels
   */
  public static void setWidth(int width) {
    PlotSettings.width = width;
  }

  /**
   * Get the plot height in pixels
   *
   * @return Plot height in pixels
   */
  public static int getHeight() {
    return height;
  }

  /**
   * Sets the plot height in pixels
   *
   * @param height Plot height in pixels
   */
  public static void setHeight(int height) {
    PlotSettings.height = height;
  }
}
