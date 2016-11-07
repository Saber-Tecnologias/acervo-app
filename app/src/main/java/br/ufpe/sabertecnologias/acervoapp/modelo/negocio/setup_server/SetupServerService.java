package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.setup_server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.AppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.ServerInfoResponse;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.NegocioSincronizacao;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class SetupServerService extends Service implements NegocioSincronizacao.SetupInicialListener, ExecutorListener.ExecutorResult {

	private final IBinder mBinder = new SetupServerBinder();

	private boolean isRunning;
	private Facade facade;
	private AppConfig appConfig;
	private NegocioSincronizacao.SetupInicialListener setupInicialListener;
	private String json;

	private boolean isStarted;

	@Override
	public void onCreate() {

		super.onCreate();
		facade = (Facade) getApplication();
		isRunning = false;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		DebugLog.d(this, "ServiceSetupServer onStart");
		DebugLog.d(this, "ServiceSetupServer isRunning = "+isRunning);

		if(!isRunning && (intent != null)){

			isStarted = true;

			setupServer(setupInicialListener);
		}else{

		}

		return START_REDELIVER_INTENT;

	}

	public void setupServer(NegocioSincronizacao.SetupInicialListener setupInicialListener) {
		try {
			if(setupInicialListener != null) {
				this.setupInicialListener = setupInicialListener;
			}
			facade.setupServidor(this);
			isRunning = true;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e("DEBUG", e.getMessage());
			isRunning = false;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		DebugLog.d(this, "Service onBind");

		return mBinder;
	}


	@Override
	public void onResponse(String json) {
		this.json = json;
		DebugLog.d(this, json);
		AsyncExecutor executor = new AsyncExecutor(this);
		facade.getAppConfig(executor);
	}

	@Override
	public void onErrorResponse( ) {
		if (this.setupInicialListener != null){
			setupInicialListener.onErrorResponse();
		}
		if(isStarted){
			stopSelf();
		}
	}

	@Override
	public void onExecutorResult(Object obj) {
		if(obj instanceof  AppConfig){
			AppConfig appConfig = (AppConfig) obj;
			boolean supportServer = false;
			ServerInfoResponse response = ServerInfoResponse.parse(json);

			//Verifica se o servidor tem suporte a versao da api suportada pelo app
			for(int i=0; i<response.protocolos_suportados.length; i++) {
				if(appConfig.getApi_version().equals(response.protocolos_suportados[i])) {
					supportServer = true;
				}
			}


			if(this.setupInicialListener != null){

				if(supportServer) {
					setupInicialListener.onResponse(json);
				} else {
					setupInicialListener.onResponse(null);
				}
			}


			isRunning = false;

			if(isStarted){
				stopSelf();
			}
		}
	}

	public void setListener(NegocioSincronizacao.SetupInicialListener setupInicialListener ) {
		this.setupInicialListener = setupInicialListener;
	}


	public class SetupServerBinder extends Binder {

		public SetupServerService getService() {
			// Return this instance of SetupServerBinder so clients can call public methods
			return SetupServerService.this;
		}
	}



	public static class ReceiverRedeDisponivelSetupServer extends BroadcastReceiver {

		Facade facade;

		@Override
		public void onReceive(Context context, Intent intent) {

			DebugLog.d(this, "ReceiverRedeDisponivelSetupServer onReceiver");
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
			if(isConnected){
				facade = (Facade) context.getApplicationContext();

				BroadcastUtil.setBroadcastEnableState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, ReceiverRedeDisponivelSetupServer.class);

				setupServer();
			}
		}

		public void setupServer(){
			try {
				//facade.attListaItens();
				facade.setupServidor();

			} catch (Exception e) {
				Log.e("DEBUG", e.getMessage());
			}

		}



	}


}
