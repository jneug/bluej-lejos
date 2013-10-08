package de.upb.bluej.lejos;

import java.io.IOException;

import bluej.extensions.BClass;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.ProjectNotOpenException;
import de.upb.bluej.lejos.ui.LeJOSExtensionUI;


public class LeJOSExtension extends Extension {

	private LeJOSPreferences preferences;

	private LeJOSMenuGenerator menu;

	private LeJOSDistribution lejos;

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
		return "0.1";
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
		if( !isConfigruationValid() || pb == null ) {
			return null;
		}

		ui.getStatusPane().clear();
		try {
			//System.out.println(pb.command().toString());
			
			// pb.inheritIO();
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

	public void invokeCompile() {
		BProject project = null;
		try {
			project = bluej.getCurrentPackage().getProject();
		} catch( ProjectNotOpenException e ) {
			BProject[] projects = bluej.getOpenProjects();
			if( projects.length > 0 )
				project = projects[0];
		}

		if( project != null )
			invokeCompile(project);
	}

	public void invokeCompile( BProject project ) {
		try {
			runProcess(lejos.invokeCompile(project));
			
			if( ui.getStatusPane().getText().isEmpty() )
				ui.getStatusPane().appendText("Compilation completed.");
		} catch( ProjectNotOpenException e ) {
		}
	}

	public void invokeCompile( BClass clazz ) {
		try {
			runProcess(lejos.invokeCompile(new BClass[] { clazz }));
		} catch( ProjectNotOpenException e ) {
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
				// pr.waitFor();
			}
			
			ui.setVisible(true);
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
			
			ui.setVisible(true);
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
			
			ui.setVisible(true);
		} catch( ProjectNotOpenException ex ) {
			System.err.println(toString() + " Can't upload class: "
					+ main_class.getName());
			System.err.println(toString() + " " + ex.getMessage());
		}
	}

}
