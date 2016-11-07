package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemComplementoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.NegocioRecomendacao;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.RecomendacoesCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.RecomendacoesResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.RecomendacoesView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentConfirmarTransferencia;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import jpttrindade.widget.tagview.Tag;

/**
 * Created by jpttrindade on 22/11/15.
 */
public class RecomendacoesController extends Fragment implements RecomendacoesResponseCallback, RepositorioObserver.RepositorioObserverCallback {


    private static final int REPOSITORIO_OBSERVER_ID = 9633;
    private RecomendacoesCallback callback;



    public static final String TAG = "recomendacoesController";
    private Context mContext;
    private ViewListener mListener;
    private RecomendacoesView mView;

    private Tag tagLabel1, tagLabel2, tagLabel3;
    private Facade facade;


    private ArrayList<Item> its1, its2, its3;
    private ArrayList<Integer> itensCodigos;

    public RecomendacoesController init(Context ctx, ArrayList<Integer> itensCodigos){
        DebugLog.d(this, "init");
        mContext = ctx;
        facade = (Facade) ctx.getApplicationContext();
        this.itensCodigos = itensCodigos;
        getTagsLabels();
        setRetainInstance(true);
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (RecomendacoesCallback) context;
    }

    private void getTagsLabels() {


        //Funçao assincrona para selecinar randomicamente as labels.

        tagLabel1 = new Tag("Texto", Color.parseColor("#f2a32c"),
                R.drawable.ic_filtro_texto_white, false);

        tagLabel1.setType(TagsController.TYPE_TIPO);
        tagLabel1.setID(MenuView.ID_TEXTO);

        tagLabel2 = new Tag("Áudio", Color.parseColor("#f2a32c"),
                R.drawable.ic_filtro_audio_white, false);

        tagLabel2.setType(TagsController.TYPE_TIPO);
        tagLabel2.setID(MenuView.ID_AUDIO);

        tagLabel3 = new Tag("Vídeo", Color.parseColor("#f2a32c"),
                R.drawable.ic_filtro_video_white, false);

        tagLabel3.setType(TagsController.TYPE_TIPO);
        tagLabel3.setID(MenuView.ID_VIDEO);



        // por fim tem que disparar as buscas dos respectivos itens.


    }

    private int randomTypeLabel() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DebugLog.d(this, "onCreateView");

        if(mView == null) {
            mListener = new ViewListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onClick(View v) {
                    Tag t;
                    switch (v.getId()){
                        case R.id.v1:
                        case R.id.v2:
                        case R.id.v3:
                            t = (Tag) v.getTag();
                            DebugLog.d(this, t.text);
                            callback.addTagFromRecomendacoes(t);
                            break;
                        case R.id.card:
                            Item i = (Item) v.getTag();
//                            callback.addTagFromRecomendacoes(i.getName());
//                            callback.addTagFromRecomendacoes(i.getInstituicoes());
                            callback.showDetalhes(i);
                            break;

                        case R.id.bt_baixar:
                            i = (Item) v.getTag();
                            if(i.getStatus() == Item.FLAG_ITEM_BAIXADO){
                                abrirItem(i);
                            } else if(i.getStatus() == Item.FLAG_ITEM_ENFILEIRADO || i.getStatus() == Item.FLAG_ITEM_BAIXANDO){
                                facade.cancelaDownload(new AsyncExecutor(null), i.getCodigo());
                                cancelDownloadItem(i);
                            } else{
                                confirmarTransferencia(i);
                            }
                            break;
                        case R.id.bt_confirmar_transferir:
                            i = (Item) v.getTag(R.id.bt_confirmar_transferir);
                            int option = (Integer)v.getTag();
                            DebugLog.d(this, "Option = "+option);

                            transferirItem(i);
                            break;
                    }

                }
            };


            mView = new RecomendacoesView(inflater, container, mListener);


          //  observer = new RepositorioObserver(this);
          //  DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID);

            facade.buscarListaRecomendacoes(this, NegocioRecomendacao.TYPE_TEXTO);
            mView.setTagLabels(tagLabel1, tagLabel2, tagLabel3);



        }

        //mView.setAdapters(its1, its2, its3, mListener,null);

        return mView.getView();
    }

    private void cancelDownloadItem(Item i) {
        i.setStatus(Item.FLAG_ITEM_DISPONIVEL);
        i.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);
        int idGrupo = i.getIDGrupo() == 0 ? -1 : i.getIDGrupo();
        AsyncExecutor executor = new AsyncExecutor(null);
        facade.attItem(i, idGrupo, executor);
    }

    private void transferirItem(Item i) {
        AsyncExecutor executor = new AsyncExecutor(null);
        facade.transferirItem(i, executor);
        executor = new AsyncExecutor(null);
        facade.empilhaLogItem(executor, i.getCodigo(), LogItemAcaoEnum.ADICIONAR, LogItemComplementoEnum.LOCAL.toString());

    }

    private void confirmarTransferencia(Item i) {
        DialogFragmentConfirmarTransferencia dialog = new DialogFragmentConfirmarTransferencia();
        dialog.init(getActivity(), mListener, i);
        dialog.show(getFragmentManager(), "confirmarTransferenciaDialog");
    }

    private void abrirItem(Item i) {
        facade.abrirItem(i);
    }

    public void setTagsLabel(Tag t1, Tag t2, Tag t3){
        mView.setTagLabels(t1, t2, t3);
    }


    @Override
    public void onResponse(ArrayList<Item> itens, int tipo) {
        DebugLog.d(this, "OnResponse - " + tipo);
        switch (tipo){
            case NegocioRecomendacao.TYPE_TEXTO:

                DebugLog.d(this, "OnResponse - " + itens.size());
                its1 = new ArrayList<>(itens);

                for(Item it : its1){
                    if(itensCodigos.contains(it.getCodigo())){
                        it.setStatus(Item.FLAG_ITEM_BAIXADO);
                    }
                }

                if(mView!= null)
                    mView.setAdapters(tipo, its1, mListener);


                facade.buscarListaRecomendacoes(this, NegocioRecomendacao.TYPE_AUDIO);

                //mView.setAdapters();
                break;
            case NegocioRecomendacao.TYPE_AUDIO:
                its2 = new ArrayList<>(itens);
                facade.buscarListaRecomendacoes(this, NegocioRecomendacao.TYPE_VIDEO);

                for(Item it : its2){
                    if(itensCodigos.contains(it.getCodigo())){
                        it.setStatus(Item.FLAG_ITEM_BAIXADO);
                    }
                }

                if(mView!= null)
                    mView.setAdapters(tipo, its2, mListener);
                break;
            case NegocioRecomendacao.TYPE_VIDEO:
                its3 = new ArrayList<>(itens);

                for(Item it : its3){
                    if(itensCodigos.contains(it.getCodigo())){
                        it.setStatus(Item.FLAG_ITEM_BAIXADO);
                    }
                }
                if(mView != null) {
                    mView.setAdapters(tipo, its3, mListener);
                }
                break;
        }
    }


    public void updateFromDetalhes(Item item) {
        DebugLog.d(this, "updateFromDetalhes");
        ArrayList<Item> its = its1;
        Item it;
        for(int j=0; j<3; j++) {
            if(j == 0) its = its1;
            else if(j == 1) its = its2;
            else if(j==2) its = its3;

            DebugLog.d(this, "j = "+j);

            if(its != null) {
                for (int i = 0; i < its.size(); i++) {
                    it = its.get(i);
                    if (it.getCodigo() == item.getCodigo()) {
                        DebugLog.d(this, "item codigo = " + item.getCodigo());
                        DebugLog.d(this, "item status = " + item.getStatus());

                        its.set(i, item);
                        i = its.size();
                        j = 3;
                    }
                }
            }
        }



        if(mView!=null) mView.notifyDatasetChanged();
    }


    @Override
    public void updateFromRepositorio(int method, Object obj) {
        Item item = (Item) obj;
        updateFromDetalhes(item);

        /*if (obj instanceof Item) {
            Item item = (Item) obj;

            switch (method) {

                case RepositorioObserver.UPDATE:

                    if(item.getStatus() == Item.FLAG_ITEM_BAIXADO){

                        ArrayList<Item> its = its1;
                        Item it;
                        for(int j=0; j<3; j++) {
                            if (j == 0) its = its1;
                            else if (j == 1) its = its2;
                            else if (j == 2) its = its3;

                            DebugLog.d(this, "j = " + j);
                            for (int i = 0; i < its.size(); i++) {
                                it = its.get(i);
                                if (it.getCodigo() == item.getCodigo()) {
                                    its.set(i, item);
                                    i = its.size();
                                    j = 3;
                                }
                            }

                        }
                        if(mView!=null) mView.notifyDatasetChanged();

                    }
                    break;
            }



        }*/
    }


}
