package de.upb.bluej.lejos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the debug output produced while linking a leJOS binary and stores the
 * information about classes and methods for structured access.
 * 
 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
 */
public class LeJOSDebug {

	private static final Pattern P_CLASS = Pattern
			.compile("^Class (\\d+): ([.a-zA-Z0-9_$]+)(.*)$");
	private static final Pattern P_METHOD = Pattern
			.compile("^Method (\\d+): ([.a-zA-Z0-9_()<>, ]*) PC (.*)$");

	private Map<String, DebugClassItem> classes = new HashMap<String, DebugClassItem>();
	private Map<String, DebugMethodItem> methods = new HashMap<String, DebugMethodItem>();

	public LeJOSDebug() {
	}

	/**
	 * Clears all debug information.
	 */
	public void clear() {
		classes.clear();
		methods.clear();
	}

	/**
	 * @return The collection of class information currently available
	 */
	public Collection<DebugClassItem> getClasses() {
		return this.classes.values();
	}

	/**
	 * @param no
	 *            A class number
	 * @return The corresponding class information of {@code null}
	 */
	public DebugClassItem getClassItem( int no ) {
		return getClassItem(no + "");
	}

	/**
	 * @param no
	 *            A class number
	 * @return The corresponding class information of {@code null}
	 */
	public DebugClassItem getClassItem( String no ) {
		if( classes.containsKey(no) )
			return classes.get(no);
		else
			return null;
	}

	/**
	 * @return The collection of method information currently available
	 */
	public Collection<DebugMethodItem> getMethods() {
		return this.methods.values();
	}

	/**
	 * @param no
	 *            A method number
	 * @return The corresponding method information of {@code null}
	 */
	public DebugMethodItem getMethodItem( int no ) {
		return getMethodItem(no + "");
	}

	/**
	 * @param no
	 *            A method number
	 * @return The corresponding method information of {@code null}
	 */
	public DebugMethodItem getMethodItem( String no ) {
		if( methods.containsKey(no) )
			return methods.get(no);
		else
			return null;
	}

	/**
	 * Parses a string into class or method information. If the provided line
	 * matches one of the patterns for a class or method debug output it will be
	 * inserted into the appropriate collection.
	 * 
	 * @param line
	 */
	public void parseLine( String line ) {
		if( line != null && !line.isEmpty() ) {
			Matcher m_class = P_CLASS.matcher(line);
			Matcher m_method = P_METHOD.matcher(line);

			if( m_class.matches() ) {
				int no = Integer.parseInt(m_class.group(1));
				DebugClassItem dci = new DebugClassItem(no,
						m_class.group(2));
				classes.put(no + "", dci);
			} else if( m_method.matches() ) {
				int no = Integer.parseInt(m_method.group(1));
				DebugMethodItem dmi = new DebugMethodItem(no,
						m_method.group(2));
				methods.put(no + "", dmi);
			}
		}
	}

	/**
	 * Parses a string line by line debug information. If you want to parse just
	 * a single line use {@link #parseLine(String)}.
	 * 
	 * @param str
	 */
	public void fromString( String str ) {
		try {
			this.parse(new StringReader(str));
		} catch( IOException ex ) {
			// Won't happen
		}
	}

	/**
	 * Parses the data from an {@code InputStream} into debug information. The
	 * data is read line by line until the stream is closed or throws an
	 * exception.
	 * 
	 * @param in
	 * @throws IOException
	 */
	public void fromInputStream( InputStream in ) throws IOException {
		this.parse(new InputStreamReader(in));
	}

	/**
	 * Parses data from an Reader into debug information.
	 * 
	 * @param read
	 * @throws IOException
	 */
	private void parse( Reader read ) throws IOException {
		BufferedReader bfr = new BufferedReader(read);

		this.classes.clear();
		this.methods.clear();

		String line;
		do {
			line = bfr.readLine();
			this.parseLine(line);
		} while( line != null );
	}

	/**
	 * General base class for leJOS debug information.
	 * 
	 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
	 */
	public static class DebugItem {
		public int no;
		public String name;

		public DebugItem( int no, String name ) {
			this.no = no;
			this.name = name;
		}
	}

	/**
	 * Debug information for a class.
	 * 
	 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
	 */
	public static class DebugClassItem extends DebugItem {
		public String file;

		public DebugClassItem( int no, String name ) {
			super(no, name);
		}

		public String toString() {
			return "Class " + no + ": " + name;
		}
	}

	/**
	 * Debug information for a method.
	 * 
	 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
	 */
	public static class DebugMethodItem extends DebugItem {
		public DebugMethodItem( int no, String name ) {
			super(no, name);
		}

		public String toString() {
			return "Method " + no + ": " + name;
		}
	}

}
