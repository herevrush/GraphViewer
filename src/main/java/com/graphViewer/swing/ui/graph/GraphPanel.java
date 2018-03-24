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
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class GraphPanel extends JSplitPane implements ViewerListener,MouseInputListener {

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

//    private ScrollListener scroll;

    private int zoomLevel = 0;


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
        GraphController controller = app.getGraphController();
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
        zoomLevel = 5;
        applyZoomLevel(zoomLevel);
        return ret;
    }


    public int getZoomLevel() {
        return zoomLevel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(" mouse mouseClicked");

    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println(" mouse mousePressed");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println(" mouse released");
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
        System.out.println(" mouse mouseDragged");

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println(" mouse mouseMoved");

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
        Viewer graphStreamViewer = new Viewer(graph,
                Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        graphStreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
//        graphStreamViewer.enableAutoLayout();
        Layout layout = new SpringBox(false);
        layout.setQuality(0.9);
        graphStreamViewer.enableAutoLayout(layout);
//
//        viewPanel = new DefaultView(graphStreamViewer, Viewer.DEFAULT_VIEW_ID,
//                new J2DGraphRenderer());
//        graphStreamViewer.addView(viewPanel);

        viewPanel = graphStreamViewer.addDefaultView(false);
        viewPanel.addMouseListener(this);
        viewPanel.setMinimumSize(viewSize);
        viewPanel.setPreferredSize(viewSize);
        viewPanel.setMaximumSize(viewSize);
//        viewPanel.setEnabled(false);
        viewPanel.getCamera().setViewCenter(0,0,0);
        viewerPipe = graphStreamViewer.newViewerPipe();
        viewerPipe.addViewerListener(this);
        viewerPipe.addSink(graph);

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
//        GraphData graphData = app.getGraphController().getGraphData();
//        String selectedNode = graphData.getSelectedNode();
//        if (selectedNode != null) {
//            Node selected = graph.getNode(String.valueOf(selectedNode));
//            if (selected != null) {
//                selected.setAttribute("ui.class",
//                        selected.getAttribute("oldclass"));
//                selected.setAttribute("oldclass",
//                        selected
//                                .getAttribute("collapsed"));
//            }
//        }
//
//        // Assigns new selected node and stores old ui.class
//        graphData.setSelectedNode(newSelectedNode.getId());
//        newSelectedNode.setAttribute("oldclass",
//                newSelectedNode.getAttribute("ui.class"));
//        newSelectedNode.setAttribute("ui.class", "selected");
//
//        StatusUtils.getInstance(app).setInfoStatus("Selected node: " + newSelectedNode.getId());
    }

    /**
     * Lets you zoom out one level back.
     */
    public void zoomOut() {
        applyZoomLevel(zoomLevel - 1);
    }

//    class ScrollListener implements MouseWheelListener {
//
//        /**
//         * How far there has to be zoomed in to get to a new zoomlevel.
//         **/
//        private static final int NEWLEVEL = 3;
//
//        @Override
//        public void mouseWheelMoved(final MouseWheelEvent e) {
//            int rotation = e.getWheelRotation();
//			System.out.println(" zoomLevel == " + zoomLevel);
//            if (zoomLevel > NEWLEVEL) {
//                zoomOut();
//            } else if (zoomLevel < -NEWLEVEL) {
//                zoomIn();
//            } else if (rotation > 0) {
//                zoomLevel--;
//            } else if (rotation < 0) {
//                zoomLevel++;
//            }
//        }
//
//    }
    @Override
    public void viewClosed(String s) {
        loop = false;
        viewPanel.removeMouseListener(this);
    }

    @Override
    public void buttonPushed(String s) {
        System.out.println("Button pushed on node " + s);
        setSelectedNode(graph.getNode(s));
    }

    @Override
    public void buttonReleased(String s) {
        System.out.println(" Button released" + s);
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


}
