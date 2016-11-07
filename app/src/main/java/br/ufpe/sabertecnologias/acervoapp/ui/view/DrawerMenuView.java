package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;

public class DrawerMenuView extends BasicSingleView {

	TextView tv_acervoremoto, tv_meuacervo, tv_atualizar, tv_suporte, tv_tutorial, tv_transferencias, tv_counter, tv_creditos;
	ImageView iv_sync;
	AlertDialog dialog;
	OnClickListener mListener;
	View bt_close;
	boolean isSmartphone;
	public boolean isRefreshing;
	private int totalTranferindo;

	public DrawerMenuView(LayoutInflater inflater, ViewGroup container,
						  ViewListener listener) {
		super(inflater, container, listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View v = inflater.inflate(R.layout.nav_drawer, container, false);
		tv_acervoremoto = (TextView) v.findViewById(R.id.tv_acervoremoto);
		tv_meuacervo = (TextView) v.findViewById(R.id.tv_meuacervo);
		tv_atualizar = (TextView) v.findViewById(R.id.tv_atualizar);
		tv_suporte = (TextView) v.findViewById(R.id.tv_suporte);
		tv_tutorial = (TextView) v.findViewById(R.id.tv_tutorial);
		tv_creditos = (TextView) v.findViewById(R.id.tv_creditos);
		iv_sync = (ImageView) v.findViewById(R.id.iv_sync);
		tv_transferencias = (TextView) v.findViewById(R.id.tv_transferencias);
		tv_counter = (TextView) v.findViewById(R.id.tv_counter);
		if(tv_transferencias != null){
			isSmartphone = true;
		}
		totalTranferindo = 0;
		return v;
	}

	public void selectItem(int id){
		switch (id){
			case R.id.tv_acervoremoto:
				tv_acervoremoto.setSelected(true);
				tv_meuacervo.setSelected(false);
				if(tv_transferencias != null)
					tv_transferencias.setSelected(false);
				break;
			case R.id.tv_meuacervo:
				tv_acervoremoto.setSelected(false);
				tv_meuacervo.setSelected(true);
				if(tv_transferencias != null)
					tv_transferencias.setSelected(false);
				break;
			case R.id.tv_transferencias:
				tv_acervoremoto.setSelected(false);
				tv_meuacervo.setSelected(false);
				if(tv_transferencias != null)
					tv_transferencias.setSelected(true);
				break;
		}
	}

	@Override
	public void setListenerOnViews(OnClickListener listener) {
		mListener = listener;
		tv_acervoremoto.setOnClickListener(listener);
		tv_meuacervo.setOnClickListener(listener);
		tv_atualizar.setOnClickListener(listener);
		tv_suporte.setOnClickListener(listener);
		tv_tutorial.setOnClickListener(listener);
		tv_creditos.setOnClickListener(listener);
		iv_sync.setOnClickListener(listener);
		if (isSmartphone){
			tv_transferencias.setOnClickListener(listener);
		}
	}

	@Override
	protected void onViewCreated(View view) {}

	public boolean isSelected(int id) {
		switch (id) {
			case R.id.tv_acervoremoto:
				return tv_acervoremoto.isSelected();
			case R.id.tv_meuacervo:
				return tv_meuacervo.isSelected();
			case R.id.tv_transferencias:
				return  (tv_transferencias != null ? tv_transferencias.isSelected() : false);
			default:
				return  false;
		}
	}

	public void refresh() {
		AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
		builder.setCancelable(false);
		View titleView = getTitleView();
		bt_close = titleView.findViewById(R.id.bt_close);
		ImageView iv_sync = (ImageView) titleView.findViewById(R.id.iv_sync);
		RotateAnimation anim = new RotateAnimation(180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(1000);
		anim.setRepeatCount(Animation.INFINITE);
		iv_sync.startAnimation(anim);
		builder.setCustomTitle(titleView);
		builder.setTitle(R.string.sync_dialog_title);
		builder.setMessage(R.string.sync_dialog_message);
		builder.setNegativeButton(R.string.sync_dialog_bt_negative, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isRefreshing = false;
				mListener.onClick(bt_close);
			}
		});
		dialog = builder.show();
		isRefreshing = true;
	}

	private View getTitleView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		return inflater.inflate(R.layout.layout_sync_title, null, false);
	}

	public void stopRefresh(boolean updateRefreshing){
		if (dialog != null){
			dialog.dismiss();
			dialog = null;
		}
		if(updateRefreshing) {
			isRefreshing = false;
		}
	}

	public void setTransferencias(int qtd_transferencias) {

		totalTranferindo = qtd_transferencias;

		if(totalTranferindo == 0){
			tv_counter.setVisibility(View.GONE);
		} else {
			tv_counter.setVisibility(View.VISIBLE);
			tv_counter.setText(totalTranferindo + "");
		}
	}

	public boolean isSmartphone() {
		return isSmartphone;
	}

	public void addTranferencia(){
		if(totalTranferindo >= 0){
			tv_counter.setVisibility(View.VISIBLE);
		}
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.qtd_transferencias_anim);
		totalTranferindo++;
		tv_counter.setText(totalTranferindo+"");
		tv_counter.startAnimation(anim);
	}

	public void finishTransferencia(){

		totalTranferindo--;

		if(totalTranferindo == 0){
			tv_counter.setVisibility(View.GONE);
		}
		tv_counter.setText(totalTranferindo + "");
	}
}