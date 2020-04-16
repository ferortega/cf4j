package es.upm.etsisi.cf4j.plot;

import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;
import java.awt.*;

public class PlotFrame extends JFrame {

    public PlotFrame(AbstractPlot plot) {
        getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(getContentPane().getMinimumSize());
        setSize(PlotSettings.getWidth(), PlotSettings.getHeight());
    }
}
