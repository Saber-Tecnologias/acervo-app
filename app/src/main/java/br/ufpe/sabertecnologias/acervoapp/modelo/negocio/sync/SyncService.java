package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Executor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class SyncService extends Service implements ExecutorListener.ExecutorResult, SyncCallback, NegocioSincronizacao.GetGruposListener, NegocioSincronizacao.GetItensListener {

	private boolean isRunning;
	private boolean isFinished;

	private Facade facade;
	private JSONObject gruposJson;
	private JSONObject itensJson;
	private ArrayList<Item> itensListOld;

	public static final String START_FROM_REFRESH = "start_from_refresh";

    public static final int PHASE_SEND_LOG = 20;
    public static final int PHASE_RECEIVE_SYNC = 21;
    public static final int PHASE_NONE = -20;
    private int currentPhase = PHASE_NONE;

	private Messenger mCallback;

	public static final int REFRESH_OK = 1;
	public static final int REFRESH_ERROR = -1;

	private final IBinder mBinder = new SyncServerBinder();

	private boolean fromRefresh;
    private boolean cancelled;


    public class SyncServerBinder extends Binder {

		public SyncService getService() {
			// Return this instance of SetupServerBinder so clients can call public methods
			return SyncService.this;
		}
	}

	@Override
	public void onCreate() {

		super.onCreate();
		facade = (Facade) getApplication();
		isRunning = false;
		isFinished = false;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DebugLog.d(this, "Start Service");
		DebugLog.d(this, "Start Service - intent = "+intent);
		DebugLog.d(this, "Start Service - isRunning = " + isRunning);

		if(!isRunning && (intent != null)){
			//new SyncPostAsyncTask().execute();
			fromRefresh = intent.getBooleanExtra(START_FROM_REFRESH, false);


			DebugLog.d(this, "isFromRefresh = "+fromRefresh );

			if(!fromRefresh){
				//foi disparado pelo AlarmeManager como uma rotina
				Executor executor = new Executor(null);
				facade.enviarPilhaLog(executor, this, this.getApplicationContext());
				isRunning = true;

			} else{
				DebugLog.d(this, "service started but isnt RUNNING!");
			}
		}else{

		}

		return START_REDELIVER_INTENT;

	}

	@Override
	public IBinder onBind(Intent intent) {

		DebugLog.d(this, "onBind()");
		DebugLog.d(this, "intent != null = "+(intent!=null));

		if(intent != null) {
			mCallback = (Messenger) intent.getParcelableExtra("messenger");
			DebugLog.d(this, "mCallback = "+ mCallback);

		}

		return mBinder;

	}

	public boolean isFinished(){
		return isFinished;
	}

	public boolean isRunning(){
		return isRunning;
	}

	public void refresh(){
		isRunning = true;
		Executor executor = new Executor(null);
		facade.enviarPilhaLog(executor, this, this.getApplicationContext());
        currentPhase = PHASE_SEND_LOG;
	}

	@Override
	public void onExecutorResult(Object obj) {
		//resultados das assync
        currentPhase = PHASE_NONE;
		DebugLog.d(this, "obj: " + obj);
		if (obj instanceof Integer){
			int result = ((Integer) obj).intValue();
			switch (result){
				case NegocioSincronizacao.SYNC_RESPONSE_GET_GRUPO://caso retorno do persistirGruposFromSync
					AsyncExecutor aExecutor = new AsyncExecutor(this);
					facade.persistirItensFromSync(aExecutor, itensJson, itensListOld);
					break;
				case NegocioSincronizacao.SYNC_RESPONSE_GET_GRUPO_ERROR:
					finishRefresh(REFRESH_ERROR);
					break;
				case NegocioSincronizacao.SYNC_RESPONSE_GET_ITEM://caso retorno do persistirItensFromSync
					finishRefresh(REFRESH_OK);
					break;
				case NegocioSincronizacao.SYNC_RESPONSE_GET_ITEM_ERROR:
					finishRefresh(REFRESH_ERROR);
					break;
				default:
					finishRefresh(REFRESH_OK);
					break;
			}
		}
		else {
			if(obj instanceof List<?>){
				itensListOld = (ArrayList<Item>) obj;
				AsyncExecutor asyncExecutor = new AsyncExecutor(this);
				facade.persistirGruposFromSync(asyncExecutor, gruposJson);
			} else {
				finishRefresh(REFRESH_ERROR);
			}
		}
	}

	private void finishRefresh(int refreshResult) {
		try {
			if(!fromRefresh) {
				isRunning = false;
				stopSelf();
			} else {
				Message m = new Message();
				m.what = refreshResult;
				mCallback.send(m);
				isFinished = true;
			}
		} catch (RemoteException e) {
			Log.e("DEBUG", e.getMessage());
		}
	}
    /**
     * Cancela o sync se estiver em andamento
     */
    public boolean cancelSync()
    {
        cancelled = true;
        if(isRunning())
        {
            facade.cancelaSendLog();
            facade.cancelaReceiveSync();
        }
        return isRunning;

    }

    @Override
	public void onResponseSendLog(JSONObject jsonObject) {

		DebugLog.d(this, "onResponseSendLog");
		if(jsonObject != null){
			DebugLog.d(this, "OnResponse = "+jsonObject.toString());
		}
		if(!cancelled) {
            currentPhase = PHASE_RECEIVE_SYNC;
			DebugLog.d(this, "Tentou getGruposFromServer()");
            facade.getGruposFromServer(this);
        }
	}

	@Override
	public void onErrorSendLog() {
		finishRefresh(REFRESH_ERROR);
	}

	@Override
	public void onResponseGetGrupos(JSONObject response) {
		DebugLog.d(this, "GET GRUPOS (onResponseSendLog) : recebeu o JSON: >>" + response.toString());
		gruposJson = response;
		facade.getItensFromServer(this);
	}

	@Override
	public void onErrorResponseGetGrupos() {
		Log.e("DEBUG", "Erro no GET GRUPOS");
	}

	@Override
	public void onResponseGetItens(JSONObject response) {
		DebugLog.d(this, "!!!GET ITENS (onResponseSendLog) : recebeu o JSON: >>" + response.toString());
		itensJson = response;
		AsyncExecutor executor = new AsyncExecutor(this);
		facade.getItens(executor);
	}

	@Override
	public void onErrorResponseGetItens() {
		Log.e("DEBUG","Erro no GET ITENS");
	}

    public static class ReceiverRedeDisponivelSync extends BroadcastReceiver {
		Facade facade;
		@Override
		public void onReceive(Context context, Intent intent) {
			DebugLog.d(this, "ReceiverRedeDisponivelSync onReceiver");
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
			if(isConnected){
				facade = (Facade) context.getApplicationContext();
				BroadcastUtil.setBroadcastEnableState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, this.getClass());
				sync();
			}
		}

		public void sync(){
			try {
				facade.sync(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
