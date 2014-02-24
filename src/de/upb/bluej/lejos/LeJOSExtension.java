package de.upb.bluej.lejos;

import java.io.File;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import bluej.extensions.BClass;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import de.upb.bluej.lejos.LeJOSProcess.InputStreamListener;
import de.upb.bluej.lejos.ui.LeJOSExtensionUI;
import de.upb.bluej.lejos.ui.LeJOSTextPane;

/**
 * Main extension class. Entry point for the BlueJ extension API.
 * 
 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
 */
public class LeJOSExtension extends Extension {

	/**
	 * Extension name
	 */
	public static final String NAME = "BlueJ.leJOS";

	/**
	 * The current version string in the format {@code MAJOR.MINOR.DEV}
	 */
	public static final String VERSION = "0.2.2";


	private BlueJ bluej;

	private LeJOSPreferences preferences;

	private LeJOSMenuGenerator menu;

	private LeJOSExtensionUI ui;

	/**
	 * The current leJOS distribution used for compiling and uploading leJOS
	 * programs.
	 */
	private LeJOSDistribution lejos;

	/**
	 * Stores debug data from the last call to
	 * {@link LeJOSDistribution#invokeLink(BClass)}.
	 */
	private LeJOSDebug debug;

	/**
	 * Language specific label for the leJOS version.
	 */
	private String lejosVersion = "";

	/**
	 * Result of the last call to {@link LeJOSDistribution#isValid()}.
	 */
	private boolean configuration_valid = false;

	/**
	 * Default C'tor.
	 */
	public LeJOSExtension() {
	}

	/**
	 * @return The leJOS distribution currently in use.
	 */
	public LeJOSDistribution getLejosVersion() {
		return lejos;
	}

	/**
	 * Sets the leJOS distribution to use for compiling and uploading.
	 * 
	 * @param lejos
	 */
	public void setLejosVersion( LeJOSDistribution lejos ) {
		this.lejos = lejos;
		this.lejosVersion = String.format("%s %s", bluej.getLabel("lejos"),
				lejos.getVersion());
		this.configuration_valid = lejos.isValid();
	}

	/**
	 * @return If the current leJOS distribution is valid.
	 * @see LeJOSDistribution#isValid()
	 */
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

	/**
	 * @return The BlueJ application class
	 */
	public BlueJ getBlueJ() {
		return this.bluej;
	}

	/**
	 * @return The preferences for the extension
	 */
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

	@Override
	public URL getURL() {
		try {
			return new URL("https://github.com/jneug/bluej-lejos");
		} catch( Exception e ) {
			return null;
		}
	}

	/**
	 * @return A language dependent version string for the current leJOS
	 *         distribution
	 */
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

	@Override
	public String toString() {
		return "[" + getName() + " (" + lejosVersion + ")]";
	}

	/**
	 * Shows the debug window if not already visible.
	 */
	public void showExtensionUI() {
		ui.setVisible(true);
	}

	/**
	 * Returns a formated language dependent {@link BlueJ#getLabel(String)
	 * label}.
	 * 
	 * @param key
	 *            The key to retrieve
	 * @param args
	 *            A set of arguments for formatting
	 * @return The formatted string
	 * @see String#format(String, Object...)
	 * @see BlueJ#getLabel(String)
	 */
	public String getLabel( String key, Object... args ) {
		String label = bluej.getLabel(key);
		if( args.length == 0 )
			return label;
		else
			return String.format(label, args);
	}

	/**
	 * Runs a process from the current leJOS distribution.
	 * 
	 * @param pb
	 *            The pre-configured {@code ProcessBuilder}
	 * @return The new process or {@code null} if there was an error
	 */
	private LeJOSProcess/* Process */runProcess( ProcessBuilder pb ) {
		if( !isConfigruationValid() || pb == null ) {
			return null;
		}

		final LeJOSTextPane pane = ui.getStatusPane();
		pane.clear();
		ui.setVisible(preferences.open_debug);
		try {
			// System.out.println(pb.command().toString());

			LeJOSProcess process = new LeJOSProcess(pb);
			process.addErrorListener(new InputStreamListener() {
				@Override
				public void nextLine( final String line ) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							pane.appendError(line);
						}
					});
				}
			});
			if( this.preferences.show_output ) {
				process.addOutputListener(new InputStreamListener() {
					@Override
					public void nextLine( final String line ) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								pane.appendText(line);
							}
						});
					}
				});
			}
			boolean success = process.start();

			if( !success )
				throw new Exception("doo");

			// pb.inheritIO();
			// Process process = pb.start();

			// pane.captureInputStream(process.getErrorStream(),
			// pane.getStyle(LeJOSTextPane.ERROR_STYLE));
			return process;
		} catch( Exception ex ) {
			System.out.println(toString() + " Failed to run command: "
					+ pb.command().toString());
			System.out.println(toString() + " " + ex.getMessage());
			return null;
		}
	}

	/**
	 * Invokes the flashing process.
	 * 
	 * @see {@link LeJOSDistribution#invokeFlash()}
	 */
	public void invokeFlash() {
		runProcess(this.lejos.invokeFlash());
	}

	/**
	 * Invokes the compile project process with the currently open project.
	 * 
	 * @seek LeJOSDistribution#invokeCompile(BProject)
	 */
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

	/**
	 * Invokes the compile process for the provided project.
	 * 
	 * @param project
	 * @see LeJOSDistribution#invokeCompile(BProject)
	 */
	public void invokeCompile( BProject project ) {
		ui.getStatusPane().clear();
		ui.setVisible(preferences.open_debug);
		try {
			LeJOSCompiler compiler = new LeJOSCompiler(project, lejos);

			boolean success = compiler.compile();
			if( !success ) {
				handleCompilerErrors(compiler);
			} else {
				ui.getStatusPane().appendSuccess(
						getLabel("info.compile.project", lejosVersion));
			}
		} catch( Exception ex ) {
//			ui.getStatusPane().appendText(
//					"Unknown error while compiling for leJOS "+lejos.getVersion());
			ui.getStatusPane().appendError(
					getLabel("exception.compile.project", lejosVersion));
		}
	}

	/**
	 * Invokes the compile process for the specified class.
	 * 
	 * @param clazz
	 * @see LeJOSDistribution#invokeCompile(BClass[])
	 */
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
				ui.getStatusPane().appendSuccess(
						getLabel("info.compile.class", lejosVersion));
			}
		} catch( Exception ex ) {
			ui.getStatusPane().appendError(
					getLabel("exception.compile.class", lejosVersion));
		}
	}

	/**
	 * Handles any compile errors and warnings produces by a compile process.
	 * Might open up a class editor at the first error location.
	 * 
	 * @param compiler
	 * @throws ProjectNotOpenException
	 * @throws PackageNotFoundException
	 */
	private void handleCompilerErrors( LeJOSCompiler compiler )
			throws ProjectNotOpenException, PackageNotFoundException {
		LeJOSTextPane status = ui.getStatusPane();

		for( Diagnostic<? extends JavaFileObject> d: compiler
				.getDiagnostics() ) {
			status.appendError(d.getKind().toString() + ": "
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

	/**
	 * Invokes the link process.
	 * 
	 * @param main_class
	 * @see LeJOSDistribution#invokeLink(BClass)
	 */
	public void invokeLink( BClass main_class ) {
		try {
			invokeCompile(main_class);

			LeJOSProcess pr = runProcess(lejos.invokeLink(main_class));
			pr.addOutputListener(new InputStreamListener() {
				@Override
				public void nextLine( String line ) {
					debug.parseLine(line);
				}
			});

//			if( pr != null ) {
//				try {
//					debug.fromInputStream(pr.getInputStream());
//				} catch( IOException ex ) {
//				}
//				ui.updateLabels();
//			}
		} catch( ProjectNotOpenException ex ) {
			ui.getStatusPane().appendError(
					getLabel("exception.link", lejosVersion));

			// Log error
			System.out.println(toString() + " Can't link class: "
					+ main_class.getName());
			System.out.println(toString() + " " + ex.getMessage());
		}
	}

	/**
	 * Invokes the upload process.
	 * 
	 * @param main_class
	 * @see LeJOSDistribution#invokeUpload(BClass)
	 */
	public void invokeUpload( BClass main_class ) {
		try {
			invokeLink(main_class);

			runProcess(lejos.invokeUpload(main_class));
		} catch( ProjectNotOpenException ex ) {
			ui.getStatusPane().appendError(
					getLabel("exception.upload", lejosVersion));

			System.err.println(toString() + " Can't upload class: "
					+ main_class.getName());
			System.err.println(toString() + " " + ex.getMessage());
		}
	}

	/**
	 * Invokes the upload and run process.
	 * 
	 * @param main_class
	 * @see LeJOSDistribution#invokeUploadAndRun(BClass)
	 */
	public void invokeUploadAndRun( BClass main_class ) {
		try {
			invokeLink(main_class);

			runProcess(lejos.invokeUploadAndRun(main_class));
		} catch( ProjectNotOpenException ex ) {
			ui.getStatusPane().appendError(
					getLabel("exception.run", lejosVersion));

			System.err.println(toString() + " Can't upload class: "
					+ main_class.getName());
			System.err.println(toString() + " " + ex.getMessage());
		}
	}

}
