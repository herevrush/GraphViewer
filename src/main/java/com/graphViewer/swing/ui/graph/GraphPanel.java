package com.graphViewer.swing.ui.graph;


import com.graphViewer.core.GraphController;
import com.graphViewer.model.GraphData;

import com.graphViewer.swing.ui.GraphViewer;
import com.graphViewer.swing.ui.ProgressDialog;
import com.graphViewer.swing.utils.StatusUtils;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.DefaultMouseManager;
import org.graphstream.ui.view.util.MouseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class GraphPanel extends JSplitPane {

    /**
     * The swingApp this panel is part of.
     */
    private GraphViewer app;

    /**
     * The top pane showing the graph.
     */
    private JScrollPane graphPane;

    private Graph graph;

    private boolean loop = true;

    private ViewPanel viewPanel;

    private Dimension viewSize;

    private ViewerPipe viewerPipe;

    private JPanel infoPane;

    private ScrollListener scroll;

    private int zoomLevel = 0;


    public GraphPanel(final GraphViewer app) {
        super(JSplitPane.VERTICAL_SPLIT, true);
        this.app = app;

        graphPane = new JScrollPane();
        graphPane.setMinimumSize(new Dimension(2, 2));

        infoPane = new JPanel(new BorderLayout());


        setTopComponent(graphPane);
        setBottomComponent(infoPane);

        setDividerLocation(500);
        setResizeWeight(1);

//		new GraphScrollListener(graphPane.getHorizontalScrollBar());
//		graphPane.getHorizontalScrollBar().setUnitIncrement(400);

    }

    public void applyZoomLevel(final int newZoomLevel) {
        zoomLevel = 0;
        if (newZoomLevel < 0) {
            StatusUtils.getInstance(app).setErrorStatus("There is no zoom level further from the current level");
        } else if (newZoomLevel > 10) {
            StatusUtils.getInstance(app).setErrorStatus("There is no zoom level further from the current level");
        } else {
            zoomLevel = newZoomLevel;
            int threshold;
            if (newZoomLevel == 0) {
                threshold = 100;
            } else {
                threshold = 100 - newZoomLevel * 10;
            }
            visualizeGraph();

            StatusUtils.getInstance(app).setInfoStatus("Zoom level set to: " + zoomLevel);
        }

    }

    /**
     * @param newViewSize The new view size
     */
    private void setViewSize(final Dimension newViewSize) {
        this.viewSize = newViewSize;
    }


    /**
     * Loads a graph into the content scroll pane.
     */
    public final boolean loadGraph() {
        boolean ret = true;
        final ProgressDialog progressDialog = new ProgressDialog(app, "Generating Graph...",
                true);
        GraphController controller = app.getGraphController();
        Worker worker = new Worker(app.getGraphController(), progressDialog);
        try {
            worker.execute();
            progressDialog.start();
            this.graph = worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();
            ret = false;
        }
//		visualizeGraph();
        applyZoomLevel(5);
        return ret;
    }


    class Worker extends SwingWorker<Graph, Void> {

        private GraphController graphController;


        public Worker(GraphController graphController,
                      final ProgressDialog pd) {
            this.graphController = graphController;
            this.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("state")) {
                        if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                            pd.end();
                        }
                    } else if (evt.getPropertyName().equals("progress")) {
                        pd.repaint();
                    }
                }
            });

        }

        @Override
        protected Graph doInBackground() throws Exception {
            try {
                graph = graphController.createNewGraph();
                viewSize = new Dimension(2000, 2000);
                visualizeGraph();
            } catch (Exception e) {
                e.printStackTrace();
                StatusUtils.getInstance(app).setErrorStatus(e.getMessage());
            }
            return graph;
        }
    }


    /**
     * Takes the virtual (GraphStream) graph and shows it in the panel.
     */
    private void visualizeGraph() {
        Viewer graphStreamViewer = new Viewer(graph,
                Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        graphStreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        Layout layout = new SpringBox(false);
        layout.setQuality(0.9);
        graphStreamViewer.enableAutoLayout(layout);
//
//        viewPanel = new DefaultView(graphStreamViewer, Viewer.DEFAULT_VIEW_ID,
//                new J2DGraphRenderer());
//        graphStreamViewer.addView(viewPanel);
        viewPanel = graphStreamViewer.addDefaultView(false);
        viewPanel.setMouseManager(new DefaultMouseManager());
        viewPanel.setMinimumSize(viewSize);
        viewPanel.setPreferredSize(viewSize);
        viewPanel.setMaximumSize(viewSize);
        viewPanel.setEnabled(false);

        viewerPipe = graphStreamViewer.newViewerPipe();
        viewerPipe.addViewerListener(new GraphClickListener());

//
//		graph.addSink(layout);
//		layout.addAttributeSink(graph);
//		try {
//
////			Thread.sleep(200);
//
//			viewerPipe.blockingPump();
//			layout.compute();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


//		while (loop) {
//
//
//
//
//		}

        scroll = new ScrollListener();
        viewPanel.addMouseWheelListener(scroll);

        graphPane.setViewportView(viewPanel);
        graphPane.getHorizontalScrollBar().setValue(graphPane.getHorizontalScrollBar().getValue());


        graph.addAttribute("ui.stylesheet", "url('style.css')");
        app.revalidate();
        centerVertical();

    }


    /**
     * Vertically centers the scroll pane scroll bar.
     */
    private void centerVertical() {
        graphPane.getVerticalScrollBar().setValue(
                graphPane.getVerticalScrollBar().getMaximum());
        graphPane.getVerticalScrollBar().setValue(
                graphPane.getVerticalScrollBar().getValue() / 2);
    }

    @Override
    public final String toString() {
        return this.getClass().getName();
    }

    /**
     * Lets you zoom in one level further.
     */
    public void zoomIn() {
        applyZoomLevel(zoomLevel + 1);
    }


    public final void setSelectedNode(final Node newSelectedNode) {
        GraphData graphData = app.getGraphController().getGraphData();
        String selectedNode = graphData.getSelectedNode();
        if (selectedNode != null) {
            Node selected = graph.getNode(String.valueOf(selectedNode));
            if (selected != null) {
                selected.setAttribute("ui.class",
                        selected.getAttribute("oldclass"));
                selected.setAttribute("oldclass",
                        selected
                                .getAttribute("collapsed"));
            }
        }

        // Assigns new selected node and stores old ui.class
        graphData.setSelectedNode(newSelectedNode.getId());
        newSelectedNode.setAttribute("oldclass",
                newSelectedNode.getAttribute("ui.class"));
        newSelectedNode.setAttribute("ui.class", "selected");

        StatusUtils.getInstance(app).setInfoStatus("Selected node: " + newSelectedNode.getId());
    }

    /**
     * Lets you zoom out one level back.
     */
    public void zoomOut() {
        applyZoomLevel(zoomLevel - 1);
    }

    class ScrollListener implements MouseWheelListener {

        /**
         * How far there has to be zoomed in to get to a new zoomlevel.
         **/
        private static final int NEWLEVEL = 3;

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            int rotation = e.getWheelRotation();
//			System.out.println(" rotation == " + rotation);
            if (zoomLevel > NEWLEVEL) {
                zoomOut();
            } else if (zoomLevel < -NEWLEVEL) {
                zoomIn();
            } else if (rotation > 0) {
                zoomLevel--;
            } else if (rotation < 0) {
                zoomLevel++;
            }
        }

    }

    class GraphClickListener implements ViewerListener {

        @Override
        public void viewClosed(String s) {
            loop = false;
        }

        @Override
        public void buttonPushed(String s) {
            System.out.println(" Button selected" + s);
            setSelectedNode(graph.getNode(s));
        }

        @Override
        public void buttonReleased(String s) {
            System.out.println(" Button released" + s);
        }
    }


    final class GraphScrollListener implements AdjustmentListener {

        /**
         * Initialize the scroll listener and make it observe a given scroll
         * bar.
         *
         * @param scrollBar The scroll bar to observe.
         */
        private GraphScrollListener(final JScrollBar scrollBar) {
            scrollBar.addAdjustmentListener(this);
        }

        @Override
        public void adjustmentValueChanged(final AdjustmentEvent e) {

        }

    }

    class MouseManager extends DefaultMouseManager{

    }
}
