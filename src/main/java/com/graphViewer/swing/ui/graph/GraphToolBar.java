package com.graphViewer.swing.ui.graph;




import com.graphViewer.core.GraphController;

import com.graphViewer.swing.ui.GraphViewer;
import com.graphViewer.swing.ui.ProgressDialog;
import com.graphViewer.swing.utils.StatusUtils;
import com.graphViewer.swing.utils.UIUtils;
import org.graphstream.graph.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.IconUIResource;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
		graphController = new GraphController();
		this.app = app;
		setRollover(true);
		setVisible(true);
		JButton loadNodes = createNewButton(" Load Nodes ", event ->{
			String filename = UIUtils.getInstance().openFileDialog(app,event.getActionCommand());
			if (filename != null){
				int nodes = app.getGraphController().addNodes(filename);
				nodesLabel.setText(String.valueOf(nodes));
				StatusUtils.getInstance(app).setInfoStatus(" nodes = " + app.getGraphController().getGraphData().getNodes().size());
			}
//            String fd = "/home/vs/Downloads/data/papers.csv";
		}, " Select Nodes File",null);

		JButton loadEdges = createNewButton(" Load Edges ",  event ->{
			String filename = UIUtils.getInstance().openFileDialog(app,event.getActionCommand());
			if (filename != null){
//				String fd = "/home/vs/Downloads/data/paperlinks.csv";
				if(app.getGraphController().getGraphData().getNodes() != null){
					int edges = app.getGraphController().addEdges(filename);
					edgesLabel.setText(String.valueOf(edges));
					StatusUtils.getInstance(app).setInfoStatus(" edges = " + app.getGraphController().getGraphData().getEdges().size());
				}
				else{
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
				final ProgressDialog progressDialog = new ProgressDialog(app, "Zooming In Graph...",
						true);
			ZoomInWorker worker = new ZoomInWorker(app.getGraphController(), progressDialog);
				try {
					worker.execute();
					progressDialog.start();

				} catch (Exception e1) {
					e1.printStackTrace();

				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}, " Zoom In Graph",new ImageIcon("images/magnifier--plus.png"));

		JButton zoomOut = createNewButton(" Zoom Out ", event ->{
			try {
				app.getGraphPanel().zoomOut();//loadGraph();
				int zoomLevel= app.getGraphPanel().getZoomLevel();
				zoomLevelLabel.setText(String.valueOf(zoomLevel));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}, " Zoom Out Graph",new ImageIcon("images/magnifier--minus.png"));
		add(loadNodes);
		add(loadEdges);
		addSeparator();
		add(graph);
		addSeparator();
		buttonsPanel.add(zoomIn);
		buttonsPanel.add(zoomOut);
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


		public ZoomInWorker(GraphController graphController,
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
				app.getGraphPanel().zoomIn();//loadGraph();
				int zoomLevel= app.getGraphPanel().getZoomLevel();
				zoomLevelLabel.setText(String.valueOf(zoomLevel));
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
