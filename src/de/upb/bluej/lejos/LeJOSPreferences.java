package de.upb.bluej.lejos;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bluej.extensions.BlueJ;
import bluej.extensions.PreferenceGenerator;

public class LeJOSPreferences implements PreferenceGenerator {

	private final String PROPKEY_NXJ_HOME = "NXJ_HOME";

	private final String PROPKEY_LEJOS_VERSION = "LEJOS_VERSION";


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
		panel.jlVersion.setSelectedValue(dist, true);

		// Verify active Distribution
		if( !dist.isValid() ) {
			// TODO: Show information that version is corrupted and introduce a
			// variable to track if the dist is properly configured
			JOptionPane
					.showMessageDialog(
							panel,
							"The selected leJOS folder seems to be corrupted. Please select a valid installation for the slected leJOS version.",
							main.getName() + " Warning",
							JOptionPane.WARNING_MESSAGE);
		}
	}

	@Override
	public void saveValues() {
		LeJOSDistribution dist = panel.jlVersion.getSelectedValue();
		if( dist == null ) {
			dist = LeJOSDistribution.getLatestLeJOSVersion();
			panel.jlVersion.setSelectedValue(dist, true);
		}
		bluej.setExtensionPropertyString(PROPKEY_LEJOS_VERSION,
				dist.getVersion());
		main.setLejosVersion(dist);
		System.out.println("[leJOS Ext] " + PROPKEY_LEJOS_VERSION + "="
				+ dist.getVersion());

		File nxjHome = new File(panel.jtfNxjHome.getText().trim());
		if( nxjHome.isDirectory() && dist.isValid(nxjHome) ) {
			dist.setDirectory(nxjHome);
			bluej.setExtensionPropertyString(PROPKEY_NXJ_HOME,
					nxjHome.getAbsolutePath());
			panel.jtfNxjHome.setText(nxjHome.getAbsolutePath());
			System.out.println("[leJOS Ext] " + PROPKEY_NXJ_HOME + "="
					+ nxjHome);
		} else {
			// TODO: set version correct configured to false
			dist.setDirectory(null);
			bluej.setExtensionPropertyString(PROPKEY_NXJ_HOME, null);
//			panel.jtfNxjHome.setText("");
			System.out.println("[leJOS Ext] " + PROPKEY_NXJ_HOME + "=null");

			JOptionPane
					.showMessageDialog(
							panel,
							"The selected leJOS folder seems to be corrupted. Please select a valid installation for the slected leJOS version.",
							main.getName() + " Warning",
							JOptionPane.WARNING_MESSAGE);
		}
	}

}
