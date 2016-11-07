package br.ufpe.sabertecnologias.acervoapp.ui.view.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.util.DateUtil;
import br.ufpe.sabertecnologias.acervoapp.util.FormatoConvert;

/**
 * Created by joaotrindade on 03/02/16.
 */
public class AdapterRecentes extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View.OnClickListener mListener;
    private Context mContext;
    private ArrayList<Item> mDataset;

    public AdapterRecentes(Context context, ArrayList<Item> dataset,  View.OnClickListener clickListener){
        mContext = context;
        mDataset = dataset;
        mListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recentes_item, parent, false);
        return new RecentesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecentesViewHolder vh = (RecentesViewHolder) holder;
        Item i = mDataset.get(position);
        vh.tv_tamanho.setText(UIUtils.tamanhoFormat(i.getTamanho()));
        vh.tv_counter.setText(position + 1 + "");
        vh.tv_titulo.setText(i.getNome());
        vh.tv_formato.setText(FormatoConvert.covertFormato(i.getFormato()));
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
    public int getItemCount() {
        return mDataset.size();
    }

    class RecentesViewHolder extends RecyclerView.ViewHolder{
        CardView card;
        View cor;
        AppCompatButton bt_baixar;
        TextView tv_tamanho;
        TextView tv_formato;
        TextView tv_titulo;
        TextView tv_counter;
        TextView tv_data;

        public RecentesViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView;
            cor = card.findViewById(R.id.v_cor);
            tv_counter = (TextView) card.findViewById(R.id.tv_counter);
            bt_baixar = (AppCompatButton) card.findViewById(R.id.bt_baixar);
            tv_tamanho = (TextView) card.findViewById(R.id.tv_tamanho);
            tv_formato = (TextView) card.findViewById(R.id.tv_formato);
            tv_titulo = (TextView) card.findViewById(R.id.tv_titulo);
            tv_data = (TextView) card.findViewById(R.id.tv_data);
            card.setOnClickListener(mListener);
            bt_baixar.setOnClickListener(mListener);
        }
    }

}
