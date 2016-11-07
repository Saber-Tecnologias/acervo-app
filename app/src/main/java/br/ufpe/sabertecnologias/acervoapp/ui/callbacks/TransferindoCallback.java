package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;

/**
 * Created by joaotrindade on 25/10/16.
 */
public interface TransferindoCallback{
    public void piscarTransferindo();
    public void showDetalhes(Item item);
    public void addTransferencia();
    public void setTransferencias(int qtd_transferencias);
    public void finishTransferencia();
}