package com.graphViewer.main;

import com.graphViewer.main.ui.MainLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.swing.*;
import java.util.logging.Logger;

public class GraphViewer {
    private final static Logger LOGGER = Logger.getLogger(GraphViewer.class.getName());

    private MainLayout mainLayout;

    private void createAndShowGUI() {

        Display display = new Display();
        //Use the Java look and feel.
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                mainLayout = new MainLayout(display);
                mainLayout.showUI();
            }
        });

    }
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        GraphViewer graphViewer = new GraphViewer();
        graphViewer.createAndShowGUI();




    }
}
