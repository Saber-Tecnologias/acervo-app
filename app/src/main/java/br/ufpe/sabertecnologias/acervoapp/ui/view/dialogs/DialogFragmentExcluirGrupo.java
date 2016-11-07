package br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class DialogFragmentExcluirGrupo extends DialogFragment implements OnClickListener {
	private Context mContext;
	private OnClickListener mClickListener;
	private ImageView iv_close;
	private AppCompatButton bt_confirmar_grupo;
	private AppCompatButton bt_confirmar_tudo;
	private TextView tv_texto;
	private Grupo mGrupo;
	private ArrayList<Grupo> mGrupos;
	private View returnView;

	public void init(Context ctx, OnClickListener clickListener, Grupo g, ArrayList<Grupo> grupos){
		mContext = ctx;
		mClickListener = clickListener;
		mGrupo = g;
		mGrupos = new ArrayList<Grupo>();
		for(Grupo gs: grupos){
			if(!mGrupo.getUUID_grupo().equals(gs.getUUID_grupo()) && gs.getUUID_grupo() != null){
				mGrupos.add(gs);
			}
		}
		setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_excluirgrupo, null);
		iv_close = (ImageView) v.findViewById(R.id.iv_close);
		bt_confirmar_grupo = (AppCompatButton) v.findViewById(R.id.bt_confirmar_excluir_grupo);
		bt_confirmar_tudo = (AppCompatButton) v.findViewById(R.id.bt_confirmar_excluir_tudo);
		ColorStateList csl2 = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext, R.color.meu_acervo_primary)});
		bt_confirmar_grupo.setSupportBackgroundTintList(csl2);
		bt_confirmar_tudo.setSupportBackgroundTintList(csl2);
		tv_texto = (TextView) v.findViewById(R.id.tv_texto);
		returnView = bt_confirmar_grupo;
		if(mGrupo.getItens().size()>0) {
			int count = mGrupo.getItens().size();
			tv_texto.setText(mContext.getResources().getQuantityString(R.plurals.excluir_grupo, count, count ));
		}else {
			bt_confirmar_grupo.setVisibility(View.GONE);
			bt_confirmar_tudo.setText("Confirmar");
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bt_confirmar_tudo.getLayoutParams();
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN_MR1){
				lp.addRule(RelativeLayout.ALIGN_END, R.id.iv_close);
			} else{
				lp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.iv_close);
			}
		}
		bt_confirmar_grupo.setOnClickListener(this);
		bt_confirmar_tudo.setOnClickListener(this);
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
		case R.id.bt_confirmar_excluir_grupo:
			v.setTag(R.id.bt_confirmar_excluir_grupo, mGrupo);
			mostrarDialogMoverItens();
			break;
		case R.id.bt_confirmar_excluir_tudo:
			v.setTag(R.id.bt_confirmar_excluir_tudo, mGrupo);
			mClickListener.onClick(v);
			dismiss();
			break;
		case R.id.tv_titulo:
		case R.id.bt_criargrupo:
			Grupo grupoToMove = (Grupo) v.getTag();
			List<Item> itens = mGrupo.getItens();
			for(Item i : itens){
				DebugLog.d(this, i.getNome()+" - "+i.getCodigo());
				i.setGrupo(grupoToMove.getID());
				grupoToMove.getItens().add(i);
			}
			mGrupo.getItens().clear();
			returnView.setTag(R.id.bt_confirmar_excluir_grupo, mGrupo);
			returnView.setTag(R.id.bt_confirmar_excluir_tudo, grupoToMove);
			mClickListener.onClick(returnView);
			dismiss();
			break;
		default:
			break;
		}
	}

	private void mostrarDialogMoverItens() {
		DialogFragmentMoverItens dialogFragmentMoverItens = new DialogFragmentMoverItens();
		dialogFragmentMoverItens.init(mContext, this, mGrupo.getItens(), mGrupos);
		dialogFragmentMoverItens.show(getFragmentManager(), "dialogMoverItens");
	}
}