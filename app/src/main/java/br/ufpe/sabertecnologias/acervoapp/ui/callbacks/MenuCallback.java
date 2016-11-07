package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import jpttrindade.widget.tagview.Tag;

public interface MenuCallback {
	public void showFiltro(int id, MenuView.TYPE type);
	public void showTransferindo();
	public void showMeusGrupos();
	public void hideFiltro(int id);
	public void removeTag(Tag tag);
	public void addTag(Tag tag);
	public void habilitaToolbarContextual(int valorParaGruposSelecionados, int itemStatus, boolean disable);
}
