package br.ufpe.sabertecnologias.acervoapp.util;

import java.io.File;

import android.content.Context;

public class FolderControl {

	public static final String base_folder = "AcervoApp";
	public static final String itens_folder = "itens";
	public static final String cache_folder = "cache";

	public static String getBasePath(Context context){
		return String.format("%s/%s/",context.getExternalFilesDir(null), base_folder);
	}
	
	public static boolean makeDir(Context ctx){
		return new File(getItensPath(ctx)).mkdirs();
	}

	public static String getItensPath(Context ctx) {
		return String.format("%s%s/",getBasePath(ctx), itens_folder);
	}

	public static String getCachePath(Context ctx) {
		return String.format("%s%s/%s/",getBasePath(ctx), itens_folder, cache_folder);
	}
}
