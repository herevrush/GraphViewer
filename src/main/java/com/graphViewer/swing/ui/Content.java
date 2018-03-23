package com.graphViewer.swing.ui;





import javax.swing.*;
import java.awt.*;


/**
 * A panel representing the content pane of the main app..
 * 
 * @see GraphPanel

 */
public class Content extends JPanel {



	/** Flag to indicate whether a graph has been loaded into the graph panel. */
	private boolean graphLoaded = false;



	/** The app this content pane is part of. */
	private GraphViewer app;


//	/** Content of the graph tab showing most importantly the graph. */
	private GraphPanel graphPanel;

	/**
	 * Initializes the content panel.
	 */
	public Content(final GraphViewer app) {
		this.app = app;
		setLayout(new BorderLayout());

		graphPanel = new GraphPanel(app);

		add(graphPanel, BorderLayout.CENTER);
	}


	public final void loadGraph()
			 {
		graphLoaded = graphPanel.loadGraph();
	}



	@Override
	public final String toString() {
		return this.getClass().toString();
	}



}
