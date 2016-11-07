package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import jpttrindade.widget.tagview.Tag;

/**
 * Created by joaotrindade on 25/10/16.
 */

public interface RecomendacoesCallback{
    public void addTagFromRecomendacoes(Tag tag);
    public void addTagFromRecomendacoes(String query);
    public void showDetalhes(Item i);
}