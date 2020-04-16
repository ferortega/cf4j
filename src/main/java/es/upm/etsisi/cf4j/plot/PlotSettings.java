package es.upm.etsisi.cf4j.plot;

import java.awt.*;

public class PlotSettings {

    private static Color[] colorPalette;

    private static Color backgroundColor;

    private static double topInset;

    private static double leftInset;

    private static double bottomInset;

    private static double rightInset;

    private static Font primaryFont;

    private static Font secondaryFont;

    private static double xAxisLabelDistance;

    private static double yAxisLabelDistance;

    private static double legendDistance;

    private static int width;

    private static int height;

    static {
        colorPalette = new Color[]{
                new Color(0,69,134),
                new Color(255,66,14),
                new Color(255,211,32),
                new Color(87,157,28),
                new Color(126,0,33),
                new Color(131,202,205),
                new Color(49,64,4),
                new Color(174,207,0),
                new Color(75,31,111),
                new Color(255,149,14)
        };
        backgroundColor = Color.WHITE;
        topInset = 70.0;
        leftInset = 95.0;
        bottomInset = 75.0;
        rightInset = 20;
        primaryFont = new Font("Dialog", Font.PLAIN, 17);
        secondaryFont =  new Font("Dialog", Font.PLAIN, 15);
        xAxisLabelDistance = 0.8;
        yAxisLabelDistance = 1.8;
        legendDistance = 0.5;
        width = 800;
        height = 600;
    }

    public static Color[] getColorPalette() {
        return colorPalette;
    }

    public static void setColorPalette(Color[] colorPalette) {
        PlotSettings.colorPalette = colorPalette;
    }

    public static Color getColor(int i) {
        return colorPalette[i % colorPalette.length];
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static void setBackgroundColor(Color backgroundColor) {
        PlotSettings.backgroundColor = backgroundColor;
    }

    public static double getTopInset() {
        return topInset;
    }

    public static void setTopInset(double topInset) {
        PlotSettings.topInset = topInset;
    }

    public static double getLeftInset() {
        return leftInset;
    }

    public static void setLeftInset(double leftInset) {
        PlotSettings.leftInset = leftInset;
    }

    public static double getBottomInset() {
        return bottomInset;
    }

    public static void setBottomInset(double bottomInset) {
        PlotSettings.bottomInset = bottomInset;
    }

    public static double getRightInset() {
        return rightInset;
    }

    public static void setRightInset(double rightInset) {
        PlotSettings.rightInset = rightInset;
    }

    public static Font getPrimaryFont() {
        return primaryFont;
    }

    public static void setPrimaryFont(Font primaryFont) {
        PlotSettings.primaryFont = primaryFont;
    }

    public static Font getSecondaryFont() {
        return secondaryFont;
    }

    public static void setSecondaryFont(Font secondaryFont) {
        PlotSettings.secondaryFont = secondaryFont;
    }

    public static double getxAxisLabelDistance() {
        return xAxisLabelDistance;
    }

    public static void setxAxisLabelDistance(double xAxisLabelDistance) {
        PlotSettings.xAxisLabelDistance = xAxisLabelDistance;
    }

    public static double getyAxisLabelDistance() {
        return yAxisLabelDistance;
    }

    public static void setyAxisLabelDistance(double yAxisLabelDistance) {
        PlotSettings.yAxisLabelDistance = yAxisLabelDistance;
    }

    public static double getLegendDistance() {
        return legendDistance;
    }

    public static void setLegendDistance(double legendDistance) {
        PlotSettings.legendDistance = legendDistance;
    }

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        PlotSettings.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        PlotSettings.height = height;
    }
}
