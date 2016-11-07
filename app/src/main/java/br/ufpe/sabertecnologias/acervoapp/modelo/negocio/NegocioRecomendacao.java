package br.ufpe.sabertecnologias.acervoapp.modelo.negocio;

import android.content.Context;

import org.json.JSONObject;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.JsonObjectFieldsEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.ResponseItens;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.IService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.LocalService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.MockConfig;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.RecomendacoesResponseCallback;

/**
 * Created by jpttrindade on 23/11/15.
 */
public class NegocioRecomendacao extends BaseNegocio {

    public static final int TYPE_TEXTO = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;


    private static NegocioRecomendacao instance;
    private final IService service;
    private RecomendacoesResponseCallback recomendacoesResponseCallback;


    private NegocioRecomendacao(Context context) {
        super(context);
        service = LocalService.getInstance(mContext);
    }

    public static NegocioRecomendacao getInstance(Context context) {
        if(instance == null){
            instance = new NegocioRecomendacao(context);
        }

        return instance;
    }

    public void buscarListaRecomendacoes(RecomendacoesResponseCallback recomendacoesResponseCallback, int type) {

        this.recomendacoesResponseCallback = recomendacoesResponseCallback;
        try {
            switch (type){
                case TYPE_TEXTO:
                    buscarTextos();
                    break;
                case TYPE_AUDIO:
                    buscarAudios();
                    break;
                case TYPE_VIDEO:
                    buscarVideos();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buscarAudios() throws Exception {

        service.getItens(getJson(), 0, 10, IService.FORMATO_ITEM_0, new String[]{"Áudio"}, new String[]{}, false, new IService.GetItensCallback() {
            @Override
            public void onCancelRequestGetItens() { }

            @Override
            public void onRequestGetItens() { }

            @Override
            public void onGetItensResponse(ResponseItens responseItens) {
                recomendacoesResponseCallback.onResponse(responseItens.getItens(), TYPE_AUDIO);
            }

            @Override
            public void onGetItensError() { }
        });

    }

    private void buscarVideos() throws Exception {
        service.getItens(getJson(), 0, 10, IService.FORMATO_ITEM_0, new String[]{"Vídeo"},new String[]{}, false, new IService.GetItensCallback() {
            @Override
            public void onCancelRequestGetItens() { }

            @Override
            public void onRequestGetItens() { }

            @Override
            public void onGetItensResponse(ResponseItens responseItens) {
                recomendacoesResponseCallback.onResponse(responseItens.getItens(), TYPE_VIDEO);
            }

            @Override
            public void onGetItensError() { }
        });

    }

    private void buscarTextos() throws Exception {

        service.getItens(getJson(), 0, 10, IService.FORMATO_ITEM_0, new String[]{"Texto"}, new String[]{}, false, new IService.GetItensCallback() {
            @Override
            public void onCancelRequestGetItens() { }

            @Override
            public void onRequestGetItens() { }

            @Override
            public void onGetItensResponse(ResponseItens responseItens) {
                recomendacoesResponseCallback.onResponse(responseItens.getItens(), TYPE_TEXTO);
            }

            @Override
            public void onGetItensError() { }
        });
    }

    public JSONObject getJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put(JsonObjectFieldsEnum.USER_ID.toString(), MockConfig.MOCK_USERID);
        json.put(JsonObjectFieldsEnum.DEVICE_ID.toString(),MockConfig.MOCK_DEVICE_ID );
        json.put(JsonObjectFieldsEnum.SIGNATURE.toString(), usuario.getToken());
        return json;
    }
}
