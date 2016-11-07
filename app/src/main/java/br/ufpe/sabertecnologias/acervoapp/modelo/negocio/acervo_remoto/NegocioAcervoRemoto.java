package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.JsonObjectFieldsEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.ResponseItens;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.IService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.LocalService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.MockConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.BaseNegocio;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class NegocioAcervoRemoto extends BaseNegocio implements INegocioRemoto {

	private final IService service;
	private ResponseItens responseBusca;
	private int buscaOffset;
	private ArrayList<String> termosBusca;
	private ArrayList<String> tiposBusca;

	private BuscaResponseCallback buscaCallback;

	private static NegocioAcervoRemoto singleton;

	public static NegocioAcervoRemoto getInstance(Context ctx) {
		if (singleton == null)
			singleton = new NegocioAcervoRemoto(ctx);
		return singleton;
	}

	private NegocioAcervoRemoto(Context ctx){
		super(ctx);
		service = LocalService.getInstance(ctx);

		termosBusca = new ArrayList<String>();
		tiposBusca = new ArrayList<String>();

	}

	private void processaResponse() {
		boolean isNext = (buscaOffset>0);
		buscaOffset+=responseBusca.getQtdItensRetornados();
		buscaCallback.onResponse(responseBusca.getItens(),isNext);
	}

	@Override
	public void setBuscaResponseListener(BuscaResponseCallback listener){
		buscaCallback = listener;
	}


	@Override
	public void addTermoBuscaAcervoRemoto(String termo, TipoTermoBusca tipoTermo)
	{
		boolean changed = true;

		switch(tipoTermo)
		{
			case NORMAL:
				if(!termosBusca.contains(termo))
				{
					termosBusca.add(termo);

				}
				break;
			case TIPO:
				if(!tiposBusca.contains(termo))
				{
					tiposBusca.add(termo);
				}
				break;
			default:
				changed=false;
		}

		if(changed)
		{
			buscaOffset = 0;
		}

		//triggar a busca por aqui
		try{
			buscarItens();
		} catch(Exception e){
			DebugLog.d(this, e.getMessage());
		};


	}
	@Override
	public void removeAllTermos(){
		service.cancelGetItens();
		termosBusca.clear();
		tiposBusca.clear();
	}

	@Override
	public void removeTermo(String termo, TipoTermoBusca tipoTermo)
	{
		boolean modified = false;
		switch(tipoTermo)
		{
			case NORMAL:
				modified = termosBusca.remove(termo);
				break;
			case TIPO:
				modified = tiposBusca.remove(termo);
				break;
		}

		if(modified)
		{
			buscaOffset = 0;
		}

		if(!(termosBusca.isEmpty() && tiposBusca.isEmpty()))
		{
			try{
				buscarItens();
			} catch(Exception e){
				DebugLog.d(this, e.getMessage());
			}

		}else  {
			service.cancelGetItens();
			if(buscaCallback!=null)
				buscaCallback.onCancell();
		}
	}
	@Override
	public void getNextPageBusca()
	{
		DebugLog.d(this, "nextPage");


		try {
			if (!(termosBusca.isEmpty() && tiposBusca.isEmpty())){
				buscarItens();
			} else {
				DebugLog.d(this, "hasnt tag");
			}
		} catch(Exception e){
			DebugLog.d(this, e.getMessage());
		}
	}

	private void buscarItens() throws Exception
	{
		service.getItens(getJson(), buscaOffset, 10,
				IService.FORMATO_ITEM_0,
				tiposBusca.toArray(new String[tiposBusca.size()]),
				termosBusca.toArray(new String[termosBusca.size()]),
				true,
				new IService.GetItensCallback() {
					@Override
					public void onCancelRequestGetItens() {
						buscaCallback.onCancell();
					}

					@Override
					public void onRequestGetItens() {
						buscaCallback.onRequest();
					}

					@Override
					public void onGetItensResponse(ResponseItens responseItens) {
						responseBusca = responseItens;
						processaResponse();
					}

					@Override
					public void onGetItensError() {
						buscaCallback.onError();
					}
				});

	}

	private JSONObject getJson() throws Exception {
		JSONObject json = new JSONObject();


		json.put(JsonObjectFieldsEnum.USER_ID.toString(), MockConfig.MOCK_USERID);
		json.put(JsonObjectFieldsEnum.DEVICE_ID.toString(),MockConfig.MOCK_DEVICE_ID );
		json.put(JsonObjectFieldsEnum.SIGNATURE.toString(), usuario.getToken());

		DebugLog.d(this, json.toString());
		return json;
	}

	@Override
	public boolean hasNextPage()
	{
		boolean retorno = false;
		if(responseBusca!=null)
		{
			retorno = (buscaOffset>=responseBusca.getQtdItensFiltrados());
		}
		return retorno;
	}

	public void buscarRecentes(String tipo, final BuscaResponseCallback callback) throws Exception {

		service.getRecentes(getJson(), tipo, 10, IService.FORMATO_ITEM_0,new IService.GetRecentesCallback() {

			@Override
			public void onResponseGetRecentes(ResponseItens responseItens) {
				callback.onResponse(responseItens.getItens(), false);
			}

			@Override
			public void onCancelRequestGetRecentes() {
				callback.onCancell();
			}

			@Override
			public void onRequestGetRecentes() {
				callback.onRequest();
			}

			@Override
			public void onErrorGetRecentes() {
				callback.onError();
			}
		});
	}

}
