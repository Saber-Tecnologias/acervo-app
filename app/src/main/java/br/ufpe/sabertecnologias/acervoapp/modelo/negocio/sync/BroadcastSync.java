package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;

import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class BroadcastSync extends BroadcastReceiver {
	
	/**
	 * 
	 * Broadcast que sincroniza os dados a partir do status da internet.
	 * 
	 */
	
	private Facade facade;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		DebugLog.d(this, "OnReceiver - "+intent.getAction());

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) { //tem internet
			
			facade = (Facade) context.getApplicationContext();
			
			try {
				//facade.attListaItens();
				facade.sync(false);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			Log.d("DEBUG", "tem internet");
			
		} else {
			
			//Habilita o BroadcastUtil.
			 
			BroadcastUtil.setBroadcastEnableState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, SyncService.ReceiverRedeDisponivelSync.class);
			
//			Log.d("DEBUG", "não tem internet");
			//Toast.makeText(context, "não tem internet", Toast.LENGTH_LONG).show();
			
		}
	}

}
