package br.ufpe.sabertecnologias.acervoapp.modelo.mock;

import org.json.JSONObject;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.ResponseItens;

/**
 * Created by joaotrindade on 25/10/16.
 */

public interface IService {

    int FORMATO_ITEM_0 = 0;
    int FORMATO_ITEM_1 = 1;
    int FORMATO_ITEM_2 = 2;

    interface GetItensCallback {
        public void onCancelRequestGetItens();
        public void onRequestGetItens();
        public void onGetItensResponse(ResponseItens responseItens);
        public void onGetItensError();

    }

    interface GetRecentesCallback {

        public void onResponseGetRecentes(ResponseItens responseItens);
        public void onCancelRequestGetRecentes();
        public void onRequestGetRecentes();
        public void onErrorGetRecentes();

    }


    interface SendLogCallback {
        public void onResponseSendLog(JSONObject jsonObject);
        public void onErrorSendLog();

    }


    interface GetMeusGruposCallback {
        public void onResponseGetMeusGrupos(JSONObject response);
        public void onErrorGetMeusGrupos();
    }


    interface GetMeusItensCallback {
        public void onResponseGetMeusItens(JSONObject response);
        public void onErrorGetMeusItens();
    }


    interface SetupServidorCallback {
        public void onResponseSetupServidor(String response);
        public void onErrorSetupServidor();
    }

    void getRecentes(JSONObject body, String tipo, int limit, int formato, GetRecentesCallback callback);
    void getItens(JSONObject body, int offset, int limit, int format, String[] tipos, String[] termos, boolean cancelCurrent, GetItensCallback callback);
    void cancelGetItens();



    void sendLog(JSONObject body, SendLogCallback callback);
    void cancelSendLog();




    void getMeusGrupos(JSONObject body, GetMeusGruposCallback callback);

    void getMeusItens(JSONObject body, GetMeusItensCallback callback);

    void cancelSync();

    void setupServidor(String versao, SetupServidorCallback callback);

}
