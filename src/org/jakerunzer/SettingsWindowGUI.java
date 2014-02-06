package org.jakerunzer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;

public class SettingsWindowGUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsWindowGUI window = new SettingsWindowGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SettingsWindowGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel pnlButtons = new JPanel();
		frame.getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		
		JButton btnApply = new JButton("Apply");
		pnlButtons.add(btnApply);
		
		JButton btnCancel = new JButton("Cancel");
		pnlButtons.add(btnCancel);
		
		JPanel pnlGeneral = new JPanel();
		frame.getContentPane().add(pnlGeneral, BorderLayout.CENTER);
		pnlGeneral.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JCheckBox chckNewline = new JCheckBox("Convert \"\\n\" to \"\\\\\"");
		pnlGeneral.add(chckNewline);
		chckNewline.setSelected(true);
	}

}
