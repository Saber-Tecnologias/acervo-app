package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioObserver;
import br.ufpe.sabertecnologias.acervoapp.ui.tablet.RecomendacoesTabsAdapter;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

/**
 * Created by joaotrindade on 03/02/16.
 */
public class RecomendacoesControllerComposite implements RepositorioObserver.RepositorioObserverCallback {


    private final int REPOSITORIO_OBSERVER_ID = 117;
    private  Context mContext;
    private  FragmentManager mFragmetnManager;
    private RecomendacoesTabsAdapter recomendacoesTabAdapter;
    private  Facade facade;
    private  ArrayList<Integer> itensCodigos;
    private HashMap<Integer, RecentesController> recentesControllers;
    private RecomendacoesController recomendacoesController;
    private RepositorioObserver observer;





    public RecomendacoesControllerComposite(Context context, FragmentManager fm){
        mContext = context;

        facade = (Facade) context.getApplicationContext();


        itensCodigos = new ArrayList<Integer>();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                itensCodigos.addAll(facade.getItensCodigos());
                return null;
            }

        }.execute();

        mFragmetnManager = fm;
        recentesControllers = new HashMap<Integer, RecentesController>();


        List<Fragment> frags =  mFragmetnManager.getFragments();

        if(frags != null) {
            int position = 0;
            for (Fragment f :frags) {
                if (f instanceof RecomendacoesController) {
                    recomendacoesController = (RecomendacoesController) f;
                    position++;
                } else if (f instanceof RecentesController) {
                    recentesControllers.put(position, (RecentesController) f);
                    position++;
                }
            }
        }
        recomendacoesTabAdapter = new RecomendacoesTabsAdapter(mFragmetnManager, mContext, this);
        observer = new  RepositorioObserver(this);
        DatabaseObserverManager.addRepositorioItemObserver(observer, REPOSITORIO_OBSERVER_ID);

    }

    public RecomendacoesTabsAdapter getRecomendacoesTabAdapter() {
        return recomendacoesTabAdapter;
    }

    public void updateFromDetalhes(Item item) {
        itensCodigos.add(item.getCodigo());
        recomendacoesController.updateFromDetalhes(item);
        for(RecentesController rc : recentesControllers.values()){
            rc.updateFromDetalhes(item);
        }
    }

    public Fragment getRecomendacoesController() {
        if(recomendacoesController == null){
            recomendacoesController = new RecomendacoesController().init(mContext, itensCodigos);
        }
        return recomendacoesController;
    }

    public  RecentesController getRecentesController(int position){
        RecentesController recentesController;

        DebugLog.d(this, "recentesControllers.size() = "+ recentesControllers.size());

        if(recentesControllers.containsKey(position)){
            recentesController = recentesControllers.get(position);
        } else {
            recentesController = new RecentesController().init(position, mContext, itensCodigos);
            recentesControllers.put(position,recentesController);
        }
        return recentesController;
    }

    @Override
    public void updateFromRepositorio(int method, Object obj) {
        if(obj instanceof Item){
            if(recomendacoesController != null) recomendacoesController.updateFromRepositorio(method, obj);
            for(RecentesController recentesController : recentesControllers.values()){
                recentesController.updateFromRepositorio(method, obj);
            }
        }
    }
}
