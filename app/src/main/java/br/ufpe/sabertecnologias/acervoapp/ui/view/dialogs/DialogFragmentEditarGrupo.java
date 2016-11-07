package br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
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
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterDialogFragmentEditGrupo;

public class DialogFragmentEditarGrupo extends DialogFragment implements OnClickListener {

	private Context mContext;
	private RecyclerView rv_cores;
	private OnClickListener mClickListener;
	private EditText et_nome;
	private String cor;
	private ImageView iv_close;
	private AppCompatButton bt_salvar, bt_excluir;
	private AdapterDialogFragmentEditGrupo mAdapter;
	private Grupo grupo;

	public void init(Context ctx, OnClickListener clickListener, Grupo g) {;
		mContext = ctx;
		mClickListener = clickListener;
		grupo = g;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_editgrupo, null);
		et_nome = (EditText) v.findViewById(R.id.et_nome);
		et_nome.setText(grupo.getNome());
		iv_close = (ImageView) v.findViewById(R.id.iv_close);
		iv_close.setOnClickListener(this);
		ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
		bt_salvar = (AppCompatButton) v.findViewById(R.id.bt_salvar);
		bt_salvar.setOnClickListener(this);
		bt_salvar.setSupportBackgroundTintList(csl);
		ColorStateList csl2 = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.delete_color)});
		bt_excluir = (AppCompatButton) v.findViewById(R.id.bt_excluir);
		bt_excluir.setOnClickListener(this);
		bt_excluir.setSupportBackgroundTintList(csl2);
		rv_cores = (RecyclerView) v.findViewById(R.id.rv_cores);
		rv_cores.setHasFixedSize(true);
		mAdapter = new AdapterDialogFragmentEditGrupo(mContext, this, grupo.getCor());
		rv_cores.setAdapter(mAdapter);
		rv_cores.setLayoutManager(new GridLayoutManager(mContext, 4));
		Dialog d = new Dialog(mContext);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(v);
		return d;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_salvar:
			String nome = et_nome.getText().toString();
			if(!nome.isEmpty()){
				cor = mAdapter.getCor(-1);
				grupo.setNome(nome);
				grupo.setCor(Color.parseColor(cor));
				v.setTag(grupo);
				mClickListener.onClick(v);
				dismiss();
			}
			break;
		case R.id.bt_excluir:
			v.setTag(grupo);
			mClickListener.onClick(v);
			dismiss();
			break;
		case R.id.iv_cor:
			int i = (Integer) v.getTag();
			mAdapter.selectCor(i);
			break;
		case R.id.iv_close:
			dismiss();
			break;
		default:
			break;
		}
	}
}