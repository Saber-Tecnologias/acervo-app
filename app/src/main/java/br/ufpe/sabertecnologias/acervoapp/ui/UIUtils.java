package br.ufpe.sabertecnologias.acervoapp.ui;

import android.content.Context;

import java.util.Locale;

public class UIUtils {
	
	public static boolean isTablet(Context ctx){
		return ctx.getResources().getConfiguration().smallestScreenWidthDp >= 600;
	}
	
	public static String tamanhoFormat(String tamanho){
		
		float tamanho_MB = Float.parseFloat(tamanho)/1000000f;
		String tamanho_final = tamanho_MB >= 1000 
				? String.format(Locale.ENGLISH,"%.2fGB",tamanho_MB/1000) 
				: String.format(Locale.ENGLISH,"%.2fMB", tamanho_MB);
		return tamanho_final;
	}
	
	

}
