package de.upb.bluej.lejos.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.upb.bluej.lejos.LeJOSDebug;
import de.upb.bluej.lejos.LeJOSDebug.DebugClassItem;
import de.upb.bluej.lejos.LeJOSDebug.DebugMethodItem;

public class LeJOSExtensionUI extends JFrame {

	private static final long serialVersionUID = 7480402637416353261L;

	private LeJOSTextPane statusPane;
	
	private LeJOSDebug debug;

	private JTextField jtfSearch;

	private JLabel jlClass, jlMethod;
	
	public LeJOSExtensionUI( LeJOSDebug debug ) {
		super("Debug Panel");
		this.setLayout(new BorderLayout());

		this.debug = debug;
		
		this.statusPane = new LeJOSTextPane();
		this.statusPane.setPreferredSize(new Dimension(600, 120));
		this.statusPane.setMinimumSize(new Dimension(320, 60));
		
		JScrollPane jspScroll = new JScrollPane(this.statusPane);
		
		final String searchLabel = "Enter class or method number ...";
		this.jtfSearch = new JTextField(searchLabel);
		final Color fg = this.jtfSearch.getForeground();
		this.jtfSearch.setForeground(Color.GRAY);
		this.jtfSearch.addFocusListener(new FocusListener() {
			@Override
			public void focusLost( FocusEvent e ) {
			}
			@Override
			public void focusGained( FocusEvent e ) {
				if( jtfSearch.getText().equals(searchLabel) ) {
					jtfSearch.setText("");
					jtfSearch.setForeground(fg);
				}
			}
		});
		this.jtfSearch.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void insertUpdate( DocumentEvent e ) {
						updateLabels(jtfSearch.getText());
					}

					@Override
					public void removeUpdate( DocumentEvent e ) {
						updateLabels(jtfSearch.getText());
					}

					@Override
					public void changedUpdate( DocumentEvent e ) {
					}
				});

		this.jlClass = new JLabel();
		this.jlMethod = new JLabel();
		
		JLabel jlClassLabel = new JLabel("Class:");
		JLabel jlMethodLabel = new JLabel("Method:");
		
		JPanel mainPanel = new JPanel();
		GroupLayout layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(jspScroll)
					.addComponent(jtfSearch)
					.addGroup(layout.createSequentialGroup()
							.addComponent(jlClassLabel)
							.addComponent(jlClass))
					.addGroup(layout.createSequentialGroup()
							.addComponent(jlMethodLabel)
							.addComponent(jlMethod))		
				);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(jspScroll)
					.addComponent(jtfSearch,
							GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE,
					        GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(jlClassLabel)
							.addComponent(jlClass))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(jlMethodLabel)
							.addComponent(jlMethod))
				);

		this.add(mainPanel, BorderLayout.CENTER);
		this.updateLabels("");
		this.pack();
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	public LeJOSTextPane getStatusPane() {
		return this.statusPane;
	}
	
	public void updateLabels( String filter ) {
		if( filter.isEmpty() ) {
			this.jlClass.setText("");
			this.jlMethod.setText("");
			return;
		}
		
		DebugClassItem dci = debug.getClassItem(filter);
		if( dci != null )
			this.jlClass.setText(dci.name);
		else
			this.jlClass.setText("No class found for "+filter);
	
		DebugMethodItem dmi = debug.getMethodItem(filter);
		if( dmi != null )
			this.jlMethod.setText(dmi.name);
		else
			this.jlMethod.setText("No method found for "+filter);
	}
	
	public static void main( String[] args ) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LeJOSExtensionUI ui = new LeJOSExtensionUI(null);
				ui.setVisible(true);
				ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
	
}
