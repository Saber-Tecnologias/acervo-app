package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;

public class DatabaseObserverManager {

	private static RepositorioGrupo2 rg;
	private static RepositorioItem2 ri;
	private static RepositorioUser2 ru;

	public static void init(Context ctx) {
		rg = RepositorioGrupo2.getInstance(ctx);
		ri = RepositorioItem2.getInstance(ctx);
		ru = RepositorioUser2.getInstance(ctx);
	}
	
	
	public static void addRepositorioItemObserver(RepositorioObserver observer, int id) {
		ri.addObserver(observer, id);
	}
	
	public static void removeRepositorioItemObserver(RepositorioObserver observer) {
		ri.removeObserver(observer);
	}
	
	public static void addRepositorioGrupoObserver(RepositorioObserver observer, int id) {
		rg.addObserver(observer, id);
	}
	
	public static void removeRepositorioGrupoObserver(RepositorioObserver observer) {
		rg.removeObserver(observer);
	}
	
	public static void addRepositorioUserObserver(RepositorioObserver observer, int id) {
		ru.addObserver(observer, id);
	}
	
	public static void removeRepositorioUserObserver(RepositorioObserver observer) {
		ru.removeObserver(observer);
	}
	
	
	

	
}
