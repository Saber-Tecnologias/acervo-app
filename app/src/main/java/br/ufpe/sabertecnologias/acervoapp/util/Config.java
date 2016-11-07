package br.ufpe.sabertecnologias.acervoapp.util;


import br.ufpe.sabertecnologias.acervoapp.BuildConfig;

/**
 * Created by joaotrindade on 26/10/15.
 */
public class Config {
    //0 -> DEV;
    //1 -> PRODUCAO
    public static  final int APP_MODE = BuildConfig.DEBUG ? 0 : 1;

}
