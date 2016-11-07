package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.phone.ActivityTransferencias;
import br.ufpe.sabertecnologias.acervoapp.ui.tablet.MultiPaneActivityMeuAcervo;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServiceStatusMemoriaDownload extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_BOOT = "br.ufpe.sabertecnologias.acervoapp.modelo.negocio.action.BOOT";
    private static final String ACTION_DEFAULT = "br.ufpe.sabertecnologias.acervoapp.modelo.negocio.action.DEFAULT";
    private static final String ACTION_CANCEL_NOTIFICATION = "br.ufpe.sabertecnologias.acervoapp.modelo.negocio.action.CANCEL_NOTIFICATION";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "br.ufpe.sabertecnologias.acervoapp.modelo.negocio.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "br.ufpe.sabertecnologias.acervoapp.modelo.negocio.extra.PARAM2";

    private static final String PREFERENCES_NAME = "serviceStatusMemoriaDownloadPreferences";
    private static final String FLAG_HAS_NOTIFICATION = "flag_hasNotification";
    private static final int NOTIFICATION_ID = 66666;

    private SharedPreferences preferences;
    private boolean fl_has_notification;


    private Facade facade;

    public ServiceStatusMemoriaDownload() {
        super("ServiceStatusMemoriaDownload");

    }


    public static void startActionBoot(Context context) {
        Intent intent = new Intent(context, ServiceStatusMemoriaDownload.class);
        intent.setAction(ACTION_BOOT);
        context.startService(intent);
    }

    public static void startActionDefault(Context context) {
        Intent intent = new Intent(context, ServiceStatusMemoriaDownload.class);
        intent.setAction(ACTION_DEFAULT);
        context.startService(intent);
    }

    public static void startActionCancelNotification(Context context) {
        Intent intent = new Intent(context, ServiceStatusMemoriaDownload.class);
        intent.setAction(ACTION_CANCEL_NOTIFICATION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        facade = (Facade) getApplication();

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BOOT.equals(action)) {
                handleActionBoot();
            } else if (ACTION_DEFAULT.equals(action)) {
                handleActionDefault();
            } else if(ACTION_CANCEL_NOTIFICATION.equals(action)){
                handleActionCancelNotification();
            }
        }
    }


    private void showNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_notification_logo);
        mBuilder.setContentTitle(getResources().getString(R.string.aviso_titulo));
        mBuilder.setContentText(getResources().getString(R.string.aviso_texto));




        Intent resultIntent;

        if(UIUtils.isTablet(this)){
            //resultIntent = new Intent(getApplicationContext(), MultiPaneActivityAcervoRemoto.class);
            resultIntent = new Intent(getApplicationContext(), MultiPaneActivityMeuAcervo.class);
            resultIntent.putExtra("fromNotification", true);
        } else {
            resultIntent = new Intent(getApplicationContext(), ActivityTransferencias.class);
        }

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			/*TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addaddParentStack(ActivityMeuAcervo.class);

			stackBuilder.addNextIntent(resultIntent);*/


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setAutoCancel(false);


        Notification n = mBuilder.build();

        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, n);



    }



    private void handleActionBoot() {

        fl_has_notification = facade.isShowingAviso();//preferences.getBoolean(FLAG_HAS_NOTIFICATION, false);

        if (fl_has_notification){
            showNotification();
        }

    }

    private void handleActionDefault() {
      //  preferences.edit().putBoolean(FLAG_HAS_NOTIFICATION, true).commit();

        facade.showAviso();

        showNotification();

    }

    private void handleActionCancelNotification() {

        facade.hideAviso();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
