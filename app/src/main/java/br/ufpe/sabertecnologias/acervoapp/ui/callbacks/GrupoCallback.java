package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;

/**
 * Created by joaotrindade on 25/10/16.
 */

public interface GrupoCallback{
    public void transferirItem(Item item);
    public void showDetalhes(Item itemToEdit);
    public void backToMeuAcervo();
    /**
     * @param valorParaGruposSelecionados valor a somar ao total atual. Se algum grupo foi deselecionado, será -1,
     *                                    se foi selecionado 1, mas se nenhum desses, então 0.
     * */
    public void habilitaToolbarContextual(int valorParaGruposSelecionados, int itemStatus, boolean disable);
}