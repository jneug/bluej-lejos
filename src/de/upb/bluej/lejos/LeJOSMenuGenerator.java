package de.upb.bluej.lejos;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.MenuGenerator;

public class LeJOSMenuGenerator extends MenuGenerator {

	private final LeJOSExtension ext;
	
	public LeJOSMenuGenerator(LeJOSExtension ext) {
		this.ext = ext;
	}
	
	@Override
	public JMenuItem getViewMenuItem( BPackage bPackage ) {
		return new JMenuItem(new LeJOSViewUIAction());
	}

	@Override
	public JMenuItem getToolsMenuItem(BPackage aPackage) {
		return new JMenuItem(new LeJOSFlashAction());
    }
	
	@Override
	public JMenuItem getPackageMenuItem(BPackage aPackage) {
		return new JMenuItem(new LeJOSCompileAction());
    }
	
	@Override
	@SuppressWarnings("serial")
	public JMenuItem getClassMenuItem( final BClass aClass ) {
		JMenu jm = new JMenu(ext.getName());

		if( LeJOSUtils.hasMain(aClass) ) {
			jm.add(new JMenuItem(new AbstractAction("Run"){
				public void actionPerformed( ActionEvent anEvent ) {
					ext.invokeUploadAndRun(aClass);
				}
			}));
			jm.add(new JMenuItem(new AbstractAction("Upload"){
				public void actionPerformed( ActionEvent anEvent ) {
					ext.invokeUpload(aClass);
				}
			}));
			jm.add(new JMenuItem(new AbstractAction("Link"){
				public void actionPerformed( ActionEvent anEvent ) {
					ext.invokeLink(aClass);
				}
			}));
			jm.addSeparator();
		}

		jm.add(new JMenuItem(new AbstractAction("Compile"){
					public void actionPerformed( ActionEvent anEvent ) {
						ext.invokeCompile(aClass);
					}
				}));
		
		if( !ext.isConfigruationValid() )
			jm.setEnabled(false);
		
		return jm;
	}

	@SuppressWarnings("serial")
	class LeJOSViewUIAction extends AbstractAction {
		public LeJOSViewUIAction() {
			putValue(AbstractAction.NAME, "leJOS Debug");
		}

		public void actionPerformed( ActionEvent anEvent ) {
			ext.showExtensionUI();
		}
	}

	@SuppressWarnings("serial")
	class LeJOSFlashAction extends AbstractAction {
		public LeJOSFlashAction() {
			putValue(AbstractAction.NAME, "Flash NXT");
		}

		public void actionPerformed( ActionEvent anEvent ) {
			ext.invokeFlash();
		}
	}

	@SuppressWarnings("serial")
	class LeJOSCompileAction extends AbstractAction {
		public LeJOSCompileAction() {
			putValue(AbstractAction.NAME, "Compile project for NXT");
		}

		public void actionPerformed( ActionEvent anEvent ) {
			ext.invokeCompile();
		}
	}


}
