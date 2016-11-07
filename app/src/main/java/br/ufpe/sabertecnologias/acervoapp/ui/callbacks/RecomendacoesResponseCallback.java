package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;

/**
 * Created by jpttrindade on 23/11/15.
 */
public interface RecomendacoesResponseCallback {
    public void onResponse(ArrayList<Item> itens, int tipo);
}
