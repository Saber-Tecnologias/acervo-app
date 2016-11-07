package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener.ExecutorResult;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupoAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver.RepositorioObserverCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.GruposCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.GruposView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentEditarGrupo;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentNovoGrupo;
import br.ufpe.sabertecnologias.acervoapp.util.DateUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class GruposController extends Fragment implements OnMenuItemClickListener, RepositorioObserverCallback {

	public static final String TAG = "gruposController";
	private static final int REPOSITORIO_OBSERVER_ID = 100;

	private GruposView mView;

	private ViewListener mListener;

	private Facade facade;
	private ExecutorResult result;

	private ArrayList<Grupo> mGrupos;

	private RepositorioObserver mObserver;

	private Grupo grupoToRemoveOrEdit;

	private GruposCallback callback;

	private boolean showDialogCriarNovoDialog;



	public Fragment init(Context mContext){
		DebugLog.d(this, "GruposController - init()");
		facade = (Facade) mContext.getApplicationContext();
		if(mView == null) {
			DebugLog.d(this, "GruposController - mView = "+mView);

			mGrupos = new ArrayList<Grupo>();

			mGrupos.add(facade.getDefaultGrupo());

			result = new ExecutorResult() {

				@SuppressWarnings("unchecked")
				@Override
				public void onExecutorResult(Object obj) {
					if(obj instanceof ArrayList<?>){
						mGrupos.addAll((ArrayList<Grupo>) obj);
						if(mView!=null)
							mView.notifyDatasetChanged();
					}

				}
			};

			AsyncExecutor executor = new  AsyncExecutor(result);
			facade.getGrupos(executor);
			mObserver = new RepositorioObserver(this);
			DatabaseObserverManager.addRepositorioGrupoObserver(mObserver, REPOSITORIO_OBSERVER_ID);
			DatabaseObserverManager.addRepositorioItemObserver(mObserver, REPOSITORIO_OBSERVER_ID);
		} else {
			DebugLog.d(this, "GruposController - mView = "+mView);
		}

		setRetainInstance(true);
		return this;
	}


	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

		callback = (GruposCallback) activity;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		/*DatabaseObserverManager.addRepositorioGrupoObserver(mObserver);
		DatabaseObserverManager.addRepositorioItemObserver(mObserver);*/
		DebugLog.d(this, "GruposController OnCreateView");

		if(mView == null){

			DebugLog.d(this, "GruposController - mView == NULL");

			mListener = new ViewListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
										long id) {

				}

				@Override
				public void onClick(View v) {

					final Grupo g;
					switch (v.getId()) {

						case R.id.menu:
							DebugLog.d(this, "MENU");
							grupoToRemoveOrEdit = (Grupo) v.getTag();
							PopupMenu popup = new PopupMenu(getActivity(), v);
							popup.setOnMenuItemClickListener(GruposController.this);
							popup.inflate(R.menu.grupos_item_menu);
							popup.show();
							break;

						case R.id.tv_nome:
							g = (Grupo) v.getTag();
							callback.abrirGrupo(g);
							break;
						case R.id.bt_criargrupo:
							g = (Grupo) v.getTag();
							ExecutorResult executorResult = new ExecutorResult() {

								@Override
								public void onExecutorResult(Object obj) {
									if(obj instanceof Grupo) {
										final Grupo g = (Grupo) obj;

										if(g.getID() != -2){

											Snackbar
													.make(getActivity().getCurrentFocus(), "Grupo "+g.getNome()+ " criado com sucesso.", Snackbar.LENGTH_LONG)
													.show();


											AsyncExecutor executor = new AsyncExecutor(null);
											facade.empilhaLogGrupo(executor, LogGrupoAcaoEnum.ADICIONAR, g.getNome(), g.getUUID_grupo(), g.getCorHexa());
										} else{
											//Grupo já existente!
											DebugLog.d(this, "Grupo já existe, campeão!");
											Snackbar
													.make(mView.getView(), "Já existe um grupo com este nome.", Snackbar.LENGTH_LONG)
													.show();
										}


									}
								}
							};

							addGrupo(g, executorResult);


							break;

						case R.id.bt_salvar:
							g = (Grupo) v.getTag();

							AsyncExecutor executor1 = new AsyncExecutor(new ExecutorResult() {
								@Override
								public void onExecutorResult(Object obj) {

									if(obj instanceof  Grupo) {
										final Grupo grupoAlterado = (Grupo) obj;
										facade.empilhaLogGrupo(new AsyncExecutor(null), LogGrupoAcaoEnum.ALTERAR, grupoAlterado.getNome(), grupoAlterado.getUUID_grupo(),grupoAlterado.getCorHexa());
									}
								}
							});
							facade.updateGrupo(g, executor1);




							break;
						case R.id.bt_excluir:
							g= (Grupo) v.getTag();

							mostrarDialogExcluirGrupo(g);

							break;
						case R.id.iv_close_2:
							showDialogCriarNovoDialog = false;
							break;

						case R.id.bt_confirmar_excluir_grupo:
							/* grupo a ser excluido */
							g = (Grupo) v.getTag(R.id.bt_confirmar_excluir_grupo);
							final Grupo grupoToAtt = (Grupo) v.getTag(R.id.bt_confirmar_excluir_tudo);

							final AsyncExecutor executor2 = new AsyncExecutor(new ExecutorResult() {
								@Override
								public void onExecutorResult(Object obj) {

									List<Item> is = (List<Item>) obj;

									AsyncExecutor executor3;// = new AsyncExecutor(null);
									for(Item i: is){
										executor3 = new AsyncExecutor(null);
										facade.empilhaLogTransaction(executor3,i,g,grupoToAtt,(grupoToAtt.getID() != 0 ? true : false));
									}

									excluirGrupo(g, false);
								}
							});

							final ArrayList<Item> itens = new ArrayList<Item>();
							itens.addAll(grupoToAtt.getItens());


							if(grupoToAtt.getID() == 0){
								//Grupo foi criado agora//
								ExecutorResult executorResult2 = new ExecutorResult() {

									@Override
									public void onExecutorResult(Object obj) {
										if(obj instanceof Grupo) {
											Grupo g = (Grupo) obj;

											if(g.getID() != -2){

												ExecutorResult  executorResult1 = new ExecutorResult() {
													@Override
													public void onExecutorResult(Object obj) {
														facade.attItem(itens, grupoToAtt.getID(), executor2);
													}
												};

												AsyncExecutor executor = new AsyncExecutor(executorResult1);
												facade.empilhaLogGrupo(executor, LogGrupoAcaoEnum.ADICIONAR, g.getNome(), g.getUUID_grupo(), g.getCorHexa());
											} else{
												//Grupo já existente!
												DebugLog.d(this, "Grupo já existe, campeão!");
												Snackbar
														.make(mView.getView(), "Já existe um grupo com este nome.", Snackbar.LENGTH_LONG)
														.show();
											}


										}
									}
								};

								addGrupo(grupoToAtt, executorResult2);

							} else if(grupoToAtt.getID() == -1){
								// nao permite!
							} else{
								facade.attItem(itens, grupoToAtt.getID(), executor2);
							}

							break;
						case R.id.bt_confirmar_excluir_tudo:
							g= (Grupo) v.getTag(R.id.bt_confirmar_excluir_tudo);

							excluirGrupo(g, true);
							break;
						default:
							break;
					}

				}
			};

			showDialogCriarNovoDialog = false;

			mView = new GruposView(inflater, container, mListener);
			mView.setAdapter(mGrupos, mListener);
		} else {
			DebugLog.d(this, "GruposController - mView != NULL");
			if(showDialogCriarNovoDialog){
				criarNovoGrupoDialog();
			}
		}


		AsyncExecutor executor = new AsyncExecutor(null);

		//TODO mudar rotina de enviar pilha de log
		//facade.enviarPilhaLog(executor);
		mObserver = new RepositorioObserver(this);
		DatabaseObserverManager.addRepositorioGrupoObserver(mObserver, REPOSITORIO_OBSERVER_ID);
		DatabaseObserverManager.addRepositorioItemObserver(mObserver, REPOSITORIO_OBSERVER_ID);
		DebugLog.d(this, "DateTime = " + DateUtil.getTimestampObject(System.currentTimeMillis()).getDatetime());

		return mView.getView();
	}

	private void addGrupo(Grupo g, ExecutorResult executorResult) {
		showDialogCriarNovoDialog = false;
		AsyncExecutor executor = new AsyncExecutor(executorResult);
		facade.addGrupo(g, executor);
	}


	public void criarNovoGrupoDialog() {
		DialogFragmentNovoGrupo newFragment = new DialogFragmentNovoGrupo();
		newFragment.init(getActivity(), mListener);
		newFragment.show(getFragmentManager(), "op");
		showDialogCriarNovoDialog = true;


	}

	public void editarGrupoDialog(Grupo g) {
		DialogFragmentEditarGrupo newFragment = new DialogFragmentEditarGrupo();
		newFragment.init(getActivity(), mListener, g);
		newFragment.show(getFragmentManager(), "op");
	}

	private void excluirGrupo(Grupo g, final boolean withItens) {
		final ExecutorResult executorResult = new ExecutorResult() {
			@Override
			public void onExecutorResult(Object obj) {

				if(obj instanceof Grupo){
					final Grupo grupoRemovido = (Grupo) obj;

					final ExecutorResult executorResult1 = new ExecutorResult() {
						@Override
						public void onExecutorResult(Object obj) {
							AsyncExecutor executor = new AsyncExecutor(null);
							facade.empilhaLogGrupo(executor, LogGrupoAcaoEnum.REMOVER, grupoRemovido.getNome(), grupoRemovido.getUUID_grupo(), grupoRemovido.getCorHexa());
						}
					};

					if(withItens) {
						AsyncExecutor executor = new AsyncExecutor(executorResult1);
						facade.empilhaLogItensRemovidosFromGrupoRemovido(executor, grupoRemovido);
					} else {
						AsyncExecutor executor = new AsyncExecutor(null);
						facade.empilhaLogGrupo(executor, LogGrupoAcaoEnum.REMOVER, grupoRemovido.getNome(), grupoRemovido.getUUID_grupo(), grupoRemovido.getCorHexa());
					}



				}

			}
		};
		AsyncExecutor executor = new AsyncExecutor(executorResult);

		facade.removeGrupo(g, executor);

	}

	public void mostrarDialogExcluirGrupo(Grupo grupo) {
		mView.excluirGrupo(getFragmentManager(),grupo, mListener, mGrupos);
	}


	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.excluir:

				mostrarDialogExcluirGrupo(grupoToRemoveOrEdit);


				return true;
			case R.id.editar:
				editarGrupoDialog(grupoToRemoveOrEdit);
				return true;
			default:
				return false;
		}
	}


	@Override
	public void updateFromRepositorio(int method, Object obj) {

		if(obj != null) {


			if (obj instanceof Grupo) {
				updateFromRepositorioGrupo(method, (Grupo) obj);
			} else if (obj instanceof Item) {
				updateFromRepositorioItem(method, (Item) obj);

			} else if (obj instanceof List) {

				if (method == RepositorioObserver.SYNC_GRUPOS) {
					updateFromRepositorioGrupo(RepositorioObserver.SYNC, (List) obj);

				} else if (method == RepositorioObserver.SYNC_ITENS) {
					updateFromRepositorioItem(RepositorioObserver.SYNC, (List) obj);

				}
			}
		}

	}


	private void updateFromRepositorioItem(int method, Item item) {
		switch (method) {
			case RepositorioObserver.INSERT:

				break;
			case RepositorioObserver.DELETE:

				break;
			case RepositorioObserver.UPDATE:
				Grupo g;
				List<Item> itens;
				Item it;
				for(int i=0; i<mGrupos.size(); i++){
					g = mGrupos.get(i);
					itens = g.getItens();
					for(int j=0; j<itens.size(); j++){
						it = itens.get(j);
						if(it.getCodigo() == item.getCodigo()){
							itens.remove(j);
						}
					}



					if(g.getID() == -1 && item.getIDGrupo() == 0){
						itens.add(item);

					} else if(item.getIDGrupo() == g.getID()) {
						itens.add(item);
					}
				}
				break;
			default:
				break;
		}
	}

	private Grupo getGrupoById(int idGrupo) {
		Grupo g;
		for(int i=0;i<mGrupos.size();i++){
			g = mGrupos.get(i);
			if(g.getID() == idGrupo){
				return g;
			}
		}
		return null;
	}


	private void updateFromRepositorioGrupo(int method, Grupo obj) {
		int position;
		switch (method) {
			case RepositorioObserver.INSERT:
				position = mGrupos.size();

				mGrupos.add(obj);
				mView.notifyItemInserted(position);

				break;

			case RepositorioObserver.DELETE:
				position = mGrupos.indexOf(grupoToRemoveOrEdit);
				mGrupos.remove(grupoToRemoveOrEdit);
				mView.notifyItemRemoved(position);

				break;

			case RepositorioObserver.UPDATE:
				Grupo g = (Grupo) obj;
				final int position2 = mGrupos.indexOf(g);
				mGrupos.set(position2, g);
				mView.notifyItemChanged(position2);
				break;

//			case RepositorioObserver.SYNC:
//				mGrupos.clear();
//				mGrupos.add(facade.getDefaultGrupo());
//				mGrupos.addAll(novosGrupos);
//				mView.notifyDatasetChanged();
//				break;

			default:
				break;
		}
	}

	private void updateFromRepositorioGrupo(int method, List<Grupo> objList) {

		switch (method) {
			case RepositorioObserver.SYNC:
				mGrupos.clear();
				mGrupos.add(facade.getDefaultGrupo());
				DebugLog.d(this, "Grupos = " + objList.size());
				for(Grupo g: objList) {

					mGrupos.add(g);
				}
				//mGrupos.addAll(objList);
				mView.notifyDatasetChanged();
				break;
			default:
				break;
		}
	}


	private void updateFromRepositorioItem(int method, List<Item> objList) {

		switch (method) {
			case RepositorioObserver.SYNC:

				Item item;
				Grupo g;
				DebugLog.d(this, "itens from json = "+objList.size());

				for(int i=0; i<objList.size(); i++){
					item = objList.get(i);
					for(int j=0; j<mGrupos.size(); j++){
						g = mGrupos.get(j);
						if(item.getIDGrupo() == g.getID()){
							//Log.d(TAG, "grupo.itens.size() = "+g.getItensFromServer().size());
							g.getItens().add(item);
							DebugLog.d(this, "Item " + item.getNome() + " adicionado no grupo " + g.getNome() + "!");
							DebugLog.d(this, "grupo <<<"+g.getNome()+">>> tem "+g.getItens().size()+" itens");
							j = mGrupos.size();
							//Log.d(TAG, "item "+item.getId() +" adicionado ao grupo "+g.getID());
						}
					}
				}

				callback.notifyGrupoController(mGrupos);

				break;
			default:
				break;
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		DebugLog.d(this, "GruposController OnDestroyView");
	}


}
