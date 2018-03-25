package com.graph.viewer.ui.graph;


import com.graph.viewer.core.GraphController;
import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.ui.GraphViewer;
import com.graph.viewer.utils.StatusUtils;

import com.graph.viewer.model.GraphNode;
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
                    app.getToolBar().hideProgressBar();
                }

            } catch (Exception e) {
                e.printStackTrace();
                StatusUtils.getInstance(app).setErrorStatus(e.getMessage());
            }
            return null;
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
                            Thread.sleep(10000);
                            app.getToolBar().hideProgressBar();
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
    }


    public void refreshLayout(){
        graphController.getGraphStreamViewer().enableAutoLayout();
    }
    /**
     * Takes the virtual (GraphStream) graph and shows it in the panel.
     */
    private void visualizeGraph() {
        graphController.visualize((double)zoomLevel/10);

        graphPane.setViewportView(graphController.getGraphStreamViewer().getDefaultView());
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
        GraphNode graphNode = graphData.getNodes().get(graphData.getSelectedNode());
        //add data to infopane


        //Node Details
        nodeDetailsPanel.removeAll();
        nodeDetailsPanel.add(new JLabel(" Node : " + graphNode.getName()));
        graphNode.getProperties().forEach((key,value) -> nodeDetailsPanel.add(new JLabel(key + " : " + value)));

        //incoming edges
        inEdgePanel.removeAll();
        node.getEachEnteringEdge().forEach(edge -> getEdgeDetails(inEdgePanel, edge, graphData));

        outEdgePanel.removeAll();
        node.getEachLeavingEdge().forEach(edge -> getEdgeDetails(outEdgePanel, edge, graphData));
        StatusUtils.getInstance(app).setInfoStatus("Selected node: " + node.getId());
    }

    /**
     * Lets you zoom out one level back.
     */
    public void zoomOut() {
        applyZoomLevel(zoomLevel - 1);
    }

    private void getEdgeDetails(JPanel panel, Edge edge, GraphData graphData) {
//        System.out.println( " Edge == " + edge.getId());
        String edgeId = edge.getId();
        GraphEdge e = graphData.getEdges().get(edgeId);
        e.getProperties().forEach((key, value) -> {
            panel.add (new JLabel(edge.getSourceNode().getId() + " " + key + " : " + value));
        });
    }

//    @Override
//    public void viewClosed(String s) {
//        loop = false;
//        graphStreamViewer.getDefaultView().removeMouseListener(new GraphMouseListener());
//    }
//
//    @Override
//    public void buttonPushed(String s) {
//
//        System.out.println("Button pushed on node " + s);
//        Node node = graphController.getGraph().getNode(s);
//        if(node != null){
//            node.addAttribute("layout.frozen", new Object[0]);
//            setSelectedNode(graphController.getGraph().getNode(s));
//        }
//
//    }
//
//    @Override
//    public void buttonReleased(String s) {
//        System.out.println(" Button released" + s);
//        Node node = graphController.getGraph().getNode(s);
//        if(node != null){
//            node.removeAttribute("layout.frozen");
//        }
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

//    class GraphMouseListener implements MouseInputListener{
//
//        @Override
//        public void mouseClicked(MouseEvent e) {
////            System.out.println(" mouse mouseClicked");
////            viewerPipe.pump();
//
//        }
//
//        @Override
//        public void mousePressed(MouseEvent e) {
//        System.out.println(" mouse mousePressed");
//            graphStreamViewer.getDefaultView().requestFocus();
//            viewerPipe.pump();
//        }
//
//        @Override
//        public void mouseReleased(MouseEvent e) {
//            System.out.println(" mouse released");
//            graphStreamViewer.getDefaultView().requestFocus();
//            viewerPipe.pump();
//
//        }
//
//        @Override
//        public void mouseEntered(MouseEvent e) {
////        System.out.println(" mouse mouseEntered");
//
//        }
//
//        @Override
//        public void mouseExited(MouseEvent e) {
//
//        }
//
//        @Override
//        public void mouseDragged(MouseEvent e) {
////            System.out.println(" mouse mouseDragged");
//
//        }
//
//        @Override
//        public void mouseMoved(MouseEvent e) {
////            System.out.println(" mouse mouseMoved");
//
//        }
//    }

}
