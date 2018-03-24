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
		Font font = new Font("Verdana", Font.BOLD, 15);
		mainLabel.setFont(font);
		mainLabel.setForeground(Color.BLUE);
		endLabel.setText("Welcome.");
	}

	/**
	 * Shows a message in the left text label.

	 */
	public final void setMessage(final String message, final StatusMessage type) {
		if(type == StatusMessage.ERROR){
			Font font = new Font("Verdana", Font.BOLD, 20);
			mainLabel.setFont(font);
			mainLabel.setForeground(Color.RED);
		}
		mainLabel.setText(type.getPrefix() + message);
	}


	public final void writeEnd(final String message) {
		endLabel.setText(message);
	}



}
