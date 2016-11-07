package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import jpttrindade.widget.tagview.Tag;

/**
 * Created by joaotrindade on 25/10/16.
 */


public interface BuscaControllerCallback{
    public void showResultadoBusca();
    public void addTag(Tag tag);
    public void transferirItem(Item i);
    public void showDetalhes(Item i);

}