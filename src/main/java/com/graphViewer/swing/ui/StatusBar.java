package com.graphViewer.swing.ui;

import com.graphViewer.model.StatusMessage;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;


public class StatusBar extends JPanel {


	private JLabel mainLabel = new JLabel();
	private JLabel endLabel = new JLabel();

	/**
	 * Initializes the status bar.
	 */
	public StatusBar() {
		setLayout(new BorderLayout());
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(mainLabel, BorderLayout.WEST);
		add(endLabel, BorderLayout.EAST);
		endLabel.setText("Welcome.");
	}

	/**
	 * Shows a message in the left text label.

	 */
	public final void setMessage(final String message, final StatusMessage type) {
		mainLabel.setText(type.getPrefix() + message);
	}


	public final void writeEnd(final String message) {
		endLabel.setText(message);
	}



}
