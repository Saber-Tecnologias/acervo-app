package br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Map;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;

public class DialogFragmentExcluirItem extends DialogFragment implements OnClickListener {

	public static final int OPTION_DISPOSITIVO = 1;
	private Context mContext;
	private OnClickListener mClickListener;
	private ImageView iv_close;
	private RadioGroup rg;
	protected int option;
	private AppCompatButton bt_confirmar;
	private Item item;
	private TextView tv_texto;
	private TextView tv_label_titulo;
	private Map<Item, Boolean> itensSelecionados;

	public void init(Context ctx, OnClickListener clickListener, Object obj) {
		mContext = ctx;
		mClickListener = clickListener;
		if(obj instanceof Item) {
			item = (Item) obj;
		} else if (obj instanceof Map){
			itensSelecionados = (Map<Item, Boolean>) obj;
		}
		setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_excluiritem2, null);
		iv_close = (ImageView) v.findViewById(R.id.iv_close);
		bt_confirmar = (AppCompatButton) v.findViewById(R.id.bt_confirmar_excluir);
		tv_texto = (TextView) v.findViewById(R.id.tv_texto);
		tv_label_titulo = (TextView) v.findViewById(R.id.label_titulo);
		int quant=1;
		int statusItem=-1;
		if(itensSelecionados != null) {
			quant=0;
			for (Item i : itensSelecionados.keySet()) {
				if(itensSelecionados.get(i)) {
					quant++;
					statusItem = i.getStatus();
				}
			}
			if(quant > 1) {
				tv_label_titulo.setText(R.string.remover_itens_selecionados);
			}
		} else {
			statusItem = item.getStatus();
		}
		if(statusItem == Item.FLAG_ITEM_DISPONIVEL){
			tv_texto.setText(mContext.getResources().getQuantityString(R.plurals.remover_item_meu_acervo_geral, quant, quant));
		} else if(statusItem == Item.FLAG_ITEM_BAIXADO) {
			tv_texto.setText(mContext.getResources().getQuantityString(R.plurals.remover_item_meu_acervo_dispositivo,quant, quant));
		}
		ColorStateList csl2 = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
		bt_confirmar.setSupportBackgroundTintList(csl2);
		bt_confirmar.setOnClickListener(this);
		iv_close.setOnClickListener(this);
		option = OPTION_DISPOSITIVO;
		Dialog d = new Dialog(mContext);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(v);
		return d;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_close:
			dismiss();
			break;
		case R.id.bt_confirmar_excluir:
				v.setTag(R.id.bt_confirmar_excluir, item);
				v.setTag(option);
				mClickListener.onClick(v);
				dismiss();
		default:
			break;
		}
	}
}
