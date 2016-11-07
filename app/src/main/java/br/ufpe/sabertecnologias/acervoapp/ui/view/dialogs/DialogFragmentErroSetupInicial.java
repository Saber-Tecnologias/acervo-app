package br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DialogFragmentErroSetupInicial extends DialogFragment {

	Context mContext;
	private OnClickListener mListener;
	
	public void init(Context mcontext, OnClickListener onClick){
		mContext = mcontext;
		mListener = onClick;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = "Algo deu Errado!";

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton("Tentar novamente", mListener)
                .create();
    }
}
