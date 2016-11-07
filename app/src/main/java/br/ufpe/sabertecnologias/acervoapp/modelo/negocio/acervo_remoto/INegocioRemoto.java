package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto;

public interface INegocioRemoto {

	public boolean hasNextPage();
	public void addTermoBuscaAcervoRemoto(String termo, TipoTermoBusca tipoTermo);
	public void getNextPageBusca();
	public void removeAllTermos();
	public void removeTermo(String termo, TipoTermoBusca tipoTermo);
	public void setBuscaResponseListener(BuscaResponseCallback listener);



	
}
