package es.upm.etsisi.cf4j.examples.plotting;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.plot.ColumnPlot;
import es.upm.etsisi.cf4j.plot.PlotSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ColumnPlotExample {

    public static void main(String[] args) throws IOException {
        DataModel datamodel = BenchmarkDataModels.MovieLens1M();

        Map<String, Integer> count = new HashMap<>();
        count.put("1.0", 0);
        count.put("2.0", 0);
        count.put("3.0", 0);
        count.put("4.0", 0);
        count.put("5.0", 0);

        for (User user : datamodel.getUsers()) {
            for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                double rating = user.getRatingAt(pos);
                String key = String.valueOf(rating);
                count.put(key, count.get(key)+1);
            }
        }

        PlotSettings.setyAxisInset(110);
        PlotSettings.setyAxisLabelDistance(3.0);

        ColumnPlot plot = new ColumnPlot("Rating value", "Number of ratings");
        plot.addColumn("1.0", count.get("1.0"));
        plot.addColumn("2.0", count.get("2.0"));
        plot.addColumn("3.0", count.get("3.0"));
        plot.addColumn("4.0", count.get("4.0"));
        plot.addColumn("5.0", count.get("5.0"));

        plot.draw();
        plot.exportPlot("exports/column-plot.png");
        plot.printData("0");
        plot.exportData("exports/column-plot-data.csv");
    }
}
