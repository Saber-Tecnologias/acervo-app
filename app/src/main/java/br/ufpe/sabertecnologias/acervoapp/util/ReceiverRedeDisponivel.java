package br.ufpe.sabertecnologias.acervoapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.SyncService;


public class ReceiverRedeDisponivel extends BroadcastReceiver{
	Facade facade;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		if(isConnected){
			facade = (Facade) context.getApplicationContext();
			BroadcastUtil.setBroadcastEnableState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, SyncService.ReceiverRedeDisponivelSync.class);
			sync();
		}
	}

	public void sync(){
		try {
			facade.sync(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
