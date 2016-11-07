package br.ufpe.sabertecnologias.acervoapp.ui.autenticacao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class AuthSessionExpiredReceiver extends BroadcastReceiver {


		public AuthSessionExpiredReceiver() {}

		@Override
		public void onReceive(Context context, Intent intent) {
			DebugLog.d(this, "onReceiver -- ");
			Facade facade = (Facade)context.getApplicationContext();
			facade.logout();
		}


}
