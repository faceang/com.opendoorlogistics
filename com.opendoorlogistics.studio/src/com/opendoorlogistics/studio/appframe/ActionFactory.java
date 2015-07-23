package com.opendoorlogistics.studio.appframe;

import java.awt.Desktop;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.opendoorlogistics.api.io.ImportFileType;
import com.opendoorlogistics.core.scripts.execution.ExecutionReportImpl;
import com.opendoorlogistics.core.utils.IOUtils;
import com.opendoorlogistics.core.utils.ui.ExecutionReportDialog;
import com.opendoorlogistics.studio.LoadedDatastore.HasLoadedDatastore;
import com.opendoorlogistics.studio.PreferencesManager;
import com.opendoorlogistics.studio.PreferencesManager.PrefKey;
import com.opendoorlogistics.utils.ui.SimpleAction;

public class ActionFactory {
	public AppFrameAction createUndo(HasLoadedDatastore hld){
		return new AppFrameAction("Undo", "Undo last action", null, "edit-undo-7-32x32.png", true, KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK), hld) {

			@Override
			public void actionPerformed(ActionEvent e) {
				hld.getLoadedDatastore().getDs().undo();
			}

			@Override
			public void updateEnabled() {
				setEnabled(hld.getLoadedDatastore() != null && hld.getLoadedDatastore().getDs().hasUndo());
			}
		};	
	}
	
	public AppFrameAction createRedo(HasLoadedDatastore hld){
		return new AppFrameAction("Redo", "Redo last undone action", null, "edit-redo-7-32x32.png", true, KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK), hld) {
	

		@Override
		public void actionPerformed(ActionEvent e) {
			hld.getLoadedDatastore().getDs().redo();
		}

		@Override
		public void updateEnabled() {
			setEnabled(hld.getLoadedDatastore() != null && hld.getLoadedDatastore().getDs().hasRedo());
		}
	};
	}
	
	public SimpleAction createGotoWebsiteAction(JFrame parent) {
		String shortDescription ="Go to help website";
		String longDescription ="Go to www.opendoorlogistics for help";
		String website = "http://www.opendoorlogistics.com";
		
		return createGotoWebsiteAction(parent, shortDescription, longDescription, website);
	}

	private SimpleAction createGotoWebsiteAction(JFrame parent, String shortDescription, String longDescription, String website) {
		return new SimpleAction(shortDescription,longDescription, "help 16x16.png", "help 32x32.png") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported() && Desktop.getDesktop() != null) {
					Desktop desktop = Desktop.getDesktop();
					ExecutionReportImpl report = new ExecutionReportImpl();
					try {
						desktop.browse(java.net.URI.create(website));
					} catch (Exception e2) {
						report.setFailed(e2);
						ExecutionReportDialog.show(parent, "Failed to open website", report);
					}

				}
			}
		};
	}
	
	public List<AppFrameAction> createFileActions(AbstractAppFrame appFrame) {
		ArrayList<AppFrameAction> ret = new ArrayList<>();
		ret.add(new AppFrameAction("New", "Create new file", null, "document-new-6.png", false, KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK), appFrame) {

			@Override
			public void actionPerformed(ActionEvent e) {
				appFrame.createNewDatastore();
			}
		});

		ret.add(new AppFrameAction("Open", "Open file", null, "document-open-3.png", false, KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.Event.CTRL_MASK), appFrame) {

			@Override
			public void actionPerformed(ActionEvent e) {
				appFrame.openDatastoreWithUserPrompt();
			}
		});

		ret.add(null);

		ret.add(new AppFrameAction("Close", "Close file", null, "document-close-4.png", true, KeyStroke.getKeyStroke(KeyEvent.VK_W, java.awt.Event.CTRL_MASK), appFrame) {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!appFrame.canCloseDatastore()) {
					return;
				}
				appFrame.closeDatastore();
			}
		});

		ret.add(null);

		ret.add(new AppFrameAction("Save", "Save file", null, "document-save-2.png", true, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), appFrame) {

			@Override
			public void actionPerformed(ActionEvent e) {
				appFrame.saveDatastoreWithoutUserPrompt(appFrame.getLoadedDatastore().getLastFile());
			}

			@Override
			public void updateEnabled() {

				setEnabled(appFrame.getLoadedDatastore() != null && appFrame.getLoadedDatastore().getLastFile() != null);
			}

		});
		ret.add(new AppFrameAction("Save as", "Save file as", null, "document-save-as-2.png", true, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.ALT_MASK), appFrame) {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser =new JFileChooser();
				chooser.setFileFilter( ImportFileType.EXCEL.getFilter());
				if (appFrame.getLoadedDatastore().getLastFile() != null) {
					chooser.setSelectedFile(appFrame.getLoadedDatastore().getLastFile());
				} else {
					File file = PreferencesManager.getSingleton().getFile(PrefKey.LAST_IO_DIR);
					IOUtils.setFile(file, chooser);
				}
				if (chooser.showSaveDialog(appFrame) == JFileChooser.APPROVE_OPTION) {
					appFrame.saveDatastoreWithoutUserPrompt(chooser.getSelectedFile());
				}

			}
		});

		return ret;
	}

	public List<AppFrameAction> createLoadRecentFilesActions(AbstractAppFrame appFrame){
		ArrayList<AppFrameAction> ret = new ArrayList<AppFrameAction>();
		List<File> recent = PreferencesManager.getSingleton().getRecentFiles();
		for (int i = 0; i < recent.size(); i++) {
			final File file = recent.get(i);
			String s = Integer.toString(i + 1) + ". " + file.getAbsolutePath();
			int maxLen = 100;
			if (s.length() > maxLen) {
				s = s.substring(0, maxLen) + "...";
			}
			ret.add(new AppFrameAction(s, "Load file " + file.getAbsolutePath(), null, null, false, null, appFrame) {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!appFrame.canCloseDatastore()) {
						return;
					}

					appFrame.openFile(file);
					appFrame.updateAppearance();
				}

			});
		}
		return ret;
	}

}
