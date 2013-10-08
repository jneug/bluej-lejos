package de.upb.bluej.lejos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

public class LeJOS092 extends LeJOSDistribution {

	private String nxt_dir = "lib" + File.separator + "nxt" + File.separator;
	private String pc_dir = "lib" + File.separator + "pc" + File.separator;
	private String party_dir = "lib" + File.separator + "pc" + File.separator
			+ "3rdparty" + File.separator;

	private String[] folders = new String[] {
			nxt_dir, pc_dir, party_dir
	};

	private String[] nxtclasspath = new String[] {
			nxt_dir + "classes.jar"
	};

	private String[] pcclasspath = new String[] {
			pc_dir + "pccomm.jar",
			pc_dir + "pctools.jar",
			pc_dir + "jtools.jar",
			party_dir + "bcel.jar",
			party_dir + "bluecove.jar",
			party_dir + "commons-cli.jar",
			party_dir + "bluecove-gpl.jar"
	};

	public LeJOS092() {
		super("0.9dev");

		if( !LeJOSUtils.IS_UNIX ) {
			this.pcclasspath = Arrays.copyOf(pcclasspath,
					pcclasspath.length - 1);
		}
	}

	public File[] getNxtClasspathFiles() {
		return new File[] {
				directory.toPath().resolve(nxtclasspath[0]).toFile()
		};
	}

	@Override
	public boolean isValid() {
		return isValid(getDirectory());
	}

	@Override
	public boolean isValid( File path ) {
		Path p = path.toPath();

		if( !Files.isDirectory(p) )
			return false;

		if( !checkPaths(p, folders) )
			return false;
		if( !checkPaths(p, nxtclasspath) )
			return false;
		if( !checkPaths(p, pcclasspath) )
			return false;

		return true;
	}

	/**
	 * Helper method to check for the existence of all the provided
	 * {@code paths} relative to the parent {@code p}.
	 * 
	 * @param p
	 * @param paths
	 * @return
	 */
	private boolean checkPaths( Path p, String[] paths ) {
		for( String resolve: paths ) {
			if( !Files.exists(p.resolve(resolve)) ) {
				System.out
						.println("[leJOS " + getVersion()
								+ "] missing dependency: " + p.resolve(resolve));
				return false;
			}
		}
		return true;
	}

//	 "$JAVA" $NXJ_FORCE32 -Dnxj.home="$NXJ_HOME" -DCOMMAND_NAME="$NXJ_COMMAND"
//			 -classpath "$NXJ_CP_PC" lejos.pc.tools.NXJFlashG "$@"
	@Override
	public ProcessBuilder invokeFlash() {
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome());
		if( LeJOSUtils.isForce32() )
			cmd.add("-d32");
		if( !LeJOSUtils.IS_WINDOWS )
			cmd.add("-Dnxj.home=\"" + directory.getAbsolutePath() + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjflashg\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(directory, pcclasspath));

		cmd.add("lejos.pc.tools.NXJFlashG");

		ProcessBuilder builder = new ProcessBuilder(cmd);
		builder.directory(directory);
		return builder;
	}

	// "$JAVAC" -bootclasspath "$NXJ_CP_NXT" -extdirs "" "$@"
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
		if( classes.length == 0 ) {
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

//	 "$JAVA" $NXJ_FORCE32 -Dnxj.home="$NXJ_HOME" -DCOMMAND_NAME="$NXJ_COMMAND"
//			 -classpath "$NXJ_CP_PC" lejos.pc.tools.NXJLink --bootclasspath "$NXJ_CP_NXT"
//			 --writeorder "LE" --classpath "." "$@"
//	usage: java lejos.pc.tools.NXJLink [options] main-class [more classes]
//			options:
//			-a,--all                          do not filter classes
//			-bp,--bootclasspath <classpath>   where to find leJOS classes
//			-cp,--classpath <classpath>       where to find user's classes
//			-dm,--disablememcompact           disable memory compaction
//			-ea,--enableassert                enable assertions
//			-ec,--enablechecks                enable run time checks
//			-g,--debug                        include debug monitor
//			-gr,--remotedebug                 include remote debug monitor
//			-h,--help                         show this help
//			-o,--output <path to file>        dump binary to file
//			-od,--outputdebug <path to file>  dump debug info to file
//			-v,--verbose                      print class and signature information
//			-wo,--writeorder <write order>    endianness (BE or LE)
	@Override
	public ProcessBuilder invokeLink( BClass main_class )
			throws ProjectNotOpenException {
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome());
		if( LeJOSUtils.isForce32() )
			cmd.add("-d32");
		// TODO: How does this affect other systems?
		if( !LeJOSUtils.IS_WINDOWS )
			cmd.add("-Dnxj.home=\"" + directory.getAbsolutePath() + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjlink\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(directory, pcclasspath));

		cmd.add("lejos.pc.tools.NXJLink");

		cmd.add("--bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(directory, nxtclasspath));
		cmd.add("--writeorder");
		cmd.add("\"LE\"");
		cmd.add("--classpath");
		cmd.add("\".\"");

		cmd.add("--output");
		cmd.add("\"" + main_class.getName() + ".nxj\"");
		cmd.add("--outputdebug");
		cmd.add("\"" + main_class.getName() + ".dbg\"");

		cmd.add(main_class.getName());

		cmd.add("--verbose");

		ProcessBuilder builder = new ProcessBuilder(cmd);
		try {
			builder.directory(main_class.getJavaFile().getParentFile());
		} catch( PackageNotFoundException ex ) {
			System.out.println("[leJOS " + getVersion()
					+ "] error linking binary for class: "
					+ main_class.getName());
			return null;
		}
		return builder;
	}

//	"$JAVA" $NXJ_FORCE32 -Dnxj.home="$NXJ_HOME" -DCOMMAND_NAME="$NXJ_COMMAND" 
//			-classpath "$NXJ_CP_PC" lejos.pc.tools.NXJUpload  "$@"
//	usage: java lejos.pc.tools.NXJUpload [options] filename [more filenames]
//			options:
//			-b,--bluetooth          use bluetooth
//			-d,--address <address>  look for NXT with given address
//			-h,--help               help
//			-n,--name <name>        look for named NXT
//			-r,--run                start program (last file)
//			-u,--usb                use usb
	@Override
	public ProcessBuilder invokeUpload( BClass main_class )
			throws ProjectNotOpenException {
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome());
		if( LeJOSUtils.isForce32() )
			cmd.add("-d32");
		if( !LeJOSUtils.IS_WINDOWS )
			cmd.add("-Dnxj.home=\"" + directory.getAbsolutePath() + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjupload\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(directory, pcclasspath));

		cmd.add("lejos.pc.tools.NXJUpload");

		cmd.add(main_class.getName() + ".nxj");

		ProcessBuilder builder = new ProcessBuilder(cmd);
		try {
			builder.directory(main_class.getJavaFile().getParentFile());
		} catch( PackageNotFoundException ex ) {
			System.out.println("[leJOS " + getVersion()
					+ "] error uploading binary for class: "
					+ main_class.getName());
			return null;
		}
		return builder;
	}

	@Override
	public ProcessBuilder invokeUploadAndRun( BClass main_class )
			throws ProjectNotOpenException {
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome());
		if( LeJOSUtils.isForce32() )
			cmd.add("-d32");
		if( !LeJOSUtils.IS_WINDOWS )
			cmd.add("-Dnxj.home=\"" + directory.getAbsolutePath() + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjupload\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(directory, pcclasspath));

		cmd.add("lejos.pc.tools.NXJUpload");
		cmd.add("--run");

		cmd.add(main_class.getName() + ".nxj");

		ProcessBuilder builder = new ProcessBuilder(cmd);
		try {
			builder.directory(main_class.getJavaFile().getParentFile());
		} catch( PackageNotFoundException ex ) {
			System.out.println("[leJOS " + getVersion()
					+ "] error uploading binary for class: "
					+ main_class.getName());
			return null;
		}
		return builder;
	}

//	usage: java lejos.pc.tools.NXJDebugTool [options] [classNr] [methodNr [PC]]
//			options:
//			-c,--class                   resolve class number
//			-di,--debuginfo <debugfile>  use the specified debug file
//			   --dump                    dump class and method table
//			-h,--help                    help
//			-m,--method                  resolve method number
//
//			Examples:
//			  java lejos.pc.tools.NXJDebugTool -di <filename> --dump 
//			  java lejos.pc.tools.NXJDebugTool -di <filename> -c 16
//			  java lejos.pc.tools.NXJDebugTool -di <filename> -m 45 30
//			  java lejos.pc.tools.NXJDebugTool -di <filename> -c -m 16 45 30
//	public ProcessBuilder invokeDebug( BClass main_class )
//			throws ProjectNotOpenException {
//		
//	}

}
