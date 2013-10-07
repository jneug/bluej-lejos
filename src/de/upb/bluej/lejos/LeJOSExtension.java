package de.upb.bluej.lejos;

import java.io.IOException;

import bluej.extensions.BClass;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;


public class LeJOSExtension extends Extension {

	private LeJOSPreferences preferences;

	private LeJOSMenuGenerator menu;

	private LeJOSDistribution lejos;

	private BlueJ bluej;

	public LeJOSDistribution getLejosVersion() {
		return lejos;
	}

	public void setLejosVersion( LeJOSDistribution lejos ) {
		this.lejos = lejos;
	}

	@Override
	public void startup( BlueJ bluej ) {
		this.bluej = bluej;

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
		return "["+getName()+" ("+lejos.getVersion()+")]";
	}

	private void runProcess( ProcessBuilder pb ) {
		try {
			System.out.println(toString()+" attempting to run " + pb.command().toString());
			
			pb.inheritIO();
			Process process = pb.start();
//			process.waitFor();
		} catch( IOException ex ) {
			System.out.println(toString()+" Failed to run command: "
					+ pb.command().toString());
			System.out.println(toString()+" " + ex.getMessage());
		}
	}

	public void invokeFlash() {
		runProcess(this.lejos.invokeFlash());
	}

	public void invokeCompile( BClass clazz ) {
		System.out.println(toString()+" compiling: " + clazz.getName());
		// TODO: Allow compilation of single classes (with dependencies)
		//runProcess(lejos.invokeCompile(new BClass[] { clazz }));
		try {
			BProject project = clazz.getPackage().getProject();
			runProcess(lejos.invokeCompile(project));
		} catch( ProjectNotOpenException | PackageNotFoundException e ) {
		}
	}

	public void invokeLink( BClass main_class ) {
		System.out.println(toString()+" linking: " + main_class.getName());
		try {
			if( !main_class.isCompiled() )
				invokeCompile(main_class);

			runProcess(lejos.invokeLink(main_class));
		} catch( ProjectNotOpenException | PackageNotFoundException ex ) {
			System.out.println(toString()+" Can't link class: "
					+ main_class.getName());
			System.out.println(toString()+" " + ex.getMessage());
		}
	}

	public void invokeUpload( BClass main_class ) {
		try {
			if( !main_class.isCompiled() )
				invokeLink(main_class);

			runProcess(lejos.invokeUpload(main_class));
		} catch( ProjectNotOpenException | PackageNotFoundException ex ) {
			System.err.println(toString()+" Can't upload class: "
					+ main_class.getName());
			System.err.println(toString()+" " + ex.getMessage());
		}
	}

	public void invokeUploadAndRun( BClass main_class ) {
		try {
			if( !main_class.isCompiled() )
				invokeLink(main_class);

			runProcess(lejos.invokeUploadAndRun(main_class));
		} catch( ProjectNotOpenException | PackageNotFoundException ex ) {
			System.err.println(toString()+" Can't upload class: "
					+ main_class.getName());
			System.err.println(toString()+" " + ex.getMessage());
		}
	}

}
