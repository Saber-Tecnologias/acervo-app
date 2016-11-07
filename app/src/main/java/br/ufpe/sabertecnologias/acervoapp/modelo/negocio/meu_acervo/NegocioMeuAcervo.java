package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Task;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.BaseNegocio;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.TipoTermoBusca;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaMeuAcervoResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.ufpe.sabertecnologias.acervoapp.util.FolderControl;
import br.ufpe.sabertecnologias.acervoapp.util.FormatoConvert;

public class NegocioMeuAcervo extends BaseNegocio implements INegocioMeuAcervo {

	private static final String GOOGLE_PLAY_URL = "https://play.google.com/store/search?c=apps&q=";

	private final String PREFERENCES_NAME = "negocio_meu_acervo_preferences";

	private static final String TITULO_GRUPO_ITENS_NAO_ORGANIZADOS = "Itens transferidos";
	private SharedPreferences preference;


	private ArrayList<String> termosBusca;
	private ArrayList<String> tiposBusca;

	private static NegocioMeuAcervo singleton;
	private BuscaMeuAcervoResponseCallback buscaMeuAcervoCallback;

	public static NegocioMeuAcervo getInstance(Context ctx) {
		if(singleton == null)
			singleton = new NegocioMeuAcervo(ctx);
		return singleton;
	}

	private NegocioMeuAcervo(Context ctx){
		super(ctx);
		preference = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		termosBusca = new ArrayList<String>();
		tiposBusca = new ArrayList<String>();
	}

	@Override
	public void abrirItem(Item item){

		try {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			File file = new File(FolderControl.getItensPath(mContext)+item.getCodigo()+"."+FormatoConvert.covertFormato(item.getFormato()));
			Uri uri = Uri.fromFile(file);
			DebugLog.d(this, uri.toString());
			intent.setDataAndType(uri, item.getFormato());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);


		} catch(ActivityNotFoundException e) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL+FormatoConvert.covertFormato(item.getFormato())));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(intent);
		}

	}


	@Override
	public Grupo getDefaultGrupo() {

		return new Grupo(-1, TITULO_GRUPO_ITENS_NAO_ORGANIZADOS, Color.parseColor("#4d4d4f"), new ArrayList<Item>());//preference.getInt("defaultGroupID", 0);
	}

	@Override
	public void setDefaultGrupoID(int _id){
		preference.edit().putInt("defaultGroupID", _id).apply();
	}

	@Override
	public void addGrupo(final Grupo g, IExecutor executor) {
		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {

				g.setId_grupo(UUID.randomUUID().toString());
				Integer i = repositorioGrupo.insert(g);


				g.setId(i);
				//executorListener.postResult(g);

				return g;

			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {

				if(executorListener != null)
					executorListener.postResult(obj);
			}

		};

		executor.execute(task);
	}

	@Override
	public ArrayList<Integer> getItensCodigos(){
		return (ArrayList<Integer>) repositorioItem.getItensCodigos();
	}

	@Override
	public void removeItem(final Item i, IExecutor executor) {
		Task task = new Task(){

			@Override
			public Object run(ExecutorListener executorListener) {
				repositorioItem.delete(i, -1);
				return i;
			}
			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener != null)
					executorListener.postResult(obj);
			}
		};

		executor.execute(task);
	}

	public void getGrupos(IExecutor executor) {

		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {
				return repositorioGrupo.get();
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				executorListener.postResult(obj);

			}
		};

		executor.execute(task);


	}

	public void removeGrupo(final Grupo grupo, final IExecutor executor) {

		Task task = new Task(){

			@Override
			public Object run(ExecutorListener executorListener) {

				if(repositorioGrupo.delete(grupo) != -1){
					return grupo;
				}

				return null;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener!=null){
					executorListener.postResult(obj);
				}
			}

		};

		executor.execute(task);
	}

	public void updateGrupo(final Grupo g1, final IExecutor executor) {
		Task task = new Task(){

			@Override
			public Object run(ExecutorListener executorListener) {
				repositorioGrupo.update(g1);
				return g1;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener != null){
					executorListener.postResult(obj);
				}
			}

		};

		executor.execute(task);

	}

	@Override
	public void getItens(IExecutor executor) {
		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {

				return repositorioItem.get(null);
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener != null){
					executorListener.postResult(obj);
				}
			}
		};

		executor.execute(task);
	}

	@Override
	public Item getItemByCodigo(final int item_codigo, IExecutor executor){
		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {
				List<Item> itens = repositorioItem.get(AcervoAppContract.Item.COLUMN_CODIGO+"=?", ""+item_codigo);
				DebugLog.d(this, "getItemByID returnsize = "+itens.size());
				return itens.get(0);
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {}
		};

		return (Item) executor.execute(task);
	}

	@Override
	public Item getItemByID(final int itemId, IExecutor executor) {

		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {
				List<Item> itens = repositorioItem.get(AcervoAppContract.Item._ID+"=?", ""+itemId);
				DebugLog.d(this, "getItemByID returnsize = "+itens.size());
				return itens.get(0);
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {}
		};

		return (Item) executor.execute(task);
	}

	@Override
	public void getItensByGrupo(final int grupoID, IExecutor executor) {

		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {

				if(grupoID == -1){
					return repositorioItem.get(AcervoAppContract.Item.COLUMN_ID_GRUPO+" is null");
				}else{
					return repositorioItem.get(
							AcervoAppContract.Item.COLUMN_ID_GRUPO+"=?", grupoID+"");
				}

			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				executorListener.postResult(obj);

			}
		};
		executor.execute(task);
	}

	@Override
	public void addTermoBuscaMeuAcervo(String termo, TipoTermoBusca tipoTermo) {
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
		buscaMeuAcervo();
	}


	@Override
	public void attItem(final Item i, final int idGrupo, IExecutor executor) {
		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener) {
				repositorioItem.update(i, idGrupo);
				return i;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener!=null)
					executorListener.postResult(obj);
			}
		};

		executor.execute(task);
	}


	@Override
	public void attItem(final List<Item> is, final int idGrupo, IExecutor executor) {
		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {
				Item item;
				for(int i=0; i<is.size(); i++){
					item = is.get(i);
					item.setGrupo(idGrupo);
					repositorioItem.update(item, idGrupo);
				}
				return is;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener!=null)
					executorListener.postResult(obj);

			}
		};

		executor.execute(task);

	}



	@Override
	public void addItem(final Item item, final int idGrupo, IExecutor executor) {
		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener) {
				int _id = repositorioItem.insertTransaction(item, idGrupo);
				item.setId(_id);
				DebugLog.d(this, "addItem.run - _id = " + _id);
				return item;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				Item i = (Item) obj;
				if( executorListener != null)
					executorListener.postResult(i);
				DebugLog.d(this, "addItem.postRun - _id = "+i.getId());
			}
		};
		executor.execute(task);
	}

	public void buscaMeuAcervo(){

		final AsyncExecutor asyncExecutor = new AsyncExecutor(new ExecutorListener.ExecutorResult() {
			@Override
			public void onExecutorResult(Object obj) {
				buscaMeuAcervoCallback.onResponse((ArrayList<Item>) obj, false);
			}
		});



		Task task = new Task(){


			@Override
			public Object run(ExecutorListener executorListener) {
				String tipoTermoBusca = null;
				if(tiposBusca != null  && tiposBusca.size() > 0) {
					tipoTermoBusca = tiposBusca.get(0);
				}
				ArrayList<Item> itensList = repositorioItem.getItensBusca(tipoTermoBusca ,termosBusca);
				return itensList;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				executorListener.postResult(obj);
			}
		};
		asyncExecutor.execute(task);

	}

	public void setBuscaMeuAcervoResponseListener(BuscaMeuAcervoResponseCallback buscaMeuAcervoCallback) {
		this.buscaMeuAcervoCallback = buscaMeuAcervoCallback;
	}

	public void removerTermoBusca(String termo, TipoTermoBusca tipo) {
		switch(tipo)
		{
			case NORMAL:
				if(termosBusca.contains(termo))
				{
					termosBusca.remove(termo);
				}
				break;
			case TIPO:
				if(tiposBusca.contains(termo))
				{
					tiposBusca.remove(termo);
				}
				break;

		}

		//TODO refazer a busca!!!!

		if(!(tiposBusca.isEmpty() && termosBusca.isEmpty())){
			buscaMeuAcervo();
		}

	}

	public void removeAllTermos() {
		termosBusca.clear();
		tiposBusca.clear();
	}
}

