package com.graph.viewer.ui.graph;


import com.graph.viewer.core.GraphController;
import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.ui.GraphViewer;
import com.graph.viewer.utils.StatusUtils;

import com.graph.viewer.model.GraphNode;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.basicRenderer.SwingBasicGraphRenderer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GraphPanel extends JSplitPane  {

    /**
     * The swingApp this panel is part of.
     */
    private GraphViewer app;

    /**
     * The top pane showing the graph.
     */
    private JScrollPane graphPane;

    private JPanel nodeDetailsPanel;

    private JPanel inEdgePanel;

    private JPanel outEdgePanel;

    private int zoomLevel = 0;

    private Node selectedNode;

    private GraphController graphController;


    public GraphPanel(final GraphViewer app) {
        super(JSplitPane.VERTICAL_SPLIT, true);
        graphController = new GraphController(this);
        this.app = app;
        graphPane = new JScrollPane();
        graphPane.setMinimumSize(new Dimension(2, 2));

        JPanel infoPane = new JPanel(new BorderLayout());
        initInfoPane(infoPane);


        setTopComponent(graphPane);
        setBottomComponent(infoPane);

        setDividerLocation(450);
        setResizeWeight(1);

		new GraphScrollListener(graphPane.getHorizontalScrollBar());
		graphPane.getHorizontalScrollBar().setUnitIncrement(100);
        graphPane.getVerticalScrollBar().setUnitIncrement(100);

    }

    public GraphController getGraphController() {
        return graphController;
    }

    private void initInfoPane(JPanel infoPane){
        JTabbedPane content = new JTabbedPane();
        infoPane.add(content);

        nodeDetailsPanel = new JPanel();
        nodeDetailsPanel.setLayout(new GridLayout(10,1));
        content.add("Node Details: " ,nodeDetailsPanel);


        inEdgePanel = new JPanel();
        inEdgePanel.setLayout(new GridLayout(150,1));
        JScrollPane pane = new JScrollPane(inEdgePanel);
        content.add("Referred By" ,pane);


        outEdgePanel = new JPanel();
        outEdgePanel.setLayout(new GridLayout(100,1));
        pane = new JScrollPane(outEdgePanel);
        content.add("References",pane);

    }

    private void applyZoomLevel(int newZoomLevel) {
        ApplyZoomWorker worker = new ApplyZoomWorker(newZoomLevel);
        try {
            worker.execute();
            worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();

        }

    }




    /**
     * Loads a graph into the content scroll pane.
     */
    public boolean loadGraph() {
        boolean ret = true;
        zoomLevel = 6;

//        app.getStatusBar().showProgressBar();
        CreateGraphWorker worker = new CreateGraphWorker(graphController);
        try {
            worker.execute();
            worker.get();
        } catch (Exception e1) {
            ret = false;
        }
//        app.getStatusBar().hideProgressBar();
        return ret;
    }


    public int getZoomLevel() {
        return zoomLevel;
    }



    class ApplyZoomWorker extends  SwingWorker< Void ,Void> {

        private int newZoomLevel;

        ApplyZoomWorker(int newZoomLevel) {
            this.newZoomLevel = newZoomLevel;
            this.addPropertyChangeListener(evt -> {
                if (evt.getPropertyName().equals("state")) {
                    if (evt.getNewValue() == StateValue.DONE) {
//                        app.getStatusBar().hideProgressBar();

                    }
                } else if (evt.getPropertyName().equals("progress")) {
//                    app.getToolBar().showProgressBar();
                }
            });

        }

        @Override
        protected Void doInBackground() {
            try {
                if (newZoomLevel < 0) {
                    StatusUtils.getInstance(app).setErrorStatus("There is no zoom level further from the current level");
                } else if (newZoomLevel > 10) {
                    StatusUtils.getInstance(app).setErrorStatus("There is no zoom level further from the current level");
                } else {
                    zoomLevel = newZoomLevel;
                    visualizeGraph();
                    StatusUtils.getInstance(app).setInfoStatus("Zoom level set to: " + zoomLevel);
//                    Thread.sleep(10000);
                }

            } catch (Exception e) {
                e.printStackTrace();
                StatusUtils.getInstance(app).setErrorStatus(e.getMessage());
            }
            return null;
        }

        @Override
        protected void done() {
            app.getToolBar().hideProgressBar();
            super.done();
        }
    }
    class CreateGraphWorker extends SwingWorker<Void, Void> {

        private GraphController graphController;


        CreateGraphWorker(GraphController graphController) {
            this.graphController = graphController;
            this.addPropertyChangeListener(evt -> {
                if (evt.getPropertyName().equals("state")) {
                    if (evt.getNewValue() == StateValue.DONE) {
//                        app.getStatusBar().hideProgressBar();
                    }
                } else if (evt.getPropertyName().equals("progress")) {
//                    app.getToolBar().showProgressBar();
                }
            });

        }


        @Override
        protected Void doInBackground() {
            try {
                graphController.createNewGraph();
                visualizeGraph();
                graphController.generateGraph();
//                while (loop) {
                    SwingUtilities.invokeLater(() -> {

                        try {
                            graphController.getLayout().compute();
//                            Thread.sleep(10000);
//                            app.getToolBar().hideProgressBar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
//                }

            } catch (Exception e) {
                e.printStackTrace();
                StatusUtils.getInstance(app).setErrorStatus(e.getMessage());
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();
            SwingUtilities.invokeLater(() -> {

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                app.getToolBar().hideProgressBar();
            });

        }
    }


    public void refreshLayout(){
        graphController.getGraphStreamViewer().getDefaultView().setVisible(false);
        graphController.getGraphStreamViewer().getDefaultView().setSize(1000,1000);
//        this.graphPane.repaint();
        graphController.getGraphStreamViewer().getDefaultView().setVisible(true);
//        this.graphPane.getViewport().repaint();

//        System.out.println(" layout == " + System.getProperty("gs.ui.layout"));
//        System.out.println(" layout == " + System.getProperty("org.graphstream.ui.layout"));
        graphController.getGraphStreamViewer().enableAutoLayout();
    }
    /**
     * Takes the virtual (GraphStream) graph and shows it in the panel.
     */
    private void visualizeGraph() {
        graphController.visualize((double)zoomLevel/10);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(graphController.getGraphStreamViewer().getDefaultView(), "Center");
        panel.setSize(2500, 2500);
        graphPane.setViewportView(panel);

        graphPane.getHorizontalScrollBar().setValue(graphPane.getHorizontalScrollBar().getValue());

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




    public void updateInfo(Node node, GraphData graphData){
        this.selectedNode = node;
        GraphNode graphNode = graphData.getNodes().get(graphData.getSelectedNode());
        //add data to infopane


        //Node Details
        nodeDetailsPanel.removeAll();
        nodeDetailsPanel.add(new JLabel(" Node : " + graphNode.getName()));
        graphNode.getProperties().forEach((key,value) -> nodeDetailsPanel.add(new JLabel(key + " : " + value)));

        //incoming edges
        inEdgePanel.removeAll();
        node.getEachEnteringEdge().forEach(edge -> {
            String edgeId = edge.getId();
            GraphEdge e = graphData.getEdges().get(edgeId);
            e.getProperties().forEach((key, value) -> {
                inEdgePanel.add (new JLabel(edge.getSourceNode().getId() + " " + key + " : " + value));
            });
        });

        outEdgePanel.removeAll();
        node.getEachLeavingEdge().forEach(edge -> {
            String edgeId = edge.getId();
            GraphEdge e = graphData.getEdges().get(edgeId);
            e.getProperties().forEach((key, value) -> {
                outEdgePanel.add (new JLabel(edge.getTargetNode().getId() + " " + key + " : " + value));
            });
        });
        StatusUtils.getInstance(app).setInfoStatus("Selected node: " + node.getId());
    }

    /**
     * Lets you zoom out one level back.
     */
    public void zoomOut() {
        applyZoomLevel(zoomLevel - 1);
    }

//    private void getEdgeDetails(JPanel panel, Edge edge, GraphData graphData) {
////        System.out.println( " Edge == " + edge.getId());
//        String edgeId = edge.getId();
//        GraphEdge e = graphData.getEdges().get(edgeId);
//        e.getProperties().forEach((key, value) -> {
//            panel.add (new JLabel(edge.getSourceNode().getId() + " " + key + " : " + value));
//        });
//    }




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



}
