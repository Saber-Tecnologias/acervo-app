package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.SessaoControle;

public class RepositorioSessaoControle {
	private Dao<SessaoControle> dao;
	
	//TODO USAR SINGLETON, GET_INSTANCE
	public RepositorioSessaoControle(Context context) {
		dao = new Dao<SessaoControle>(context, AcervoAppContract.SessaoControle.TABLE_NAME);
	}
	
	public int insert(SessaoControle sessao) {
		return dao.insert(sessao, 0);
	}
	
	public int insertTransaction(SessaoControle sessao){
		int retorno = -1;
		try{
			dao.beginTransaction();

			retorno = insert(sessao);

			dao.setTransactionSuccessful();

		}finally{
			dao.endTransaction();

		}
		return retorno;
	}

	public int insertTransaction(List<SessaoControle> sessao){
		int retorno = -1;
		try{
			dao.beginTransaction();
			for(SessaoControle l : sessao){
				retorno = insert(l);
			}
			dao.setTransactionSuccessful();

		}finally{
			dao.endTransaction();

		}
		return retorno;
	}
	
	private SessaoControle getSessaoControleFromCursor(Cursor cursor){
		String numSessao = cursor.getString(cursor.getColumnIndex(AcervoAppContract.SessaoControle.COLUMN_NUMSESSAO));
		String status = cursor.getString(cursor.getColumnIndex(AcervoAppContract.SessaoControle.COLUMN_STATUS));
		return new SessaoControle(numSessao, status);
	}
	
	public List<SessaoControle> get() {

		Cursor cLog = dao.select(null, null, null, null, null, AcervoAppContract.SessaoControle._ID);
		ArrayList<SessaoControle> sc = new ArrayList<SessaoControle>();
		if(cLog.moveToFirst()){
			do{
				sc.add(getSessaoControleFromCursor(cLog));

			}while(cLog.moveToNext());
		}
		return sc;
		
	}

	public int delete() {
		return dao.delete(null, null);
	}
}
