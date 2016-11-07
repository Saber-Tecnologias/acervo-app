package br.ufpe.sabertecnologias.acervoapp.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileManager {

	public static final String FOLDER = "/AcervoApp";
	private static FileManager mFileManager;

	private Context mContext;

	private FileManager(Context context){
		mContext = context;
	}


	public static FileManager getInstance(Context context){

		if(mFileManager == null){
			mFileManager = new FileManager(context.getApplicationContext());
		}

		return mFileManager;
	}


	public boolean init() throws ExternalStorageNotFoundException{
		File cacheFolder = null;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
			throw new ExternalStorageNotFoundException();
		} else {

			cacheFolder = new File(FolderControl.getCachePath(mContext));
		}
		

		boolean success = cacheFolder.mkdirs();

		return success;

	}

	private void deleteDirectoryFiles(File dir) {
		for (File f : dir.listFiles()) {
			if(f.isDirectory()) {
				deleteDirectoryFiles(f);
			}
			f.delete();
		}
	}

	public void deleteCache() {
		File f = new File(FolderControl.getCachePath(mContext));
		deleteDirectoryFiles(f);
	}
}
