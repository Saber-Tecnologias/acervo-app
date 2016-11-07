package br.ufpe.sabertecnologias.acervoapp.modelo.negocio;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Task;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.util.FolderControl;
import br.ufpe.sabertecnologias.acervoapp.util.FormatoConvert;
import br.saber.downloadservice.DownloadService;

public class NegocioTransferindo extends BaseNegocio{

	private static final String PREFERENCES_NAME = "negocioTransferindoName";
	private static final String FLAG_HAS_NOTIFICATION = "fl_has_notification";

	
	private SharedPreferences preferences;
	
	private static NegocioTransferindo singleton;


	public static NegocioTransferindo getInstance(Context ctx) {
		if(singleton == null) {
			singleton = new NegocioTransferindo(ctx);
		}
		return singleton;
	}

	private NegocioTransferindo(Context ctx) {
		super(ctx);
		preferences =  mContext.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	public void transferirItem(final Item i, IExecutor executor){
		
		Task task = new Task() {
			
			@Override
			public Object run(ExecutorListener executorListener) {
				boolean fromAcervoRemoto = i.getStatus() == Item.FLAG_ITEM_INDISPONIVEL ? true : false;

				i.setStatus(Item.FLAG_ITEM_ENFILEIRADO);
				i.setIn_transferindo(Item.IN_TRANSFERINDO_TRUE);

				int idGrupo = i.getIDGrupo() == 0 ? -1 : i.getIDGrupo();

				if(fromAcervoRemoto){
					//add item
					List<Item> itens = repositorioItem.get(AcervoAppContract.Item.COLUMN_CODIGO +"=?", new String[]{i.getCodigo()+""});
					if(itens.size() == 0) {
						int _id = repositorioItem.insertTransaction(i, idGrupo);
						i.setId(_id);
					} else {
						Item it = itens.get(0);
						i.setId(it.getId());
						i.setGrupo(it.getIDGrupo());

						repositorioItem.update(i, i.getIDGrupo());
					}

				}else {
					//att item
					repositorioItem.update(i, idGrupo);
				}
				return i;
			}
			
			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				Item i = (Item) obj;

				if( executorListener != null)
					executorListener.postResult(i);
				
				downloadItem(i.getCodigo(), i.getUrl(), i.getFormato(), i.getChecksum());
			}
		};
				
		executor.execute(task);
	}

	public void downloadItem(int item_codigo, String url, String formato, String md5) {
		Intent downloadIntent = new Intent(DownloadService.DOWNLOADSERVICE_MANAGER);
		downloadIntent.putExtra(DownloadService.DOWNLOADSERVICE_ID, ""+item_codigo);
		downloadIntent.putExtra(DownloadService.DOWNLOADSERVICE_URL, url);
		String path = FolderControl.getItensPath(mContext)+item_codigo+"."+FormatoConvert.covertFormato(formato);
		downloadIntent.putExtra(DownloadService.DOWNLOADSERVICE_PATH, path);
		downloadIntent.putExtra(DownloadService.DOWNLOADSERVICE_MD5, md5);
		mContext.sendBroadcast(downloadIntent);
	}

	public List<Item> getItensTransferindo(){
		return repositorioItem.get(AcervoAppContract.Item.COLUMN_IN_TRANSFERINDO+"=?", ""+Item.IN_TRANSFERINDO_TRUE);
	}

	public void cancelaDownload(int id) {
		Intent it  = new Intent();
		it.setAction(DownloadService.ACTION_CANCEL);
		it.putExtra(DownloadService.DOWNLOADSERVICE_ID, ""+id);
		mContext.sendBroadcast(it);

	}

	public void limparTransferindo(IExecutor executor) {
		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {

				List<Item> itens = getItensTransferindo();

				Item item;

				for(int i=0; i<itens.size(); i++){
					item = itens.get(i);
					if(item.getStatus() == Item.FLAG_ITEM_BAIXADO){
						item.setIn_transferindo(Item.IN_TRANSFERINDO_FALSE);
						repositorioItem.update(item, -1);
					}
				}

				return null;

			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener != null)
					executorListener.postResult(obj);
			}
		};

		executor.execute(task);
	}

	public void showAviso() {

		preferences.edit().putBoolean(FLAG_HAS_NOTIFICATION, true).commit();
	}

	public void hideAviso(){
		preferences.edit().putBoolean(FLAG_HAS_NOTIFICATION, false).commit();

	}

	public boolean isShowingAviso(){
		return 	preferences.getBoolean(FLAG_HAS_NOTIFICATION, false);
	}
}
