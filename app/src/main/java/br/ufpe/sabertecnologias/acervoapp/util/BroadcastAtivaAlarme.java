package br.ufpe.sabertecnologias.acervoapp.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.BroadcastSync;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.NegocioSincronizacao;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.setup_server.ServiceAtivaAlarmeSetupServer;

public class BroadcastAtivaAlarme extends BroadcastReceiver {

	/**
	 *
	 * Broadcast que ativa o Alarme Manager a cada intervalo de repetição.
	 *
	 * Tem duas actions no manifest:
	 * 	.BOOT_COMPLETED (quando ligar o smartphone)
	 * 	.BROADCAST_START_ALARMMANAGER (quando abre o aplicativo a primeira vez) TODO: colocar a chamada na Applicaiton
	 *
	 */

	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	private static final long INTERVALO_REPETICAO_MINUTOS = 2;
	private static final long INTERVALO_REPETICAO_HORAS = 1;//* AlarmManager.INTERVAL_DAY; //TODO: precisa ser definido!

	@Override
	public void onReceive(Context ctx, Intent intent) {
		this.enableAlarm(ctx, intent.getAction());
	}

	/**
	 *
	 * AlarmManager: Dispara o Broadcast do download da atualização da lista (BroadcastAtualizarDownloadItens).
	 *
	 */

	public void enableAlarm(Context ctx, String action) {
		if(action.equals(NegocioSincronizacao.ACTION_ATIVAR_ALARME_SETUP_SERVER)){
			ativaAlarmeSetupServer(ctx);
		} else if(action.equals(NegocioSincronizacao.ACTION_ATIVAR_ALARME_ATT_LISTA_ITENS)){
			ativaAlarmeSync(ctx);
		} else {
			ativaAlarmeSync(ctx);
			ativaAlarmeSetupServer(ctx);
		}
	}

	private void ativaAlarmeSetupServer(Context ctx) {
		Intent it = new Intent(ctx, ServiceAtivaAlarmeSetupServer.class);
		ctx.startService(it);
	}

	private void ativaAlarmeSync(Context ctx) {
		alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, BroadcastSync.class);
		alarmIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		long inicialTime = System.currentTimeMillis() + (INTERVALO_REPETICAO_HORAS*INTERVALO_REPETICAO_MINUTOS*60*1000);
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				inicialTime,
				INTERVALO_REPETICAO_HORAS*INTERVALO_REPETICAO_MINUTOS*60*1000,
				alarmIntent);
	}
}
