package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
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
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.BuscaResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.TipoTermoBusca;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver.RepositorioObserverCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaControllerCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.BuscaView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentConfirmarTransferencia;
import br.ufpe.sabertecnologias.acervoapp.util.ConnectionUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class BuscaController extends Fragment implements BuscaResponseCallback, RepositorioObserverCallback {

	private static final int REPOSITORIO_OBSERVER_ID = 111;
	public static String TAG = "buscaController";

	private BuscaView mView;
	private ViewListener listener;

	private ArrayList<Item> mDataset;
	private ArrayList<Integer> itensCodigos;

	private Facade facade;
	private OnScrollListener scrollListner;
	private BuscaControllerCallback callback;
	private RepositorioObserver observer;

	protected boolean loading;
	private boolean onRequest;



	public BuscaController init(Context ctx) {
		facade = (Facade) ctx.getApplicationContext();
		facade.setBuscaResponseListener(this);
		mDataset = new ArrayList<Item>();
		itensCodigos = new ArrayList<Integer>();
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				itensCodigos = facade.getItensCodigos();
				return null;
			}

		}.execute();

		observer = new  RepositorioObserver(this);
		DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID);
		setRetainInstance(true);
		return this;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		callback = (BuscaControllerCallback)context;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		DebugLog.d(this, "ONCREATEVIEW");

		DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID);

		if(mView == null){
			listener = new BuscaControllerListener();

			mView = new BuscaView(inflater, container, listener);
			if(onRequest){
				mView.showProgress();
			}
			scrollListner = new OnScrollListener() {
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

					int totalItemCount = mView.getLayoutManager().getItemCount();
					int last = ((GridLayoutManager)mView.getLayoutManager()).findLastVisibleItemPosition();

					if (!loading) {
						if (last+1 == totalItemCount) {
							loading = true;
							if(!facade.hasNextPage())
								facade.getNextPageBusca();
						}
					}
				}
			};
			mView.setAdapter(mDataset, scrollListner);
		} else{
			mView.setLayoutManager();
		}
		return mView.getView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}


	class BuscaControllerListener extends ViewListener{

		@Override
		public void onClick(View v) {
			Item i;
			switch (v.getId()) {
				case R.id.bt_baixar:
					i = (Item) v.getTag();
					if(i.getStatus() == Item.FLAG_ITEM_BAIXADO){
						abrirItem(i);
					} else if(i.getStatus() == Item.FLAG_ITEM_DISPONIVEL || i.getStatus() == Item.FLAG_ITEM_INDISPONIVEL){
                        confirmarTransferencia(i);
                    }else{
                        cancelDownloadItem(i);
					}
					break;
				case R.id.bt_confirmar_transferir:
					i = (Item) v.getTag(R.id.bt_confirmar_transferir);
					transferirItem(i);
					break;
				case R.id.tv_titulo:
					i = (Item) v.getTag();
					callback.showDetalhes(i);
					break;
				case R.id.bt_retry:
					retryBusca();
					break;
				default:
					i = (Item) v.getTag();
					callback.showDetalhes(i);
					break;
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//nada
		}

	}

	private void abrirItem(Item i) {
		facade.abrirItem(i);
	}

	private void transferirItem(Item i) {
		AsyncExecutor executor = new AsyncExecutor(null);
		facade.transferirItem(i, executor);
		executor = new AsyncExecutor(null);
		facade.empilhaLogItem(executor, i.getCodigo(), LogItemAcaoEnum.ADICIONAR, LogItemComplementoEnum.LOCAL.toString());
	}

	@Override
	public void onResponse(ArrayList<Item> items, boolean isNext) {
		DebugLog.d(this, "BuscaController");
		int positionStart = mDataset.size();
		for(int i=0; i<items.size(); i++){
			if(itensCodigos.contains(items.get(i).getCodigo())){
				items.get(i).setStatus(Item.FLAG_ITEM_BAIXADO);
			}
		}
		int itemCount = items.size();

		if(isNext){
			mDataset.addAll(items);

			mView.notifyItemRangeInserted(positionStart, itemCount);
		}else{
			mDataset.clear();
			mDataset.addAll(items);

			mView.notifyDataSetChanged();
		}
		mView.hiddeProgress();
		onRequest = false;
	}


	public void confirmarTransferencia(Item i) {
		DialogFragmentConfirmarTransferencia dialog = new DialogFragmentConfirmarTransferencia();
		dialog.init(getActivity(), listener, i);
		dialog.show(getFragmentManager(), "confirmarTransferenciaDialog");

	}

	@Override
	public void onError( ) {
		DebugLog.d(this, "ERROR");

		if(ConnectionUtil.getConnectivityStatus(getActivity()) == ConnectionUtil.TYPE_NOT_CONNECTED){
			mView.semConexao();
		} else {
			clear();
			onRequest = false;
		}
	}

	public void retryBusca() {
		facade.addTermoBuscaAcervoRemoto("", TipoTermoBusca.NULL);
		mView.retry();
	}

	@Override
	public void onRequest() {
		DebugLog.d(this, "OnRequest");
		onRequest = true;
		loading = false;
		if(mView != null)
			mView.showProgress();
	}

	public void clear() {
		mDataset.clear();
		if(mView != null)
			mView.notifyDataSetChanged();
	}

	@Override
	public void onCancell() {
		loading = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

    protected void cancelDownloadItem(final Item i) {
        i.setStatus(Item.FLAG_ITEM_DISPONIVEL);
        i.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);
        int idGrupo = i.getIDGrupo() == 0 ? -1 : i.getIDGrupo();
        AsyncExecutor executor = new AsyncExecutor(null);
        facade.attItem(i, idGrupo, executor);
    }

	public void updateFromDetalhes(Item item) {
		mView.updateFromDetalhes(item);
	}

	@Override
	public void updateFromRepositorio(int method, Object obj) {
		int position;
		Item item;

		DebugLog.d(this, "Busca Controller notificado");
		if(obj instanceof Item) {
			item = (Item) obj;
			switch (method) {
				case RepositorioObserver.INSERT:
					DebugLog.d(this, "Busca Controller notificado - insert");
					itensCodigos.add(item.getCodigo());
					position = mDataset.indexOf(item);
					if(position > -1){
						DebugLog.d(this, "Busca Controller notificado - remove");
						mDataset.set(position, item);
						mView.notifyItemChanged(position);
					}

					break;

				case RepositorioObserver.UPDATE:
					DebugLog.d(this, "Busca Controller notificado - UPDATE - "+item.getStatus());
                    position = indexOf(item);
                    if(position >-1){
                        mDataset.set(position, item);
                        mView.notifyItemChanged(position);
                        if(item.getStatus() == Item.FLAG_ITEM_BAIXADO && !itensCodigos.contains(item.getCodigo())){
                            itensCodigos.add(item.getCodigo());
                        }
                    } else {
                        DebugLog.d(this, "index <= -1");
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
        for(int i=0; i<mDataset.size(); i++){
            if(mDataset.get(i).getCodigo() == item.getCodigo()){
                return i;
            }
        }
        return -1;
    }
}

