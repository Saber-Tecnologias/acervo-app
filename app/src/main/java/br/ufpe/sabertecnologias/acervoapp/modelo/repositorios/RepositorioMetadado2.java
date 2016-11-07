package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Metadado;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class RepositorioMetadado2 {
	Dao<Metadado> dao;

	public RepositorioMetadado2(Context ctx) {
		dao = new Dao<Metadado>(ctx, AcervoAppContract.Metadado.TABLE_NAME);
	}

	public int insert(Metadado metadado, int idItem) {
		return dao.insert(metadado, idItem);
	}


	public int insertTransaction(Metadado metadado, int idItem){
		int retorno = -1;
		try{
			dao.beginTransaction();

			retorno = insert(metadado, idItem);

			dao.setTransactionSuccessful();


		}finally{
			dao.endTransaction();

		}
		return retorno;
	}

	public int insertTransaction(List<Metadado> metadados, int idItem){
		int retorno = -1;
		try{
			dao.beginTransaction();
			for(Metadado m : metadados){
				retorno = insert(m, idItem);
			}
			dao.setTransactionSuccessful();

		}finally{
			dao.endTransaction();
		}
		return retorno;
	}

	public boolean update(Metadado metadado, int idItem) {
		String[] whereArgs = new String[]{metadado.getID()+"", idItem +""};
		boolean retorno = true;

		try{
			dao.beginTransaction();
			retorno = dao.update(metadado, idItem, AcervoAppContract.Metadado._ID+"=? AND "+ AcervoAppContract.Metadado.COLUMN_ID_ITEM+"=?", whereArgs) == -1?false:true;
			dao.setTransactionSuccessful();
		}finally{
			dao.endTransaction();
		}

		return retorno;

	}

	public List<Metadado> get(int idItem) {

		String[] whereArgs = new String[]{idItem+""};
		Cursor cMetadado = dao.select(null, AcervoAppContract.Metadado.COLUMN_ID_ITEM+"=?", whereArgs, null, null, null);
		ArrayList<Metadado> metadados = new ArrayList<Metadado>();
		if(cMetadado.moveToFirst()){
			do{
				int id = cMetadado.getInt(cMetadado.getColumnIndex(AcervoAppContract.Metadado._ID));
				String nome = cMetadado.getString(cMetadado.getColumnIndex(AcervoAppContract.Metadado.COLUMN_NOME));
				String valor = cMetadado.getString(cMetadado.getColumnIndex(AcervoAppContract.Metadado.COLUMN_VALOR));;
				String idioma = cMetadado.getString(cMetadado.getColumnIndex(AcervoAppContract.Metadado.COLUMN_IDIOMA));;
				String autoridade = cMetadado.getString(cMetadado.getColumnIndex(AcervoAppContract.Metadado.COLUMN_AUTORIDADE));;
				metadados.add(new Metadado(id, nome, valor, idioma, autoridade));


			}while(cMetadado.moveToNext());
		}
		return metadados;
	}

	public int delete(Metadado metadado) {
		if(metadado != null) {
			return dao.delete(AcervoAppContract.Metadado._ID + "=?", new String[]{metadado.getID() + ""});
		}else {
			return dao.delete(null, null);
		}
	}

	public ArrayList<Integer> getItensIDsBusca(String tipo, ArrayList<String> termosRaw) {

		ArrayList<String> termos = pureInjectAvoidance(termosRaw);
		StringBuilder termosLike = new StringBuilder();
		StringBuilder finalQuery = new StringBuilder();

		for(int i=0 ; i < termos.size() ; i++){
			if (i>0){
				termosLike.append("or valor LIKE ('%");
			}
			else{
				termosLike.append("valor LIKE ('%");
			}
			termosLike.append(termos.get(i)).append("%') ");
		}

		if (tipo != null && (termos.size() > 0)){
			finalQuery.append("select distinct id_item from metadado where id_item in (select id_item from metadado where (nome = 'dc.type' and valor = '").append(tipo).append("')) and ((nome ='dc.description.abstract' and (").append(termosLike.toString()).append(") or  (nome ='dc.title' and (").append(termosLike.toString()).append(")) or (nome ='dc.subject.classification' and (").append(termosLike.toString()).append(")) or (nome ='dc.audience.mediator' and (").append(termosLike.toString()).append(")) ));");
		}
		else if (tipo == null && (termos.size() > 0)) {
			finalQuery.append("select distinct id_item from metadado where ((nome ='dc.description.abstract' and (").append(termosLike.toString()).append(") or  (nome ='dc.title' and (").append(termosLike.toString()).append(")) or (nome ='dc.subject.classification' and (").append(termosLike.toString()).append(")) or (nome ='dc.audience.mediator' and (").append(termosLike.toString()).append(")) ));");
		}
		else if (tipo != null && (termos.size() == 0)){
			finalQuery.append("select distinct id_item from metadado where id_item in (select id_item from metadado where (nome = 'dc.type' and valor = '").append(tipo).append("'));");
		}

		DebugLog.d(this, finalQuery.toString());

		Cursor c = dao.rawQuery(finalQuery.toString());
		ArrayList<Integer> idList = new ArrayList<Integer>();
		if(c.moveToFirst()){
			do{
				int id = c.getInt(0);
				idList.add(new Integer(id));

			}while(c.moveToNext());
		}
		return idList;
	}

	private ArrayList<String> pureInjectAvoidance(ArrayList<String> load){
		ArrayList<String> ret = new ArrayList<String>();
		for (String str : load){
			ret.add(str.replace("'","\\'").replace("\"", "\\\""));
		}

		return ret;
	}

}
