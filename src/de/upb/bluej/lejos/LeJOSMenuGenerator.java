package de.upb.bluej.lejos;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BlueJ;
import bluej.extensions.MenuGenerator;

public class LeJOSMenuGenerator extends MenuGenerator {

	private final LeJOSExtension ext;
	
	private final BlueJ bluej;
	
	public LeJOSMenuGenerator(LeJOSExtension ext) {
		this.ext = ext;
		this.bluej = ext.getBlueJ();
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
		JMenu jm = new JMenu(String.format(bluej.getLabel("menu.class"), ext.getName()));

		if( LeJOSUtils.hasMain(aClass) ) {
			jm.add(new JMenuItem(new AbstractAction(bluej.getLabel("menu.class.run")){
				public void actionPerformed( ActionEvent anEvent ) {
					ext.invokeUploadAndRun(aClass);
				}
			}));
			jm.add(new JMenuItem(new AbstractAction(bluej.getLabel("menu.class.upload")){
				public void actionPerformed( ActionEvent anEvent ) {
					ext.invokeUpload(aClass);
				}
			}));
			if( ext.getPreferences().show_link ) {
				jm.add(new JMenuItem(new AbstractAction(bluej.getLabel("menu.class.link")){
					public void actionPerformed( ActionEvent anEvent ) {
						ext.invokeLink(aClass);
					}
				}));
			}
			if( ext.getPreferences().show_compile )
				jm.addSeparator();
		}

		if( ext.getPreferences().show_compile ) {
			jm.add(new JMenuItem(new AbstractAction(bluej.getLabel("menu.class.compile")){
						public void actionPerformed( ActionEvent anEvent ) {
							ext.invokeCompile(aClass);
						}
					}));
		}
		
		if( jm.getItemCount() == 0 || !ext.isConfigruationValid() )
			jm.setEnabled(false);
		
		return jm;
	}

	@SuppressWarnings("serial")
	class LeJOSViewUIAction extends AbstractAction {
		public LeJOSViewUIAction() {
			putValue(AbstractAction.NAME, bluej.getLabel("menu.view.showDebug"));
		}

		public void actionPerformed( ActionEvent anEvent ) {
			ext.showExtensionUI();
		}
	}

	@SuppressWarnings("serial")
	class LeJOSFlashAction extends AbstractAction {
		public LeJOSFlashAction() {
			putValue(AbstractAction.NAME, bluej.getLabel("menu.tools.flash"));
		}

		public void actionPerformed( ActionEvent anEvent ) {
			ext.invokeFlash();
		}
	}

	@SuppressWarnings("serial")
	class LeJOSCompileAction extends AbstractAction {
		public LeJOSCompileAction() {
			putValue(AbstractAction.NAME, bluej.getLabel("menu.pkg.compile"));
		}

		public void actionPerformed( ActionEvent anEvent ) {
			ext.invokeCompile();
		}
	}


}
