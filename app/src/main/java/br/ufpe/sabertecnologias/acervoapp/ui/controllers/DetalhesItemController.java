package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.io.File;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemComplementoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver.RepositorioObserverCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.DetalhesItemView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentConfirmarTransferencia;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentExcluirItem;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.ufpe.sabertecnologias.acervoapp.util.FolderControl;
import br.ufpe.sabertecnologias.acervoapp.util.FormatoConvert;

public class DetalhesItemController extends Fragment implements RepositorioObserverCallback {

	private static final int REPOSITORIO_OBSERVER_ID = 220;
	public static final String TAG = "detalhesItemControllerTag";
	private DetalhesItemView mView;
	private Item item;
	private int cor;
	private Facade facade;
	private ViewListener mListener;
	private RepositorioObserver observer;
	public enum DetalhesContext {MEU_ACERVO, ACERVO_REMOTO, fromContext, TRANSFERENCIAS};
	private DetalhesContext mDetalhesContext;

	public DetalhesItemController init(Context ctx, DetalhesContext detalhesContext){
		facade = (Facade) ctx.getApplicationContext();
		observer = new RepositorioObserver(this);
		mDetalhesContext = detalhesContext;

		if(mView == null) {
				mListener = new ViewListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}

				@Override
				public void onClick(View v) {
					Item item;
					switch (v.getId()) {
					case R.id.tv_abrir:
						item = (Item) v.getTag();
						onClickBotao(item);
						break;
					case R.id.bt_confirmar_transferir:
						item = (Item) v.getTag(R.id.bt_confirmar_transferir);
						tansferirItem(item);
						break;
					case R.id.bt_excluir:
						item = (Item) v.getTag();
						DialogFragmentExcluirItem dialog = new DialogFragmentExcluirItem();
						dialog.init(getActivity(), mListener, item);
						dialog.show(getFragmentManager(), "dialogFragmentExcluirItem");
						break;
					case R.id.bt_confirmar_excluir:
							item = (Item) v.getTag(R.id.bt_confirmar_excluir);
							IExecutor executor;
							if(item.getStatus() == Item.FLAG_ITEM_BAIXADO){
								// EXCLUIR DO DISPOSITIVO
								item.setStatus(Item.FLAG_ITEM_DISPONIVEL);
								item.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);
								int idGrupo = item.getIDGrupo() == 0 ? -1 : item.getIDGrupo();
								executor = new AsyncExecutor(new ExecutorListener.ExecutorResult() {
									@Override
									public void onExecutorResult(Object obj) {
										if(obj instanceof Item){
											Item item = (Item) obj;
											mView.nofityItemUpdated(item);
											Snackbar.make(mView.getView(), "Item apagado do dispositivo com sucesso!", Snackbar.LENGTH_LONG).show();
										}
									}
								});
								facade.attItem(item, idGrupo, executor);
								File f = new File(FolderControl.getItensPath(getContext())+item.getCodigo()+"."+ FormatoConvert.covertFormato(item.getFormato()));
								if(f.delete()){
									DebugLog.d(this, "Item deletado do arquivo");
								}
								executor = new AsyncExecutor(null);
								facade.empilhaLogItem(executor, item.getCodigo(), LogItemAcaoEnum.EXCLUIR, LogItemComplementoEnum.LOCAL.toString());
							} else if(item.getStatus() == Item.FLAG_ITEM_DISPONIVEL){
								// EXCLUIR DA MINHA ESTANT
								executor = new AsyncExecutor(new ExecutorListener.ExecutorResult() {
									@Override
									public void onExecutorResult(Object obj) {
										if(obj instanceof Item){
											Item item = (Item) obj;
											mView.nofityItemUpdated(item);
											exit();
										}
									}
								});
								facade.removeItem(item, executor);
								executor = new AsyncExecutor(null);
								facade.empilhaLogItem(executor, item.getCodigo(), LogItemAcaoEnum.REMOVER, null);
							}
						break;
					default:
						break;
					}
				}
			};
		}
		setRetainInstance(true);
        return this;
	}

	private void exit() {
		getActivity().onBackPressed();
	}

	private void 	tansferirItem(Item item) {
		AsyncExecutor executor = new AsyncExecutor(null);
		facade.transferirItem(item, executor);
		switch (mDetalhesContext){
			case ACERVO_REMOTO:
				executor = new AsyncExecutor(null);
				facade.empilhaLogItem(executor, item.getCodigo(), LogItemAcaoEnum.ADICIONAR, LogItemComplementoEnum.LOCAL.toString());
				break;
			case MEU_ACERVO:
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mView == null) {
			mView = new DetalhesItemView(inflater, container, mListener);
		}
		mView.setItem(item, cor, mDetalhesContext);
		return mView.getView();
	}

    public void setItem(Item i, int c){
		DatabaseObserverManager.addRepositorioItemObserver(observer,REPOSITORIO_OBSERVER_ID);
		item = i;
        cor = c;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	public void confirmarTransferencia(Item i) {
		DialogFragmentConfirmarTransferencia dialog = new DialogFragmentConfirmarTransferencia();
		dialog.init(getActivity(), mListener, i);
		dialog.show(getFragmentManager(), "confirmarTransferenciaDialog");
	}

	private void onClickBotao(Item item) {
		IExecutor executor;
		switch (item.getStatus()) {
		case Item.FLAG_ITEM_INDISPONIVEL:
		case Item.FLAG_ITEM_DISPONIVEL:
			confirmarTransferencia(item);
			break;
		case Item.FLAG_ITEM_BAIXADO:
			facade.abrirItem(item);
			executor = new AsyncExecutor(null);
			facade.empilhaLogItem(executor, item.getCodigo(), LogItemAcaoEnum.ABRIR, null);
			break;
		case Item.	FLAG_ITEM_ENFILEIRADO:
		case Item.FLAG_ITEM_BAIXANDO:
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DatabaseObserverManager.removeRepositorioItemObserver(observer);
	}

	@Override
	public void updateFromRepositorio(int method, Object obj) {
		if(obj instanceof Item) {
			Item i = (Item) obj;
			if (mView != null) {
				switch (method) {
					case RepositorioObserver.UPDATE:
						if (i.getCodigo() == item.getCodigo()) {
							item.setStatus(i.getStatus());
							mView.nofityItemUpdated(i);
						}
						break;
					case RepositorioObserver.INSERT:
						if (item.equals(i)) {
							mView.nofityItemUpdated(i);
						}
						break;
					default:
						break;
				}
			}
		}
	}
}
