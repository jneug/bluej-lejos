package de.upb.bluej.lejos;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

public class LeJOS085 extends LeJOSDistribution {

	private String[] folders = new String[] {
			"lib", "3rdparty", "3rdparty/lib"
	};

	private String[] nxtclasspath = new String[] {
			"lib/classes.jar"
	};

	private String[] pcclasspath = new String[] {
			"lib/pccomm.jar",
			"lib/pctools.jar",
			"lib/jtools.jar",
			"3rdparty/lib/bcel.jar",
			"3rdparty/lib/bluecove.jar",
			"3rdparty/lib/bluecove-gpl.jar",
			"3rdparty/lib/commons-cli.jar",
	};

	public LeJOS085() {
		super("0.85beta");
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

//	 java -d32 -Dnxj.home="$NXJ_HOME" -DCOMMAND_NAME="$NXJ_COMMAND" -Djava.library.path="$NXJ_BIN" 
//			-classpath "$NXJ_CP_TOOL" lejos.pc.tools.NXJFlashG  "$@"
	@Override
	public ProcessBuilder invokeFlash() {
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome());
		if( LeJOSUtils.isForce32() )
			cmd.add("-d32");
		cmd.add("-Dnxj.home=\"" + directory.getAbsolutePath() + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjflashg\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(directory, pcclasspath));

		cmd.add("lejos.pc.tools.NXJFlashG");

		ProcessBuilder builder = new ProcessBuilder(cmd);
		builder.directory(directory);
		return builder;
	}

	// javac -bootclasspath "$NXJ_CP_BOOT" "$@"
	@Override
	public ProcessBuilder invokeCompile( BProject project )
			throws ProjectNotOpenException {
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome() + "c");

		cmd.add("-bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(directory, nxtclasspath));

		cmd.add("-extdirs");
		cmd.add("\"\"");

		// cmd.add("*.java");
		// Workaround because "*.java" throws an error ...
		BPackage[] bpackages = project.getPackages();
		URI root = project.getDir().toURI();
		for( BPackage bpackage: bpackages ) {
			try {
				BClass[] bclasses = bpackage.getClasses();
				for( BClass bclass: bclasses ) {
					URI rel_path = root
							.relativize(bclass.getJavaFile().toURI());
					cmd.add(rel_path.getPath());
				}
			} catch( PackageNotFoundException ex ) {
			}
		}

		ProcessBuilder builder = new ProcessBuilder(cmd);
		builder.directory(project.getDir());
		return builder;
	}

	// javac -bootclasspath "$NXJ_CP_BOOT" "$@"
	@Override
	public ProcessBuilder invokeCompile( BClass[] classes ) {
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome() + "c");

		cmd.add("-bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(directory, nxtclasspath));

		cmd.add("-extdirs");
		cmd.add("\"\"");

		for( BClass bclass: classes ) {
			try {
				cmd.add(bclass.getJavaFile().getAbsolutePath());
			} catch( ProjectNotOpenException | PackageNotFoundException e ) {
				System.out.println("[leJOS " + getVersion()
						+ "] error compiling class: " + bclass.getName());
			}
		}

		ProcessBuilder builder = new ProcessBuilder(cmd);
//		builder.directory(classes[0].getPackage().getProject().getDir());
		return builder;
	}


//	 java -d32 -Dnxj.home="$NXJ_HOME" -DCOMMAND_NAME="$NXJ_COMMAND" -Djava.library.path="$NXJ_BIN" 
//			-classpath "$NXJ_CP_TOOL" js.tinyvm.TinyVM --bootclasspath "$NXJ_CP_BOOT" 
//			--writeorder "LE" --classpath "." "$@"
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
		cmd.add("-Dnxj.home=\"" + directory.getAbsolutePath() + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjlink\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(directory, pcclasspath));

		cmd.add("js.tinyvm.TinyVM");

		cmd.add("--bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(directory, nxtclasspath));
		cmd.add("--writeorder");
		cmd.add("\"LE\"");
		cmd.add("--classpath");
		cmd.add("\".\"");

		cmd.add("--output");
		cmd.add("\"" + main_class.getName() + ".nxj\"");

		cmd.add(main_class.getName());

//		cmd.add("--verbose");

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

//	java -d32 -Dnxj.home="$NXJ_HOME" -DCOMMAND_NAME="$NXJ_COMMAND" -Djava.library.path="$NXJ_BIN" 
//			-classpath "$NXJ_CP_TOOL" lejos.pc.tools.NXJUpload  "$@"
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
