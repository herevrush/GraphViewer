package com.graphViewer.swing.ui;




import com.graphViewer.core.GraphController;
import com.graphViewer.swing.events.Events;

import javax.swing.*;
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
		addGeneralButtons();
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


	/** Adds the control elements to the tool bar. */
	private void addGeneralButtons() {

		JButton loadNodes = createNewButton(" Load Nodes ", Events.IMPORT_NODES, " Select Nodes File");

		JButton loadEdges = createNewButton(" Load Edges ", Events.IMPORT_EDGES, " Select Edges File");

		JButton graph = createNewButton(" Generate Graph", Events.GENERATE_GRAPH, " Select Edges File");
		add(loadNodes);
		add(loadEdges);
		addSeparator();
		add(graph);
	}


}
