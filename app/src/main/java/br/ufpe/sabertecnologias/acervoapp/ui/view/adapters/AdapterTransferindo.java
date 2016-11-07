package br.ufpe.sabertecnologias.acervoapp.ui.view.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;

public class AdapterTransferindo extends Adapter<ViewHolder> {

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;
	private static final int TYPE_BOTTOM = 2;
	
	private Context mContext;
	private OnClickListener mListener;
	private ArrayList<Item> mDataset;
	private ArrayList<ProgressBar> currentProgressBar;
	private ArrayList<TextView> currentTextViewProgresso;
	private boolean showButtonClean;

	public AdapterTransferindo(Context context, OnClickListener mListener, ArrayList<Item> mDataset, ArrayList<ProgressBar> currentProgressBar,
			ArrayList<TextView> currentTextViewProgresso) {
		
		mContext = context;
		this.mListener = mListener;
		this.mDataset = mDataset;
		this.currentProgressBar = currentProgressBar;
		this.currentTextViewProgresso = currentTextViewProgresso;
		showButtonClean = mDataset.size() > 0 ? true : false;
	}

	@Override
	public int getItemCount() {
		
		return mDataset.size()+2;
	}
	

	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
		if(vh instanceof VHItem){
			VHItem mvh = (VHItem) vh;
			Item i = mDataset.get(position-1);
			mvh.tv_titulo.setText(i.getNome());
			mvh.tv_formato.setText(i.getType());
			mvh.progress.setTag(i.getCodigo());
			Integer color = Color.RED;
			mvh.cor.setBackgroundColor(color);
			mvh.bt_menu.setTag(i);
			mvh.tv_titulo.setTag(i);
			mvh.tv_abrir.setTag(i);
			mvh.card.setTag(i);
			ColorStateList csl;
			switch (i.getStatus()) {
			case Item.FLAG_ITEM_ENFILEIRADO:
				mvh.progress.setIndeterminate(true);
				mvh.tv_porcentagem.setText("0%");
				mvh.progress.setVisibility(View.VISIBLE);
				mvh.bt_menu.setVisibility(View.GONE);
				csl= new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.delete_color)});
				mvh.tv_abrir.setSupportBackgroundTintList(csl);
				mvh.tv_abrir.setText(R.string.bt_cancelar_item_text);
				mvh.tv_abrir.setTag(i);
				mvh.tv_porcentagem.setVisibility(View.VISIBLE);
				break;
			case Item.FLAG_ITEM_BAIXANDO:
				setCurrentProgressIndicators(mvh.progress, mvh.tv_porcentagem);
				mvh.progress.setIndeterminate(false);
				mvh.tv_abrir.setTag(i);
				mvh.progress.setVisibility(View.VISIBLE);
				mvh.bt_menu.setVisibility(View.GONE);
				csl= new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.delete_color)});
				mvh.tv_abrir.setSupportBackgroundTintList(csl);
				mvh.tv_abrir.setTag(i);
				mvh.tv_abrir.setText(R.string.bt_cancelar_item_text);
				mvh.tv_porcentagem.setVisibility(View.VISIBLE);
				mvh.progress.setProgress(1);
				mvh.tv_porcentagem.setText("1%");
				break;
			case Item.FLAG_ITEM_BAIXADO:
				mvh.progress.setIndeterminate(false);
				mvh.progress.setVisibility(View.INVISIBLE);
				mvh.tv_porcentagem.setText("100%");
				mvh.bt_menu.setVisibility(View.VISIBLE);
				mvh.tv_abrir.setText(R.string.bt_abrir_item_text);
				csl= new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
				mvh.tv_abrir.setSupportBackgroundTintList(csl);
				mvh.tv_porcentagem.setVisibility(View.GONE);
			default:
				break;
			}
		}else if(vh instanceof VHHeader){
			VHHeader vhh= (VHHeader) vh;
			if(UIUtils.isTablet(mContext)) {
				vhh.title.setText(R.string.transferencias);
			} else {
				vhh.title.setVisibility(View.GONE);
			}
		} else if(vh instanceof VHBottom){
			VHBottom vhh= (VHBottom) vh;
			if(mDataset.size()>0)
				vhh.ib_clean.setVisibility(View.VISIBLE);
			else
				vhh.ib_clean.setVisibility(View.GONE);
				
			vhh.ib_clean.setOnClickListener(mListener);
		}
	}
	
	@Override
	public int getItemViewType(int position) {	
		return position==0? TYPE_HEADER : position==mDataset.size()+1 ? TYPE_BOTTOM : TYPE_ITEM;
	}

	private void setCurrentProgressIndicators(ProgressBar progress, TextView tv) {
		currentProgressBar.clear();
		currentTextViewProgresso.clear();
		currentProgressBar.add(progress);	
		currentTextViewProgresso.add(tv);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		ViewHolder vh;
		View v;
		switch (type) {
		case TYPE_HEADER:
			v = LayoutInflater.from(mContext).inflate(R.layout.transferindo_header, parent, false);
			vh = new VHHeader(v);
			break;
		case TYPE_BOTTOM:
			v = LayoutInflater.from(mContext).inflate(R.layout.transferindo_bottom, parent, false);
			vh = new VHBottom(v);			
			break;
		default:
			v = LayoutInflater.from(mContext).inflate(R.layout.transferindo_item, parent, false);
			vh = new VHItem(v);
			break;
		}
		return vh;
	}
	
	class VHHeader extends ViewHolder {
        TextView title;

        public VHHeader(View itemView) {
            super(itemView);
            title = (TextView) itemView;
        }
    }
	
	
	class VHBottom extends ViewHolder {
        ImageButton ib_clean;

        public VHBottom(View itemView) {
            super(itemView);
            ib_clean = (ImageButton) itemView.findViewById(R.id.ib_clean);
        }
    }
	
	class VHItem extends ViewHolder{
		CardView card;
		View cor;
		TextView tv_porcentagem;
		AppCompatButton tv_abrir;
		ImageButton bt_menu;
		TextView tv_formato;
		TextView tv_titulo;
		ProgressBar progress;
		View separator;

		public VHItem(View itemView) {
			super(itemView);
			card = (CardView) itemView;
			cor = card.findViewById(R.id.v_cor);
			tv_abrir = (AppCompatButton) card.findViewById(R.id.tv_abrir);
			bt_menu = (ImageButton) card.findViewById(R.id.bt_menu);
			
			separator = card.findViewById(R.id.separator);
			tv_porcentagem = (TextView) card.findViewById(R.id.tv_porcentagem);
			tv_formato = (TextView) card.findViewById(R.id.tv_formato);
			tv_titulo = (TextView) card.findViewById(R.id.tv_titulo);

			card.setOnClickListener(mListener);
			tv_abrir.setOnClickListener(mListener);
			bt_menu.setOnClickListener(mListener);
			progress = (ProgressBar) card.findViewById(R.id.progress);
		}
		
	}

	public void hideButtonClean() {
		showButtonClean = false;
	}

	public void showButtonClean() {
		showButtonClean = true;
	}

}
