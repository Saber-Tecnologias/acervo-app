package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;


public interface BuscaResponseCallback {
	
	public void onResponse(ArrayList<Item> items, boolean isNext);
	
	public void onError( );
	
	public void onRequest();
	
	public void onCancell();
}
