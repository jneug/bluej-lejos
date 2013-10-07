package de.upb.bluej.lejos;

import java.io.File;

import bluej.extensions.BClass;
import bluej.extensions.BMethod;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.ProjectNotOpenException;

public class LeJOSUtils {

	/**
	 * Checks if the provided class has a main method.
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean hasMain( BClass clazz ) {
		try {
			BMethod main = clazz.getMethod("main",
					new Class<?>[] { String[].class });
			return (main != null);
		} catch( ProjectNotOpenException | ClassNotFoundException e ) {
			return false;
		}
	}

	/**
	 * Read the {@code NXJ_HOME} environment variable and return an empty string
	 * if it is not set.
	 * 
	 * @return
	 */
	public static String getNxjHomeEnv() {
		String nxjHome = System.getenv("NXJ_HOME");
		if( nxjHome == null )
			nxjHome = "";
		return nxjHome;
	}

	/**
	 * Returns the path to the {@code java} command to run new java processes.
	 * It will not check if the returned java command is valid.
	 * 
	 * @return
	 */
	public static String getJavaHome() {
		String java_home = System.getenv("LEJOS_NXT_JAVA_HOME");
		if( java_home != null )
			return java_home + "/bin/java";

		java_home = System.getenv("JAVA_HOME");
		if( java_home != null )
			return java_home + "/bin/java";

//		java_home = System.getProperty("java.home");
//		if( java_home != null )
//			return java_home+"/bin/java";

		return "java";
	}


	/**
	 * Checks if the "-d32" flag need to be present for invoked java processes.
	 * This is necessary for running leJOS on Mac OS.
	 * 
	 * @return
	 */
	public static boolean isForce32() {
		String os_name = System.getProperty("os.name");
		return (os_name != null && os_name.startsWith("MAC"));
	}

	/**
	 * Builds a classpath string for the provided absolute path names.
	 * 
	 * @param paths
	 * @return
	 */
	public static String buildClasspath( String[] paths ) {
		return buildClasspath(null, paths);
	}

	/**
	 * Builds a classpath string for the provided path names relative to the
	 * provided root directory.
	 * 
	 * @param root
	 * @param paths
	 * @return
	 */
	public static String buildClasspath( File root, String[] paths ) {
		if( paths.length == 0 )
			return "";

		String dir = "";
		if( root != null ) {
			if( root.isDirectory() )
				dir = root.getAbsolutePath() + File.separator;
			else
				dir = root.getParentFile().getAbsolutePath() + File.separator;
		}

		String sep = System.getProperty("path.separator");
		if( sep == null )
			sep = ":";

		String cp = dir + paths[0];
		for( int i = 1; i < paths.length; i++ ) {
			cp += sep + dir + paths[i];
		}

		return cp;
	}

}
