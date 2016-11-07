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

public class DialogFragmentConfirmarTransferencia extends DialogFragment implements OnClickListener{
	private Context mContext;
	private OnClickListener mClickListener;
	private Item item;
	private Map<Item, Boolean> itensSelecionados;
	private RadioGroup rg;
	private ImageView iv_close;
	private AppCompatButton bt_confirmar;
	private int option;
	private TextView txt_titulo;

	public void init(Context ctx, OnClickListener clickListener, Object obj){
		mContext = ctx;
		mClickListener = clickListener;
		option = 1;
		if(obj instanceof Item) {
			item = (Item) obj;

		} else if (obj instanceof Map){
			itensSelecionados = (Map<Item, Boolean>) obj;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_confirmartransferencia, null);
		iv_close = (ImageView) v.findViewById(R.id.iv_close);
		bt_confirmar = (AppCompatButton) v.findViewById(R.id.bt_confirmar_transferir);
		ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
		txt_titulo = (TextView) v.findViewById(R.id.label_titulo);
		if(itensSelecionados != null) {
			int quant=0;
			for (Item i : itensSelecionados.keySet()) {
				if(itensSelecionados.get(i)) {
					quant++;
				}
			}
			if(quant > 1){
				txt_titulo.setText(mContext.getResources().getQuantityString(R.plurals.transferir_itens, quant, quant));
			}
		}
		bt_confirmar.setSupportBackgroundTintList(csl);
		bt_confirmar.setOnClickListener(this);
		iv_close.setOnClickListener(this);
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
		case R.id.bt_confirmar_transferir:
			v.setTag(R.id.bt_confirmar_transferir, item);
			v.setTag(option);
			mClickListener.onClick(v);
			dismiss();
			break;
		default:
			break;
		}
	}
}
