package br.ufpe.sabertecnologias.acervoapp.modelo.mock;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.ResponseItens;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.ufpe.sabertecnologias.acervoapp.util.FormatoConvert;

/**
 * Created by joaotrindade on 25/10/16.
 */

public class LocalService implements IService {


    private static LocalService instance;
    private final ArrayList<Item> dump;
    private final JSONObject meus_grupos;
    private final JSONObject meus_itens;
    private final JSONObject setup_servidor;

    private MyTask a;
    private CancellabelAsyncTask b;

    public static LocalService getInstance (Context ctx) {
        if (instance == null) {
            instance = new LocalService(ctx);
        }

        return instance;
    }

    private LocalService(Context context) {
        dump = MockData.getItensFromDataServerDump(context);
        meus_grupos = MockData.getMeus_grupos(context);
        meus_itens = MockData.getMeus_itens(context);
        setup_servidor = MockData.getSetup_servidor(context);
    }

    @Override
    public void getRecentes(final JSONObject body, final String tipo, final int limit, final int formato, final GetRecentesCallback callback) {

        new AsyncTask<Void, Void, ResponseItens>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                callback.onRequestGetRecentes();
            }

            @Override
            protected ResponseItens doInBackground(Void... params) {
                ArrayList<Item> result = new ArrayList<Item>();
                int limitCount = 0;
                Item item;
                for (int i = 0; i<dump.size(); i++) {
                    item = dump.get(i);
                    if (FormatoConvert.convertFormatoToTipo(FormatoConvert.covertFormato(item.getFormato())).equals(tipo)) {
                        result.add(item);
                        limitCount++;
                        if (limitCount == limit) {
                            i = dump.size();
                        }
                    }
                }

                return new ResponseItens(dump.size(),dump.size(), limit, result);
            }

            @Override
            protected void onPostExecute(ResponseItens responseItens) {
                callback.onResponseGetRecentes(responseItens);
            }
        }.execute();

    }

    @Override
    public void getItens(final JSONObject body, final int offset, final int limit, final int format, final String[] tipos, final String[] termos, final boolean cancelCurrent, final GetItensCallback callback) {
        if (a != null && cancelCurrent) {
            a.setCancelled();
            if (callback != null) {
                callback.onCancelRequestGetItens();
            }
        }
        a = new MyTask(callback, tipos, termos, offset, limit);
        a.execute();
    }

    @Override
    public void cancelGetItens() {
        a.setCancelled();
    }

    @Override
    public void sendLog(final JSONObject body, final SendLogCallback callback) {

        //DebugLog.d(this, "LOG = "+ body.toString());
        JSONObject jsonObject = new JSONObject();
        DebugLog.d(this, "fin do sendLog");
        callback.onResponseSendLog(jsonObject);
    }

    @Override
    public void cancelSendLog() {

    }

    @Override
    public void getMeusGrupos(JSONObject body, final GetMeusGruposCallback callback) {
        b = new GetMeusGruposTaks(callback);
        b.execute();
    }

    @Override
    public void getMeusItens(JSONObject body, GetMeusItensCallback callback) {
        b = new GetMeusItensTaks(callback);
        b.execute();
    }

    @Override
    public void cancelSync() {
        b.setCancelled();
    }

    @Override
    public void setupServidor(String versao, SetupServidorCallback callback) {


        callback.onResponseSetupServidor(setup_servidor.toString());
    }


    abstract class CancellabelAsyncTask extends AsyncTask<Void, Void, JSONObject> {
        protected volatile boolean isCancelled = false;

        public void setCancelled() {
            isCancelled = true;
        }
    }

    class MyTask extends AsyncTask<Void, Void, ResponseItens> {

        private final String[] tipos;
        private final String[] termos;
        private final int offset;
        private final int limit;
        private volatile boolean isCancelled = false;
        private volatile GetItensCallback callback;

        public MyTask (GetItensCallback callback, final String[] tipos, final String[] termos, final  int offset, final int limit) {
            this.callback = callback;
            this.tipos = tipos;
            this.termos = termos;
            this.offset = offset;
            this.limit = limit;
        }

        @Override
        protected void onPreExecute() {
            callback.onRequestGetItens();
        }

        @Override
        protected ResponseItens doInBackground(Void... params) {

            ArrayList<Item> tempResult = new ArrayList<Item>();
            ArrayList<Item> result = new ArrayList<Item>();
            Item item;

            for (int i = 0; i<dump.size(); i++) {
                item = dump.get(i);

                if (tipos.length > 0 && Arrays.asList(tipos).contains(FormatoConvert.convertFormatoToTipo(FormatoConvert.covertFormato(item.getFormato())))){
                    tempResult.add(item);
                }
            }

            if (offset+limit >= tempResult.size()) {
                for (int i = offset; i < tempResult.size(); i++) {
                    result.add(tempResult.get(i));
                }

            } else {
                for (int i = offset; i < (offset + limit); i++) {
                    result.add(tempResult.get(i));
                }
            }

            int qtdItem = dump.size();
            int qtdItensFiltrados = tempResult.size();
            int qtdItensRetornados = result.size();

            return new ResponseItens(qtdItem, qtdItensFiltrados, qtdItensRetornados, result);

        }

        @Override
        protected void onPostExecute(ResponseItens responseItens) {
            if (!isCancelled) callback.onGetItensResponse(responseItens);
        }

        public void setCancelled() {
            isCancelled = true;
        }
    }

    class GetMeusGruposTaks extends CancellabelAsyncTask{

        private volatile GetMeusGruposCallback callback;

        public GetMeusGruposTaks (GetMeusGruposCallback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return meus_grupos;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (!isCancelled) callback.onResponseGetMeusGrupos(jsonObject);
        }

    }

    class GetMeusItensTaks extends CancellabelAsyncTask {
        private volatile GetMeusItensCallback callback;

        public GetMeusItensTaks (GetMeusItensCallback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return meus_itens;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (!isCancelled) callback.onResponseGetMeusItens(jsonObject);

        }
    }
}
