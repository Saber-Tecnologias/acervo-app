package br.ufpe.sabertecnologias.acervoapp.ui.view.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.util.DateUtil;

public class AdapterItem extends Adapter<ViewHolder> {

	private ArrayList<Item> mItensBaixados, mItensDisponiveis;
	private Map<Item, Boolean> mItensSelecionados;
	
	private final int TYPE_TITULO_BAIXADOS = 0;
	private final int TYPE_ITENS_BAIXADOS = 1;
	private final int TYPE_TITULO_DISPONIVEIS = 2;
	private final int TYPE_ITENS_DISPONIVEIS = 3;
	
	private Context mContext;
	private OnClickListener mListener;
	private View.OnLongClickListener onLongClickListener;

	public AdapterItem(Context ctx, ArrayList<Item> itensBaixados, ArrayList<Item> itensDisponiveis, Map<Item, Boolean> itensSelecionados, OnClickListener clickListener, View.OnLongClickListener longClickListener) {
		mItensBaixados = itensBaixados;
		mItensDisponiveis = itensDisponiveis;
		mItensSelecionados = itensSelecionados;
		mContext = ctx;
		mListener = clickListener;
		onLongClickListener = longClickListener;
	}
	
	@Override
	public int getItemCount() {
		int size = 0;
		if(mItensBaixados.size() >0){
			size += 1+ mItensBaixados.size();
		}
		if(mItensDisponiveis.size()>0){
			size += 1 + mItensDisponiveis.size();
		}
		return size;
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
		Item item;
		Integer color = Color.RED;

		if(vh instanceof VHItemBaixado){
			VHItemBaixado vhib = (VHItemBaixado) vh;
			item = mItensBaixados.get(position-1);
			vhib.cor.setBackgroundColor(color);
			vhib.tv_formato.setText(item.getType());
			vhib.tv_titulo.setText(item.getNome());
			vhib.tv_titulo.setTag(item);
			vhib.rl_menu.setTag(item);
			vhib.bt_menu.setOnClickListener(mListener);
			vhib.bt_menu.setTag(item);
			vhib.bt_menu.setOnClickListener(mListener);
			ColorStateList csl2 = new ColorStateList(new int[][]{new int[0]}, new int[]{mContext.getResources().getColor(R.color.meu_acervo_primary)});
			vhib.tv_abrir.setSupportBackgroundTintList(csl2);
			vhib.tv_abrir.setOnClickListener(mListener);
			vhib.tv_abrir.setTag(item);
			vhib.tv_tamanho.setText(UIUtils.tamanhoFormat(item.getTamanho()));
			vhib.card.setTag(item);
			vhib.card.setTag(vhib.rl_sombra_grupo_selecionado.getId(), vhib.rl_sombra_grupo_selecionado);
			vhib.rl_sombra_grupo_selecionado.setTag(item);
			if(mItensSelecionados.containsKey(item) && mItensSelecionados.get(item)){
				vhib.rl_sombra_grupo_selecionado.setVisibility(View.VISIBLE);
			} else if(mItensSelecionados.containsKey(item)) {
				vhib.rl_sombra_grupo_selecionado.setVisibility(View.GONE);
			}
		} else if(vh instanceof VHItemDisponivel){
			VHItemDisponivel vhid = (VHItemDisponivel) vh;
			int realPosition = getRealPosition(position);
			item = mItensDisponiveis.get(realPosition);
			vhid.tv_titulo.setText(item.getNome());
			vhid.cor.setBackgroundColor(color);
			vhid.tv_formato.setText(item.getType());
			vhid.tv_data.setText(DateUtil.convertDateView(item.getData()));
			vhid.tv_tamanho.setText(UIUtils.tamanhoFormat(item.getTamanho()));
			ColorStateList csl2 = new ColorStateList(new int[][]{new int[0]}, new int[]{mContext.getResources().getColor(R.color.transferencias_color)});
			vhid.bt_baixar.setSupportBackgroundTintList(csl2);
			vhid.bt_baixar.setTag(item);
			vhid.card.setTag(item);
			vhid.card.setTag(vhid.rl_sombra_grupo_selecionado.getId(), vhid.rl_sombra_grupo_selecionado);
			vhid.tv_titulo.setTag(item);
			vhid.rl_sombra_grupo_selecionado.setTag(item);
			if(mItensSelecionados.containsKey(item) && mItensSelecionados.get(item)){
				vhid.rl_sombra_grupo_selecionado.setVisibility(View.VISIBLE);
			} else if(mItensSelecionados.containsKey(item)) {
				vhid.rl_sombra_grupo_selecionado.setVisibility(View.GONE);
			}
		} else if(vh instanceof VHTituloBaixado){
			VHTituloBaixado vhtb = (VHTituloBaixado) vh;
			vhtb.tv_titulo.setText("Itens neste dispositivo");
		}else {
			VHTituloDisponivel vhtd = (VHTituloDisponivel) vh;
			vhtd.tv_titulo.setText("Itens para transferir");
		}
	}

	public int getRealPosition(int position) {
		 int retorno;
		 if(mItensBaixados.size()>0){
			 retorno  =  position - mItensBaixados.size() - 2;
		 }else {
			 retorno = position -1;
		 }
		return retorno;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		View v;
		ViewHolder vh;
		switch (type) {
		case TYPE_TITULO_BAIXADOS:
			v = LayoutInflater.from(mContext).inflate(R.layout.grupo_item_titulo_baixado, parent, false);
			vh = new VHTituloBaixado(v);
			break;
		case TYPE_ITENS_BAIXADOS:
			v = LayoutInflater.from(mContext).inflate(R.layout.grupo_item, parent, false);
			vh = new VHItemBaixado(v);
			break;
		case TYPE_TITULO_DISPONIVEIS:
			v = LayoutInflater.from(mContext).inflate(R.layout.grupo_item_titulo_disponivel, parent, false);
			vh = new VHTituloDisponivel(v);
			break;
		default: //TYPE_ITENS_DISPONIVEIS
			v = LayoutInflater.from(mContext).inflate(R.layout.grupo_item_disponivel, parent, false);
			vh = new VHItemDisponivel(v);
			break;
		}
		return vh;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(position == 0){
			if(mItensBaixados.size() >0){
				return TYPE_TITULO_BAIXADOS;
			}else if(mItensDisponiveis.size()>0) {
				return TYPE_TITULO_DISPONIVEIS;
			} else {
				return -1;
			}
		} else {
			if(mItensBaixados.size() > 0){
				if(position < mItensBaixados.size()+1){
					return TYPE_ITENS_BAIXADOS;
				} else{
					if(position == mItensBaixados.size()+1){
						return TYPE_TITULO_DISPONIVEIS;
					}else{
						return TYPE_ITENS_DISPONIVEIS;
					}					
				}
			}else if(mItensBaixados.size() > 0){
				return TYPE_ITENS_DISPONIVEIS;
			} else {
				return -1;
			}
		}
	}

	class VHTituloBaixado extends RecyclerView.ViewHolder{
		TextView tv_titulo;
		public VHTituloBaixado(View itemView) {
			super(itemView);
			tv_titulo = (TextView) itemView.findViewById(R.id.tv_titulo);
		}
	}

	class VHTituloDisponivel extends RecyclerView.ViewHolder{
		TextView tv_titulo;
		public VHTituloDisponivel(View itemView) {
			super(itemView);
			tv_titulo = (TextView) itemView.findViewById(R.id.tv_titulo);
		}
	}
	
	class VHItemBaixado extends RecyclerView.ViewHolder{
		CardView card;
		View cor;
		ImageView bt_menu;
		TextView tv_formato;
		TextView tv_titulo;
		RelativeLayout rl_menu;
		RelativeLayout rl_sombra_grupo_selecionado;
		AppCompatButton tv_abrir;
		TextView tv_tamanho;

		public VHItemBaixado(View itemView) {
			super(itemView);
			card = (CardView) itemView;
			cor = card.findViewById(R.id.v_cor);
			bt_menu = (ImageView) card.findViewById(R.id.bt_menu);
			tv_formato = (TextView) card.findViewById(R.id.tv_formato);
			tv_titulo = (TextView) card.findViewById(R.id.tv_titulo);
			tv_abrir = (AppCompatButton) card.findViewById(R.id.tv_abrir);
			tv_tamanho = (TextView) card.findViewById(R.id.tv_tamanho);
			tv_abrir.setOnClickListener(mListener);
			card.setOnClickListener(mListener);
			card.setOnLongClickListener(onLongClickListener);
			bt_menu.setOnClickListener(mListener);
			rl_menu = (RelativeLayout) card.findViewById(R.id.rl_menu);
			rl_menu.setOnClickListener(mListener);
			rl_sombra_grupo_selecionado = (RelativeLayout) card.findViewById(R.id.rl_sombra_grupo_selecionado);
			rl_sombra_grupo_selecionado.setOnClickListener(mListener);
			rl_sombra_grupo_selecionado.setVisibility(View.GONE);
		}
	}
	
	
	class VHItemDisponivel extends RecyclerView.ViewHolder{
		CardView card;
		View cor;
		AppCompatButton bt_baixar;
		TextView tv_tamanho;
		TextView tv_formato;
		TextView tv_titulo;
		TextView tv_data;
		ImageView bt_menu;
		RelativeLayout rl_menu;
		RelativeLayout rl_sombra_grupo_selecionado;

		public VHItemDisponivel(View itemView) {
			super(itemView);
			card = (CardView) itemView;
			cor = card.findViewById(R.id.v_cor);
			bt_baixar = (AppCompatButton) card.findViewById(R.id.bt_baixar);
			tv_tamanho = (TextView) card.findViewById(R.id.tv_tamanho);
			tv_formato = (TextView) card.findViewById(R.id.tv_formato);
			tv_titulo = (TextView) card.findViewById(R.id.tv_titulo);
			tv_data = (TextView) card.findViewById(R.id.tv_data);
			tv_titulo.setAlpha(0.4f);
			card.setOnClickListener(mListener);
			card.setOnLongClickListener(onLongClickListener);
			bt_baixar.setOnClickListener(mListener);
			rl_sombra_grupo_selecionado = (RelativeLayout) card.findViewById(R.id.rl_sombra_grupo_selecionado);
			rl_sombra_grupo_selecionado.setOnClickListener(mListener);
			rl_sombra_grupo_selecionado.setVisibility(View.GONE);
		}
	}
}
