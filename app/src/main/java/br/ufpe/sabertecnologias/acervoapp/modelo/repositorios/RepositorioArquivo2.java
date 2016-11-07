package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Arquivo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;

public class RepositorioArquivo2 {
	private Dao<Arquivo> dao;

	public RepositorioArquivo2(Context ctx) {
		dao = new Dao<Arquivo>(ctx, AcervoAppContract.Arquivo.TABLE_NAME);
	}

	public int insert(Arquivo arquivo, int idItem) {
		return dao.insert(arquivo, idItem);
	}


	public int insertTransaction(Arquivo arquivo, int idItem){
		int retorno = -1;
		try{
			dao.beginTransaction();

			retorno = insert(arquivo, idItem);

			dao.setTransactionSuccessful();

		}finally{
			dao.endTransaction();

		}
		return retorno;
	}

	public int insertTransaction(List<Arquivo> arquivos, int idItem){
		int retorno = -1;
		try{
			dao.beginTransaction();
			for(Arquivo a : arquivos){
				retorno = insert(a, idItem);
			}
			dao.setTransactionSuccessful();


		}finally{
			dao.endTransaction();

		}
		return retorno;
	}

	public boolean update(Arquivo arquivo, int idItem) {
		String[] whereArgs = new String[]{arquivo.getID()+"", idItem +""};
		boolean retorno = true;

		try{
			dao.beginTransaction();
			retorno = dao.update(arquivo, idItem, AcervoAppContract.Arquivo._ID+"=? AND "+ AcervoAppContract.Arquivo.COLUMN_ID_ITEM+"=?", whereArgs) == -1?false:true;
			dao.setTransactionSuccessful();
		}finally{
			dao.endTransaction();
		}

		return retorno;

	}

	public List<Arquivo> get(int idItem) {

		String[] whereArgs = new String[]{idItem+""};
		Cursor cArquivos = dao.select(null, AcervoAppContract.Arquivo.COLUMN_ID_ITEM+"=?", whereArgs, null, null, null);
		ArrayList<Arquivo> arquivos = new ArrayList<Arquivo>();
		if(cArquivos.moveToFirst()){
			do{
				int _id = cArquivos.getInt(cArquivos.getColumnIndex(AcervoAppContract.Arquivo._ID));
				String nome = cArquivos.getString(cArquivos.getColumnIndex(AcervoAppContract.Arquivo.COLUMN_NOME));
				String mimetype = cArquivos.getString(cArquivos.getColumnIndex(AcervoAppContract.Arquivo.COLUMN_MIMETYPE));
				String tamanho = cArquivos.getString(cArquivos.getColumnIndex(AcervoAppContract.Arquivo.COLUMN_TAMANHO_BYTES));
				String checksum = cArquivos.getString(cArquivos.getColumnIndex(AcervoAppContract.Arquivo.COLUMN_CHECKSUM_MD5));
				String url = cArquivos.getString(cArquivos.getColumnIndex(AcervoAppContract.Arquivo.COLUMN_URL));

				arquivos.add(new Arquivo(_id, nome, mimetype, tamanho, checksum, url));

			}while(cArquivos.moveToNext());
		}
		cArquivos.close();
		return arquivos;
		
	}

	public int delete(Arquivo arquivo) {
		if(arquivo != null) {
			return dao.delete(AcervoAppContract.Arquivo._ID + "=?", new String[]{arquivo.getID() + ""});
		} else {
			return dao.delete(null, null);
		}
	}
}
