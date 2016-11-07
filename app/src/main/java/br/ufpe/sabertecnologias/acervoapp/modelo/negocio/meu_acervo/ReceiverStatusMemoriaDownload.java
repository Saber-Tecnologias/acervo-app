package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.saber.downloadservice.DownloadService;

/**
 * Created by joaotrindade on 01/12/15.
 */
public class ReceiverStatusMemoriaDownload extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(DownloadService.STORAGE_FULL_ACTION)){
            ServiceStatusMemoriaDownload.startActionDefault(context);
        } else {
            //BOOT
            ServiceStatusMemoriaDownload.startActionBoot(context);
        }

    }
}
