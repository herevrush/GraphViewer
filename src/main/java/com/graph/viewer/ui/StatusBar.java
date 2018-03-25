package com.graph.viewer.ui;

import com.graph.viewer.model.StatusMessage;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;


public class StatusBar extends JPanel {


	private JLabel mainLabel = new JLabel();
	private JLabel endLabel = new JLabel();
//	JProgressBar jProgressBar;

	private JPanel content;
	/**
	 * Initializes the status bar.
	 */
	public StatusBar() {

		setLayout(new BorderLayout());

		content = new JPanel();
		content.setLayout(new GridBagLayout());
		add(content);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

//		jProgressBar = new JProgressBar();
//		jProgressBar.setIndeterminate(true);
//		content.add(jProgressBar, gbc);
////		jProgressBar.setSize(500, 20);
//		jProgressBar.setVisible(false);

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		statusPanel.add(mainLabel, BorderLayout.WEST);
		statusPanel.add(endLabel, BorderLayout.EAST);
		Font font = new Font("Verdana", Font.BOLD, 15);
		mainLabel.setFont(font);
		mainLabel.setForeground(Color.BLUE);
		endLabel.setText("Welcome.");
		gbc = new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		content.add(statusPanel,gbc);

		revalidate();
	}


//	public void showProgressBar(){
////		content.add(jProgressBar, BorderLayout.NORTH);
////		jProgressBar.setSize(500, 20);
//		jProgressBar.setVisible(true);
////		content.revalidate();
////		content.repaint();
//	}
//
//	public void hideProgressBar(){
////		content.remove(jProgressBar);
//		jProgressBar.setVisible(false);
////		content.revalidate();
//
////		content.repaint();
//	}

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
