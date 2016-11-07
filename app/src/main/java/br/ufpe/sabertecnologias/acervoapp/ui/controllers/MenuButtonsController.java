package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.MenuButtonsCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuButtonsView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuButtonsViewAcervoRemoto;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import jpttrindade.widget.tagview.Tag;

public class MenuButtonsController extends Fragment {
    public static final String TAG = "menuButtonsController";
    private CONTEXTO contexto = CONTEXTO.MeuAcervo;

    public static final int ID_TEXTO = 1;
    public static final int ID_AUDIO = 2;
    public static final int ID_VIDEO = 3;
    public static final int ID_IMAGEM = 4;

    private int idTipoSelecionado;

    public static enum CONTEXTO {MeuAcervo, AcervoRemoto};

    private MenuButtonsView mView;
    private ViewListener mListener;

    private MenuButtonsCallback callback;

    private int selecetedFiltroId,selecetedFiltroTipoId;


    public MenuButtonsController init(CONTEXTO ctx, int idTipoSelecionado){
        this.idTipoSelecionado = idTipoSelecionado;
        contexto = ctx;
        setRetainInstance(true);
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (MenuButtonsCallback) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mView == null) {
            mListener = new ViewListener() {

                @Override
                public void onClick(View v) {
                    Tag tag = null;
                    Integer id = null;
                    Object obj = v.getTag();
                    if(obj instanceof Tag){
                        tag = (Tag) obj;
                        onClickFiltro(v, tag);
                    }else{
                        id = (Integer) obj;
                        onClickOptions(v, id);
                    }
                }

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            };

            switch (contexto){
                case MeuAcervo:
                    mView = new MenuButtonsView(inflater, container, mListener);
                    mView.selectFiltro(idTipoSelecionado);
                    break;
                case AcervoRemoto:
                    mView = new MenuButtonsViewAcervoRemoto(inflater, container, mListener);
                    mView.selectFiltro(idTipoSelecionado);
                    break;
            }
        } else {
            fecharBotoes();
            abrirBotoes();
        }
        return mView.getView();
    }

    protected void onClickOptions(View v, Integer id) {
        switch (v.getId()){
            case R.id.floating_fling_novo_grupo_item:
            case R.id.txt_view_menu_novo_grupo:
                callback.showFiltro(MenuView.ID_NOVO_GRUPO, MenuView.TYPE.ACERVOREMOTO);
                break;
            case R.id.floating_fling_root_item:
            default:
                callback.removerMenuDeBotoes();
            break;
        }
    }

    protected void onClickFiltro(View v, Tag tag) {
        if(tag != null){
            if(!v.isSelected()) {
                Tag old_tag = mView.selectFiltro(tag.id);
                if(old_tag!=null){
                    callback.removeTag(old_tag);
                }
                callback.addTag(tag);
            }
            callback.removerMenuDeBotoes();
        }
    }

    public void selectFiltro(int id) {
        if(mView!= null) {
            Tag t = mView.selectFiltro(id);
            if (t != null) {
                selecetedFiltroTipoId = id;
            } else {
                selecetedFiltroId = id;
            }
        }
        idTipoSelecionado = id;
    }

    public void deselectFiltro(int id) {
        if(mView!= null) {
            if (mView.deselectFiltro(id) == null) {
                selecetedFiltroId = -1;
            } else {
                selecetedFiltroTipoId = 0;
            }
        }
        idTipoSelecionado = 0;
    }

    public void abrirBotoes(){
        if(mView!= null)
            mView.abrirBotoes();
    }

    public void fecharBotoes(){
        if(mView != null)
            mView.fecharBotoes();
    }

    public void exit(View view) {
        callback.removerMenuDeBotoes();
    }

}
