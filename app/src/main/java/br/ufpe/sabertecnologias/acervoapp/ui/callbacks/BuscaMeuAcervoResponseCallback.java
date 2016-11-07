package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;

/**
 * Created by joaotrindade on 20/11/15.
 */
public interface BuscaMeuAcervoResponseCallback {
    public void onResponse(ArrayList<Item> items, boolean isNext);
    public void onRequest();
    public void onCancell();
}
