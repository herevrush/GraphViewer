package com.graphViewer.swing.ui;

import com.graphViewer.model.StatusMessage;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;


public class StatusBar extends JPanel {


	private JLabel mainLabel = new JLabel();
	private JLabel endLabel = new JLabel();
	JProgressBar jProgressBar;
	/**
	 * Initializes the status bar.
	 */
	public StatusBar() {

		setLayout(new BorderLayout());

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		add(content);


		jProgressBar = new JProgressBar();
		jProgressBar.setIndeterminate(true);
		content.add(jProgressBar, BorderLayout.NORTH);
		jProgressBar.setSize(500, 20);
		jProgressBar.setVisible(false);

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		statusPanel.add(mainLabel, BorderLayout.WEST);
		statusPanel.add(endLabel, BorderLayout.EAST);
		Font font = new Font("Verdana", Font.BOLD, 15);
		mainLabel.setFont(font);
		mainLabel.setForeground(Color.BLUE);
		endLabel.setText("Welcome.");


		content.add(statusPanel,BorderLayout.SOUTH);

		revalidate();
	}


	public void showProgressBar(){
		jProgressBar.setVisible(true);
		repaint();
		revalidate();
	}

	public void hideProgressBar(){
		jProgressBar.setVisible(false);
		repaint();
		revalidate();
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
