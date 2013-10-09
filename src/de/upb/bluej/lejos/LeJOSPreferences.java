package de.upb.bluej.lejos;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bluej.extensions.BlueJ;
import bluej.extensions.PreferenceGenerator;

public class LeJOSPreferences implements PreferenceGenerator {

	public final static String PROPKEY_NXJ_HOME = "NXJ_HOME";
	public final static String PROPKEY_LEJOS_VERSION = "LEJOS_VERSION";
	public final static String PROPKEY_SHOW_LINK = "SHOW_LINK";
	public final static String PROPKEY_SHOW_COMPILE = "SHOW_COMPILE";
	public final static String PROPKEY_OPEN_EDITOR = "OPEN_EDITOR";
	public final static String PROPKEY_OPEN_DEBUG = "OPEN_DEBUG";
	
	private final static String PROPKEY_WARN_FOR_CONFIG = "WARN_FOR_CONFIG";
	


	private LeJOSExtension main;

	private BlueJ bluej;

	private LeJOSPreferencePanel panel;
	
	// Preference store
	public String nxj_home, lejos_version;
	public boolean show_compile, show_link, open_editor, open_debug;
	
	public LeJOSPreferences( LeJOSExtension main, BlueJ bluej ) {
		this.main = main;
		this.bluej = bluej;

		this.panel = new LeJOSPreferencePanel(bluej);
		loadValues();
	}

	@Override
	public JPanel getPanel() {
		return this.panel;
	}
	
	@Override
	public void loadValues() {
		show_link = Boolean.parseBoolean(bluej.getExtensionPropertyString(PROPKEY_SHOW_LINK,"false"));
		panel.jcbShowLink.setSelected(show_link);
		show_compile = Boolean.parseBoolean(bluej.getExtensionPropertyString(PROPKEY_SHOW_COMPILE,"false"));
		panel.jcbShowCompile.setSelected(show_compile);
		open_editor = Boolean.parseBoolean(bluej.getExtensionPropertyString(PROPKEY_OPEN_EDITOR,"false"));
		panel.jcbOpenEditor.setSelected(open_editor);
		open_debug = Boolean.parseBoolean(bluej.getExtensionPropertyString(PROPKEY_OPEN_DEBUG,"true"));
		panel.jcbOpenDebug.setSelected(open_debug);
		
		
		nxj_home = bluej.getExtensionPropertyString(PROPKEY_NXJ_HOME,
				LeJOSUtils.getNxjHomeEnv());
		panel.jtfNxjHome.setText(nxj_home);

		LeJOSDistribution defaultDist = LeJOSDistribution
				.getLatestLeJOSVersion();
		String version = bluej
				.getExtensionPropertyString(PROPKEY_LEJOS_VERSION,
						defaultDist.getVersion());

		LeJOSDistribution dist = LeJOSDistribution.getLeJOSVersion(version);
		if( dist == null ) {
			dist = defaultDist;
//			bluej.setExtensionPropertyString(PROPKEY_LEJOS_VERSION, dist.getVersion());
		}

		dist.setDirectory(new File(nxj_home));
		main.setLejosVersion(dist);
		panel.jcbVersion.setSelectedItem(dist);

		if( !dist.isValid() ) {
			dist.setDirectory(null);
			this.showWarningDialog(dist);
		} else {
			bluej.setExtensionPropertyString(PROPKEY_WARN_FOR_CONFIG, "true");
		}
	}

	@Override
	public void saveValues() {
		show_compile = panel.jcbShowCompile.isSelected();
		bluej.setExtensionPropertyString(PROPKEY_SHOW_COMPILE, Boolean.toString(show_compile));
		show_link = panel.jcbShowLink.isSelected();
		bluej.setExtensionPropertyString(PROPKEY_SHOW_LINK, Boolean.toString(show_link));
		open_editor = panel.jcbOpenEditor.isSelected();
		bluej.setExtensionPropertyString(PROPKEY_OPEN_EDITOR, Boolean.toString(open_editor));
		open_debug = panel.jcbOpenDebug.isSelected();
		bluej.setExtensionPropertyString(PROPKEY_OPEN_DEBUG, Boolean.toString(open_debug));
		
		
		// Read and save NXJ_HOME value
		String nxj_home = panel.jtfNxjHome.getText().trim();
		File nxjHome = new File(nxj_home);
		bluej.setExtensionPropertyString(PROPKEY_NXJ_HOME,
				nxjHome.getAbsolutePath());
		panel.jtfNxjHome.setText(nxjHome.getAbsolutePath());

		// Read and save leJOS version
		LeJOSDistribution dist = panel.jcbVersion.getItemAt(panel.jcbVersion
				.getSelectedIndex());
		if( dist == null ) {
			dist = LeJOSDistribution.getLatestLeJOSVersion();
			panel.jcbVersion.setSelectedItem(dist);
		}
		bluej.setExtensionPropertyString(PROPKEY_LEJOS_VERSION,
				dist.getVersion());

		// Set NXJ_HOME in the distribution and save to extension
		dist.setDirectory(nxjHome);
		boolean showReminder = !main.getLejosVersion().getVersion().equals(dist.getVersion());
		main.setLejosVersion(dist);
		
		// Validate configuration
		bluej.setExtensionPropertyString(PROPKEY_WARN_FOR_CONFIG, "true");
		if( !dist.isValid() ) {
			dist.setDirectory(null);
			this.showWarningDialog(dist);
		} else if( showReminder ) {
			showClasspathReminder(dist);
		}
	}
	

	private void showClasspathReminder( LeJOSDistribution dist ) {
		JOptionPane
			.showMessageDialog(
					this.panel,
					new String[] {
							"It seems that you changed your leJOS distribution to " + dist.getVersion() + ".",
							"Remember to also set the classpath to the correct jar at:",
							dist.getNxtClasspathFiles()[0].getAbsolutePath()
					},
					main.getName() + " Information",
					JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void showWarningDialog( LeJOSDistribution dist ) {
		if( bluej.getExtensionPropertyString(PROPKEY_WARN_FOR_CONFIG, "false").equals("false") )
			return;
		else
			bluej.setExtensionPropertyString(PROPKEY_WARN_FOR_CONFIG, "false");
		
		JOptionPane
			.showMessageDialog(
					this.panel,
					new String[] {
							"The selected leJOS folder seems to be corrupted.",
							"Please select a valid installation for the selected leJOS version ("
									+ dist.getVersion() + ")."
					},
					main.getName() + " Warning",
					JOptionPane.WARNING_MESSAGE);
	}

}
