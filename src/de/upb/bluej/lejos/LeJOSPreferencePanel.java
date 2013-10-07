package de.upb.bluej.lejos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LeJOSPreferencePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -976118885935818394L;
	

	public final JTextField jtfNxjHome;

	public final JList<LeJOSDistribution> jlVersion;

	public LeJOSPreferencePanel() {
		//super(new GridLayout(2,2));
		
		this.jtfNxjHome = new JTextField(40);

		this.add(new JLabel("leJOS Folder"));
		this.add(this.jtfNxjHome);
		
		JButton jbBrowse = new JButton("...");
		this.add(jbBrowse);
		jbBrowse.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ) {
				JFileChooser jfcBrowser;
				
				File dir = new File(jtfNxjHome.getText());
				if( dir.getParentFile() != null && dir.getParentFile().exists() )
					jfcBrowser = new JFileChooser(dir.getParentFile());
				else
					jfcBrowser = new JFileChooser();
//				jfcBrowser.setAcceptAllFileFilterUsed(false);
				jfcBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int returnVal = jfcBrowser.showOpenDialog(LeJOSPreferencePanel.this.getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            String path = jfcBrowser.getSelectedFile().getAbsolutePath();
		            jtfNxjHome.setText(path);
		        }
			}
		});
		
		JButton jbGetNxjHome = new JButton("$NXJ_HOME");
		this.add(jbGetNxjHome);
		jbGetNxjHome.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ) {
				String nxjHome = LeJOSUtils.getNxjHomeEnv();
				if( !nxjHome.isEmpty() )
					jtfNxjHome.setText(nxjHome);
			}
		});

		this.jlVersion = new JList<LeJOSDistribution>(LeJOSDistribution.getSupportedLeJOSVersions());
		this.jlVersion.setVisibleRowCount(1);

		this.add(new JLabel("leJOS Version"));
		this.add(this.jlVersion);
	}

}
