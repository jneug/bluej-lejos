package de.upb.bluej.lejos;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

public class LeJOS09 extends LeJOSDistribution {

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

	public LeJOS09() {
		super("0.9");

		if( !LeJOSUtils.IS_UNIX ) {
			this.pcclasspath = Arrays.copyOf(pcclasspath,
					pcclasspath.length - 1);
		}
	}

	public File[] getNxtClasspathFiles() {
		return new File[] {
				new File(this.directory, nxtclasspath[0])
		};
	}

	@Override
	public boolean isValid() {
		return isValid(getDirectory());
	}

	@Override
	public boolean isValid( File path ) {
		if( !path.isDirectory() )
			return false;

		if( !checkPaths(path, folders) )
			return false;
		if( !checkPaths(path, nxtclasspath) )
			return false;
		if( !checkPaths(path, pcclasspath) )
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
	private boolean checkPaths( File p, String[] paths ) {
		for( String resolve: paths ) {
			File f = new File(p, resolve);
			if( !f.exists() ) {
				System.out
						.println("[leJOS " + getVersion()
								+ "] missing dependency: " + f.getAbsolutePath());
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
		assert directory != null;

		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome("javac"));

		cmd.add("-bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(directory, nxtclasspath));

		cmd.add("-extdirs");
		cmd.add("\"\"");

		//cmd.add("*.java");
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

	// "$JAVAC" -bootclasspath "$NXJ_CP_NXT" -extdirs "" "$@"
	@Override
	public ProcessBuilder invokeCompile( BClass[] classes )
			throws ProjectNotOpenException {
		assert directory != null;

		BProject project;
		try {
			project = classes[0].getPackage().getProject();
		} catch( PackageNotFoundException e ) {
			return null;
		}
		
		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome("javac"));

		cmd.add("-bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(directory, nxtclasspath));
		cmd.add("-classpath");
		cmd.add(".");

		cmd.add("-extdirs");
		cmd.add("\"\"");

		for( BClass bclass: classes ) {
			try {
				cmd.add(bclass.getJavaFile().getAbsolutePath());
			} catch( ProjectNotOpenException e1 ) {
				System.out.println("[leJOS " + getVersion()
						+ "] error compiling class: " + bclass.getName());
			} catch( PackageNotFoundException e2 ) {
				System.out.println("[leJOS " + getVersion()
						+ "] error compiling class: " + bclass.getName());
			}
		}

		ProcessBuilder builder = new ProcessBuilder(cmd);
		builder.directory(project.getDir());
		return builder;
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
