package br.ufpe.sabertecnologias.acervoapp.ui.view;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterBusca;

public class BuscaView extends BasicSingleView {

	
	private ArrayList<Item> mDataset;
	private RecyclerView rv;
	private TextView tv_vazia;
	private RecyclerView.LayoutManager mLayoutManager;

	private AdapterBusca mAdapter;
	private OnClickListener mListener;
	
	private ProgressBar progresso;

	private View rl_semconexao;
	private View rl_content;
	
	private Button bt_retry;
	
	public BuscaView(LayoutInflater inflater, ViewGroup container,
			ViewListener listener) {
		super(inflater, container, listener);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View v = inflater.inflate(R.layout.busca_view, container, false);
			rl_content = v.findViewById(R.id.rl_content);
			rl_semconexao =  v.findViewById(R.id.rl_conexao);
			
			bt_retry = (Button) v.findViewById(R.id.bt_retry);
			
			rv = (RecyclerView) v.findViewById(R.id.rv_acervo_remoto);
			progresso = (ProgressBar) v.findViewById(R.id.progresso);
			tv_vazia = (TextView) v.findViewById(R.id.tv_vazia);
			rv.setHasFixedSize(true);
			//mLayoutManager = new LinearLayoutManager(container.getContext());


		setLayoutManager();
		return v;
	}

	public void setLayoutManager() {


		if(UIUtils.isTablet(mContext)) {

			switch (mContext.getResources().getConfiguration().orientation ){
				case Configuration.ORIENTATION_PORTRAIT:
					mLayoutManager = new GridLayoutManager(mContext, 1);

					break;
				case Configuration.ORIENTATION_LANDSCAPE:
					mLayoutManager = new GridLayoutManager(mContext, 2);

					break;
			}
		}else {
			mLayoutManager = new GridLayoutManager(mContext, 1);
		}
		rv.setLayoutManager(mLayoutManager);
	}


	@Override
	protected void onViewCreated(View view) {}
	
	@Override
	public void setListenerOnViews(OnClickListener listener) {		
		mListener = listener;
	}


	public void setAdapter(ArrayList<Item> mDataset, OnScrollListener scrollListener) {
		this.mDataset = mDataset;
		this.mAdapter = new AdapterBusca(getContext(), mListener , mDataset);
		rv.setAdapter(mAdapter);	
		rv.addOnScrollListener(scrollListener);
		bt_retry.setOnClickListener(mListener);
	}


	public void notifyItemRangeInserted(int positionStart, int itemCount) {
		mAdapter.notifyItemRangeInserted(positionStart, itemCount);		
	}


	public void notifyItemRemoved(int position){
		mAdapter.notifyItemRemoved(position);
	}

	public void notifyItemChanged(int position){
		mAdapter.notifyItemChanged(position);
		if(mDataset.isEmpty()){
			tv_vazia.setVisibility(View.VISIBLE);
		}else {
			tv_vazia.setVisibility(View.GONE);
		}
	}

	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
		hiddeProgress();
		if(mDataset.isEmpty()){
			tv_vazia.setVisibility(View.VISIBLE);
		}else {
			tv_vazia.setVisibility(View.GONE);
		}
	}
	
	public void semConexao(){
		rl_semconexao.setVisibility(View.VISIBLE);
		rl_content.setVisibility(View.GONE);
	}
	
	public void retry(){
		rl_semconexao.setVisibility(View.GONE);
		rl_content.setVisibility(View.VISIBLE);
	}


	public void showProgress() {
		progresso.setVisibility(View.VISIBLE);
		tv_vazia.setVisibility(View.GONE);
	}

	public void hiddeProgress() {
		progresso.setVisibility(View.GONE);
	}
	
	public RecyclerView.LayoutManager getLayoutManager(){
		return mLayoutManager;
	}


	public void updateFromDetalhes(Item item) {
		int position = getItemPosition(item);
		if(position >= 0){
			mDataset.set(position, item);
			mAdapter.notifyItemChanged(position);
		}
	}

	private int getItemPosition(Item item) {
		for(int i = 0; i< mDataset.size(); i++){
			if(item.getCodigo() == mDataset.get(i).getCodigo()){
				return i;
			}
		}
		return -1;
	}
}
