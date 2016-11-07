package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemComplementoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.BuscaResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.RecentesCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.RecentesView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentConfirmarTransferencia;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

/**
 * Created by joaotrindade on 02/02/16.
 */
public class RecentesController extends Fragment implements RepositorioObserver.RepositorioObserverCallback, BuscaResponseCallback{

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TAB_TEXTOS, TAB_AUDIOS, TAB_VIDEOS, TAB_IMAGENS})

    public @interface Tab{}
    public static final int TAB_TEXTOS = 1;
    public static final int TAB_AUDIOS = 2;
    public static final int TAB_VIDEOS = 3;
    public static final int TAB_IMAGENS = 4;

    private RecentesView mView;

    private int mTipo;
    private Facade facade;
    private ViewListener mListener;
    private ArrayList<Integer> itensCodigos;
    private ArrayList<Item> mDataset;

    private RecentesCallback callback;

    public RecentesController init(@Tab int tipo, Context ctx, ArrayList<Integer>  itensCodigos){

        mTipo = tipo;

        facade = (Facade) ctx.getApplicationContext();

        this.itensCodigos = itensCodigos;

        String tipoBusca = "";
        mDataset = new ArrayList<Item>();
        switch (tipo){
            case RecentesController.TAB_TEXTOS:
                tipoBusca = "Texto";
                break;
            case RecentesController.TAB_AUDIOS:
                tipoBusca = "Áudio";
                break;
            case RecentesController.TAB_VIDEOS:
                tipoBusca =  "Vídeo";
                break;
            case RecentesController.TAB_IMAGENS:
                tipoBusca = "Imagem";
                break;
        }



        try {
            facade.buscarRecentes(tipoBusca, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setRetainInstance(true);

        return this;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (RecentesCallback) context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if(mView == null){

            mView = new RecentesView(inflater, container, mListener);

            mListener = new ViewListener() {
                @Override
                public void onClick(View v) {

                    Item i;
                    switch (v.getId()) {
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


                        case R.id.tv_titulo:
                            DebugLog.d(this, "Titulo");
                            i = (Item) v.getTag();
                            callback.showDetalhes(i);
                            break;
                        default:
                            DebugLog.d(this, "Card");
                            i = (Item) v.getTag();

                            callback.showDetalhes(i);

                            break;
                    }


                }

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            };

            mView.setAdapter(getContext(), mListener);

        }


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

    public void updateFromDetalhes(Item item) {
        DebugLog.d(this, "updateFromDetalhes");
        Item it;
        ArrayList<Item> its = mDataset;
        if(its != null) {
            for (int i = 0; i < its.size(); i++) {
                it = its.get(i);
                if (it.getCodigo() == item.getCodigo()) {

                    DebugLog.d(this, mTipo+ " - for OK");

                    its.set(i, item);
                    i = its.size();
                }
            }
        }

        if(mView!=null) mView.notifyDatasetChanged(mDataset);
    }



    @Override
    public void updateFromRepositorio(int method, Object obj) {
        Item item  = (Item) obj;
        updateFromDetalhes(item);
    }



    @Override
    public void onResponse(ArrayList<Item> items, boolean isNext) {
        mDataset.clear();
        mDataset.addAll(items);

        for (Item item : mDataset) {
            if(RecentesController.this.itensCodigos.contains(item.getCodigo())){
                item.setStatus(Item.FLAG_ITEM_BAIXADO);
            }
        }

        if(mView!=null) mView.notifyDatasetChanged(items);

    }

    @Override
    public void onError( ) {

    }

    @Override
    public void onRequest() {

    }

    @Override
    public void onCancell() {

    }

}
