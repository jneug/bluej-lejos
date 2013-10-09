package de.upb.bluej.lejos;

import java.io.File;
import java.io.IOException;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import bluej.extensions.BClass;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import de.upb.bluej.lejos.ui.LeJOSExtensionUI;
import de.upb.bluej.lejos.ui.LeJOSTextPane;


public class LeJOSExtension extends Extension {
	
	public static final String NAME = "BlueJ.leJOS";
	public static final String VERSION = "0.2"; 
	

	private LeJOSPreferences preferences;

	private LeJOSMenuGenerator menu;

	private LeJOSDistribution lejos;
	
	private String lejosVersion = "";

	private BlueJ bluej;

	private boolean configuration_valid = false;

	private LeJOSExtensionUI ui;

	private LeJOSDebug debug;

	public LeJOSDistribution getLejosVersion() {
		return lejos;
	}

	public void setLejosVersion( LeJOSDistribution lejos ) {
		this.lejos = lejos;
		this.lejosVersion = String.format("%s %s", bluej.getLabel("lejos"), lejos.getVersion());
		this.configuration_valid = lejos.isValid();
	}

	public boolean isConfigruationValid() {
		return this.configuration_valid;
	}

	@Override
	public void startup( BlueJ bluej ) {
		this.bluej = bluej;

		this.debug = new LeJOSDebug();
		this.ui = new LeJOSExtensionUI(this.debug, bluej);
		this.ui.setLocationRelativeTo(bluej.getCurrentFrame());

		preferences = new LeJOSPreferences(this, bluej);
		bluej.setPreferenceGenerator(preferences);

		menu = new LeJOSMenuGenerator(this);
		bluej.setMenuGenerator(menu);

		bluej.setClassTargetPainter(new LeJOSClassTargetPainter(bluej
				.getClassTargetPainter()));
	}
	
	public BlueJ getBlueJ() {
		return this.bluej;
	}
	
	public LeJOSPreferences getPreferences() {
		return this.preferences;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getVersion() {
		return VERSION;
	}
	
	public String getLeJOSVersion() {
		return this.lejosVersion;
	}

	@Override
	public boolean isCompatible() {
		return (VERSION_MAJOR >= 2);
	}

	@Override
	public String getDescription() {
		return bluej.getLabel("descr");
	}

	public String toString() {
		return "[" + getName() + " (" + lejosVersion + ")]";
	}

	public void showExtensionUI() {
		ui.setVisible(true);
	}
	
	public String getLabel( String key, Object... args ) {
		String label = bluej.getLabel(key);
		if( args.length == 0 )
			return label;
		else
			return String.format(label, args);
	}

	private Process runProcess( ProcessBuilder pb ) {
		if( !isConfigruationValid() || pb == null ) {
			return null;
		}

		ui.getStatusPane().clear();
		ui.setVisible(preferences.open_debug);
		try {
			// System.out.println(pb.command().toString());

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
//			BProject[] projects = bluej.getOpenProjects();
//			if( projects.length > 0 )
//				project = projects[0];
			return;
		}

		if( project != null )
			invokeCompile(project);
	}

	public void invokeCompile( BProject project ) {
		ui.getStatusPane().clear();
		ui.setVisible(preferences.open_debug);
		try {
			LeJOSCompiler compiler = new LeJOSCompiler(project, lejos);

			boolean success = compiler.compile();
			if( !success ) {
				handleCompilerErrors(compiler);
			} else {
				ui.getStatusPane().appendText(getLabel("info.compile.project", lejosVersion));
			}
		} catch( Exception ex ) {
//			ui.getStatusPane().appendText(
//					"Unknown error while compiling for leJOS "+lejos.getVersion());
			ui.getStatusPane().appendText(getLabel("exception.compile.project", lejosVersion));
		}
	}

	public void invokeCompile( BClass clazz ) {
		ui.getStatusPane().clear();
		ui.setVisible(preferences.open_debug);
		try {
			LeJOSCompiler compiler = new LeJOSCompiler(clazz.getPackage()
					.getProject(), lejos);

			boolean success = compiler.compile(clazz);
			if( !success ) {
				handleCompilerErrors(compiler);
			} else {
				ui.getStatusPane().appendText(getLabel("info.compile.class", lejosVersion));
			}
		} catch( Exception ex ) {
			ui.getStatusPane().appendText(getLabel("exception.compile.class", lejosVersion));
		}
	}

	private void handleCompilerErrors( LeJOSCompiler compiler )
			throws ProjectNotOpenException, PackageNotFoundException {
		LeJOSTextPane status = ui.getStatusPane();

		for( Diagnostic<? extends JavaFileObject> d: compiler
				.getDiagnostics() ) {
			status.appendText(d.getKind().toString() + ": "
					+ d.getMessage(null));
		}

		// Try to open editor for first error
		Diagnostic<? extends JavaFileObject> error = compiler.getFirstError();
		if( preferences.open_editor && error != null ) {
			JavaFileObject src = error.getSource();
			BClass bclass = LeJOSUtils.findClassForJavaFile(
					new File(src.getName()), compiler.getProject());
			if( bclass != null ) {
				Editor e = bclass.getEditor();

				if( error.getPosition() != Diagnostic.NOPOS ) {
					e.setSelection(
							e.getTextLocationFromOffset((int) error
									.getStartPosition()),
							e.getTextLocationFromOffset((int) error
									.getEndPosition()));
				}

				e.setVisible(true);
				e.showMessage(error.getMessage(null));
			}
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
				ui.updateLabels();
			}
		} catch( ProjectNotOpenException ex ) {
			ui.getStatusPane().appendText(getLabel("exception.link", lejosVersion));

			// Log error
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
			ui.getStatusPane().appendText(getLabel("exception.upload", lejosVersion));
			
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
			ui.getStatusPane().appendText(getLabel("exception.run", lejosVersion));
			
			System.err.println(toString() + " Can't upload class: "
					+ main_class.getName());
			System.err.println(toString() + " " + ex.getMessage());
		}
	}

}
