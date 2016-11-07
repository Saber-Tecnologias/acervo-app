package br.ufpe.sabertecnologias.acervoapp.ui.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;

public class AdapterGrupos extends Adapter<ViewHolder> {

	private final int TYPE_DEFAULT = 0;
	private final int TYPE_OTHERS = 1;
	
	private Context mContext;
	private ArrayList<Grupo> mDataset;
	private OnClickListener mClickListener;

	public AdapterGrupos(Context ctx, ArrayList<Grupo> mDataset, OnClickListener clickListener) {
		mContext = ctx;
		this.mDataset = mDataset;
		mClickListener = clickListener;
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		Grupo g = mDataset.get(position);
		if(viewHolder instanceof VH){
			VH vh = (VH) viewHolder;
			vh.cor.setBackgroundColor(g.getCor());
			vh.tv_nome.setText(g.getNome());
			vh.tv_nome.setTag(g);
			vh.menu.setTag(g);
		} else if(viewHolder instanceof VHDefault){
			VHDefault vhd = (VHDefault) viewHolder;
			vhd.cor.setBackgroundColor(g.getCor());
			vhd.tv_nome.setText(g.getNome());
			vhd.tv_nome.setTag(g);
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		return mDataset.get(position).getID() == -1 ?  TYPE_DEFAULT : TYPE_OTHERS;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup container, int type) {
		ViewHolder vh;
		View v;
		switch (type) {
		case TYPE_OTHERS:
			v = LayoutInflater.from(mContext).inflate(R.layout.grupos_item, container, false);
			vh = new VH(v);
			break;
		default:
			v = LayoutInflater.from(mContext).inflate(R.layout.grupos_item, container, false);
			vh = new VHDefault(v);
			break;
		}
		return vh;
	}
	
	
	class VHDefault extends RecyclerView.ViewHolder{
		View cor;
		ImageView menu;
		TextView tv_nome;
		public VHDefault(View itemView) {
			super(itemView);
			
			cor = itemView.findViewById(R.id.cor);
			menu = (ImageView) itemView.findViewById(R.id.menu);
			menu.setVisibility(View.GONE);
			tv_nome = (TextView) itemView.findViewById(R.id.tv_nome);
			tv_nome.setOnClickListener(mClickListener);
		}
	}

	class VH extends RecyclerView.ViewHolder{
		View cor;
		ImageView menu;
		TextView tv_nome;
		public VH(View itemView) {
			super(itemView);
			cor = itemView.findViewById(R.id.cor);
			menu = (ImageView) itemView.findViewById(R.id.menu);
			menu.setOnClickListener(mClickListener);
			tv_nome = (TextView) itemView.findViewById(R.id.tv_nome);
			tv_nome.setOnClickListener(mClickListener);
		}
	}
}
