package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import jpttrindade.widget.tagview.Tag;

/**
 * Created by joaotrindade on 25/10/16.
 */
public interface MenuButtonsCallback {
    /** Calls for the activity to do the back removing this fragment from view */
    void removerMenuDeBotoes();
    public void showFiltro(int id, MenuView.TYPE type);
    public void showMeusGrupos();
    public void removeTag(Tag tag);
    public void addTag(Tag tag);

}
