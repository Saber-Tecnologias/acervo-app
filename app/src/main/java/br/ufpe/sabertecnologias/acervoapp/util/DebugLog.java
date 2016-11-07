package br.ufpe.sabertecnologias.acervoapp.util;

import android.util.Log;

import br.ufpe.sabertecnologias.acervoapp.BuildConfig;


/**
 * Created by joaotrindade on 28/10/15.
 */
public class DebugLog {


    private static final String DEFAULT_TAG = "DebugLog";

    public static void d(String texto){
        d(null, texto);
    }

    public static void d(Object obj, String texto){
        if (BuildConfig.DEBUG) {
            String tag = String.format("%s - %s", DEFAULT_TAG, obj != null ? obj.getClass().getName() : DEFAULT_TAG);

            Log.d(tag, texto);
        }
    }
}
