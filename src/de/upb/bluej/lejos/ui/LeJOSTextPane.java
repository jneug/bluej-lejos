package de.upb.bluej.lejos.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class LeJOSTextPane extends JTextPane {

	private static final long serialVersionUID = -7391454769974597473L;

	public static final String DEFAULT_STYLE = StyleContext.DEFAULT_STYLE;
	public static final String ERROR_STYLE = "error";
	public static final String SUCCESS_STYLE = "success";

	private StyledDocument doc;

	public LeJOSTextPane() {
		this.setEditable(false);
		this.setupStyles();
	}

	public void setupStyles() {
		StyleContext context = new StyleContext();
		doc = new DefaultStyledDocument(context);
		this.setStyledDocument(doc);
		this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		Style defaultStyle = getStyle(DEFAULT_STYLE);

		Style error = addStyle(ERROR_STYLE, defaultStyle);
		StyleConstants.setForeground(error, Color.RED);
		
		Style success = addStyle(SUCCESS_STYLE, defaultStyle);
		StyleConstants.setForeground(success, new Color(0,184,46));
	}

	public void clear() {
		this.setText("");
	}
	
	public boolean isEmpty() {
		return getText().trim().isEmpty();
	}

	public void appendText( String text ) {
		this.appendText(text, getStyle(DEFAULT_STYLE));
	}

	public void appendError( String text ) {
		this.appendText(text, getStyle(ERROR_STYLE));
	}

	public void appendSuccess( String text ) {
		this.appendText(text, getStyle(SUCCESS_STYLE));
	}

	public void appendText( String text, AttributeSet style ) {
		try {
			if( isEmpty() )
				doc.insertString(doc.getLength(), text, style);
			else
				doc.insertString(doc.getLength(), "\n" + text, style);
		} catch( BadLocationException ex ) {
		}
	}

	public void captureInputStream( final InputStream ins ) {
		captureInputStream(ins, getStyle(DEFAULT_STYLE));
	}
	
	public void captureInputStream( final InputStream ins, final AttributeSet style ) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner in = new Scanner(ins);

				boolean running = true;
				while( running ) {
					try {
						appendText(in.nextLine(), style);
					} catch( NoSuchElementException | IllegalStateException ex ) {
						running = false;
					}
				}
				in.close();
				in = null;
			}
		}).start();
	}

}
