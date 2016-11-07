package br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterDialogFragmentNovoGrupo;

public class DialogFragmentNovoGrupo extends DialogFragment implements OnClickListener {

	private Context mContext;
	private RecyclerView rv_cores;
	private AppCompatButton bt_criar;
	private OnClickListener mClickListener;
	private EditText et_nome;
	private String cor;
	private Dialog d;
	private ImageView iv_close;
	private AdapterDialogFragmentNovoGrupo mAdapter;
	private View onDismissView;

	public void init(Context ctx, OnClickListener clickListener){
		if(d == null){
			mContext = ctx;
			mClickListener = clickListener;
		}
		setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(savedInstanceState == null){
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_novogrupo, null);
			et_nome = (EditText) v.findViewById(R.id.et_nome);
			iv_close = (ImageView) v.findViewById(R.id.iv_close_2);
			iv_close.setOnClickListener(this);
			bt_criar = (AppCompatButton) v.findViewById(R.id.bt_criargrupo);
			ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
			bt_criar.setSupportBackgroundTintList(csl);
			bt_criar.setOnClickListener(this);
			rv_cores = (RecyclerView) v.findViewById(R.id.rv_cores);
			rv_cores.setHasFixedSize(true);
			mAdapter = new AdapterDialogFragmentNovoGrupo(mContext, this);
			rv_cores.setAdapter(mAdapter);
			rv_cores.setLayoutManager(new GridLayoutManager(mContext, 4));
			d = new Dialog(mContext);
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			onDismissView = iv_close;
			d.setContentView(v);
		}
		return d;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mClickListener.onClick(onDismissView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_criargrupo:
			String nome = et_nome.getText().toString();
			if(!nome.isEmpty()){
				cor = mAdapter.getCor(-1);
				Grupo g = new Grupo(nome, Color.parseColor(cor));
				v.setTag(g);			
				onDismissView = v;
				dismiss();
			}
			break;
		case R.id.iv_cor:
			int i = (Integer) v.getTag();
			mAdapter.selectCor(i);
			break;
		case R.id.iv_close_2:
			dismiss();
			break;
		default:
			break;
		}
	}
}
