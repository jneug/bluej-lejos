package de.upb.bluej.lejos;

import java.io.IOException;

import bluej.extensions.BClass;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import de.upb.bluej.lejos.ui.LeJOSExtensionUI;


public class LeJOSExtension extends Extension {

	private LeJOSPreferences preferences;

	private LeJOSMenuGenerator menu;

	private LeJOSDistribution lejos;

	@SuppressWarnings("unused")
	private BlueJ bluej;

	private boolean configuration_valid = false;
	
	private LeJOSExtensionUI ui;
	
	private LeJOSDebug debug;

	public LeJOSDistribution getLejosVersion() {
		return lejos;
	}

	public void setLejosVersion( LeJOSDistribution lejos ) {
		this.lejos = lejos;

		this.configuration_valid = lejos.isValid();
	}

	public boolean isConfigruationValid() {
		return this.configuration_valid;
	}

	@Override
	public void startup( BlueJ bluej ) {
		this.bluej = bluej;
		
		this.debug = new LeJOSDebug();
		this.ui = new LeJOSExtensionUI(this.debug);
		this.ui.setLocationRelativeTo(bluej.getCurrentFrame());
		
		preferences = new LeJOSPreferences(this, bluej);
		bluej.setPreferenceGenerator(preferences);

		menu = new LeJOSMenuGenerator(this);
		bluej.setMenuGenerator(menu);
		
		bluej.setClassTargetPainter(new LeJOSClassTargetPainter(bluej
				.getClassTargetPainter()));
	}

	@Override
	public String getName() {
		return "BlueJ.leJOS";
	}

	@Override
	public String getVersion() {
		return "0.1dev";
	}

	@Override
	public boolean isCompatible() {
		return (VERSION_MAJOR >= 2);
	}

	@Override
	public String getDescription() {
		return "leJOS integration for BlueJ";
	}

	public String toString() {
		return "[" + getName() + " (" + lejos.getVersion() + ")]";
	}
	
	public void showExtensionUI() {
		ui.setVisible(true);
	}

	private Process runProcess( ProcessBuilder pb ) {
		if( !isConfigruationValid() ) {
			return null;
		}

		ui.getStatusPane().clear();
		ui.setVisible(true);
		try {
			//pb.inheritIO();
			Process process = pb.start();
			ui.getStatusPane().captureInputStream(process.getErrorStream());
			return process;
		} catch( IOException ex ) {
			System.out.println(toString() + " Failed to run command: "
					+ pb.command().toString());
			System.out.println(toString() + " " + ex.getMessage());
			return null;
		}
	}

	public void invokeFlash() {
		runProcess(this.lejos.invokeFlash());
	}

	public void invokeCompile( BClass clazz ) {
		// TODO: Allow compilation of single classes (with dependencies)
		// runProcess(lejos.invokeCompile(new BClass[] { clazz }));
		try {
			BProject project = clazz.getPackage().getProject();
			runProcess(lejos.invokeCompile(project));
		} catch( ProjectNotOpenException | PackageNotFoundException e ) {
		}
	}

	public void invokeLink( BClass main_class ) {
		try {
			invokeCompile(main_class);

			Process pr = runProcess(lejos.invokeLink(main_class));
			if( pr != null ) {
				try {
					debug.fromInputStream(pr.getInputStream());
				} catch( IOException ex ) {
				}
				//pr.waitFor();
			}
		} catch( ProjectNotOpenException ex ) {
			System.out.println(toString() + " Can't link class: "
					+ main_class.getName());
			System.out.println(toString() + " " + ex.getMessage());
		} 
	}

	public void invokeUpload( BClass main_class ) {
		try {
			invokeLink(main_class);

			runProcess(lejos.invokeUpload(main_class));
		} catch( ProjectNotOpenException ex ) {
			System.err.println(toString() + " Can't upload class: "
					+ main_class.getName());
			System.err.println(toString() + " " + ex.getMessage());
		}
	}

	public void invokeUploadAndRun( BClass main_class ) {
		try {
			invokeLink(main_class);

			runProcess(lejos.invokeUploadAndRun(main_class));
		} catch( ProjectNotOpenException ex ) {
			System.err.println(toString() + " Can't upload class: "
					+ main_class.getName());
			System.err.println(toString() + " " + ex.getMessage());
		}
	}

}
