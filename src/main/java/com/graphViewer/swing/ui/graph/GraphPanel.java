package com.graphViewer.swing.ui.graph;


import com.graphViewer.core.GraphController;
import com.graphViewer.model.GraphData;

import com.graphViewer.model.GraphEdge;
import com.graphViewer.model.GraphNode;
import com.graphViewer.swing.ui.GraphViewer;
import com.graphViewer.swing.ui.ProgressDialog;
import com.graphViewer.swing.utils.StatusUtils;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.swingViewer.basicRenderer.SwingBasicGraphRenderer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class GraphPanel extends JSplitPane implements ViewerListener {

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

    private Viewer graphStreamViewer;

    private Dimension viewSize;

    private ViewerPipe viewerPipe;

    private JPanel infoPane;

//    private ScrollListener scroll;

    private int zoomLevel = 0;

    private Layout layout;

    public GraphPanel(final GraphViewer app) {
        super(JSplitPane.VERTICAL_SPLIT, true);
        this.app = app;
        viewSize = new Dimension(2000, 2000);
        graphPane = new JScrollPane();
        graphPane.setMinimumSize(new Dimension(2, 2));

        infoPane = new JPanel(new BorderLayout());


        setTopComponent(graphPane);
        setBottomComponent(infoPane);

        setDividerLocation(500);
        setResizeWeight(1);

		new GraphScrollListener(graphPane.getHorizontalScrollBar());
		graphPane.getHorizontalScrollBar().setUnitIncrement(100);
        graphPane.getVerticalScrollBar().setUnitIncrement(100);

    }

    public boolean applyZoomLevel(int newZoomLevel) {
        final ProgressDialog progressDialog = new ProgressDialog(app, "Generating Graph...",
                true);
        ApplyZoomWorker worker = new ApplyZoomWorker(newZoomLevel, progressDialog);
        try {
            worker.execute();
            progressDialog.start();
            worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
        return false;
    }




    /**
     * Loads a graph into the content scroll pane.
     */
    public final boolean loadGraph() {
        boolean ret = true;
        zoomLevel = 8;
        final ProgressDialog progressDialog = new ProgressDialog(app, "Generating Graph...",
                true);
        GraphController controller = app.getGraphController();
        CreateGraphWorker worker = new CreateGraphWorker(app.getGraphController(), progressDialog);
        try {
            worker.execute();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressDialog.start();
                }
            });
            this.graph = worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();
            ret = false;
        }
        return ret;
    }


    public int getZoomLevel() {
        return zoomLevel;
    }



    class ApplyZoomWorker extends  SwingWorker< Void ,Void> {
        private ProgressDialog pd;
        private int newZoomLevel;

        public ApplyZoomWorker(int newZoomLevel,
                                 final ProgressDialog pd) {
            this.newZoomLevel = newZoomLevel;
            this.pd = pd;
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
        protected Void doInBackground() throws Exception {
            try {
                if (newZoomLevel < 0) {
                    StatusUtils.getInstance(app).setErrorStatus("There is no zoom level further from the current level");
                } else if (newZoomLevel > 10) {
                    StatusUtils.getInstance(app).setErrorStatus("There is no zoom level further from the current level");
                } else {
                    zoomLevel = newZoomLevel;
                    visualizeGraph();
                    StatusUtils.getInstance(app).setInfoStatus("Zoom level set to: " + zoomLevel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                StatusUtils.getInstance(app).setErrorStatus(e.getMessage());
            }
            return null;
        }

    }
    class CreateGraphWorker extends SwingWorker<Graph, Void> {

        private GraphController graphController;
        private ProgressDialog pd;


        public CreateGraphWorker(GraphController graphController,
                      final ProgressDialog pd) {
            this.graphController = graphController;
            this.pd = pd;
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
                visualizeGraph();
                graphController.generateGraph(graph);
//                while (loop) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                layout.compute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
//                }
//                this.done();
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
        graphStreamViewer = new Viewer(graph,
                Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        graphStreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
//        graphStreamViewer.enableAutoLayout();
        layout = new SpringBox(false);

        graph.addSink(layout);
        layout.addAttributeSink(graph);
        layout.setQuality(0.9);
//        graphStreamViewer.enableAutoLayout(layout);
//
        ViewPanel viewPanel = new DefaultView(graphStreamViewer, Viewer.DEFAULT_VIEW_ID,
                new SwingBasicGraphRenderer());
        graphStreamViewer.addView(viewPanel);

//        viewPanel = graphStreamViewer.addDefaultView(false);
        viewPanel.addMouseListener(new GraphMouseListener());
        viewPanel.setMinimumSize(viewSize);
        viewPanel.setPreferredSize(viewSize);
        viewPanel.setMaximumSize(viewSize);
//        viewPanel.setEnabled(false);
        viewPanel.getCamera().setViewPercent((double)zoomLevel/10);
        viewPanel.getCamera().setViewCenter(0,0,0);
        viewerPipe = graphStreamViewer.newViewerPipe();
        viewerPipe.addViewerListener(this);
        viewerPipe.addSink(graph);




//
//
//        scroll = new ScrollListener();
//        viewPanel.addMouseWheelListener(scroll);

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


    private void setSelectedNode(final Node newSelectedNode) {

        GraphData graphData = app.getGraphController().getGraphData();
        System.out.println( "Selected Node : " +  newSelectedNode.getId());
        if(newSelectedNode != null) {
            String previousNode = graphData.getSelectedNode();
            if (previousNode != null) {
                Node selected = graph.getNode(String.valueOf(previousNode));
                if (selected != null) {
                    //remove previous node
                    if (selected.hasAttribute("ui.selected")) {
                        selected.removeAttribute("ui.selected");
                    }
                }
            }
            if (!newSelectedNode.hasAttribute("ui.selected")) {
                newSelectedNode.addAttribute("ui.selected", new Object[0]);
            }
            graphData.setSelectedNode(newSelectedNode.getId());
            updateInfo(newSelectedNode, graphData);

        }
        StatusUtils.getInstance(app).setInfoStatus("Selected node: " + newSelectedNode.getId());
    }

    private void updateInfo(Node node, GraphData graphData){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GraphNode graphNode = graphData.getNodes().get(graphData.getSelectedNode());
                //add data to infopane
                infoPane.setLayout(new BorderLayout());

//                JTabbedPane tabs = new JTabbedPane();
//                infoPane.add(tabs, BorderLayout.CENTER);

                //Node Details
                JList nodeDetailsList = new JList();
                final DefaultListModel model = new DefaultListModel();
                nodeDetailsList.setModel(model);
                model.addElement(graphNode.getName());
                graphNode.getProperties().forEach((key,value) ->{
                    model.addElement(key + " : " + value);
                });
                JScrollPane detailsPane = new JScrollPane(nodeDetailsList);

//                tabs.add(" Node Details",detailsPane);

                infoPane.add(detailsPane, BorderLayout.EAST);
                //incoming edges
                nodeDetailsList = new JList();
                DefaultListModel edgeModel = new DefaultListModel();
                node.getEachEdge().forEach(edge -> {
                    String edgeId = edge.getId();
                    GraphEdge e = graphData.getEdges().get(edgeId);
                    e.getProperties().forEach((key, value) -> {
                        edgeModel.addElement(key + " : " + value);
                    });

                });
                JScrollPane edgesPane = new JScrollPane(nodeDetailsList);
                infoPane.add(edgesPane, BorderLayout.WEST);
//                tabs.add(" Edge Details",edgesPane);




            }
        });

        System.out.println( " ");
    }
    /**
     * Lets you zoom out one level back.
     */
    public void zoomOut() {
        applyZoomLevel(zoomLevel - 1);
    }


    @Override
    public void viewClosed(String s) {
        loop = false;
        graphStreamViewer.getDefaultView().removeMouseListener(new GraphMouseListener());
    }

    @Override
    public void buttonPushed(String s) {

        System.out.println("Button pushed on node " + s);
        Node node = graph.getNode(s);
        if(node != null){
            node.addAttribute("layout.frozen", new Object[0]);
            setSelectedNode(graph.getNode(s));
        }

    }

    @Override
    public void buttonReleased(String s) {
        System.out.println(" Button released" + s);
        Node node = graph.getNode(s);
        if(node != null){
            node.removeAttribute("layout.frozen");
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

    class GraphMouseListener implements MouseInputListener{

        @Override
        public void mouseClicked(MouseEvent e) {
//            System.out.println(" mouse mouseClicked");
//            viewerPipe.pump();

        }

        @Override
        public void mousePressed(MouseEvent e) {
//        System.out.println(" mouse mousePressed");
            graphStreamViewer.getDefaultView().requestFocus();
            viewerPipe.pump();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
//            System.out.println(" mouse released");
            graphStreamViewer.getDefaultView().requestFocus();
            viewerPipe.pump();

        }

        @Override
        public void mouseEntered(MouseEvent e) {
//        System.out.println(" mouse mouseEntered");

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
//            System.out.println(" mouse mouseDragged");

        }

        @Override
        public void mouseMoved(MouseEvent e) {
//            System.out.println(" mouse mouseMoved");

        }
    }

}
