package br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterDialogMoverItem;

public class DialogFragmentMoverItens extends DialogFragment implements OnClickListener{
	private Context mContext;
	private OnClickListener mClickListener;
	private Dialog d;
	private ImageView iv_close;
	private AppCompatButton bt_novogrupo;
	private RecyclerView rv_grupos;
	private EditText et_pesquisa;
	private AdapterDialogMoverItem adapter;

	private ArrayList<Grupo> mGrupos;
	private LinearLayoutManager layoutManager;
	private boolean showDialogCriarNovoGrupo;
	private View onDismissView;

	public void init(Context ctx, OnClickListener clickListener, List<Item> is, ArrayList<Grupo> grupos) {
		if(d == null){
			mContext = ctx;
			mClickListener = clickListener;
			mGrupos = new ArrayList<Grupo>(grupos);
			showDialogCriarNovoGrupo = false;
		}
		setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(savedInstanceState  == null){
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_moveritens, null);
			iv_close = (ImageView) v.findViewById(R.id.iv_close);
			bt_novogrupo = (AppCompatButton) v.findViewById(R.id.bt_novogrupo);
			bt_novogrupo.setOnClickListener(this);
			ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
			bt_novogrupo.setSupportBackgroundTintList(csl);
			iv_close.setOnClickListener(this);
			et_pesquisa = (EditText) v.findViewById(R.id.et_pesquisa);
			rv_grupos = (RecyclerView) v.findViewById(R.id.rv_grupos);
			layoutManager = new LinearLayoutManager(mContext);
			rv_grupos.setLayoutManager(layoutManager);
			adapter = new AdapterDialogMoverItem(mContext, mGrupos, this);
			rv_grupos.setAdapter(adapter);
			onDismissView = iv_close;
			d = new Dialog(mContext);
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			d.setContentView(v);
		}
		if(showDialogCriarNovoGrupo){
			criarNovoGrupoDialog();
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
			case R.id.iv_close_2:
				showDialogCriarNovoGrupo = false;
				break;
			case R.id.iv_close:
				dismiss();
				break;
			case R.id.bt_novogrupo:
				criarNovoGrupoDialog();
				break;
			case R.id.tv_titulo:
			case R.id.bt_criargrupo:
				showDialogCriarNovoGrupo = false;
				onDismissView = v;
				dismiss();
				break;
			default:
				break;
		}
	}

	public void criarNovoGrupoDialog() {
		DialogFragmentNovoGrupo newFragment = new DialogFragmentNovoGrupo();
		newFragment.init(getActivity(), this);
		newFragment.show(getFragmentManager(), "op");
		showDialogCriarNovoGrupo = true;
	}

	public void notifyItemInserted(int position) {
		adapter.notifyItemInserted(position);
	}

}
