package br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;

public class DialogFragmentGrupoDeprecated extends DialogFragment implements OnClickListener{
	private Context mContext;
	private OnClickListener mClickListener;
	private ImageView iv_close;
	private Button bt_confirmar;

	public DialogFragmentGrupoDeprecated() {}

	public void init (Context ctx, OnClickListener clickListener, Item i){
		mContext = ctx;
		mClickListener = clickListener;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mClickListener.onClick(bt_confirmar);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_grupodeprecated, null);
		iv_close = (ImageView) v.findViewById(R.id.iv_close);
		bt_confirmar = (Button) v.findViewById(R.id.bt_close);
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
		case R.id.bt_close:
			dismiss();
			break;
		default:
			break;
		}
	}
}
