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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bluej.extensions.BlueJ;
import de.upb.bluej.lejos.LeJOSDebug;
import de.upb.bluej.lejos.LeJOSDebug.DebugClassItem;
import de.upb.bluej.lejos.LeJOSDebug.DebugMethodItem;

public class LeJOSExtensionUI extends JFrame {

	private static final long serialVersionUID = 7480402637416353261L;

	private LeJOSTextPane statusPane;

	private LeJOSDebug debug;

	private JTextField jtfSearch;

	private JLabel jlClass, jlMethod;

	private String noClassFound, noMethodFound;
	
	private final String searchLabel;

	public LeJOSExtensionUI( LeJOSDebug debug, BlueJ bluej ) {
		super(bluej.getLabel("debug.title"));
		this.setLayout(new BorderLayout());

		this.noClassFound = bluej.getLabel("debug.noClassFound");
		this.noMethodFound = bluej.getLabel("debug.noMethodFound");

		this.debug = debug;

		this.statusPane = new LeJOSTextPane();
		this.statusPane.setPreferredSize(new Dimension(600, 120));
		this.statusPane.setMinimumSize(new Dimension(320, 60));

		JScrollPane jspScroll = new JScrollPane(this.statusPane);

		searchLabel = bluej.getLabel("debug.search.placeholder");
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
						updateLabels();
					}

					@Override
					public void removeUpdate( DocumentEvent e ) {
						updateLabels();
					}

					@Override
					public void changedUpdate( DocumentEvent e ) {
					}
				});

		this.jlClass = new JLabel();
		this.jlMethod = new JLabel();

		JLabel jlClassLabel = new JLabel(bluej.getLabel("debug.label.class"));
		JLabel jlMethodLabel = new JLabel(bluej.getLabel("debug.label.method"));

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
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
										.addComponent(jlClassLabel)
										.addComponent(jlClass))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
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

	public void updateLabels() {
		this.updateLabels(null);
	}
	
	public void updateLabels( String filter ) {
		if( filter == null ) {
			filter = this.jtfSearch.getText().trim();
		}
		
		if( filter.isEmpty() || filter.equals(searchLabel) ) {
			this.jlClass.setText("");
			this.jlMethod.setText("");
			return;
		}

		DebugClassItem dci = debug.getClassItem(filter);
		if( dci != null )
			this.jlClass.setText(dci.name);
		else
			this.jlClass.setText(String.format(noClassFound, filter));

		DebugMethodItem dmi = debug.getMethodItem(filter);
		if( dmi != null )
			this.jlMethod.setText(dmi.name);
		else
			this.jlMethod.setText(String.format(noMethodFound, filter));
	}

}
