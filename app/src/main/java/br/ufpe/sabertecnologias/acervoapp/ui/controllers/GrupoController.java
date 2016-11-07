package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener.ExecutorResult;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupoAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemComplementoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver.RepositorioObserverCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.GrupoCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.GrupoView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentConfirmarTransferencia;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentExcluirItem;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentMoverItem;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentMoverItens;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentNovoGrupo;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.ufpe.sabertecnologias.acervoapp.util.FolderControl;
import br.ufpe.sabertecnologias.acervoapp.util.FormatoConvert;

public class GrupoController extends Fragment implements OnMenuItemClickListener, RepositorioObserverCallback {

	private static final int REPOSITORIO_OBSERVER_ID = 110;
	public static final String TAG = "grupoController";

	private GrupoView mView;
	private ViewListener mListener;
	private Grupo mGrupo;

	private Facade facade;
	private GrupoCallback callback;
	private RepositorioObserver observer;
	private Item itemToEdit;
	private ArrayList<Grupo> grupos;
	private Grupo gpOldItemToMoveOrRemove;
	private boolean showDialogMoverItem;
	private boolean showDialogCriarNovoDialog;
	private List<Item> itensToMove;

	private View.OnLongClickListener onLongClickListener;
	private boolean longClickFoiChamado;

	private Map <Item, Boolean> mItensSelecionados;
	private int quantItensSelecionados;



	public GrupoController init(Context ctx) {
		facade = (Facade) ctx.getApplicationContext();

		if(grupos == null){
			grupos = new ArrayList<Grupo>();
			mItensSelecionados = new HashMap<>();
			longClickFoiChamado = false;
			quantItensSelecionados=0;
		}
		setRetainInstance(true);
		return this;
	}

	@Override
	public void onAttach(Context ctx) {
		super.onAttach(ctx);
		callback = (GrupoCallback) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if(mView == null){
			mListener = new ViewListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
										long id) {}

				@Override
				public void onClick(View v) {
					final Item item;
					Grupo grupo;
					int option;
					IExecutor executor;

					switch (v.getId()) {

						case R.id.bt_baixar:
							itemToEdit = (Item) v.getTag();
							DialogFragmentConfirmarTransferencia dialog;
							dialog = new DialogFragmentConfirmarTransferencia();
							dialog.init(getActivity(), mListener, itemToEdit);
							dialog.show(getFragmentManager(), "dialogFragmentConfirmarTransferencia");
							break;
						case R.id.rl_menu:
						case R.id.bt_menu:
							itemToEdit = (Item) v.getTag();
							PopupMenu popup = new PopupMenu(getActivity(), v);
							popup.setOnMenuItemClickListener(GrupoController.this);
							if(itemToEdit.getStatus() == Item.FLAG_ITEM_BAIXADO){
								popup.inflate(R.menu.grupo_item_menu);

							} else if(itemToEdit.getStatus() == Item.FLAG_ITEM_DISPONIVEL){
								popup.inflate(R.menu.grupo_item_menu_disponivel);

								MenuItem menuItem = popup.getMenu().getItem(2);
								SpannableString s = new SpannableString("Transferir");
								s.setSpan(new ForegroundColorSpan(Color.parseColor("#149b9e")), 0, s.length(), 0);
								menuItem.setTitle(s);
							}
							popup.show();
							break;

						case R.id.bt_confirmar_excluir:
							Object objExcluir = v.getTag(R.id.bt_confirmar_excluir);
							if (objExcluir instanceof Item) {
								item = (Item) objExcluir;
								removerItem(item);
							} else {
								List<Item> itensToRemove = new ArrayList<>();
								int itemStatus = -1;
								for (Item i : mItensSelecionados.keySet()) {
									if(mItensSelecionados.get(i)) {
										itensToRemove.add(i);
										itemStatus = i.getStatus();
									}
								}

								int sizeList = itensToRemove.size();
								for (int i=0; i < sizeList; i++) {
									removerItem(itensToRemove.get(i));
									mItensSelecionados.remove(itensToRemove.get(i));
								}
								callback.habilitaToolbarContextual(-sizeList, itemStatus, false);
							}
							break;
						case R.id.bt_confirmar_transferir:
							Object objTransferir = v.getTag(R.id.bt_confirmar_transferir);
							option = (Integer)v.getTag();
							if(objTransferir instanceof Item) {
								item = (Item) objTransferir;
								mView.excluirItem(item);
								transferirItem(item);
							} else {
								Map<Item, Boolean> temp = new HashMap<>();
								temp.putAll(mItensSelecionados);
								int quantItens = 0;
								for (Item i : temp.keySet()) {
									if (temp.get(i)) {
										mView.excluirItem(i);
										transferirItem(i);
										quantItens++;
									}
								}
								callback.habilitaToolbarContextual(-quantItens, Item.FLAG_ITEM_DISPONIVEL, false);
							}
							break;
						case R.id.tv_abrir:
							Object obj1 = v.getTag();
							item = (Item) obj1;
							facade.abrirItem(item);
							executor = new AsyncExecutor(null);
							facade.empilhaLogItem(executor, item.getCodigo(), LogItemAcaoEnum.ABRIR, null);
							break;
						case R.id.card:
							// seleciona as outras views
							if(longClickFoiChamado) {
								View sombra = (View) v.getTag(R.id.rl_sombra_grupo_selecionado);
								manageViewShadowAndToolbarContextual(v, sombra);
							} else {
								Object obj2 = v.getTag();
								item = (Item) obj2;
								callback.showDetalhes(item);
							}
							break;
						case R.id.bt_criargrupo:
							if(showDialogMoverItem){
							} else {
								itemToEdit = null;
							}
						case R.id.tv_titulo:

							if(!longClickFoiChamado || showDialogMoverItem) {
								Object obj = v.getTag();
								if (obj instanceof Item) {
									item = (Item) obj;
									callback.showDetalhes(item);
								} else if (obj instanceof Grupo) {
									grupo = (Grupo) obj;
									showDialogMoverItem = false;
									showDialogCriarNovoDialog = false;
									if (grupo.getID() == 0) {//NOVO GRUPO CRIADO
										ExecutorResult executorResult2 = new ExecutorResult() {
											boolean foiDoActionMode=true;

											@Override
											public void onExecutorResult(Object obj) {
												final Grupo g = (Grupo) obj;
												DebugLog.d(this, "GRUPO ID = " + g.getID());
												if (g.getID() != -2) { // NOVO GRUPO
													if (itemToEdit != null) { //MOVER UM ITEM PARA UM NOVO GRUPO CRIADO
														final int sizeItensToMove;
														if(itensToMove == null) {
															itensToMove = new ArrayList<>();
															itensToMove.add(itemToEdit);
															foiDoActionMode=false;
														}
														sizeItensToMove = itensToMove.size();
														ExecutorResult result3 = new ExecutorResult() {
															@Override
															public void onExecutorResult(Object obj) {
																AsyncExecutor executor;
																for(Item itemToMove : itensToMove) {
																	executor = new AsyncExecutor(null);
																	facade.empilhaLogTransaction(executor, itemToMove, gpOldItemToMoveOrRemove, g, false);
																	executor = new AsyncExecutor(null);
																	itemToMove.setGrupo(g.getID());
																	mView.excluirItem(itemToMove);
																	facade.attItem(itemToMove, itemToMove.getIDGrupo(), executor);
																}
																itensToMove = null;
																if(sizeItensToMove > 1) {
																	Snackbar
																			.make(mView.getView(), String.format(getActivity().getResources().getString(R.string.snackbar_itens_movidos_sucesso), g.getNome()), Snackbar.LENGTH_LONG)
																			.show();
																} else {
																	Snackbar
																			.make(mView.getView(), String.format(getActivity().getResources().getString(R.string.snackbar_item_movido_sucesso), g.getNome()), Snackbar.LENGTH_LONG)
																			.show();
																}
															}
														};

														AsyncExecutor executor = new AsyncExecutor(result3);
														facade.empilhaLogGrupo(executor, LogGrupoAcaoEnum.ADICIONAR, g.getNome(), g.getUUID_grupo(), g.getCorHexa());
														if(foiDoActionMode) {
															callback.habilitaToolbarContextual(-sizeItensToMove, -1, false);
														}
													} else { // CRIAR UM GRUPO SEM SER NO MOVER_ITEM
														AsyncExecutor executor = new AsyncExecutor(null);
														facade.empilhaLogGrupo(executor, LogGrupoAcaoEnum.ADICIONAR, g.getNome(), g.getUUID_grupo(), g.getCorHexa());
													}
												} else { //DEU ERRO
													Snackbar
															.make(mView.getView(), "Deu erro!", Snackbar.LENGTH_LONG)
															.show();
												}
											}

										};
										executor = new AsyncExecutor(executorResult2);
										facade.addGrupo(grupo, executor);
									} else {//GRUPO GRUPO DESTINO JÁ EXISTE
										boolean foiDoActionMode=true;
										int sizeItensMove;
										if(itensToMove == null) {
											itensToMove = new ArrayList<>();
											itensToMove.add(itemToEdit);
											foiDoActionMode=false;
										}
										sizeItensMove = itensToMove.size();
										for(int i=0; i<sizeItensMove; i++) {
											Grupo newGrupo = (Grupo) obj;
											executor = new AsyncExecutor(null);
											facade.empilhaLogTransaction(executor, itensToMove.get(i), gpOldItemToMoveOrRemove, newGrupo, true);
											itensToMove.get(i).setGrupo(newGrupo.getID());
											mView.excluirItem(itensToMove.get(i));
											facade.attItem(itensToMove.get(i), itensToMove.get(i).getIDGrupo(), executor);
										}
										itensToMove = null;
										if(sizeItensMove > 1) {
											Snackbar
													.make(mView.getView(), "Itens movidos para " + grupo.getNome() + " com sucesso!", Snackbar.LENGTH_LONG)
													.show();
										} else {
											Snackbar
													.make(mView.getView(), "Item movido para " + grupo.getNome() + " com sucesso!", Snackbar.LENGTH_LONG)
													.show();
										}
										if(foiDoActionMode) {
											callback.habilitaToolbarContextual(-sizeItensMove, -1, false);
										}
									}
								}
							} else {
								View sombra = (View) v.getTag(R.id.rl_sombra_grupo_selecionado);
								manageViewShadowAndToolbarContextual(v, sombra);
							}
							break;
						case R.id.iv_close:
							showDialogMoverItem = false;
						case R.id.iv_close_2:
							showDialogCriarNovoDialog = false;
							break;
						case R.id.bt_close:
							callback.backToMeuAcervo();
							break;
						case R.id.rl_sombra_grupo_selecionado:
							manageViewShadowAndToolbarContextual(v, v);
							break;
						default:
							break;
					}
				}
			};

			onLongClickListener = new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					View sombra;
					switch (view.getId()){
						case R.id.card:
							sombra = (View) view.getTag(R.id.rl_sombra_grupo_selecionado);
							manageViewShadowAndToolbarContextual(view, sombra);
							longClickFoiChamado=true;
							break;
						default:
							break;
					}
					return true;
				}
			};

			mView = new GrupoView(inflater, container, mListener);
			mView.setAdapter(mItensSelecionados, mListener, onLongClickListener);
			observer = new RepositorioObserver(this);
			DatabaseObserverManager.addRepositorioItemObserver(observer,REPOSITORIO_OBSERVER_ID);
			DatabaseObserverManager.addRepositorioGrupoObserver(observer, REPOSITORIO_OBSERVER_ID);
			showDialogMoverItem = false;
			showDialogCriarNovoDialog = false;
		}
		//Verifica se eh o grupo de itens nao organizados
		if(mGrupo.getID() == -1){
			getItensSemGrupo();
		}else{
			mView.setGrupo(mGrupo);
		}
		if(showDialogMoverItem){
			showDialogMoverItem();
		} else if(showDialogCriarNovoDialog){
			criarNovoGrupoDialog();
		}
		return mView.getView();
	}

	private void removerItem(Item item){
		IExecutor executor;

		if(item.getStatus() == Item.FLAG_ITEM_BAIXADO){
			// EXCLUIR DO DISPOSITIVO
			mView.excluirItem(item);
			item.setStatus(Item.FLAG_ITEM_DISPONIVEL);
			item.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);
			mView.addItem(item);
			//	att status do Item no bd e log
			int idGrupo = item.getIDGrupo() == 0 ? -1 : item.getIDGrupo();
			executor = new AsyncExecutor(new ExecutorListener.ExecutorResult() {
				@Override
				public void onExecutorResult(Object obj) {
					if(obj instanceof Item){
						Item item = (Item) obj;
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
			// EXCLUIR DO MEU ACERVO
			mView.excluirItem(item);
			executor = new AsyncExecutor(new ExecutorListener.ExecutorResult() {
				@Override
				public void onExecutorResult(Object obj) {
					if(obj instanceof Item){
						Item item = (Item) obj;
					}
				}
			});
			facade.removeItem(item, executor);
			executor = new AsyncExecutor(null);
			facade.empilhaLogItem(executor, item.getCodigo(), LogItemAcaoEnum.REMOVER, null);
		}
	}

	public void transferirItem(Item i){
		IExecutor executor = new AsyncExecutor(null);
		facade.transferirItem(i, executor);
	}

	private void getItensSemGrupo() {
		ExecutorResult executorResult = new ExecutorResult() {

			@SuppressWarnings("unchecked")
			@Override
			public void onExecutorResult(Object obj) {
				mGrupo.getItens().clear();
				mGrupo.getItens().addAll((Collection<? extends Item>) obj);
				mView.setGrupo(mGrupo);
			}
		};
		AsyncExecutor executor = new AsyncExecutor(executorResult);
		facade.getItensByGrupo(mGrupo.getID(), executor);
	}

	public void setGrupo(Grupo g) {
		mGrupo = g;
		if(mGrupo.getID() == -1) {
			gpOldItemToMoveOrRemove = null;
		}else {
			gpOldItemToMoveOrRemove = mGrupo;
		}
		ExecutorResult executorResult = new ExecutorResult() {

			@Override
			public void onExecutorResult(Object obj) {
				grupos.clear();
				ArrayList<Grupo> gps = (ArrayList<Grupo>) obj;
				Grupo gp;
				for(int i=0; i<gps.size(); i++){
					gp = gps.get(i);
					if(gp.getID() != mGrupo.getID()){
						grupos.add(gp);
					}
				}
			}
		};

		AsyncExecutor executor = new AsyncExecutor(executorResult);
		facade.getGrupos(executor);
	}

	public void notifyFromSync(ArrayList<Grupo> mGrupos) {
		String mUUID = mGrupo.getUUID_grupo();
		mGrupo = null;
		//Seta o grupo atual
		if(mUUID == null){
			for (Grupo g : mGrupos) {
				if (g.getUUID_grupo() == null) {
					mGrupo = g;
				}
			}
		} else {
			for (Grupo g : mGrupos) {
				if (g.getUUID_grupo() != null && g.getUUID_grupo().equals(mUUID)) {
					mGrupo = g;
				}
			}
		}

		//Setar os grupos e remove o grupo atual da lista
		if(mGrupo != null) {
			String gUUID;
			String mGUUID = mGrupo.getUUID_grupo();
			this.grupos = new ArrayList<Grupo>();
			Grupo g;
			if(mGUUID!=null){
				for(int i=0; i<mGrupos.size(); i++){
					if(!mGUUID.equals((g=mGrupos.get(i)).getUUID_grupo())){
						grupos.add(g);
					}
				}
			} else {
				for(int i=1; i<mGrupos.size(); i++){
					grupos.add(mGrupos.get(i));
				}
			}
			if(mGUUID != null) {
				for (int i = 1; i < grupos.size(); i++) {
					g = grupos.get(i);
					gUUID = g.getUUID_grupo();
					if (gUUID.equals(mGUUID)){
						i = grupos.size();
						grupos.remove(g);
					}
				}
			} else {
				if(grupos.size()>0) {
					grupos.remove(0);
				}
			}
			if (mView != null) {
				mView.setGrupo(mGrupo);
			}
		} else {
			DebugLog.d(this, "mGrupo Deprecated");
			showDialogGrupoDeprecated();
		}
	}

	private void showDialogGrupoDeprecated() {
		mView.showDialogGrupoDeprecated(mListener, getFragmentManager());
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		DialogFragment dialog;
		switch (item.getItemId()) {
			case R.id.excluir:
				if(itemToEdit.getStatus() == Item.FLAG_ITEM_BAIXADO) {
					dialog = new DialogFragmentExcluirItem();
					((DialogFragmentExcluirItem)dialog).init(getActivity(), mListener, itemToEdit);
					dialog.show(getFragmentManager(), "dialogFragmentExcluirItem");
				} else if(itemToEdit.getStatus() == Item.FLAG_ITEM_DISPONIVEL) {
					dialog = new DialogFragmentExcluirItem();
					((DialogFragmentExcluirItem)dialog).init(getActivity(), mListener, itemToEdit);
					dialog.show(getFragmentManager(), "dialogFragmentExcluirItem");
				}
				break;
			case R.id.mover:
				showDialogMoverItem();
				break;
			case R.id.detalhes:
				callback.showDetalhes(itemToEdit);
				break;
			case R.id.transferir:
				dialog = new DialogFragmentConfirmarTransferencia();
				((DialogFragmentConfirmarTransferencia)dialog).init(getActivity(), mListener, itemToEdit);
				dialog.show(getFragmentManager(), "dialogFragmentConfirmarTransferencia");
				break;
		}
		return false;
	}

	public void showDialogMoverItemDoActionMode(){
		itensToMove = new ArrayList<>();
		for(Item i: mItensSelecionados.keySet()) {
			if(mItensSelecionados.get(i)) {
				itensToMove.add(i);
			}
		}
		if(itensToMove.size() > 0)
			itemToEdit = itensToMove.get(0);
		DialogFragmentMoverItens dialogFragmentMoverItens = new DialogFragmentMoverItens();
		dialogFragmentMoverItens.init(getActivity(), mListener, itensToMove, grupos);
		dialogFragmentMoverItens.show(getFragmentManager(), "dialogMoverItens");
		showDialogMoverItem = true;
	}

	private void showDialogMoverItem() {
		if(itemToEdit != null){
			DialogFragment dialog = new DialogFragmentMoverItem();
			((DialogFragmentMoverItem)dialog).init(getActivity(),mListener, itemToEdit, grupos);
			dialog.show(getFragmentManager(), "dialogFragmentMoverItem");
			showDialogMoverItem = true;
		}
	}

	public void criarNovoGrupoDialog() {
		DialogFragmentNovoGrupo newFragment = new DialogFragmentNovoGrupo();
		newFragment.init(getActivity(), mListener);
		newFragment.show(getFragmentManager(), "op");
		showDialogCriarNovoDialog = true;

	}

	@Override
	public void updateFromRepositorio(int method, Object obj) {
		if(obj instanceof Item){
			Item item = (Item) obj;
			switch (method) {
				case  RepositorioObserver.INSERT:
					break;
				case RepositorioObserver.DELETE:
					if((mGrupo.getID() == -1 && item.getIDGrupo() == 0) ||
						(item.getIDGrupo() == mGrupo.getID())){
						List<Item> its = mGrupo.getItens();
						Item it;
						for(int i =0; i<its.size(); i++){
							it = its.get(i);
							if(it.getCodigo() == item.getCodigo()){
								its.remove(i);
								i = its.size();
							}
						}
						mView.excluirItem(item);
						if(isVisible())
							Snackbar.make(mView.getView(), "Item excluído do Meu Acervo com sucesso!", Snackbar.LENGTH_LONG).show();
					}
					break;
				case RepositorioObserver.UPDATE:
					if(mGrupo.getID() == -1 && item.getIDGrupo() == 0){
						mView.updateItem(item);
					} else if(item.getIDGrupo() == mGrupo.getID()){
						mView.updateItem(item);
					}
					break;
				default:
					break;
			}
		} else if(obj instanceof Grupo){
			Grupo grupo = (Grupo) obj;
			switch (method) {
				case  RepositorioObserver.INSERT:
					grupos.add(grupo);
					break;
				case RepositorioObserver.DELETE:
					break;
				case RepositorioObserver.UPDATE:
					if(mGrupo.getUUID_grupo() == grupo.getUUID_grupo()) {
						mGrupo = grupo;
						mView.updateGrupo(mGrupo);
					}
					break;
				case RepositorioObserver.SYNC:
					if(mGrupo.getUUID_grupo() == grupo.getUUID_grupo()) {
						mGrupo = grupo;
						mView.setGrupo(mGrupo);
					}
					mView.notifyDataSetChanged();
					break;
				default:
					break;
			}
		}


	}

	private void manageViewShadowAndToolbarContextual(View view, View sombra) {
		Item item;
		int value=0, statusValue=-1;
		Object obj;
		obj = view.getTag();
		if(obj instanceof Item) {
			item = (Item) obj;
			statusValue = item.getStatus();
			value = setVisibility(sombra, item);
		}
		callback.habilitaToolbarContextual(value, statusValue, false);
	}

	/**
	 * It must be called before change view selected flag because depends on it.
	 *
	 * @param view the view to has its visibility changed
	 * @return a int value representing if the view passed as argument changed its visibility state. 1 when changed to VISIBLE, -1 when GONE, 0 otherwise.
	 *
	 * @see /#setSelected(View)
	 * */
	private int setVisibility(View view, Item item){
		if(view != null) {
			if(mItensSelecionados.containsKey(item)) {
				if (mItensSelecionados.get(item)) {
					view.setVisibility(View.GONE);
					mItensSelecionados.put(item, false);
					quantItensSelecionados--;
					return -1;
				} else {
					view.setVisibility(View.VISIBLE);
					mItensSelecionados.put(item, true);
					quantItensSelecionados++;
					return 1;
				}
			}
		}
		return 0;
	}

	public void transferirDoActionMode(){
		DialogFragment dialog;
		dialog = new DialogFragmentConfirmarTransferencia();
		((DialogFragmentConfirmarTransferencia)dialog).init(getActivity(), mListener, mItensSelecionados);
		dialog.show(getFragmentManager(), "dialogFragmentConfirmarTransferencia");
	}

	public void removerDoActionMode() {
		DialogFragment dialog;
		dialog = new DialogFragmentExcluirItem();
		((DialogFragmentExcluirItem)dialog).init(getActivity(), mListener, mItensSelecionados);
		dialog.show(getFragmentManager(), "dialogFragmentExcluirItem");
	}

	public void manipularTodasSombras(boolean habilitarVal){
		for(Item i: mItensSelecionados.keySet()){
			mItensSelecionados.put(i, habilitarVal);
		}
		quantItensSelecionados = habilitarVal ? mItensSelecionados.size() : 0;
		mView.notifyDataSetChanged();
	}

	public Map<Item, Boolean> getmItensSelecionados(){
		return mItensSelecionados;
	}

	public void setLongClickFoiChamado(boolean longClickFoiChamado){
		this.longClickFoiChamado = longClickFoiChamado;
	}

	public int getItensSelecionadosSize() {
		return mItensSelecionados.size();
	}
}
