package de.upb.bluej.lejos;

import static de.upb.bluej.lejos.LeJOSUtils.buildClasspath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

/**
 * Compiles java source files for use with a {@link LeJOSDistribution} using the
 * {@link JavaCompiler} interface.
 * 
 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
 */
public class LeJOSCompiler {

	private BProject bproject;
	
	private LeJOSDistribution lejos;

	private DiagnosticCollector<JavaFileObject> diagnostics;

	private boolean success = false;

	public LeJOSCompiler( BProject project, LeJOSDistribution dist ) {
		this.lejos = dist;
		this.bproject = project;
	}

	/**
	 * Returns the diagnostics from the last compilation task.
	 * 
	 * @return The list of diagnostics or {@code null} if no compilation was
	 *         done yet.
	 */
	public Iterable<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
		return this.diagnostics.getDiagnostics();
	}

	/**
	 * Returns the first diagnostic with kind {@link Kind#ERROR}.
	 * 
	 * @return
	 */
	public Diagnostic<? extends JavaFileObject> getFirstError() {
		if( this.diagnostics != null ) {
			for( Diagnostic<? extends JavaFileObject> d: diagnostics.getDiagnostics() ) {
				if( d.getKind().equals(Kind.ERROR) )
					return d;
			}
		}
			
		return null;
	}

	/**
	 * Indicates if the last compilation task completed successfully.
	 * 
	 * @return {@code true} if a compilation task was executed successfully,
	 *         {@code false} othrwise or if co compilation was done at all.
	 */
	public boolean wasSuccess() {
		return success;
	}
	
	public BProject getProject() {
		return this.bproject;
	}

	public boolean compile()
			throws PackageNotFoundException,
			ProjectNotOpenException, IOException {
		List<File> files = new ArrayList<File>();
		BPackage[] bpackages = bproject.getPackages();
		for( BPackage bpackage: bpackages ) {
			BClass[] bclasses = bpackage.getClasses();
			for( BClass bclass: bclasses ) {
				files.add(bclass.getJavaFile());
			}
		}

		return this.doCompile(files);
	}

	public boolean compile( BPackage bpackage )
			throws PackageNotFoundException,
			ProjectNotOpenException, IOException {
		List<File> files = new ArrayList<File>();

		BClass[] bclasses = bpackage.getClasses();
		for( BClass bclass: bclasses ) {
			files.add(bclass.getJavaFile());
		}

		return this.doCompile(files);
	}

	public boolean compile( BClass bclass ) throws PackageNotFoundException,
			ProjectNotOpenException, IOException {
		List<File> files = new ArrayList<File>();
		files.add(bclass.getJavaFile());
		return this.doCompile(files);
	}

	public boolean compile( BClass[] bclasses )
			throws PackageNotFoundException,
			ProjectNotOpenException, IOException {
		List<File> files = new ArrayList<File>();

		for( BClass bclass: bclasses ) {
			files.add(bclass.getJavaFile());
		}

		return this.doCompile(files);
	}

	private boolean doCompile(Iterable<File> files )
			throws IOException, ProjectNotOpenException {
		// Compiler options
		List<String> options = new ArrayList<String>();
		options.add("-bootclasspath");
		options.add(buildClasspath(lejos.getNxtClasspathFiles()));
		options.add("-classpath");
		options.add(bproject.getDir().getAbsolutePath());
		options.add("-extdirs");
		options.add("\"\"");
		options.add("-d");
		options.add(bproject.getDir().getAbsolutePath());


		// Prepare compiler and file objects
		diagnostics = new DiagnosticCollector<JavaFileObject>();

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				diagnostics, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager
				.getJavaFileObjectsFromFiles(files);


		// Do the work
//		Writer output = new StringWriter();
//		boolean success = compiler.getTask(output, fileManager, null,
//				options, null, compilationUnits).call();
		this.success = compiler.getTask(null, fileManager, diagnostics,
				options, null, compilationUnits).call();

		fileManager.close();

		return this.success;
	}


}