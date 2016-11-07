package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.TipoTermoBusca;

public interface INegocioMeuAcervo {
	public Grupo getDefaultGrupo() ;
	public ArrayList<Integer> getItensCodigos();
	public void getItensByGrupo(int grupoID, IExecutor executor);
	public Item getItemByCodigo(int item_codigo, IExecutor executor);
	public Item getItemByID(int itemId, IExecutor executor);
	public void getItens(IExecutor executor);

	public 	void addItem(Item item, int idGrupo, IExecutor executor);
	public void attItem(Item i, int idGrupo, IExecutor executor);
	public void attItem(List<Item> is, int idGrupo, IExecutor executor);

	public void abrirItem(Item item);
	public void removeItem(Item i, IExecutor executor);
	public void setDefaultGrupoID(int _id);
	
	void addGrupo(Grupo g, IExecutor executor);
	public void addTermoBuscaMeuAcervo(String termo, TipoTermoBusca tipoTermo);

}
