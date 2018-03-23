package com.graphViewer.swing.ui;



import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog {



	/** The default dialog title. */
	private static final String DEFAULT_TITLE = " In Progress...";

	/** The size of the dialog. */
	private static final Dimension SIZE = new Dimension(400, 120);

	/** The size of the progress bar. */
	private static final Dimension BAR_SIZE = new Dimension(350, 25);



	/**
	 * Initializes a progress dialog relative to a given owner frame.
	 * 
	 *
	 */
	public ProgressDialog(final Frame owner, final String title,
                          final boolean modal) {
		super(owner, title, modal);
		setLocationRelativeTo(owner);
		setSize(SIZE);
		setBackground(Color.ORANGE);
		setResizable(false);
		setLayout(new GridBagLayout());
		/* The progress bar. */
		JProgressBar jProgressBar = new JProgressBar();
		jProgressBar.setIndeterminate(true);
		jProgressBar.setPreferredSize(BAR_SIZE);
		add(jProgressBar);
		revalidate();
	}

	/**
	 * Shows the progress dialog.
	 */
	public void start() {
		setVisible(true);
	}

	/**
	 * Hides and disposes the progress dialog.
	 */
	public final void end() {
		dispose();
	}

}
