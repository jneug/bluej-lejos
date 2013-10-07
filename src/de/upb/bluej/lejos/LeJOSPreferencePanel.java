package de.upb.bluej.lejos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LeJOSPreferencePanel extends JPanel {

	private static final long serialVersionUID = -976118885935818394L;


	public final JTextField jtfNxjHome;

	public final JComboBox<LeJOSDistribution> jcbVersion;

	public LeJOSPreferencePanel() {
		JLabel jlNxjHome = new JLabel("leJOS Folder");
		this.jtfNxjHome = new JTextField(40);

		JButton jbBrowse = new JButton("...");
		jbBrowse.addActionListener(new ActionListener() {
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

				int returnVal = jfcBrowser
						.showOpenDialog(LeJOSPreferencePanel.this.getParent());
				if( returnVal == JFileChooser.APPROVE_OPTION ) {
					String path = jfcBrowser.getSelectedFile()
							.getAbsolutePath();
					jtfNxjHome.setText(path);
				}
			}
		});

		JButton jbGetNxjHome = new JButton("$NXJ_HOME");
		jbGetNxjHome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				String nxjHome = LeJOSUtils.getNxjHomeEnv();
				if( !nxjHome.isEmpty() )
					jtfNxjHome.setText(nxjHome);
			}
		});
		if( LeJOSUtils.getNxjHomeEnv().isEmpty() )
			jbGetNxjHome.setEnabled(false);

		JLabel jlVersion = new JLabel("leJOS Version");
		this.jcbVersion = new JComboBox<LeJOSDistribution>(
				LeJOSDistribution.getSupportedLeJOSVersions());
		this.jcbVersion.setEditable(false);

		// Layout the components
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup()
										.addComponent(jlNxjHome,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(jlVersion,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.jtfNxjHome)
								.addComponent(this.jcbVersion))
						.addComponent(jbBrowse)
						.addComponent(jbGetNxjHome)
				);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
										.addComponent(jlNxjHome)
										.addComponent(this.jtfNxjHome)
										.addComponent(jbBrowse)
										.addComponent(jbGetNxjHome))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
										.addComponent(jlVersion)
										.addComponent(this.jcbVersion))
				);
	}

}
