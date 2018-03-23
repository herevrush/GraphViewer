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
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class GraphPanel extends JSplitPane  {

	/** The swingApp this panel is part of. */
	private GraphViewer app;

	/** The top pane showing the graph. */
	private JScrollPane graphPane;

	private Graph graph;

	private ViewPanel viewPanel;

	private Dimension viewSize;

	private ViewerPipe viewerPipe;

	private JPanel infoPane;

	private ZoomScrollListener scroll;

	private int zoomLevel = 0;

	/** Diameter of nodes in pixels. */
	private static final int NODE_DIAMETER = 20;


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

		new GraphScrollListener(graphPane.getHorizontalScrollBar());
		graphPane.getHorizontalScrollBar().setUnitIncrement(400);

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
				threshold = 100 - newZoomLevel
						* 10;
			}
//			setViewSize(NodePlacer.plot(graph,app.getGraphController().getGraphData(), viewSize));
//			NodePlacer.placeY(graph,app.getGraphController().getGraphData());
			visualizeGraph();

            StatusUtils.getInstance(app).setInfoStatus("Zoom level set to: " + zoomLevel);
		}

	}

	/**
	 * @param newViewSize
	 *            The new view size
	 */
	private void setViewSize(final Dimension newViewSize) {
		this.viewSize = newViewSize;
	}


	/**
	 * Loads a graph into the content scroll pane.
	 *
	 * @return true iff the graph was loaded successfully.
	 */
	public final boolean loadGraph() {
		boolean ret = true;
		final ProgressDialog progressDialog = new ProgressDialog(app, "Generating Graph...",
				true);
		GraphController controller   =app.getGraphController();
		Worker worker = new Worker(app.getGraphController(), progressDialog);
        worker.execute();
        progressDialog.start();
        try {
            this.graph = worker.get();
        } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
            ret = false;
        }

//		NodePlacer.placeY(this.graph,app.getGraphController().getGraphData());
		visualizeGraph();
		applyZoomLevel(0);
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
//				viewSize = NodePlacer.plot(graph, app.getGraphController().getGraphData());
				viewSize = new Dimension(2000,2000);
//				viewSize = NodePlacer.plot(graph, app.getGraphController().getGraphData(),viewSize);
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
		Viewer graphstreamViewer = new Viewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        graphstreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        graphstreamViewer.enableAutoLayout();

        viewPanel = graphstreamViewer.addDefaultView(false);
//		viewer.addView(view);
        viewPanel.setMinimumSize(viewSize);
        viewPanel.setPreferredSize(viewSize);
        viewPanel.setMaximumSize(viewSize);
        viewPanel.setEnabled(false);

		viewerPipe = graphstreamViewer.newViewerPipe();
		viewerPipe.addViewerListener(new GraphClickListener());

//		Layout layout = new SpringBox(false);
//        layout.setQuality(0.9);
//		graph.addSink(layout);
//		layout.addAttributeSink(graph);


//		boolean loop = true;
//		while (loop) {
//			viewerPipe.pump();
//
//			if (graph.hasAttribute("ui.viewClosed")) {
//				loop = false;
//			} else {
//				try { Thread.sleep(200); } catch (Exception e) {}
//
//				layout.compute();
//			}
//		}

		scroll = new ZoomScrollListener();
        viewPanel.addMouseWheelListener(scroll);

		graphPane.setViewportView(viewPanel);
		graphPane.getHorizontalScrollBar().setValue(graphPane.getHorizontalScrollBar().getValue());



		graph.addAttribute("ui.stylesheet",GraphController.styleSheet);
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
		String selectedNode =graphData.getSelectedNode();
		if(selectedNode != null){
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

	class ZoomScrollListener implements MouseWheelListener {

		/** How far there has to be zoomed in to get to a new zoomlevel. **/
		private static final int NEWLEVEL = 3;

		@Override
		public void mouseWheelMoved(final MouseWheelEvent e) {
			int rotation = e.getWheelRotation();
			if (zoomLevel > NEWLEVEL) {
				zoomOut();
			} else if (zoomLevel < -NEWLEVEL) {
				zoomIn();
			} else if (rotation > 0) {
				zoomLevel++;
			} else if (rotation < 0) {
				zoomLevel--;
			}
		}

	}

	class GraphClickListener implements ViewerListener{

		@Override
		public void viewClosed(String s) {

		}

		@Override
		public void buttonPushed(String s) {
			setSelectedNode(graph.getNode(s));
		}

		@Override
		public void buttonReleased(String s) {

		}
	}

	final class GraphScrollListener implements AdjustmentListener {

		/**
		 * Initialize the scroll listener and make it observe a given scroll
		 * bar.
		 *
		 * @param scrollBar
		 *            The scroll bar to observe.
		 */
		private GraphScrollListener(final JScrollBar scrollBar) {
			scrollBar.addAdjustmentListener(this);
		}

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent e) {

		}

	}
}
