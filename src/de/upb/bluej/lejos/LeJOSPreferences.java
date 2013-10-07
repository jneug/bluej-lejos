package de.upb.bluej.lejos;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bluej.extensions.BlueJ;
import bluej.extensions.PreferenceGenerator;

public class LeJOSPreferences implements PreferenceGenerator {

	private final String PROPKEY_NXJ_HOME = "NXJ_HOME";

	private final String PROPKEY_LEJOS_VERSION = "LEJOS_VERSION";
	
	private final String PROPKEY_WARN_FOR_CONFIG = "WARN_FOR_CONFIG";
	


	private LeJOSExtension main;

	private BlueJ bluej;

	private LeJOSPreferencePanel panel;
	
	public LeJOSPreferences( LeJOSExtension main, BlueJ bluej ) {
		this.main = main;
		this.bluej = bluej;

		this.panel = new LeJOSPreferencePanel();
		loadValues();
	}

	@Override
	public JPanel getPanel() {
		return this.panel;
	}

	@Override
	public void loadValues() {
		String nxjHome = bluej.getExtensionPropertyString(PROPKEY_NXJ_HOME,
				LeJOSUtils.getNxjHomeEnv());
		panel.jtfNxjHome.setText(nxjHome);

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

		dist.setDirectory(new File(nxjHome));
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
		// Read and save NXJ_HOME value
		File nxjHome = new File(panel.jtfNxjHome.getText().trim());
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
		main.setLejosVersion(dist);
		
		// Validate configuration
		bluej.setExtensionPropertyString(PROPKEY_WARN_FOR_CONFIG, "true");
		if( !dist.isValid() ) {
			dist.setDirectory(null);
			this.showWarningDialog(dist);
		}
	}
	
	private void showWarningDialog( LeJOSDistribution dist ) {
		if( bluej.getExtensionPropertyString(PROPKEY_WARN_FOR_CONFIG, "true").equals("false") )
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
