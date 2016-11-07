package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaMeuAcervoControllerCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaMeuAcervoResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.BuscaMeuAcervoView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentConfirmarTransferencia;


public class BuscaMeuAcervoController extends Fragment implements BuscaMeuAcervoResponseCallback, RepositorioObserver.RepositorioObserverCallback {

    public static String TAG = "buscaMinhaController";


    private static final int REPOSITORIO_OBSERVER_ID = 32014;
    private BuscaMeuAcervoControllerCallback callback;
    private Facade facade;
    private ArrayList<Item> mDataset;
    private RepositorioObserver observer;
    private ViewListener listener;
    private BuscaMeuAcervoView mView;
    private boolean onRequest;
    private RecyclerView.OnScrollListener scrollListner;
    private boolean loading;
    private ArrayList<Integer> itensCodigos;
    private Context mContext;



    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        callback = (BuscaMeuAcervoControllerCallback)ctx;
    }

    public BuscaMeuAcervoController init(Context ctx) {
        mContext = ctx;
        facade = (Facade) ctx.getApplicationContext();
        facade.setBuscaMeuAcervoResponseListener(this);
        mDataset = new ArrayList<Item>();
        itensCodigos = new ArrayList<Integer>();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                itensCodigos = facade.getItensCodigos();
                return null;
            }

        }.execute();


        observer = new RepositorioObserver(this);
        DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID);
        setRetainInstance(true);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID);
        if(mView == null){
            listener = new ViewListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}

                @Override
                public void onClick(View v) {
                    Item i;
                    switch (v.getId()){
                        case R.id.tv_titulo:
                        case R.id.card:
                            i = (Item) v.getTag();
                            callback.showDetalhes(i);
                            break;
                        case R.id.bt_baixar:
                            i = (Item) v.getTag();
                            if(i.getStatus() == Item.FLAG_ITEM_BAIXADO){
                                abrirItem(i);
                            } else if(i.getStatus() == Item.FLAG_ITEM_DISPONIVEL) {
                                confirmarTransferencia(i);
                            } else if(i.getStatus() == Item.FLAG_ITEM_ENFILEIRADO || i.getStatus() == Item.FLAG_ITEM_BAIXANDO){
                                facade.cancelaDownload(new AsyncExecutor(null), i.getCodigo());
                                cancelDownloadItem(i);
                            }
                            break;
                        case R.id.bt_confirmar_transferir:
                            i = (Item) v.getTag(R.id.bt_confirmar_transferir);
                            int option = (Integer)v.getTag();
                            transferirItem(i);
                            break;
                    }
                }
            };
            mView = new BuscaMeuAcervoView(inflater, container, listener);
            if(onRequest){
                mView.showProgress();
            }
            scrollListner = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) { }
            };
            mView.setAdapter(mDataset, scrollListner);
        }
        mView.setLayoutManager();
        return mView.getView();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void abrirItem(Item i) {
        facade.abrirItem(i);
    }

    public void confirmarTransferencia(Item i) {
        DialogFragmentConfirmarTransferencia dialog = new DialogFragmentConfirmarTransferencia();
        dialog.init(getActivity(), listener, i);
        dialog.show(getFragmentManager(), "confirmarTransferenciaDialog");
    }

    private void transferirItem(Item i) {
        AsyncExecutor executor = new AsyncExecutor(null);
        facade.transferirItem(i, executor);
    }

    protected void cancelDownloadItem(final Item i) {
        i.setStatus(Item.FLAG_ITEM_DISPONIVEL);
        i.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);
        int idGrupo = i.getIDGrupo() == 0 ? -1 : i.getIDGrupo();
        AsyncExecutor executor = new AsyncExecutor(null);
        facade.attItem(i, idGrupo, executor);
    }
    @Override
    public void updateFromRepositorio(int method, Object obj) {
        int position;
        Item item;
        if(obj instanceof Item) {
            item = (Item) obj;
            switch (method) {
                case RepositorioObserver.INSERT:
                    itensCodigos.add(item.getCodigo());
                    position = mDataset.indexOf(item);
                    if(position > -1){
                        mDataset.remove(position);
                        mView.notifyItemRemoved(position);
                    }
                    break;
                case RepositorioObserver.UPDATE:
                    position = indexOf(item);
                    if(position > -1){
                        mView.notifyDataSetChanged();
                    }
                    break;
                case RepositorioObserver.DELETE:
                    itensCodigos.remove((Integer)item.getCodigo());
                    break;
                default:
                    break;
            }
        }
    }

    private int indexOf(Item item) {
        int indexOf = -1;
        Item it;
        for(int i=0; i < mDataset.size(); i++){
            it = mDataset.get(i);
            if(it.getCodigo() == item.getCodigo()){
                indexOf = i;
                mDataset.set(i, item);
                i = mDataset.size();
            }
        }
        return indexOf;
    }


    @Override
    public void onResponse(ArrayList<Item> items, boolean isNext) {
        mDataset.clear();
        mDataset.addAll(items);
        mView.notifyDataSetChanged();
        mView.hiddeProgress();
        onRequest = false;
    }

    @Override
    public void onRequest() {
        onRequest = true;
        loading = false;
        if(mView != null)
            mView.showProgress();
    }

    @Override
    public void onCancell() {}

    public void clear() {
        mDataset.clear();
        if(mView != null)
            mView.notifyDataSetChanged();
    }
}
