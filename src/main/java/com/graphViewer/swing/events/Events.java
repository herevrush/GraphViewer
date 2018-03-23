package com.graphViewer.swing.events;



import com.graphViewer.model.StatusMessage;
import com.graphViewer.swing.ui.GraphViewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Vrushali Satpute
 */
public enum Events implements ActionListener {


    IMPORT_NODES {
        @Override
        public void actionPerformed(final ActionEvent e) {
            String fd = "/home/vs/Downloads/data/papers.csv";
            int nodes = app.getGraphController().addNodes(fd);
            setInfoStatus(" nodes = " + app.getGraphController().getGraphData().getNodes().size());
        }
    },
    IMPORT_EDGES {
        @Override
        public void actionPerformed(final ActionEvent e) {
            String fd = "/home/vs/Downloads/data/paperlinks.csv";
            int nodes = app.getGraphController().addEdges(fd);
            setInfoStatus(" edges = " + app.getGraphController().getGraphData().getEdges().size());
        }
    },
    GENERATE_GRAPH {
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                app.getContent().loadGraph();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    };



    /**
     * The app effected by the actions performed by these events.
     */
    private static GraphViewer app;


    public static void setApp(final GraphViewer swingApp) {
        app = swingApp;
    }

    public static void setErrorStatus(final String message) {
        if (app != null) {
            app.getStatusBar().setMessage(message, StatusMessage.ERROR);
        }
    }

    public static void setEndMessage(final String message) {
        if (app != null) {
            app.getStatusBar().writeEnd(message);
        }
    }

    public static void setInfoStatus(final String message) {
        if (app != null) {
            app.getStatusBar().setMessage(message, StatusMessage.INFO);
        }
    }
}
