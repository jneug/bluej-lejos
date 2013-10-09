package de.upb.bluej.lejos;

import java.io.File;

import bluej.extensions.BClass;
import bluej.extensions.BProject;
import bluej.extensions.ProjectNotOpenException;

/**
 * Bundles the different behaviors of different leJOS versions.
 * 
 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
 */
public abstract class LeJOSDistribution {

	private static LeJOSDistribution[] versions = new LeJOSDistribution[] {
			new LeJOS08(),
			new LeJOS09() };

	public static LeJOSDistribution[] getSupportedLeJOSVersions() {
		return versions;
	}

	public static boolean isLeJOSVersionSupported( String version ) {
		for( LeJOSDistribution dist: versions ) {
			if( dist.getVersion().equals(version) )
				return true;
		}
		return false;
	}

	public static LeJOSDistribution getLeJOSVersion( String version ) {
		for( LeJOSDistribution dist: versions ) {
			if( dist.getVersion().equals(version) )
				return dist;
		}
		return null;
	}

	public static LeJOSDistribution getLatestLeJOSVersion() {
		return versions[versions.length - 1];
	}



	protected File directory = null;

	private String version;

	public LeJOSDistribution( String version ) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public String toString() {
		return getVersion();
	}

	public void setDirectory( File path ) {
		this.directory = path;
	}

	public File getDirectory() {
		return this.directory;
	}
	
	public abstract File[] getNxtClasspathFiles();

	/**
	 * Checks if the current {@link #getDirectory() directory} points to a valid
	 * leJOS distribution for this version. Should check for all necessary files
	 * and libraries to perform all functions provided by a leJOS distribution.
	 * <p/>
	 * Will be called every time the preferences are saved (even if the lejos
	 * folder did not change) and once on startup to verify the selected
	 * distribution is still valid.
	 * 
	 * @return
	 */
	public abstract boolean isValid();

	/**
	 * Checks if the current {@code path} points to a valid leJOS distribution
	 * for this version. Should check for all necessary files and libraries to
	 * perform all functions provided by a leJOS distribution.
	 * <p/>
	 * Will be called every time the preferences are saved (even if the lejos
	 * folder did not change) and once on startup to verify the selected
	 * distribution is still valid.
	 * 
	 * @param path
	 * @return
	 */
	public abstract boolean isValid( File path );

	/**
	 * Creates a {@link ProcessBuilder} configured to flash a connected NXT with
	 * the firmware for this leJOS version. The method should only return the
	 * ProcessBuilder and not start the process.
	 * 
	 * @return A properly configured ProcessBuilder to invoke a new flash
	 *         process
	 */
	public abstract ProcessBuilder invokeFlash();

	/**
	 * Creates a {@link ProcessBuilder} configured to compile the provided
	 * {@link BProject project}. All source files within the project have to be
	 * compiled for upload to a NXT. The method should only return the
	 * ProcessBuilder and not start the process.
	 * 
	 * @param project
	 * @return A properly configured ProcessBuilder to invoke a new compile
	 *         process
	 */
	public abstract ProcessBuilder invokeCompile( BProject project )
			throws ProjectNotOpenException;

	/**
	 * Creates a {@link ProcessBuilder} configured to compile the provided
	 * {@link BClass classes}. Only the given classes and their dependencies
	 * will be compiled. The method should only return the ProcessBuilder and
	 * not start the process.
	 * 
	 * @param classes
	 * @return A properly configured ProcessBuilder to invoke a new compile
	 *         process
	 */
	public abstract ProcessBuilder invokeCompile( BClass[] classes )
			throws ProjectNotOpenException;

	/**
	 * Creates a {@link ProcessBuilder} configured to create a nxj binary file
	 * from the provided {@link BClass main class}. The returned builder should
	 * only be configured for the linking process. All necessary compilations
	 * will be handled by the extension using the compile methods of the
	 * distribution. The method should only return the ProcessBuilder and not
	 * start the process.
	 * 
	 * @param main_class
	 * @return A properly configured ProcessBuilder to invoke a new linking
	 *         process
	 */
	public abstract ProcessBuilder invokeLink( BClass main_class )
			throws ProjectNotOpenException;

	/**
	 * Creates a {@link ProcessBuilder} configured to upload a nxj binary file
	 * of the {@link BClass main class} to a connected NXT. The returned builder
	 * should only be configured for the upload process. All necessary
	 * compilations and linking operations will be handled by the extension
	 * using the methods of the distribution. The method should only return the
	 * ProcessBuilder and not start the process.
	 * 
	 * @param main_class
	 * @return A properly configured ProcessBuilder to invoke a new linking
	 *         process
	 */
	public abstract ProcessBuilder invokeUpload( BClass main_class )
			throws ProjectNotOpenException;

	/**
	 * Creates a {@link ProcessBuilder} configured to upload and run a nxj
	 * binary file of the {@link BClass main class} to a connected NXT. The
	 * returned builder should only be configured for the upload and run
	 * process. All necessary compilations and linking operations will be
	 * handled by the extension using the methods of the distribution. The
	 * method should only return the ProcessBuilder and not start the process.
	 * 
	 * @param main_class
	 * @return A properly configured ProcessBuilder to invoke a new linking
	 *         process
	 */
	public abstract ProcessBuilder invokeUploadAndRun( BClass main_class )
			throws ProjectNotOpenException;

}
