package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;

/**
 * Created by joaotrindade on 25/10/16.
 */
public interface GruposCallback{
    public void abrirGrupo(Grupo g);
    public void exit();
    void notifyGrupoController(ArrayList<Grupo> mGrupos);
}