package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.SearchRecentSuggestionsProvider;

public class RecentSearchProvider extends SearchRecentSuggestionsProvider{
	//TODO RENOMEAR ESSA AUTHORITY
	public final static String AUTHORITY = "br.ufpe.sabertecnologias.acervoapp.repositorios.RecentSearchProvider";
	public final static String  LIMIT_PARAMETER = "LIMIT";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public RecentSearchProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
	
	
}
