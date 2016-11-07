package br.ufpe.sabertecnologias.acervoapp.ui.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;

public class AdapterDialogMoverItem extends Adapter<ViewHolder> {

	private ArrayList<Grupo> mDataset;
	private Context mContext;
	private OnClickListener mListener;

	public AdapterDialogMoverItem(Context ctx, ArrayList<Grupo> grupos, OnClickListener onClikListener) {
		mDataset = grupos;
		mContext = ctx;
		mListener = onClikListener;
	}
	
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		if(viewHolder instanceof VH){
			VH vh = (VH) viewHolder;	
			Grupo g = mDataset.get(position);
			vh.cor.setBackgroundColor(g.getCor());
			vh.tv_titulo.setText(g.getNome());
			vh.tv_titulo.setTag(g);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_moveritem, parent, false);
		return new VH(v);
	}
	
	private class VH extends ViewHolder{
		View cor;
		TextView tv_titulo;
		public VH(View itemView) {
			super(itemView);
			cor = itemView.findViewById(R.id.cor);
			tv_titulo = (TextView) itemView.findViewById(R.id.tv_titulo);
			tv_titulo.setOnClickListener(mListener);
		}
	}
}
