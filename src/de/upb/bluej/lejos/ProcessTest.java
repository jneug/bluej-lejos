package de.upb.bluej.lejos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessTest {
	
	private static String[] nxtclasspath = new String[] {
			"lib/nxt/classes.jar"
	};

	private static String[] pcclasspath = new String[] {
			"lib/pc/pccomm.jar",
			"lib/pc/pctools.jar",
			"lib/pc/jtools.jar",
			"lib/pc/3rdparty/bcel.jar",
			"lib/pc/3rdparty/bluecove.jar",
			"lib/pc/3rdparty/bluecove-gpl.jar",
			"lib/pc/3rdparty/commons-cli.jar",
	};

	public static void main( String[] args ) {
		String workingDir = "/Volumes/Hyrrokkin/Projekte/Java/_Libraries/lejos-9.0";
		String projectDir = "/Volumes/Hyrrokkin/Projekte/Java/bluej/AGVTest";
	
//		compile(projectDir, workingDir);
//		link(projectDir, workingDir);
		upload(projectDir, workingDir);
		
//		for( Entry<Object, Object> e: System.getProperties().entrySet() ) {
//			System.out.println(e.getKey().toString()+"="+e.getValue().toString());
//		}
	}
	
	public static void upload( String projectDir, String workingDir ) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome());
		if( LeJOSUtils.isForce32() )
			cmd.add("-d32");
		cmd.add("-Dnxj.home=\"" + workingDir + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjlink\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(new File(workingDir), pcclasspath));

		cmd.add("lejos.pc.tools.NXJUpload");
		
		for( String s: cmd )
			System.out.print(s+" ");
		
//		try {
//			ProcessBuilder builder = new ProcessBuilder(cmd);
//			builder.directory(new File(projectDir));
//			builder.inheritIO();
//			Process process = builder.start();
//			process.waitFor();
//		} catch( IOException | InterruptedException e ) {
//			e.printStackTrace();
//		}
	}
	
	public static void link( String projectDir, String workingDir ) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome());
		if( LeJOSUtils.isForce32() )
			cmd.add("-d32");
		cmd.add("-Dnxj.home=\"" + workingDir + "\"");
		// cmd.add("-DCOMMAND_NAME=\"nxjlink\"");

		cmd.add("-classpath");
		cmd.add(LeJOSUtils.buildClasspath(new File(workingDir), pcclasspath));

		cmd.add("lejos.pc.tools.NXJLink");

		cmd.add("--bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(new File(workingDir), nxtclasspath));
		cmd.add("--writeorder");
		cmd.add("\"LE\"");
		cmd.add("--classpath");
		cmd.add("\".\"");

		cmd.add("AGV");
		cmd.add("--output");
		cmd.add("\"AGV.nxj\"");
		cmd.add("--outputdebug");
		cmd.add("\"AGV.dbg\"");
		cmd.add("--verbose");
		
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.directory(new File(projectDir));
			builder.inheritIO();
			Process process = builder.start();
			process.waitFor();
		} catch( IOException | InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
	public static void compile( String projectDir, String workingDir ) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(LeJOSUtils.getJavaHome() + "c");

		cmd.add("-bootclasspath");
		cmd.add(LeJOSUtils.buildClasspath(new File(workingDir), nxtclasspath));

		cmd.add("-extdirs");
		cmd.add("\"\"");

//		cmd.add("-sourcepath");
//		cmd.add(projectDir);
		cmd.add("*.java");
		
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);
			builder.directory(new File(projectDir));
			builder.inheritIO();
			Process process = builder.start();
			process.waitFor();
		} catch( IOException | InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
}
