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
 * Parses the debug output produced while linking a lejos binary and stores the
 * information about classes and methods for structured access.
 * 
 * @author Jonas Neugebauer <jonas.neugebauer@upb.de>
 * 
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

	public Collection<DebugClassItem> getClasses() {
		return this.classes.values();
	}

	public DebugClassItem getClassItem( int no ) {
		return getClassItem(no + "");
	}

	public DebugClassItem getClassItem( String no ) {
		if( classes.containsKey(no) )
			return classes.get(no);
		else
			return null;
	}

	public Collection<DebugMethodItem> getMethods() {
		return this.methods.values();
	}

	public DebugMethodItem getMethodItem( int no ) {
		return getMethodItem(no + "");
	}

	public DebugMethodItem getMethodItem( String no ) {
		if( methods.containsKey(no) )
			return methods.get(no);
		else
			return null;
	}

	public void fromString( String str ) {
		try {
			this.parse(new StringReader(str));
		} catch( IOException ex ) {
			// Won't happen
		}
	}

	public void fromInputStream( InputStream in ) throws IOException {
		this.parse(new InputStreamReader(in));
	}

	private void parse( Reader read ) throws IOException {
		BufferedReader bfr = new BufferedReader(read);

		this.classes.clear();
		this.methods.clear();

		String line;
		do {
			line = bfr.readLine();

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
		} while( line != null );
	}

	public static class DebugItem {
		public int no;
		public String name;

		public DebugItem( int no, String name ) {
			this.no = no;
			this.name = name;
		}
	}

	public static class DebugClassItem extends DebugItem {
		public String file;

		public DebugClassItem( int no, String name ) {
			super(no, name);
		}

		public String toString() {
			return "Class " + no + ": " + name;
		}
	}

	public static class DebugMethodItem extends DebugItem {
		public DebugMethodItem( int no, String name ) {
			super(no, name);
		}

		public String toString() {
			return "Method " + no + ": " + name;
		}
	}

}
