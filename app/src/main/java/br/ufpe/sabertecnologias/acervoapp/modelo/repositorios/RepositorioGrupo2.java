package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class RepositorioGrupo2 extends Repositorio{
	private Dao<Grupo> dao;
	private RepositorioItem2 repItem;

	private static RepositorioGrupo2 singleton;

	public static RepositorioGrupo2 getInstance(Context ctx) {
		if(singleton == null)
			singleton = new RepositorioGrupo2(ctx);
		return singleton;
	}

	private RepositorioGrupo2(Context context) {
		super();
		dao = new Dao<Grupo>(context, AcervoAppContract.Grupo.TABLE_NAME);
		repItem = RepositorioItem2.getInstance(context);
	}

	public int insert(Grupo g) {
		int i;
		try{
			i  = dao.insert(g, -1);
			g.setId(i);
			notifyObservers(RepositorioObserver.INSERT, g);
		}catch(Exception e){
			Log.e("ERROR", e.getMessage());
			i = -2;
		}
		return i;
	}

	public int silentInsert(Grupo g) {
		int i;
		try{
			i  = dao.insert(g, -1);
			g.setId(i);
		}catch(Exception e){
			Log.e("ERROR", e.getMessage());
			i = -2;
		}
		return i;
	}

	public int insertTransaction(Grupo g){
		int retorno = -1;
		try{
			dao.beginTransaction();
			retorno = insert(g);
			dao.setTransactionSuccessful();
		}finally{
			dao.endTransaction();
		}
		return retorno;
	}

	public boolean insertTransaction(List<Grupo> grupos){
		boolean retorno = true;
		try{
			dao.beginTransaction();
			for(Grupo g : grupos){
				retorno = retorno && (insert(g) != -1);
			}
			dao.setTransactionSuccessful();
			retorno = true;
		}finally{
			dao.endTransaction();
		}
		return retorno;
	}

	public boolean insertAndRemoveDifferencesTransaction(List<Grupo> grupos){
		boolean retorno = true;
		try{
			dao.beginTransaction();
			dao.delete(null,null);
			for(Grupo g : grupos){
				retorno = retorno && (silentInsert(g) != -1);
			}
			dao.setTransactionSuccessful();
			retorno = true;
		}finally{
			dao.endTransaction();

		}
		notifyObservers(RepositorioObserver.SYNC_GRUPOS, grupos);
		return retorno;
	}

	public boolean update(Grupo g) {

		boolean retorno = true;
		try{
			dao.beginTransaction();
			int idGrupo = g.getID(); 
			String[] args = new String[]{idGrupo+""};
			retorno = retorno && (dao.update(g, idGrupo, AcervoAppContract.Grupo._ID+"=?", args)==-1?false:true);
			dao.setTransactionSuccessful();
			retorno = true;
		}finally{
			dao.endTransaction();
		}

		notifyObservers(RepositorioObserver.UPDATE, g);
		return retorno;

	}


	public int delete(Grupo g){
		int resultado = -1;
		resultado = dao.delete(AcervoAppContract.Grupo._ID+"=?", new String[]{g.getID()+""});
		DebugLog.d(this, "Grupo DELETADO = " + resultado);
		if(resultado != -1)
			notifyObservers(RepositorioObserver.DELETE, g);
		return resultado;
	}


	public List<Grupo> get() {
		List<Grupo> items = new ArrayList<Grupo>();
		Cursor cGrupos = dao.select(null, null, null, null, null, null);
		if(cGrupos.moveToFirst()){
			do{
				items.add(getGrupo(cGrupos));
			}while(cGrupos.moveToNext());
		}
		return items;
	}

	public Grupo getItemByID(int id){
		Grupo g = null;
		Cursor cGrupos = dao.select(null, AcervoAppContract.Grupo._ID+"="+id, null, null, null, null);
		cGrupos.moveToFirst();
		g = getGrupo(cGrupos);
		return g;
	}

	private Grupo getGrupo(Cursor cursor) {
		int _id = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Grupo._ID));
		int cor = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Grupo.COLUMN_COR));
		String nome = cursor.getString(cursor.getColumnIndex(AcervoAppContract.Grupo.COLUMN_NOME));
		String id_grupo = cursor.getString(cursor.getColumnIndex(AcervoAppContract.Grupo.COLUMN_ID_GRUPO));
		ArrayList<Item> itens = (ArrayList<Item>) repItem.get(AcervoAppContract.Item.COLUMN_ID_GRUPO+"=?", new String[]{_id+""});
		Grupo g = new Grupo(_id, nome, cor, itens);
		g.setId_grupo(id_grupo);
		return g;
	}




}
