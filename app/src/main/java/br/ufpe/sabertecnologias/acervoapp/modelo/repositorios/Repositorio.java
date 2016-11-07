package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.util.SparseArray;

/**
 * Created by jpttrindade on 29/09/15.
 */
abstract class Repositorio {

    private SparseArray<RepositorioObserver> mObservers;

    public Repositorio(){
        mObservers = new SparseArray<RepositorioObserver>();
    }

    public void addObserver(RepositorioObserver observer, int id) {
        mObservers.put(id, observer);
    }

    public void removeObserver(RepositorioObserver observer) {
        int index = mObservers.indexOfValue(observer);
        if(index != -1)
            mObservers.removeAt(index);
    }

    public void notifyObservers(int method, Object obj) {
       for(int i=0; i<mObservers.size(); i++){
           mObservers.valueAt(i).notify(method, obj);
       }
    }
}
