package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.json.JSONObject;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener.ExecutorResult;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo.ServiceStatusMemoriaDownload;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver.RepositorioObserverCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.TransferindoCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.TransferindoView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentMoverItem;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentNovoGrupo;
import br.saber.downloadservice.DownloadService;
import br.saber.downloadservice.Downloader;

public class TransferindoController extends Fragment implements OnMenuItemClickListener, RepositorioObserverCallback{

	private static final int REPOSITORIO_OBSERVER_ID_MEU_ACERVO = 200;
	private static final int REPOSITORIO_OBSERVER_ID_ACERVOREMOTO = 2000;
	private static final int REPOSITORIO_OBSERVER_ID_TRANSFERENCIAS = 20000;

	public static final int FROM_MEUACERVO = 1;
	public static final int FROM_ACERVOREMOTO = 2;
	public static final int FROM_TRANSFERENCIAS = 3;

	public static final String TAG = "transferindoController";

	private static final int LOADER_ID = 697;

	private TransferindoView mView;
	private ViewListener mListener;

	private RepositorioObserver observer;
	private ArrayList<Item> mDataset;
	private Facade facade;

	private OnScrollListener scrollListener;

	private ReceiverProgressoDownload mDownloadReceiver;

	private TransferindoCallback callback;
	private Item itemToMoveOrRemove;

	private ArrayList<Grupo> grupos;

	private ExecutorResult executorResult;

	private DialogFragment dialog;
	private Grupo gpOldItemToMoveOrRemove;

	private boolean showDialogMoveItem;
	private boolean showDialogCriarNovoGrupo;


	int from;


	private Context mContext;

	public TransferindoController init(Context ctx, int from){
		facade = (Facade) ctx.getApplicationContext();
		mDataset = new ArrayList<Item>();
		callback = (TransferindoCallback) ctx;
		getItensTransferindo();

		grupos = new ArrayList<Grupo>();

		executorResult = new ExecutorResult() {

			@Override
			public void onExecutorResult(Object obj) {
				grupos.clear();
				grupos.addAll((ArrayList<Grupo>) obj);
			}
		};
		AsyncExecutor executor = new AsyncExecutor(executorResult);
		facade.getGrupos(executor);
		observer = new RepositorioObserver(this);
		this.from = from;
		if(from == FROM_ACERVOREMOTO) {
			DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID_ACERVOREMOTO);
			DatabaseObserverManager.addRepositorioGrupoObserver(observer, REPOSITORIO_OBSERVER_ID_ACERVOREMOTO);
		} else if(from == FROM_MEUACERVO){
			DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID_MEU_ACERVO);
			DatabaseObserverManager.addRepositorioGrupoObserver(observer, REPOSITORIO_OBSERVER_ID_MEU_ACERVO);
		} else if (from == FROM_TRANSFERENCIAS) {
			DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID_TRANSFERENCIAS);
			DatabaseObserverManager.addRepositorioGrupoObserver(observer, REPOSITORIO_OBSERVER_ID_TRANSFERENCIAS);
		}

		mContext = ctx;
		mDownloadReceiver = new ReceiverProgressoDownload();

		setRetainInstance(true);

		return this;
	}

	private void getItensTransferindo() {
		new AsyncTask<Void, Void, ArrayList<Item>>(){
			@Override
			protected ArrayList<Item> doInBackground(Void... params) {
				return facade.getItensTransferindo();
			}

			protected void onPostExecute(java.util.ArrayList<Item> result) {

				adicionarItensTransferindoNoMenuView(result, true);

			};
		}.execute();
	}

	public void adicionarItensTransferindoNoMenuView(ArrayList<Item> result, boolean fromGetItensTransferindo) {
		ArrayList<Item> dataset;
		if(result != null)
			dataset = result;
		else
			dataset = mDataset;

		int totalTransferindo = 0;
		for(Item i : dataset){
			switch (i.getStatus()){
				case Item.FLAG_ITEM_BAIXANDO:
				case Item.FLAG_ITEM_ENFILEIRADO:
					totalTransferindo++;
			}

			if(fromGetItensTransferindo)
				mDataset.add(i);
		}
		callback.setTransferencias(totalTransferindo);
	}


	@Override
	public void onAttach(Context context) {

		super.onAttach(context);
		callback = (TransferindoCallback) context;

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if(mView ==null){
			mListener = new ViewListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,long id) {}

				@Override
				public void onClick(View v) {
					Grupo g;
					Item i;
					IExecutor executor;
					switch (v.getId()) {
						case R.id.bt_menu:
							itemToMoveOrRemove = (Item) v.getTag();
							PopupMenu popup = new PopupMenu(getActivity(), v);
							popup.setOnMenuItemClickListener(TransferindoController.this);
							popup.inflate(R.menu.transferindo_item_menu);
							popup.show();
							break;
						case R.id.ib_clean:
							limparTransferindo();
							break;
						case R.id.tv_abrir:
							i = (Item) v.getTag();
							switch (i.getStatus()){
								case Item.FLAG_ITEM_ENFILEIRADO:
								case Item.FLAG_ITEM_BAIXANDO:
									i = (Item) v.getTag();
									facade.cancelaDownload(new AsyncExecutor(null), i.getCodigo());
									cancelDownloadItem(i);
									break;
								case Item.FLAG_ITEM_BAIXADO:
									facade.abrirItem(i);
									executor = new AsyncExecutor(null);
									facade.empilhaLogItem(executor, i.getCodigo(), LogItemAcaoEnum.ABRIR, JSONObject.NULL.toString());
									break;
							}
							break;
						case R.id.card:
							Object obj1  = v.getTag();
							i = (Item)obj1;
							callback.showDetalhes(i);
							break;
						case R.id.tv_titulo:
						case R.id.bt_criargrupo:
							showDialogMoveItem = false;
							Object obj  = v.getTag();
							if(obj instanceof Grupo) {
								g = (Grupo) obj;
								if(g.getID() == 0){
									ExecutorResult executorResult2 = new ExecutorResult() {
										@Override
										public void onExecutorResult(Object obj) {
											if(itemToMoveOrRemove != null){
												Grupo newGrupo = (Grupo) obj;
												AsyncExecutor executor = new AsyncExecutor(null);
												facade.empilhaLogTransaction(executor, itemToMoveOrRemove, gpOldItemToMoveOrRemove, newGrupo, false);
												executor = new AsyncExecutor(null);
												itemToMoveOrRemove.setGrupo(newGrupo.getID());
												facade.attItem(itemToMoveOrRemove, itemToMoveOrRemove.getIDGrupo(), executor);
												Snackbar
														.make(mView.getView(), String.format(getActivity().getResources().getString(R.string.snackbar_item_movido_sucesso), newGrupo.getNome()), Snackbar.LENGTH_LONG)
														.show();
											}
										}
									};
									executor = new AsyncExecutor(executorResult2  );
									facade.addGrupo(g, executor);
								}else {
									//GRUPO GRUPO DESTINO JÁ EXISTE
									Grupo newGrupo = (Grupo) obj;
									executor = new AsyncExecutor(null);
									facade.empilhaLogTransaction(executor, itemToMoveOrRemove, gpOldItemToMoveOrRemove, newGrupo, true);
									itemToMoveOrRemove.setGrupo(newGrupo.getID());
									facade.attItem(itemToMoveOrRemove, itemToMoveOrRemove.getIDGrupo(), executor);
									Snackbar
											.make(mView.getView(), String.format(getActivity().getResources().getString(R.string.snackbar_item_movido_sucesso), newGrupo.getNome()), Snackbar.LENGTH_LONG)
											.show();
								}
								gpOldItemToMoveOrRemove = null;

							} else if(obj instanceof Item){
								i = (Item)obj;
								if(i.getStatus() == Item.FLAG_ITEM_BAIXADO) {
									facade.abrirItem(i);
									executor = new AsyncExecutor(null);
									facade.empilhaLogItem(executor, i.getCodigo(), LogItemAcaoEnum.ABRIR, JSONObject.NULL.toString());
								} else {
									callback.showDetalhes(i);
								}
							}
							break;
						case R.id.iv_close:
							showDialogMoveItem = false;
						case R.id.iv_close_2:
							showDialogCriarNovoGrupo = false;
							break;
						case R.id.bt_tentar_novamente:
							hideAviso();
							break;
						default:
							break;
					}
				}
			};
			scrollListener = new OnScrollListener() {
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				}
			};
			mView = new TransferindoView(inflater, container, mListener);

			mView.setAdapter(mDataset, scrollListener);
			showDialogMoveItem = false;
			showDialogCriarNovoGrupo = false;

		} else {


			adicionarItensTransferindoNoMenuView(null, false);
		}

		if(showDialogMoveItem){
			showDialogMoveItem();
		} else if(showDialogCriarNovoGrupo){
			criarNovoGrupoDialog();
		}

		IntentFilter intentFilter = new IntentFilter(Downloader.PROGRESS_ACTION);
		intentFilter.addAction(DownloadService.STORAGE_FULL_ACTION);
	// 	intentFilter.addAction(Downloader.DOWNLOAD_CANCELED_ACTION);
		mContext.getApplicationContext().registerReceiver(mDownloadReceiver, intentFilter);


		if(facade.isShowingAviso()){
			showAviso();
		}

		return mView.getView();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	protected void limparTransferindo() {
		AsyncExecutor executor = new AsyncExecutor(null);
		facade.limparTransferindo(executor);
	}

	private int indexOf(Item item){
		int index = -1;
		int cod;
		int codigo;
		for(int i=0; i<mDataset.size(); i++){
			cod = mDataset.get(i).getCodigo();
			codigo = item.getCodigo();
			if( cod == codigo){
				index = i;
				return index;
			}
		}

		return index;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mContext.getApplicationContext().unregisterReceiver(mDownloadReceiver);
		getLoaderManager().destroyLoader(LOADER_ID);
	}


	@Deprecated
	public void transferirItem(Item i){

		boolean fromAcervoRemoto = i.getStatus() == Item.FLAG_ITEM_INDISPONIVEL ? true : false;

		i.setStatus(Item.FLAG_ITEM_ENFILEIRADO);
		i.setIn_transferindo(Item.IN_TRANSFERINDO_TRUE);

		int position = mDataset.size();

		int idGrupo = i.getIDGrupo() == 0 ? -1 : i.getIDGrupo();

		if(fromAcervoRemoto){
			AsyncExecutor executor = new AsyncExecutor(null);

			facade.addItem(i, idGrupo, executor);
		} else {
			ExecutorResult result = new ExecutorResult() {

				@Override
				public void onExecutorResult(Object obj) {
					if(obj instanceof Item){
						Item item = (Item) obj;
						facade.downloadItem(item.getId(), item.getUrl(), item.getFormato(), item.getChecksum());
					}
				}
			};

			AsyncExecutor executor = new AsyncExecutor(result);
			facade.attItem(i, idGrupo, executor);
		}
		callback.piscarTransferindo();
	}

	protected void cancelDownloadItem(final Item i) {
		i.setStatus(Item.FLAG_ITEM_DISPONIVEL);
		i.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);
		int idGrupo = i.getIDGrupo() == 0 ? -1 : i.getIDGrupo();
		AsyncExecutor executor = new AsyncExecutor(null);
		facade.attItem(i, idGrupo, executor);
		//callback.finishTransferencia();

		hideAviso();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				//called when the up affordance/carat in actionbar is pressed
				getActivity().onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {

		int position;
		AsyncExecutor executor = new AsyncExecutor(null);

		switch (item.getItemId()) {
			case R.id.mover:
				showDialogMoveItem();
				return true;
			case R.id.excluir:
				if(itemToMoveOrRemove != null){
				/*				dialog = new DialogFragmentExcluirItem(getActivity(), mListener, itemToMoveOrRemove); 
				dialog.show(getFragmentManager(), "dialogFragmentExcluirItem");*/
					position=  mDataset.indexOf(itemToMoveOrRemove);
					mDataset.remove(itemToMoveOrRemove);

					mView.notifyItemRemoved(position);
					itemToMoveOrRemove.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);

					int idGrupo = itemToMoveOrRemove.getIDGrupo() == 0 ? -1 : itemToMoveOrRemove.getIDGrupo();

					facade.attItem(itemToMoveOrRemove, idGrupo, executor);


				}
				return true;
			case R.id.detalhes:
				callback.showDetalhes(itemToMoveOrRemove);
				return true;
			default:
				return false;
		}

	}

	private void showDialogMoveItem() {
		if(itemToMoveOrRemove != null){
			ArrayList<Grupo> gps = new ArrayList<Grupo>();

			Grupo gp;
			for(int i=0; i<grupos.size(); i++){
				gp = grupos.get(i);
				if(gp.getID() != itemToMoveOrRemove.getIDGrupo()){
					gps.add(gp);
				}else {
					if(gp.getID() != -1) {
						//O item ja tem um grupo
						gpOldItemToMoveOrRemove = gp;
					} else {
						//O item NAO tem grupo
						gpOldItemToMoveOrRemove = null;
					}
				}
			}
			dialog = new DialogFragmentMoverItem();
			((DialogFragmentMoverItem)dialog).init(getActivity(),mListener, itemToMoveOrRemove, gps);
			dialog.show(getFragmentManager(), "dialogFragmentMoverItem");
			showDialogMoveItem = true;
		}
	}


	public class ReceiverProgressoDownload extends BroadcastReceiver{

		String action;
		String url;
		int progresso;
		int codigo;
		String path;
		boolean hasNext;


		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if(action.equals(DownloadService.STORAGE_FULL_ACTION)){
					showAviso();
			} else if(action.equals(Downloader.PROGRESS_ACTION)){
				url = intent.getStringExtra(Downloader.CURRENT_URL);

				codigo = Integer.parseInt(intent.getStringExtra(Downloader.CURRENT_ID));

				progresso = intent.getIntExtra(Downloader.CURRENT_PROGRESS, 0);

				if( mView != null)
					mView.setProgresso(codigo, progresso);

			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void updateFromRepositorio(int method, Object obj) {
		Item item;
		Grupo gp;
		if(obj instanceof Item) {
			item = (Item) obj;
			updateFromRepositorioItem(method, item);
		} else if(obj instanceof Grupo){
			gp = (Grupo) obj;
			updateFromRepositorioGrupo(method, gp);
		}
	}

	private void updateFromRepositorioGrupo(int method, Grupo gp){
		switch (method) {
			case RepositorioObserver.INSERT:
				grupos.add(gp);
				break;
			case RepositorioObserver.DELETE:
				grupos.remove(gp);
				removeItensOfGrupo(gp);
				if(mView!=null)
					mView.notifyDataSetChanged();
				break;
			default:
				break;
		}

	}

	private void removeItensOfGrupo(Grupo gp) {
		Item item;
		for(int i=0; i<mDataset.size(); i++){
			item = mDataset.get(i);
			if(item.getIDGrupo() == gp.getID()){
				mDataset.remove(i);
				i--;
			}
		}
	}

	private void updateFromRepositorioItem(int method, Item item){
		int position = mDataset.size();

		switch (method) {
			case RepositorioObserver.INSERT:
				mDataset.add(item);
				if(mView != null)
					mView.notifyItemInserted(position);

				callback.addTransferencia();
				break;

			case RepositorioObserver.UPDATE:
				final int index = indexOf(item);
				if(item.getIn_transferindo() == Item.IN_TRANSFERINDO_TRUE){

					if(index != -1){
						//esta sendo mostrado e foi atualizado(algum atributo)
						mDataset.set(index, item);

						if(itemToMoveOrRemove != null){
							if(itemToMoveOrRemove.getCodigo() == item.getCodigo())
								itemToMoveOrRemove  = item;
						}

						if(mView != null)
							mView.notifyItemUpdated(index);
					}else {
						// eh pra ser mostrado e vai ser adicionado ao conjuto de dados.
						mDataset.add(item);
						if(mView != null)
							mView.notifyItemInserted(position);
						switch (item.getStatus()){
							case Item.FLAG_ITEM_BAIXANDO:
							case Item.FLAG_ITEM_ENFILEIRADO:
								callback.addTransferencia();
								break;
						}
					}



				} else if(index != -1){
					// esta sendo mostrado e
					// foi setado para ser removido da tela de transferindo
					mDataset.remove(index);
					if(mView != null)
						mView.notifyItemRemoved(index);
				}
				break;
			case RepositorioObserver.DELETE:
				break;
			default:
				break;
		}
	}


	public void criarNovoGrupoDialog() {
		DialogFragmentNovoGrupo newFragment = new DialogFragmentNovoGrupo();
		newFragment.init(getActivity(), mListener);
		newFragment.show(getFragmentManager(), "op");
		showDialogCriarNovoGrupo = true;
	}

	public void showAviso(){
		if (mView!=null){
			mView.showAviso();
		}
	}

	public void hideAviso(){
		if (mView!=null){
			mView.hideAviso();
		}
		ServiceStatusMemoriaDownload.startActionCancelNotification(mContext.getApplicationContext());
		Intent it = new Intent(DownloadService.ACTION_START_AND_UNBLOCK);
		mContext.sendBroadcast(it);
	}

}
