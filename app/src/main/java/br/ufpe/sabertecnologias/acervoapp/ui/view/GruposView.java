package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterGrupos;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentExcluirGrupo;

public class GruposView extends BasicSingleView {
	
	private RecyclerView rv_grupos;
	private GridLayoutManager layoutManager;
	private AdapterGrupos mAdapter;

	public GruposView(LayoutInflater inflater, ViewGroup container,
			ViewListener listener) {
		super(inflater, container, listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View v = inflater.inflate(R.layout.grupos_view, container, false);
		rv_grupos = (RecyclerView) v.findViewById(R.id.rv_grupos);
		int columns = 2;
		if(UIUtils.isTablet(v.getContext())){
			columns = 3;
		}
		layoutManager = new GridLayoutManager(getContext(), columns);
		rv_grupos.setLayoutManager(layoutManager);
		return v;
	}

	@Override
	protected void onViewCreated(View view) {}

	public void setAdapter(ArrayList<Grupo> mDataset, OnClickListener clickListener){
		mAdapter = new AdapterGrupos(getContext(), mDataset,  clickListener);
		rv_grupos.setAdapter(mAdapter);
	}

	@Override
	public void setListenerOnViews(OnClickListener listener) {}

	public void notifyItemInserted(int position) {
		if(mAdapter!= null)
			mAdapter.notifyItemInserted(position);
	}

	public void notifyDatasetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	public void notifyItemChanged(int position){
		mAdapter.notifyItemChanged(position);
	}

	public void notifyItemRemoved(int position) {
		mAdapter.notifyItemRemoved(position);
	}

	public void excluirGrupo(FragmentManager fragmentManager, Grupo grupo, ViewListener mListener, ArrayList<Grupo> mGrupos) {
		DialogFragmentExcluirGrupo excluirGrupo = new DialogFragmentExcluirGrupo();
		excluirGrupo.init(mContext, mListener, grupo, mGrupos);
		excluirGrupo.show(fragmentManager, "dialogExcluirGrupo");
	}
}
