package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.LogItem;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogBase;

public abstract class RepositorioLog {
	private Dao<LogBase> dao;

	public RepositorioLog(Context context, String tableName) {
		dao = new Dao<LogBase>(context, tableName);
	}
	
	public int insert(LogBase log) {
		return dao.insert(log, 0);
	}
	
	public int insertTransaction(LogBase log){
		int retorno = -1;
		try{
			dao.beginTransaction();

			retorno = insert(log);

			dao.setTransactionSuccessful();

		}finally{
			dao.endTransaction();

		}
		return retorno;
	}

	public int insertTransaction(List<LogBase> logs){
		int retorno = -1;
		try{
			dao.beginTransaction();
			for(LogBase l : logs){
				retorno = insert(l);
			}
			dao.setTransactionSuccessful();


		}finally{
			dao.endTransaction();

		}
		return retorno;
	}
	
	abstract LogBase getLogFromCursor(Cursor cursor);
	
	public List<LogBase> get() {

		Cursor cLog = dao.select(null, null, null, null, null, null);
		ArrayList<LogBase> logs = new ArrayList<LogBase>();
		if(cLog.moveToFirst()){
			do{
				logs.add(getLogFromCursor(cLog));

			}while(cLog.moveToNext());
		}
		return logs;
		
	}

	public int delete() {
		return dao.delete(null, null);
	}
	
	public int delete(LogBase log) {
		return dao.delete(LogItem._ID+"=?", new String[]{String.valueOf(log.getID())} );
	}
}
