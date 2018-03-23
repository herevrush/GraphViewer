package com.graphViewer.swing;

import com.graphViewer.swing.ui.GraphViewer;

import javax.swing.*;

public class Start {

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GraphViewer();
            }
        });
    }
}
