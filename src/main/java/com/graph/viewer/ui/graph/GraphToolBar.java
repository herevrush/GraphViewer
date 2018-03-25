package com.graph.viewer.ui.graph;




import com.graph.viewer.core.GraphController;
import com.graph.viewer.ui.GraphViewer;
import com.graph.viewer.utils.StatusUtils;

import com.graph.viewer.utils.UIUtils;
import org.graphstream.graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The main tool bar.
 */
public class GraphToolBar extends JToolBar {
	private GraphViewer app;

	private JLabel nodesLabel;
	private JLabel edgesLabel;
	private JLabel zoomLevelLabel;
	private GraphController graphController;

	/**
	 * Initializes the tool bar.
	 */
	public GraphToolBar(final GraphViewer app) {

		super("App toolbar", JToolBar.HORIZONTAL);

		this.app = app;
		setRollover(true);
		setVisible(true);
		JButton loadNodes = createNewButton(" Load Nodes ", event ->{
			String filename = UIUtils.getInstance().openFileDialog(app,event.getActionCommand());
			if (filename != null){
				int nodes = app.getGraphPanel().getGraphController().addNodes(filename);
				nodesLabel.setText(String.valueOf(nodes));
				StatusUtils.getInstance(app).setInfoStatus(" nodes = " + app.getGraphPanel().getGraphController().getGraphData().getNodes().size());
			}
//            String fd = "/home/vs/Downloads/data/papers.csv";
		}, " Select Nodes File",null);

		JButton loadEdges = createNewButton(" Load Edges ",  event ->{
			String filename = UIUtils.getInstance().openFileDialog(app,event.getActionCommand());
			if (filename != null){
//				String fd = "/home/vs/Downloads/data/paperlinks.csv";
				try{
					int edges = app.getGraphPanel().getGraphController().addEdges(filename);
					edgesLabel.setText(String.valueOf(edges));
					StatusUtils.getInstance(app).setInfoStatus(" edges = " + app.getGraphPanel().getGraphController().getGraphData().getEdges().size());
				}
				catch (Exception e){
					UIUtils.getInstance().openMessageDialog(app, " Nodes Not Found" , "Please load Graph nodes before loading edges.");
				}
			}
//            String fd = "/home/vs/Downloads/data/papers.csv";
		}, " Select Edges File",null);


		JButton graph = createNewButton(" Generate Graph", event ->{
			try {
				app.getGraphPanel().loadGraph();
				int zoomLevel= app.getGraphPanel().getZoomLevel();
				zoomLevelLabel.setText(String.valueOf(zoomLevel));
				StatusUtils.getInstance(app).setInfoStatus(" Generating Graph ........ " );

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}, " Generate Graph",null);

		JPanel buttonsPanel = new JPanel();

		JButton zoomIn = createNewButton(" Zoom In ", event ->{
			try {
			app.getStatusBar().showProgressBar();
			StatusUtils.getInstance(app).setInfoStatus(" Zooming In........ " );
			ZoomInWorker worker = new ZoomInWorker(app.getGraphPanel().getGraphController());
				try {
					worker.execute();
					worker.get();
				} catch (Exception e1) {
					e1.printStackTrace();

				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			app.getStatusBar().hideProgressBar();
		}, " Zoom In Graph",new ImageIcon("images/magnifier--plus.png"));

		JButton zoomOut = createNewButton(" Zoom Out ", event ->{
			try {
				app.getStatusBar().showProgressBar();
				StatusUtils.getInstance(app).setInfoStatus(" Zooming out........ " );
				app.getGraphPanel().zoomOut();//loadGraph();
				int zoomLevel= app.getGraphPanel().getZoomLevel();
				zoomLevelLabel.setText(String.valueOf(zoomLevel));
				app.getStatusBar().hideProgressBar();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}, " Zoom Out Graph",new ImageIcon("images/magnifier--minus.png"));

		JButton layoutBtn = createNewButton(" Beautify Graph", event ->{
			try {
				app.getGraphPanel().refreshLayout();

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}, " Beautify Graph",null);


		add(loadNodes);
		add(loadEdges);
		addSeparator();
		add(graph);
		addSeparator();
		buttonsPanel.add(zoomIn);
		buttonsPanel.add(zoomOut);
		addSeparator();
		buttonsPanel.add(layoutBtn);
		add(buttonsPanel);

		JPanel nodesPanel = new JPanel();
		nodesPanel.add(new JLabel("Nodes : "));
		nodesLabel = new JLabel(" 0 ");
		nodesPanel.add(nodesLabel);

		addSeparator();
		add(nodesPanel);

		nodesPanel = new JPanel();
		nodesPanel.add(new JLabel("Edges : "));
		edgesLabel = new JLabel(" 0 ");
		nodesPanel.add(edgesLabel);

		addSeparator();
		add(nodesPanel);

		nodesPanel = new JPanel();
		nodesPanel.add(new JLabel("Zoom Level : "));
		zoomLevelLabel = new JLabel(" 0 ");
		nodesPanel.add(zoomLevelLabel);

		addSeparator();
		add(nodesPanel);

	}



	class ZoomInWorker extends SwingWorker<Graph, Void> {

		private GraphController graphController;


		public ZoomInWorker(GraphController graphController) {
			this.graphController = graphController;
//			this.addPropertyChangeListener(new PropertyChangeListener() {
//				@Override
//				public void propertyChange(final PropertyChangeEvent evt) {
//					if (evt.getPropertyName().equals("state")) {
//						if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
//							pd.end();
//						}
//					} else if (evt.getPropertyName().equals("progress")) {
//						pd.repaint();
//					}
//				}
//			});

		}

		@Override
		protected Graph doInBackground() throws Exception {
			try {
				app.getStatusBar().showProgressBar();
				app.getGraphPanel().zoomIn();//loadGraph();
				int zoomLevel= app.getGraphPanel().getZoomLevel();
				zoomLevelLabel.setText(String.valueOf(zoomLevel));
				app.getStatusBar().hideProgressBar();
			} catch (Exception e) {
				e.printStackTrace();
				StatusUtils.getInstance(app).setErrorStatus(e.getMessage());
			}
			return null;
		}
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	/**
	 *
	 * @param text
	 *            The text to show.
	 * @param action
	 *            Action id.
	 * @param toolTipText
	 *            Tool tip text.
	 * @return The created button.
	 */
	public static JButton createNewButton(final String text,
										  final ActionListener action, final String toolTipText, Icon icon) {
		JButton button = new JButton();
		if(icon != null){
			button.setIcon(icon);
		}
		button.setVerticalTextPosition(AbstractButton.CENTER);
		button.setHorizontalTextPosition(AbstractButton.LEADING);
		button.setBackground(Color.ORANGE);
		button.setMargin(new Insets(2,2,2,2));
		button.setText(text);
		button.setToolTipText(toolTipText);
		button.addActionListener(action);
		return button;
	}




}
