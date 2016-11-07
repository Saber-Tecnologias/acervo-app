package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterItem;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentGrupoDeprecated;

public class GrupoView extends BasicSingleView {

	private Grupo mGrupo;
	private TextView tv_titulo;
	private View cor;
	private RecyclerView rv_itens;

	private AdapterItem mAdapter;
	private LinearLayoutManager layoutManager;


	private ArrayList<Item> mItensBaixados;
	private ArrayList<Item> mItensDisponiveis;
	private Map<Item, Boolean> mItensSelecionados;
	private View bt_close;
	private AlertDialog dialog;


	public GrupoView(LayoutInflater inflater, ViewGroup container,
					 ViewListener listener) {
		super(inflater, container, listener);
	}

	@Override
	public void setListenerOnViews(OnClickListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View v = inflater.inflate(R.layout.grupo_view, container, false);
		tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);
		rv_itens = (RecyclerView) v.findViewById(R.id.rv_itens);
		mItensBaixados = new ArrayList<Item>();
		mItensDisponiveis = new ArrayList<Item>();
		mItensSelecionados = new HashMap<>();
		layoutManager = new LinearLayoutManager(mContext);
		rv_itens.setLayoutManager(layoutManager);
		return v;
	}


	public void setAdapter(Map<Item, Boolean> itensSelecionados, OnClickListener mListener, View.OnLongClickListener longClickListener){
		mItensSelecionados = itensSelecionados;
		mAdapter = new AdapterItem(mContext, mItensBaixados, mItensDisponiveis ,mItensSelecionados, mListener, longClickListener);
		rv_itens.setAdapter(mAdapter);
	}

	@Override
	protected void onViewCreated(View view) {

	}

	private void setViewDoGrupo(Grupo g){
		mGrupo = g;
		SpannableString s = new SpannableString(g.getNome());
		s.setSpan(new ForegroundColorSpan(g.getCor()), 0, s.length(), 0);
		tv_titulo.setText(s);
		mItensBaixados.clear();
		mItensDisponiveis.clear();
		mItensSelecionados.clear();
		List<Item> itens = g.getItens();
		Boolean value;
		Item item;
		for(int i=0; i<itens.size(); i++){
			item = itens.get(i);
			if(item.getStatus() == Item.FLAG_ITEM_DISPONIVEL){
				mItensDisponiveis.add(item);
				if((value = mItensSelecionados.get(item)) == null) {
					value = false;
				}
				mItensSelecionados.put(item, value);
			} else if(item.getStatus() == Item.FLAG_ITEM_BAIXADO){
				mItensBaixados.add(item);
				if((value = mItensSelecionados.get(item)) == null) {
					value = false;
				}
				mItensSelecionados.put(item, value);

			}
		}
		mAdapter.notifyDataSetChanged();
	}

	public void setGrupo(Grupo g) {
		String strUUID_grupo = g.getUUID_grupo();
		if(mGrupo != null){
			if (strUUID_grupo != null && !strUUID_grupo.equals(mGrupo.getUUID_grupo())) {
				setViewDoGrupo(g);
			} else if(strUUID_grupo == null && mGrupo.getID() != -1) {
				setViewDoGrupo(g);
			} else if (strUUID_grupo == null && g.getID() == mGrupo.getID()) {
				setViewDoGrupo(g);
			}
		} else {
			setViewDoGrupo(g);
		}
	}

	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	public void excluirItem(Item item) {
		int position;

		if(containsItemBaixado(item)){
			position = getItemPosition(mItensBaixados, item);
			mItensBaixados.remove(position);
			mItensSelecionados.remove(item);
			if(mItensBaixados.size() == 0) {
				mAdapter.notifyDataSetChanged();
			}else {
				mAdapter.notifyItemRemoved(1 + position);
			}
		} else if(containsItemDisponivel(item)){
			position = getItemPosition(mItensDisponiveis, item);
			mItensDisponiveis.remove(position);
			mItensSelecionados.remove(item);
			int titulos =  mItensBaixados.size() > 0 ? 2 : 1;
			int finalPosition = titulos + mItensBaixados.size() + position;
			if(mItensDisponiveis.size() == 0){
				mAdapter.notifyDataSetChanged();
			}else{
				mAdapter.notifyItemRangeRemoved(finalPosition, 1);
			}

		}
	}

	public void addItem(Item item){
		if(item.getStatus() == Item.FLAG_ITEM_DISPONIVEL){
			mItensDisponiveis.add(item);
			mItensSelecionados.put(item, false);
			mAdapter.notifyItemInserted(1 + mItensBaixados.size() + mItensDisponiveis.size());
		}else if (item.getStatus() == Item.FLAG_ITEM_BAIXADO) {
			mItensBaixados.add(item);
			mItensSelecionados.put(item, false);
			mAdapter.notifyItemInserted(mItensBaixados.size());
		}
	}

	public boolean containsItemDisponivel(Item item){
		for(Item i : mItensDisponiveis){
			if(i.getCodigo() == item.getCodigo()){
				return true;
			}
		}
		return false;
	}

	public boolean containsItemBaixado(Item item){
		for(Item i : mItensBaixados){
			if(i.getCodigo() == item.getCodigo()){
				return true;
			}
		}
		return false;
	}


	public void showDialogGrupoDeprecated(final ViewListener mListener, FragmentManager fm)
	{
		DialogFragmentGrupoDeprecated dialog = new DialogFragmentGrupoDeprecated();
		dialog.init(getContext(), mListener, null);
		dialog.show(fm, "dialogGrupoDeprecated");
	}

	private View getTitleView() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		return inflater.inflate(R.layout.layout_grupodeprecated_title, null, false);
	}

	public void changeItem(Item item) {
		int position = -1;
		Boolean valorSelecao;
		if(item.getStatus() == Item.FLAG_ITEM_DISPONIVEL){
			position = getItemPosition(mItensDisponiveis, item);
			mItensDisponiveis.set(position, item);
			// se for null quer dizer que nao foi adicionado ainda e por isso tambem nao teve view selecionada
			if((valorSelecao = mItensSelecionados.get(item)) == null) {
				valorSelecao = false;
			}
			mItensSelecionados.remove(item);
			mItensSelecionados.put(item, valorSelecao);
		}else if (item.getStatus() == Item.FLAG_ITEM_BAIXADO) {
			position = getItemPosition(mItensBaixados, item);
			mItensBaixados.set(position, item);
			if((valorSelecao = mItensSelecionados.get(item)) == null) {
				valorSelecao = false;
			}
			mItensSelecionados.remove(item);
			mItensSelecionados.put(item, valorSelecao);
		}
		mAdapter.notifyDataSetChanged();
	}

	private int getItemPosition(ArrayList<Item> itens, Item item) {
		for(int i = 0; i < itens.size(); i++){
			if(item.getCodigo() == itens.get(i).getCodigo()){
				return i;
			}
		}
		return -1;
	}

	public void updateItem(Item item){
		int position;
		Boolean valorSelecao;
		if(item.getStatus() == Item.FLAG_ITEM_DISPONIVEL){
			if(containsItemDisponivel(item)){
				position = getItemPosition(mItensDisponiveis, item);
				mItensDisponiveis.set(position, item);
				// se for null quer dizer que nao foi adicionado ainda e por isso tambem nao foi selecionado
				if((valorSelecao = mItensSelecionados.get(item)) == null) {
					valorSelecao = false;
				}
				mItensSelecionados.remove(item);
				mItensSelecionados.put(item, valorSelecao);
				mAdapter.notifyItemChanged(getGlobalItemPosition(item, position, false));
			} else  {
				mItensDisponiveis.add(item);
				mItensSelecionados.put(item, false);
				mAdapter.notifyItemInserted(mAdapter.getItemCount());
			}
			if(containsItemBaixado(item)){
				position = getItemPosition(mItensBaixados, item);
				mItensBaixados.remove(position);
				mItensSelecionados.remove(item);
				mAdapter.notifyItemRemoved(getGlobalItemPosition(item, position, true));
			}
		} else if (item.getStatus() == Item.FLAG_ITEM_BAIXADO){
			if(containsItemBaixado(item)){
				position = getItemPosition(mItensBaixados, item);
				mItensBaixados.set(position, item);
				if((valorSelecao = mItensSelecionados.get(item)) == null) {
					valorSelecao = false;
				}
				mItensSelecionados.remove(item);
				mItensSelecionados.put(item, valorSelecao);
				mAdapter.notifyItemChanged(getGlobalItemPosition(item, position, false));
			} else {
				mItensBaixados.add(item);
				mItensSelecionados.put(item, false);
				mAdapter.notifyItemInserted(mItensBaixados.size()+1);

			}
			if(containsItemDisponivel(item)){
				position = getItemPosition(mItensDisponiveis, item);
				mItensDisponiveis.remove(position);
				mItensSelecionados.remove(item);
				mAdapter.notifyItemRemoved(getGlobalItemPosition(item, position, true));
			}
		} else if (item.getStatus() == Item.FLAG_ITEM_ENFILEIRADO || item.getStatus() == Item.FLAG_ITEM_BAIXANDO) {
			if(containsItemDisponivel(item)){
				position = getItemPosition(mItensDisponiveis, item);
				mItensDisponiveis.remove(position);
				mItensSelecionados.remove(item);
				mAdapter.notifyDataSetChanged();
			}
		}
	}


	public int getGlobalItemPosition(Item item, int localPosition, boolean isRemove){
		int itensDisponiveisSize = mItensDisponiveis.size();
		int itensBaixadosSize = mItensBaixados.size();
		if (item.getStatus() == Item.FLAG_ITEM_DISPONIVEL) {
			if(isRemove) {
				return 1 + localPosition;
			} else {
				if (itensBaixadosSize == 0) {
					return 1 + localPosition;
				} else {
					return 1 + itensBaixadosSize + 1 + localPosition;
				}
			}
		} else if (item.getStatus() == Item.FLAG_ITEM_BAIXADO) {
			if(isRemove) {
				if (itensBaixadosSize == 0) {
					return 1 + localPosition;
				} else {
					return 1 + itensBaixadosSize + 1 + localPosition;
				}
			} else{
				return 1 + localPosition;
			}
		}
		return 0;
	}
	public void updateGrupo(Grupo grupo) {
		setViewDoGrupo(grupo);
	}
}
