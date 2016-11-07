package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.setup_server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;

import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;

/**
 * Created by joaotrindade on 14/09/15.
 */
public class BroadcastSetupServer extends BroadcastReceiver{
    private Facade facade;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) { //tem internet

            facade = (Facade) context.getApplicationContext();

            try {
                //facade.attListaItens();
              setupServer(context);

            } catch (Exception e) {
                Log.e("DEBUG", e.getMessage());

            }



        } else {

            //TODO habilitar o listener de REDE.

           BroadcastUtil.setBroadcastEnableState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, SetupServerService.ReceiverRedeDisponivelSetupServer.class);


        }
    }

    private void setupServer(Context context) {
        facade.setupServidor();
    }
}
