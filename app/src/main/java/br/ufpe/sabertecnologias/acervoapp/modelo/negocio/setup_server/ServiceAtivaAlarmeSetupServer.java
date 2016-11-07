package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.setup_server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import br.ufpe.sabertecnologias.acervoapp.modelo.Executor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.AppConfig;

/**
 * Created by joaotrindade on 14/09/15.
 */
public class ServiceAtivaAlarmeSetupServer extends Service {

    private Facade facade;
    private boolean isRunning;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    public void onCreate() {

        super.onCreate();
        facade = (Facade) getApplication();
        isRunning = false;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                final ExecutorListener.ExecutorResult result = new ExecutorListener.ExecutorResult() {
                    @Override
                    public void onExecutorResult(Object obj) {
                        AppConfig appConfig = (AppConfig) obj;
                        ativaAlarmeSetupServer(appConfig);
                    }
                };

                Executor executor = new Executor(result);

                facade.getAppConfig(executor);


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                stopSelf();
            }
        }.execute();


        return START_STICKY;

    }

    private void ativaAlarmeSetupServer(AppConfig appConfig) {

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BroadcastSetupServer.class);

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, appConfig.getSetupTPMax() *10 * 1000, alarmIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, appConfig.getInitialTime(), appConfig.getSetupTPMax() * 60 * 60 * 1000, alarmIntent);
    }
}
