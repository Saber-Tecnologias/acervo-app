package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

/**
 * Created by joaotrindade on 25/10/16.
 */

public interface NavMenuCallback {
    public boolean onSelectNavItem(int position);
    public void abrirSuporte();
    public void abrirCreditos();
    public void mostrarTutorial();
    public void showTransferindo();
}
