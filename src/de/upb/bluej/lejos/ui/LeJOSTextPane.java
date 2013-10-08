package de.upb.bluej.lejos.ui;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JTextPane;

public class LeJOSTextPane extends JTextPane {

	private static final long serialVersionUID = -7391454769974597473L;
	

	public LeJOSTextPane() {
		this.setEditable(false);
	}
	
	public void captureInputStream( final InputStream ins ) {
		new Thread( new Runnable() {
			@Override
			public void run() {
				Scanner in = new Scanner(ins);
				
				boolean running = true;
				while( running ) {
					try {
						appendText(in.nextLine());
					} catch( NoSuchElementException | IllegalStateException ex ) {
						running = false;
					}
				}
				in.close();
				in = null;
			}
		}).start();
	}
	
	public void clear() {
		this.setText("");
	}
	
	public void appendText( String text ) {
		String t = this.getText();
		if( t.isEmpty() )
			this.setText(text);
		else
			this.setText(t+"\n"+text);
	}
	
}
