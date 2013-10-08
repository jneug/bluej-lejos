package de.upb.bluej.lejos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
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

public class LeJOSTest extends LeJOSDistribution {

	public LeJOSTest() {
		super("LeJOSTest");
	}

	@Override
	public File[] getNxtClasspathFiles() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean isValid( File path ) {
		return true;
	}

	@Override
	public ProcessBuilder invokeFlash() {
		return null;
	}

	@Override
	public ProcessBuilder invokeCompile( BProject project )
			throws ProjectNotOpenException {

		List<File> files = new ArrayList<File>();
		BPackage[] bpackages = project.getPackages();
		for( BPackage bpackage: bpackages ) {
			try {
				BClass[] bclasses = bpackage.getClasses();
				for( BClass bclass: bclasses ) {
					files.add(bclass.getJavaFile());
				}
			} catch( PackageNotFoundException ex ) {
			}
		}

		List<String> options = new ArrayList<String>();
		options.add("-bootclasspath");
		options.add("/Volumes/Hyrrokkin/Projekte/Java/_Libraries/lejos-9.0/lib/nxt/classes.jar");
		options.add("-extdirs");
		options.add("\"\"");
		options.add("-d");
		options.add(project.getDir().getAbsolutePath());

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager
				.getJavaFileObjectsFromFiles(files);

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		boolean success = compiler.getTask(null, fileManager, null, options,
				null,
				compilationUnits).call();

		if( !success ) {
			System.err.println("Compile fail!");
		} else
			System.out.println("Compile success!");

		for( Diagnostic<? extends JavaFileObject> diagnostic: diagnostics
				.getDiagnostics() )
			System.out.format("Error on line %d in %s%n",
					diagnostic.getLineNumber(),
					diagnostic.getSource().toString());

		try {
			fileManager.close();
		} catch( IOException io ) {
			io.printStackTrace();
		}

		return null;
	}

	@Override
	public ProcessBuilder invokeCompile( BClass[] classes )
			throws ProjectNotOpenException {
		if( classes.length == 0  ) {
			return null;
		} 
		
		BProject project;
		try {
			project = classes[0].getPackage().getProject();
		} catch( PackageNotFoundException e ) {
			return null;
		}

		List<File> files = new ArrayList<File>();
		try {
			for( BClass bclass: classes ) {
				files.add(bclass.getJavaFile());
			}
		} catch( PackageNotFoundException ex ) {
		}

		List<String> options = new ArrayList<String>();
		options.add("-bootclasspath");
		options.add("/Volumes/Hyrrokkin/Projekte/Java/_Libraries/lejos-9.0/lib/nxt/classes.jar");
		options.add("-classpath");
		options.add(project.getDir().getAbsolutePath());
		options.add("-extdirs");
		options.add("\"\"");
		options.add("-d");
		options.add(project.getDir().getAbsolutePath());

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager
				.getJavaFileObjectsFromFiles(files);

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		boolean success = compiler.getTask(null, fileManager, null, options,
				null,
				compilationUnits).call();

		if( !success ) {
			System.err.println("Compile fail!");
		} else
			System.out.println("Compile success!");

		for( Diagnostic<? extends JavaFileObject> diagnostic: diagnostics
				.getDiagnostics() )
			System.out.format("Error on line %d in %s%n",
					diagnostic.getLineNumber(),
					diagnostic.getSource().toString());

		try {
			fileManager.close();
		} catch( IOException io ) {
			io.printStackTrace();
		}

		return null;
	}

	@Override
	public ProcessBuilder invokeLink( BClass main_class )
			throws ProjectNotOpenException {
		return null;
	}

	@Override
	public ProcessBuilder invokeUpload( BClass main_class )
			throws ProjectNotOpenException {
		return null;
	}

	@Override
	public ProcessBuilder invokeUploadAndRun( BClass main_class )
			throws ProjectNotOpenException {
		return null;
	}

	public static void main( String[] args ) {
		List<File> files = new ArrayList<File>();
		files.add(new File(
				"/Volumes/Hyrrokkin/Projekte/Java/bluej/AGVTest/AGV.java"));
		files.add(new File(
				"/Volumes/Hyrrokkin/Projekte/Java/bluej/AGVTest/LineNavigator.java"));
		files.add(new File(
				"/Volumes/Hyrrokkin/Projekte/Java/bluej/AGVTest/Settings.java"));
		files.add(new File(
				"/Volumes/Hyrrokkin/Projekte/Java/bluej/AGVTest/util/SynchronousTimer.java"));

		List<String> options = new ArrayList<String>();
		options.add("-bootclasspath");
		options.add("/Volumes/Hyrrokkin/Projekte/Java/_Libraries/lejos-9.0/lib/nxt/classes.jar");
		options.add("-extdirs");
		options.add("\"\"");
		options.add("-d");
		options.add("/Volumes/Hyrrokkin/Projekte/Java/bluej/AGVTest");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager
				.getJavaFileObjectsFromFiles(files);

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		boolean success = compiler.getTask(null, fileManager, null, options,
				null,
				compilationUnits).call();

		if( !success ) {
			System.err.println("Compile fail!");
		} else
			System.out.println("Compile success!");

		for( Diagnostic<? extends JavaFileObject> diagnostic: diagnostics
				.getDiagnostics() )
			System.out.format("Error on line %d in %s%n",
					diagnostic.getLineNumber(),
					diagnostic.getSource().toString());

		try {
			fileManager.close();
		} catch( IOException io ) {
			io.printStackTrace();
		}
	}

}
