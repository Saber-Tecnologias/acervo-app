package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync;

import org.json.JSONObject;

/**
 * Created by joaotrindade on 26/10/16.
 */

public interface SyncCallback{
    public void onResponseSendLog(JSONObject arg0);
    public void onErrorSendLog();
}