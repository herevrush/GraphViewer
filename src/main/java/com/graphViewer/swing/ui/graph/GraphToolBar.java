package com.graphViewer.swing.ui.graph;




import com.graphViewer.core.GraphController;

import com.graphViewer.swing.ui.GraphViewer;
import com.graphViewer.swing.utils.StatusUtils;
import com.graphViewer.swing.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The main tool bar.
 */
public class GraphToolBar extends JToolBar {
	private GraphViewer app;


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
				StatusUtils.getInstance(app).setInfoStatus(" nodes = " + app.getGraphController().getGraphData().getNodes().size());
			}
//            String fd = "/home/vs/Downloads/data/papers.csv";
		}, " Select Nodes File");

		JButton loadEdges = createNewButton(" Load Edges ",  event ->{
			String filename = UIUtils.getInstance().openFileDialog(app,event.getActionCommand());
			if (filename != null){
//				String fd = "/home/vs/Downloads/data/paperlinks.csv";
				if(app.getGraphController().getGraphData().getNodes() != null){
					int edges = app.getGraphController().addEdges(filename);
					StatusUtils.getInstance(app).setInfoStatus(" edges = " + app.getGraphController().getGraphData().getEdges().size());
				}
				else{
					UIUtils.getInstance().openMessageDialog(app, " Nodes Not Found" , "Please load Graph nodes before loading edges.");
				}
			}
//            String fd = "/home/vs/Downloads/data/papers.csv";
		}, " Select Edges File");

		JButton graph = createNewButton(" Generate Graph", event ->{
			try {
				app.getGraphPanel().loadGraph();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}, " Select Edges File");
		add(loadNodes);
		add(loadEdges);
		addSeparator();
		add(graph);


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
										  final ActionListener action, final String toolTipText) {
		JButton button = new JButton();
		button.setVerticalTextPosition(AbstractButton.CENTER);
		button.setHorizontalTextPosition(AbstractButton.LEADING);
		button.setBackground(Color.ORANGE);
		button.setMargin(new Insets(2,2,2,2));
		button.setText(text);
		button.setToolTipText(toolTipText);
		button.addActionListener(action);
		return button;
	}


	public static JButton createNewButton(Icon icon,
										  final ActionListener action, final String toolTipText) {
		JButton button = new JButton();
		button.setIcon(icon);
		button.setToolTipText(toolTipText);
		button.addActionListener(action);
		return button;
	}


}
