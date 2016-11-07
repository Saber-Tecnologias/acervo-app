package br.ufpe.sabertecnologias.acervoapp.ui.controllers;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.MenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import br.saber.downloadservice.Downloader;
import jpttrindade.widget.tagview.Tag;


public class MenuController extends Fragment {

	public static final String TAG = "menuController";

	private MenuView mView;
	private ViewListener mListener;
	private MenuView.TYPE type;
	private MenuCallback callback;
	private int selecetedFiltroId,selecetedFiltroTipoId;

	private Context mContext;
	private DownloadStatusReceiver downloadStatusReceiver;

	public Fragment init(Context ctx, MenuView.TYPE type){
		mContext = ctx;
		this.type = type;
		downloadStatusReceiver = new DownloadStatusReceiver();
		setRetainInstance(true);
		return this;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = (MenuCallback) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mListener = new ViewListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				}

			@Override
			public void onClick(View v) {
				Tag tag = null;
				Integer id = null;
				Object obj = v.getTag();
				if(obj instanceof Tag){
					tag = (Tag) obj;
					onClickFiltro(v, tag);
				}else{
					id = (Integer) obj;
					onClickOptions(v, id);
				}
				callback.habilitaToolbarContextual(0, -1, true);
			}
		};
		mView= new MenuView(inflater, container, mListener );
		mView.setContent(type, mListener);
		if(selecetedFiltroId != -1){
			mView.selectFiltro(selecetedFiltroId);
		}
		if(selecetedFiltroTipoId != 0){
			mView.selectFiltro(selecetedFiltroTipoId);
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Downloader.DOWNLOAD_CANCELED_ACTION);
		intentFilter.addAction(Downloader.DOWNLOAD_TERMINATED_ACTION);
		mContext.getApplicationContext().registerReceiver(downloadStatusReceiver, intentFilter);
		return mView.getView();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mContext.getApplicationContext().unregisterReceiver(downloadStatusReceiver);
	}

	protected void onClickOptions(View v, Integer id) {
		if(!v.isSelected()){
			selectFiltro(id);
			callback.showFiltro(id, type);
		}else{
			if(id != MenuView.ID_MEUS_GRUPOS) {
				deselectFiltro(id);
				callback.hideFiltro(id);
			}
		}
	}

	protected void onClickFiltro(View v, Tag tag) {
		if(tag != null){
			if(v.isSelected()){
				deselectFiltro(tag.id);
				callback.removeTag(tag);
			}
			else{
				Tag old_tag = mView.selectFiltro(tag.id);
				if(old_tag!=null){
					callback.removeTag(old_tag);
				}
				callback.addTag(tag);
			}
		}		
	}

	public void selectFiltro(int id) {
		if(mView!=null){
			Tag t = mView.selectFiltro(id);
			if (t != null) {
				selecetedFiltroTipoId = id;
			} else {
				selecetedFiltroId = id;
			}
		}
	}

	public void deselectFiltro(int id) {

		if(mView!= null) {
			if (mView.deselectFiltro(id) == null) {
				selecetedFiltroId = -1;
			} else {
				selecetedFiltroTipoId = 0;
			}
		}
	}

	public void piscarTransferindo() {}

	public void addTransferencia(){
		mView.addTranferencia();
	}

	public void finishTranferencia(){
		mView.finishTransferencia();
	}

	public void setTransferencias(int transferencias) {
		mView.setTransferencias(transferencias);
	}

	public class DownloadStatusReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Downloader.DOWNLOAD_TERMINATED_ACTION)) {
				finishTranferencia();
			} else if(action.equals(Downloader.DOWNLOAD_CANCELED_ACTION)){
				finishTranferencia();
			}
		}
	}
}
