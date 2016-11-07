package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Messenger;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.Executor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Task;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.JsonObjectFieldsEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.IService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.LocalService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.MockConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.BaseNegocio;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.setup_server.SetupServerService;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.ufpe.sabertecnologias.acervoapp.util.FolderControl;
import br.ufpe.sabertecnologias.acervoapp.util.FormatoConvert;

public class NegocioSincronizacao extends BaseNegocio {

	public static final String ACTION_ATIVAR_ALARME_ATT_LISTA_ITENS = "br.ufpe.sabertecnologias.acervoapp.negocio.sincronizacao.STARTALARME_ATTLISTAITENS";
	public static final String ACTION_ATIVAR_ALARME_SETUP_SERVER = "br.ufpe.sabertecnologias.acervoapp.negocio.sincronizacao.STARTALARME_SETUPSERVER";


	public static final int SYNC_RESPONSE_GET_ITEM = 2;
	public static final int SYNC_RESPONSE_GET_ITEM_ERROR = -2;

	public static final int SYNC_RESPONSE_GET_GRUPO = 1;
	public static final int SYNC_RESPONSE_GET_GRUPO_ERROR = -1;

	private static NegocioSincronizacao instance;
	private final IService service;
	private JSONObject sendInfo;


    public interface SetupInicialListener {
		public void onResponse(String response);
		public void onErrorResponse();
	}

	public interface GetGruposListener {
		public void onResponseGetGrupos(JSONObject response);
		public void onErrorResponseGetGrupos();
	}

	public interface GetItensListener {
		public void onResponseGetItens(JSONObject response);
		public void onErrorResponseGetItens();
	}

	public static NegocioSincronizacao getInstance(Context ctx){
		if(instance == null){
			instance = new NegocioSincronizacao(ctx);
		}
		return instance;
	}

	private NegocioSincronizacao(Context ctx) {
		super(ctx);
		service = LocalService.getInstance(ctx);//new RemoteService(ctx);
	}

	public void setupServidor(){
		Intent it = new Intent(mContext, SetupServerService.class);
		mContext.startService(it);
	}

	public void setupServidor(final SetupInicialListener callback) throws NameNotFoundException {
		PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
		String versao = pInfo.versionName;;
		DebugLog.d(this, "Versao = "+versao);
		service.setupServidor(versao, new IService.SetupServidorCallback() {
			@Override
			public void onResponseSetupServidor(String response) {
				callback.onResponse(response);
			}

			@Override
			public void onErrorSetupServidor() {
				callback.onErrorResponse();
			}
		});
	}

    public void cancelSync() {
		DebugLog.d(this, "CancelSync()");
       service.cancelSync();
    }

	//Metodos de parsing JSON

	private JSONObject prepareJson() throws Exception {
		JSONObject json = new JSONObject();

		//JSONArray logArrayJson = getLogArray();
		if(usuario == null){
			setUser(new Executor(null));
		}

		//dados de sessão/usuário
		json.put(JsonObjectFieldsEnum.USER_ID.toString(), MockConfig.MOCK_USERID);
		json.put(JsonObjectFieldsEnum.DEVICE_ID.toString(), MockConfig.MOCK_DEVICE_ID);
		json.put(JsonObjectFieldsEnum.SIGNATURE.toString(), usuario.getToken());

		DebugLog.d(this, json.toString());

		return json;

	}

	public void getGruposFromServer(final GetGruposListener callback) {
		try {
			sendInfo = prepareJson();
		} catch (Exception e) {
			Log.e("NEGOCIO_LOG", "erro preparando json para envio!! em GETGRUPOS\n" + e.getMessage());
		}

		service.getMeusGrupos(sendInfo, new IService.GetMeusGruposCallback() {

			@Override
			public void onResponseGetMeusGrupos(JSONObject response) {
				callback.onResponseGetGrupos(response);
			}

			@Override
			public void onErrorGetMeusGrupos() {
				callback.onErrorResponseGetGrupos();
			}
		});
	}

	public void getItensFromServer(final GetItensListener callback) {
		try {
			sendInfo = prepareJson();
		} catch (Exception e) {
			Log.e("NEGOCIO_LOG", "erro preparando json para envio!! em GETGRUPOS\n" + e.getMessage());
		}

		service.getMeusItens(sendInfo, new IService.GetMeusItensCallback() {
			@Override
			public void onResponseGetMeusItens(JSONObject response) {
				callback.onResponseGetItens(response);
			}

			@Override
			public void onErrorGetMeusItens() {
				callback.onErrorResponseGetItens();
			}
		});
	}

	public void persistirGruposFromSync(IExecutor executor, final JSONObject json){

		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener) {
				//
				JSONArray grupoJSONList = null;
				Integer result;
				try {
					grupoJSONList = json.getJSONArray("grupos_itens");
					ArrayList<Grupo> grupoArrayList = new ArrayList<Grupo>();

					for (int i = 0; i < grupoJSONList.length() ;i++ ){
						JSONObject obj = grupoJSONList.getJSONObject(i);
						Grupo grupo = new Grupo(obj.getString("nome"), Color.parseColor(obj.getString("cor")));

						grupo.setId_grupo(obj.getString("id"));

						grupoArrayList.add(grupo);


					}

					repositorioGrupo.insertAndRemoveDifferencesTransaction(grupoArrayList);

					result = new Integer(SYNC_RESPONSE_GET_GRUPO);
				} catch (JSONException e) {
					//.printStackTrace();
					Log.e("ERROU!",e.getMessage());
					result = new Integer(SYNC_RESPONSE_GET_GRUPO_ERROR);
				}

				return result;//retorno do persistirGruposFromSync
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if (executorListener != null) executorListener.postResult(obj);

			}
		};

		executor.execute(task);


	}

	private void excluirArquivo(Item item) {
		File f = new File(FolderControl.getItensPath(mContext)+item.getCodigo()+"."+ FormatoConvert.covertFormato(item.getFormato()));
		DebugLog.d(this, "File name - "+f.getAbsolutePath());
		boolean foiDeletado = f.delete();
		DebugLog.d(this, "foi deletado? "+foiDeletado);


	}

	public void persistirItensFromSync(IExecutor executor, final JSONObject json, final List<Item> itensListOld){

		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener) {
				//
				JSONArray itensJSONList = null;
				//List <Item> itemListOld = repositorioItem.get(null,null);

				DebugLog.d(this, "itemListOld.Size() = " + itensListOld.size());
				for(Item i:itensListOld){
					DebugLog.d(this, "item_"+i.getId()+" codigo="+ i.getCodigo());
				}

				Integer result;
				try {
					itensJSONList = json.getJSONArray("itens");
					ArrayList<Item> itemArrayList= new ArrayList<Item>();

					List<Grupo> grupoList = repositorioGrupo.get();
					JSONObject obj;
					Item item;
					for (int i = 0; i < itensJSONList.length() ;i++ ){
						obj = itensJSONList.getJSONObject(i);

						item = Item.getItemFromJSON(obj, itensListOld);
						DebugLog.d(this, "STATUS IEM = "+item.getStatus());
						if(item.getStatus() == Item.FLAG_ITEM_REMOVIDO){
							//NADA
							excluirArquivo(item);
						} else if(item.getStatus() == Item.FLAG_ITEM_REMOVIDO_E_EXISTELOCAL){
							//TODO excluir ARQUIVO DO DIRETORIO
							excluirArquivo(item);
						} else {
							item.setGrupo(getGrupoIDFromUUID(obj.getString("grupo_id"), grupoList));
							itemArrayList.add(item);
						}
					}

					repositorioItem.insertAndRemoveDifferencesTransaction(itemArrayList);
					result = new Integer(SYNC_RESPONSE_GET_ITEM);
				} catch (JSONException e) {
					Log.e("ERROU!",e.getMessage());
					result = new Integer(SYNC_RESPONSE_GET_ITEM_ERROR);
				}

				return result;//retorno do persistirItensFromSync
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if (executorListener != null) executorListener.postResult(obj);

			}
		};

		executor.execute(task);

	}

	private int getGrupoIDFromUUID(String uuid, List<Grupo> grupoList){

		int ret = -1;
		for (int i =0 ; i < grupoList.size(); i++ ){
			if (grupoList.get(i).getUUID_grupo().equals(uuid)) {
				ret = grupoList.get(i).getID();
			}
		}
		if (ret ==-1) Log.e ("DEBUG","ID do grupo para o UUID: " + uuid + " NAO ENCONTRADO! RETORNADO GRUPO ID=-1");
		return ret;
	}

	public void enableSetupServerAlarme(){
		DebugLog.d(this, "NegocioSincronizacao - enableSetupServerAlarme()");

		Intent intentAlarm = new Intent(ACTION_ATIVAR_ALARME_SETUP_SERVER);
		mContext.sendBroadcast(intentAlarm);
	}

	public void sync(Messenger callback, ServiceConnection conn) {
		Intent i = new Intent(mContext, SyncService.class);
		DebugLog.d(this, "bindServiceSync");
		i.putExtra("messenger", callback);
		mContext.bindService(i, conn, Context.BIND_AUTO_CREATE);
	}

	public void sync(boolean isFromRefresh) {
		Intent i = new Intent(mContext, SyncService.class);
		i.putExtra(SyncService.START_FROM_REFRESH,  isFromRefresh);
		mContext.startService(i);
	}

	public void finishSync(ServiceConnection conn) {
		mContext.unbindService(conn);
	}

	public void enableSyncAlarm(){
		DebugLog.d(this, "NegocioMeuAcervo - enableSyncAlarm()");
		Intent intentAlarm = new Intent(ACTION_ATIVAR_ALARME_ATT_LISTA_ITENS);
		mContext.sendBroadcast(intentAlarm);
	}

}
