package de.upb.bluej.lejos;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class LeJOSStatusFrame extends JFrame {

	private JTextArea jtpText;
	
	public LeJOSStatusFrame( JFrame parent ) {
		this.setLayout(new BorderLayout());
		
		this.jtpText = new JTextArea();
		this.add(jtpText, BorderLayout.CENTER);
		
		this.setPreferredSize(new Dimension(480, 240));
		this.setLocationRelativeTo(parent);
	}
	
	public void addText( String text ) {
		this.jtpText.setText(this.jtpText.getText()+Character.LINE_SEPARATOR+text);
	}
	
}
