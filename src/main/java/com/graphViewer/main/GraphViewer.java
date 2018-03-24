//package com.graphViewer.main;
//
//
//import com.graphViewer.core.GraphController;
//import com.graphViewer.main.ui.MainLayout;
//
//import org.eclipse.swt.widgets.*;
//
//import java.util.logging.Logger;
//
//public class GraphViewer {
//    private final static Logger LOGGER = Logger.getLogger(GraphViewer.class.getName());
//
//    private MainLayout mainLayout;
//
//
//
//    private void createAndShowGUI() {
//        GraphController graphController = new GraphController();
//        Display display = new Display();
//        //Use the Java look and feel.
//        display.syncExec(() -> {
//            mainLayout = new MainLayout(display,graphController);
//            mainLayout.showUI();
//        });
//
//    }
//    public static void main(String[] args) {
//        LOGGER.info(" Starting Application :: GraphViewer ");
//        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
//
//        GraphViewer graphViewer = new GraphViewer();
//        graphViewer.createAndShowGUI();
//
//
//
//
//    }
//}
