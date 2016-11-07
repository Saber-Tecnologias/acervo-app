package br.ufpe.sabertecnologias.acervoapp.ui.view.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.util.DateUtil;

public class AdapterBusca extends Adapter<AdapterBusca.MyViewHolder> {

	private ArrayList<Item> mDataset;
	private OnClickListener mListener;
	private Context mContext;

	public AdapterBusca(Context ctx, OnClickListener clickListener, ArrayList<Item> itens) {
		mContext = ctx;
		mListener = clickListener;
		mDataset = itens;
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(MyViewHolder vh, int position) {

		Item i = mDataset.get(position);
		vh.tv_tamanho.setText(UIUtils.tamanhoFormat(i.getTamanho()));
		vh.tv_counter.setText(position + 1 + "");
		vh.tv_titulo.setText(i.getNome());
		vh.tv_formato.setText(i.getType());
		vh.tv_data.setText(DateUtil.convertDateView(i.getData()));
		Integer color = Color.RED;
		vh.cor.setBackgroundColor(color);
		vh.tv_counter.setTextColor(color);
		vh.bt_baixar.setTag(i);
		vh.card.setTag(i);
		vh.tv_titulo.setTag(i);
		ColorStateList csl;

		switch (i.getStatus()){
			case Item.FLAG_ITEM_BAIXADO:
				csl = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
				vh.bt_baixar.setSupportBackgroundTintList(csl);
				vh.bt_baixar.setText(R.string.bt_abrir_item_text);
				break;
			case Item.FLAG_ITEM_ENFILEIRADO:
			case Item.FLAG_ITEM_BAIXANDO:
				csl = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.delete_color)});
				vh.bt_baixar.setSupportBackgroundTintList(csl);
				vh.bt_baixar.setText(R.string.bt_cancelar_item_text);
				vh.tv_tamanho.setText(R.string.progresso_transferindo);
				break;
			default:
				csl = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.transferencias_color)});
				vh.bt_baixar.setSupportBackgroundTintList(csl);
				vh.bt_baixar.setText(R.string.bt_transferir_item_text);
				break;
		}
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.busca_item, parent, false);
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	class MyViewHolder extends RecyclerView.ViewHolder{
		CardView card;
		View cor;
		AppCompatButton bt_baixar;
		TextView tv_tamanho;
		TextView tv_formato;
		TextView tv_titulo;
		TextView tv_counter;
		TextView tv_data;

		public MyViewHolder(View itemView) {
			super(itemView);
			card = (CardView) itemView;
			cor = card.findViewById(R.id.v_cor);
			tv_counter = (TextView) card.findViewById(R.id.tv_counter);
			bt_baixar = (AppCompatButton) card.findViewById(R.id.bt_baixar);
			tv_tamanho = (TextView) card.findViewById(R.id.tv_tamanho);
			tv_formato = (TextView) card.findViewById(R.id.tv_formato);
			tv_titulo = (TextView) card.findViewById(R.id.tv_titulo);
			tv_data = (TextView) card.findViewById(R.id.tv_data);
			tv_titulo.setOnClickListener(mListener);
			card.setOnClickListener(mListener);
			bt_baixar.setOnClickListener(mListener);
		}
	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).getCodigo();
	}
}
