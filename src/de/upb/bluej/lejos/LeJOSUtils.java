package de.upb.bluej.lejos;

import java.io.File;

import bluej.extensions.BClass;
import bluej.extensions.BMethod;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

public class LeJOSUtils {
	
	public static final String OS_NAME = getOSName();
	
	static String getOSName() {
		try {
			String os = System.getProperty("os.name");
			return (os == null ? "" : os);
		} catch( SecurityException ex ) {
			return "";
		}
	}

	public static final boolean IS_MAC = isOS("Mac"); 
	public static final boolean IS_LINUX = isOS("Linux") || isOS("LINUX");
	public static final boolean IS_WINDOWS = isOS("Windows");
	public static final boolean IS_UNIX = IS_MAC || IS_LINUX || isOS("AIX","HP-UX","Irix","FreeBSD","OpenBSD","NetBSD","OS/2","Solaris","SunOS");
	
	static boolean isOS(final String osNamePrefix) {
        return OS_NAME.startsWith(osNamePrefix);
    }
	
	static boolean isOS(final String... osNamePrefix) {
		for( String prefix: osNamePrefix) {
			if( !isOS(prefix) )
				return false;
		}
        return true;
    }

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
		} catch( ProjectNotOpenException e1 ) {
			return false;
		} catch( ClassNotFoundException e2 ) {  
			return false;
		}
	}
	
	public static BClass findClassForJavaFile( File f, BProject bproject ) {
		try {
			BPackage[] bpackages = bproject.getPackages();
			for( BPackage bpackage: bpackages ) {
				BClass[] bclasses = bpackage.getClasses();
				for( BClass bclass: bclasses ) {
					if( bclass.getJavaFile().equals(f) )
						return bclass;
				}
			}
		} catch( PackageNotFoundException e1 ) {
		} catch( ProjectNotOpenException e2 ) {
		}
		return null;
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
		return getJavaHome("java");
	}
	
	public static String getJavaHome( String cmd ) {
		String java_home = "";
		String path = cmd;

		java_home = System.getenv("JAVA_HOME");
		if( java_home != null ) {
			path = java_home + File.separator + "bin" + File.separator + cmd;
		}
		
		java_home = System.getenv("LEJOS_NXT_JAVA_HOME");
		if( java_home != null ) {
			path = java_home + File.separator + "bin" + File.separator + cmd;
		}

//		java_home = System.getProperty("java.home");
//		if( java_home != null )
//			return Paths.get(java_home, "bin", "java").toString();

		return path;
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

		String sep = File.pathSeparator;

		String cp = "";
		for( int i = 0; i < paths.length; i++ ) {
			String new_cp = dir + paths[i];
//			if( new_cp.indexOf(" ") != -1 )
//				new_cp = "\""+new_cp+"\"";
			if( cp.isEmpty() )
				cp = new_cp;
			else
				cp += sep + new_cp;
		}

		return cp;
	}
	
	public static String buildClasspath( File[] files ) {
		String[] paths = new String[files.length];
		for( int i = 0; i < files.length; i++ ) {
			paths[i] = files[i].getAbsolutePath();
		}
		return buildClasspath(null, paths);
	}
	
}
