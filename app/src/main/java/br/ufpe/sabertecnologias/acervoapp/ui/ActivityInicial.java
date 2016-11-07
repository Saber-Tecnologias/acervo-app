package br.ufpe.sabertecnologias.acervoapp.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Executor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener.ExecutorResult;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.AppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.NegocioSincronizacao.SetupInicialListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.setup_server.SetupServerService;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.SyncService;
import br.ufpe.sabertecnologias.acervoapp.ui.autenticacao.ActivityAutenticacao;
import br.ufpe.sabertecnologias.acervoapp.ui.phone.ActivityMeuAcervo;
import br.ufpe.sabertecnologias.acervoapp.ui.tablet.MultiPaneActivityMeuAcervo;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentErroSetupInicial;
import br.ufpe.sabertecnologias.acervoapp.util.ConnectionUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.ufpe.sabertecnologias.acervoapp.util.ExternalStorageNotFoundException;
import br.ufpe.sabertecnologias.acervoapp.util.FileManager;
import br.ufpe.sabertecnologias.acervoapp.util.FolderControl;

public class ActivityInicial extends AppCompatActivity implements SetupInicialListener, ExecutorResult, OnClickListener, ServiceConnection {

	private static final String PREFERENCE_KEY = "activity_inicial_preference_key";
	private static final String IS_FIRST_ACCESS_KEY = "is_first_access_key";
	private static final String APP_CONFIG_KEY = "app_config_key";
	private static final String BINDED_KEY = "binded_key" ;
	private static final String BINDED_ON_SYNC_KEY = "bounded_on_sync_key";


	private static final int REQUEST_CODE_AUTENTICACAO = 11;
	private static final int REQUEST_CODE_TUTORIAL = 12;

	private Messenger mRefreshCallback = new Messenger(new SyncCallbackHandler() );

	private AppConfig appConfig;
	private Facade facade;
	private boolean isFirstAccess;
	private SharedPreferences preferences;

	private SetupServerService mService;
	private SyncService mSyncService;

	private boolean binded;
	private boolean bindedOnSyncService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		facade = (Facade) getApplication();
		preferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        isFirstAccess = preferences.getBoolean(IS_FIRST_ACCESS_KEY, true);

		if(savedInstanceState == null) {
			final AsyncExecutor executor = new AsyncExecutor(this);
			facade.getAppConfig(executor);
			binded = false;
			bindedOnSyncService = false;
		} else {
			appConfig = savedInstanceState.getParcelable(APP_CONFIG_KEY);
			binded = savedInstanceState.getBoolean(BINDED_KEY);
			bindedOnSyncService = savedInstanceState.getBoolean(BINDED_ON_SYNC_KEY);
		}
		setContentView(R.layout.activity_loading_inicial);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if(binded) {
			setupServidor(this);
		}

		if(bindedOnSyncService){
			sync();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(APP_CONFIG_KEY, appConfig);
		outState.putBoolean(BINDED_KEY, binded);
		outState.putBoolean(BINDED_ON_SYNC_KEY, bindedOnSyncService);

	}

	@Override
	protected void onStop() {
		super.onStop();
		if(binded ){
			unbind();
		}

		if(bindedOnSyncService){
			facade.finishSync(ActivityInicial.this);
			bindedOnSyncService = false;
		}

	}

	@Override
	public void onExecutorResult(Object obj) {
		if(obj instanceof AppConfig){
			appConfig = (AppConfig) obj;
			acessar();
		} else if(obj instanceof String) {
			autenticar();
		}

	}

	private void acessar() {
		int connection = ConnectionUtil.getConnectivityStatus(this);
		if(!isFirstAccess){
			if(appConfig.getHas_att() == 1) {
                //Tem atualização

				appConfig.setHas_att(0);
				new AsyncTask<Void, Void, Void>(){

					@Override
					protected Void doInBackground(Void... params) {
						facade.attAppConfig(new Executor(null), appConfig);
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						acessar();
					}
				}.execute();

            }else if(appConfig.getHas_tutorial() == 1){
				mostrarTutorial();
			} else {
				iniciarSessao();
			}

		} else {
			//Primeiro Acesso
			if(connection != ConnectionUtil.TYPE_NOT_CONNECTED){
				//Tem conexao
				long current = System.currentTimeMillis();

				long offset = appConfig.getSetupTPMax()*60*60*1000;

				DebugLog.d(this, "CURRENT = " + current + ", offset = " + offset + ", Final = " + (current + offset));
				appConfig.setInitialTime(current);

				new AsyncTask<Void, Void, Void>(){

					@Override
					protected Void doInBackground(Void... params) {
						facade.attAppConfig(new Executor(null), appConfig);
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						/* 0 */ setupServidor(ActivityInicial.this);
						/* 1 */   //autenticar();
		                /* 2 */   //setup_temas;
					}
				}.execute();
			} else {
				//TODO SOLICITAR REDE
			}
		}
	}


	private void iniciarSessao() {
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				Executor executor = new Executor(null);
				facade.setUser(executor);
				FolderControl.makeDir(getApplicationContext());
				try {
					FileManager.getInstance(getApplicationContext()).init();
				} catch (ExternalStorageNotFoundException e) {
					e.printStackTrace();
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				Intent intent;
				intent = new Intent(ActivityInicial.this, UIUtils.isTablet(ActivityInicial.this) ? MultiPaneActivityMeuAcervo.class : ActivityMeuAcervo.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}

		}.execute();
	}

	private void setupServidor(SetupInicialListener listener) {
		Intent it = new Intent(this, SetupServerService.class);
		bindService(it, this, Context.BIND_AUTO_CREATE);
	}

    /**
     *
     * Response da função de setupServidor
     *
     * @param json
     */
	@Override
	public void onResponse(String json) {
		unbind();
		DebugLog.d(this, "ActivityInicial - onResponseSendLog = "+json);
		boolean supportServer = false;
		if(json != null){
			supportServer = true;
		}
		if(supportServer) {
			autenticar();
		} else {
            //TODO NOTIFICAR QUE O APLICATIVO PRECISA SER ATUALIZADO
        }
	}

	@Override
	public void onErrorResponse( ) {
		unbind();
		DialogFragmentErroSetupInicial dialog = new DialogFragmentErroSetupInicial();
		dialog.init(this, this);
		dialog.show(getSupportFragmentManager(), "dialogFragmentErroSetupInicial");
	}

	private void unbind() {
		unbindService(this);
		binded = false;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		setupServidor(this);
		dialog.dismiss();		
	}

	private void sync() {
		facade.sync(true);
		facade.sync(mRefreshCallback, this);
	}

	private void mostrarTutorial() {
		Intent it = new Intent(this, ActivityTutorial.class);
		startActivityForResult(it, REQUEST_CODE_TUTORIAL);
	}

	private void autenticar() {
		Intent it = new Intent(this, ActivityAutenticacao.class);
		startActivityForResult(it, REQUEST_CODE_AUTENTICACAO );
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_AUTENTICACAO:
			if(resultCode == RESULT_OK) {
				new AsyncTask<Void, Void, Void>(){

					@Override
					protected Void doInBackground(Void... params) {
						Executor executor = new Executor(null);

						facade.setUser(executor);
						return null;
					}
					protected void onPostExecute(Void result) {
						if(isFirstAccess){
							preferences.edit().putBoolean(IS_FIRST_ACCESS_KEY, false).apply();
							isFirstAccess = false;
							sync();
						} else {
							acessar();
						}
					};
				}.execute();
			} else {
				finish();
			}
			break;
		case REQUEST_CODE_TUTORIAL:
			if(resultCode == RESULT_OK) {
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... params) {
						Executor executor = new Executor(null);
						appConfig.setHas_tutorial(0);
						facade.attAppConfig(executor, appConfig);
						return null;
					}
					protected void onPostExecute(Void result) {
                        acessar();
					};
				}.execute();

			} else {
				finish();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if(service instanceof SetupServerService.SetupServerBinder ) {
			SetupServerService.SetupServerBinder mBinder = ((SetupServerService.SetupServerBinder) service);
			mService = mBinder.getService();
			mService.setListener(this);
			facade.enableSetupServerAlarme();
			binded = true;
		} else if(service instanceof SyncService.SyncServerBinder) {
			DebugLog.d(this," onService Connected");
			SyncService.SyncServerBinder mBinder = ((SyncService.SyncServerBinder)service);
			mSyncService = mBinder.getService();
			if(!mSyncService.isRunning()) {
				mSyncService.refresh();
			} else {
				if(mSyncService.isFinished()){
					mSyncService.stopSelf();
				}
			}
			bindedOnSyncService = true;
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		DebugLog.d(this, "OnDisconnect");
		binded = false;
	}

	class SyncCallbackHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SyncService.REFRESH_OK:
					DebugLog.d(this, "REFRESH RESULT OK!");
					mSyncService.stopSelf();
					facade.finishSync(ActivityInicial.this);
					isFirstAccess = false;
					facade.enableSyncAlarme();
					bindedOnSyncService = false;
					acessar();
					break;
				case SyncService.REFRESH_ERROR:
					DebugLog.d(this, "REFRESH RESULT ERROR!");
					mSyncService.stopSelf();
					facade.finishSync(ActivityInicial.this);
					bindedOnSyncService = false;
					sync();
					break;
				default:

			}
		}
	}

}
