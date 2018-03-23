package com.graphViewer.swing.ui;



import com.graphViewer.core.GraphController;
import com.graphViewer.swing.events.Events;
import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GraphPanel extends JSplitPane  {

	/** The swingApp this panel is part of. */
	private GraphViewer app;

	/** The top pane showing the graph. */
	private JScrollPane graphPane;

	/** The graph loaded into the panel. */
	private Graph graph;

	/** The graph's view panel. */
	private ViewPanel view;

	/** The size of the view panel. */
	private Dimension viewSize;

	/** The graph's view pipe. Used to listen for node click events. */
	private ViewerPipe vp;

	/** The info pane below the graph. */
	private JPanel infoPane;



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

		graphPane.getHorizontalScrollBar().setUnitIncrement(400);

	}



	/**
	 * Loads a graph into the content scroll pane.
	 *
	 * @return true iff the graph was loaded successfully.
	 */
	public final boolean loadGraph() {
		boolean ret = true;
		final ProgressDialog pd = new ProgressDialog(app, "Generating Graph...",
				true);
		GraphLoadWorker pw = new GraphLoadWorker(app.getGraphController(), pd);
		pw.execute();
		pd.start();
		try {
			this.graph = pw.get();
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
			ret = false;
		}
		visualizeGraph(graph);

		return ret;
	}




	class GraphLoadWorker extends SwingWorker<Graph, Void> {

		private GraphController graphController;


		public GraphLoadWorker(GraphController graphController,
				final ProgressDialog pd) {
			this.graphController = graphController;

			this.addPropertyChangeListener(evt -> {
                if (evt.getPropertyName().equals("state")) {
                    if (evt.getNewValue() == StateValue.DONE) {
                        pd.end();
                    }
                } else if (evt.getPropertyName().equals("progress")) {
                    pd.repaint();
                }
            });
		}

		@Override
		protected Graph doInBackground() throws Exception {
			try {
				graph = graphController.createNewGraph();
				viewSize = new Dimension(1200,1000);
			} catch (Exception e) {
				e.printStackTrace();
				Events.setErrorStatus(e.getMessage());
			}
			return graph;
		}
	}




	/**
	 * Takes the virtual (GraphStream) graph and shows it in the panel.
	 *
	 * @param vGraph
	 *            The visual graph to draw
	 */
	private void visualizeGraph(final Graph vGraph) {

		Viewer viewer = new Viewer(vGraph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		view = new DefaultView(viewer, Viewer.DEFAULT_VIEW_ID,
				Viewer.newGraphRenderer()) {

			@Override
			public void paintComponent(final Graphics g) {
				super.paintComponent(g);
			}
		};
		viewer.addView(view);
		view.setMinimumSize(viewSize);
		view.setPreferredSize(viewSize);
		view.setMaximumSize(viewSize);
		view.setEnabled(false);

		vp = viewer.newViewerPipe();

		graphPane.setViewportView(view);
		graphPane.getHorizontalScrollBar().setValue(graphPane.getHorizontalScrollBar().getValue());

		this.graph = vGraph;

		vGraph.addAttribute("ui.stylesheet",GraphController.styleSheet);
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
}
