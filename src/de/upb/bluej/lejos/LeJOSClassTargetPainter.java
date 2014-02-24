package de.upb.bluej.lejos;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import bluej.extensions.BClassTarget;
import bluej.extensions.BMethod;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.painter.ExtensionClassTargetPainter;

public class LeJOSClassTargetPainter implements ExtensionClassTargetPainter {

	private Image nxjIcon;
	
	private ExtensionClassTargetPainter parent;
	
	public LeJOSClassTargetPainter( ExtensionClassTargetPainter parent ) {
		this.parent = parent;
		
		URL url = getClass().getClassLoader().getResource("de/upb/bluej/lejos/nxj.jpg");
		this.nxjIcon = Toolkit.getDefaultToolkit().getImage(url);
	}
	
	@Override
	public void drawClassTargetBackground( BClassTarget arg0, Graphics2D arg1,
			int arg2, int arg3 ) {
		if( parent != null )
			parent.drawClassTargetBackground(arg0, arg1, arg2, arg3);
	}

	@Override
	public void drawClassTargetForeground( BClassTarget clazz, Graphics2D g,
			int arg2, int arg3 ) {
		if( parent != null )
			parent.drawClassTargetForeground(clazz, g, arg2, arg3);
		
		try {
			BMethod main = clazz.getBClass().getDeclaredMethod("main", new Class<?>[] { String[].class });
			if( main != null ) {
				g.drawImage(nxjIcon, 2, 2, null);
			}
		} catch( ProjectNotOpenException e1 ) {
		} catch( ClassNotFoundException e2 ) {
		} catch( PackageNotFoundException e ) {
		}
	}

}
