package com.opendoorlogistics.api.app;

import java.io.File;

import com.opendoorlogistics.api.io.ImportFileType;
import com.opendoorlogistics.api.scripts.AsynchronousScriptExecutor;
import com.opendoorlogistics.api.tables.ODLDatastoreAlterable;
import com.opendoorlogistics.api.tables.ODLTableAlterable;

public interface ODLApp extends AsynchronousScriptExecutor{
	/**
	 * See the datastore open in the app 
	 * @param newDs
	 * @param filesource
	 */
	void setDatastore(ODLDatastoreAlterable<? extends ODLTableAlterable> newDs, File filesource);
	
	/**
	 * See the scripts directory used by the app
	 * @param directory
	 */
	void setScriptsDirectory(File directory);
	
	/*
	 * Should the Java Virtual Machine halt when the app is disposed?
	 */
	void setHaltJVMOnDispose(boolean halt);
	
	/**
	 * Import the file. If a datastore is already open in the app, the new datastore is
	 * added to the app.
	 * @param file
	 * @param option
	 */
	void importFile(File file, ImportFileType option);
	
	/*
	 * Does closing a datastore require the user to confirm?
	 */
	void setDatastoreCloseNeedsUseConfirmation(boolean needsUserConfirmation);
	
	
	void addOnDisposedListener(AppDisposedListener listener);

}
