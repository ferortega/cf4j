package es.upm.etsisi.cf4j.plot;

import java.io.IOException;

public abstract class Plot {

    public void exportData(String filename) throws IOException {
        this.exportData(filename, ",");
    }

    public abstract void exportData(String filename, String separator) throws IOException;


    public void printData() {
        this.printData("0.0000");
    }

    public void printData(String numberFormat) {
        this.printData(numberFormat, numberFormat);
    }

    public abstract void printData(String xFormat, String yFormat);
    public abstract void draw();
    public abstract void exportPlot(String filename) throws IOException;
}
