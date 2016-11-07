package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Arquivo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Metadado;
import br.ufpe.sabertecnologias.acervoapp.util.DateUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class RepositorioItem2 extends Repositorio{
	private Dao<Item> dao;
	private RepositorioArquivo2 repArquivo;
	private RepositorioMetadado2 repMetadado;
	private static RepositorioItem2 singleton;

	public static RepositorioItem2 getInstance(Context ctx) {
		if(singleton == null) {
			singleton = new RepositorioItem2(ctx);
		}
		return singleton;
	}

	private RepositorioItem2(Context ctx) {
		super();
		dao = new Dao<Item>(ctx, AcervoAppContract.Item.TABLE_NAME);
		repArquivo = new RepositorioArquivo2(ctx);
		repMetadado = new RepositorioMetadado2(ctx);
	}

	public int insert(Item t, int idGrupo) {
		int retorno = -1;

		ArrayList<Metadado> metadados = t.getMetadados();
		ArrayList<Arquivo> arquivos = t.getArquivos();
		int idItem = dao.insert(t, idGrupo);

		retorno = idItem;

		for(Metadado metadado: metadados){
			retorno = repMetadado.insert(metadado, idItem) != -1 ? retorno : -1;
		}

		for(Arquivo arquivo: arquivos){
			retorno = repArquivo.insert(arquivo, idItem) != -1 ? retorno : -1;
		}

		return retorno;
	}

	public int delete(Item t, int idGrupo){
		int resultado = -1;
		ArrayList<Arquivo> arquivos = t.getArquivos();
		for(int i=0; i<arquivos.size(); i++){
			resultado = repArquivo.delete(arquivos.get(i));
		}

		ArrayList<Metadado> metas = t.getMetadados();
		for(int i=0; i<metas.size(); i++){
			resultado = repMetadado.delete(metas.get(i));
		}

		resultado = dao.delete(AcervoAppContract.Item._ID+"=?", new String[]{t.getId()+""});

		notifyObservers(RepositorioObserver.DELETE, t);

		return resultado;
	}

	public int insertTransaction(Item i, int idGrupo){
		int retorno = -1;
		try{
			dao.beginTransaction();
			retorno = insert(i, idGrupo);
			dao.setTransactionSuccessful();
		}finally{
			dao.endTransaction();
		}

		if(retorno != -1) {
			i.setId(retorno);
			notifyObservers(RepositorioObserver.INSERT, i);
		}
		return retorno;
	}


	public int insertTransaction(List<Item> its, int idGrupo){
		int retorno = -1;
		try{
			dao.beginTransaction();
			for(Item i : its){
				retorno = insert(i, idGrupo);
			}
			dao.setTransactionSuccessful();

		}finally{
			dao.endTransaction();
		}
		return retorno;
	}

	public int insertAndRemoveDifferencesTransaction(List<Item> its){
		int retorno = -1;
		try{
			dao.beginTransaction();
			repMetadado.delete(null);
			repArquivo.delete(null);
			int deletResult = dao.delete(null, null);
			DebugLog.d(this, "insertAndRemoveDifferencesTransaction deletResult = " + deletResult);
			for(Item i : its){
				retorno = insert(i, i.getIDGrupo());
				i.setId(retorno);
			}

			dao.setTransactionSuccessful();

		}finally{
			dao.endTransaction();
		}
		notifyObservers(RepositorioObserver.SYNC_ITENS, its);

		return retorno;
	}


	public boolean update(Item t, int idGrupo) {

		boolean retorno = true;
		try{
			dao.beginTransaction();
			ArrayList<Metadado> metadados = t.getMetadados();
			ArrayList<Arquivo> arquivos = t.getArquivos();
			int idItem = t.getId();
			String[] args = new String[]{idItem+""};
			retorno = retorno && (dao.update(t, idGrupo, AcervoAppContract.Item._ID+"=?", args)==-1?false:true);

			for(Metadado metadado: metadados){
				retorno = retorno && repMetadado.update(metadado, idItem);
			}

			for(Arquivo arquivo: arquivos){
				retorno = retorno && repArquivo.update(arquivo, idItem);
			}
			dao.setTransactionSuccessful();
			retorno = true;
		}finally{
			dao.endTransaction();
		}

		notifyObservers(RepositorioObserver.UPDATE, t);

		return retorno;

	}


	public List<Item> get(String where, String... args) {
		List<Item> items = new ArrayList<Item>();
		Cursor cItem = dao.select(null, where, args, null, null, null);
		if(cItem.moveToFirst()){
			do{
				items.add(getItem(cItem));
			}while(cItem.moveToNext());
		}

		cItem.close();
		return items;
	}

	private Item getItem(Cursor cursor) {
		Item i;
		int _id = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item._ID));
		int codigoItem = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_CODIGO));
		int handle = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_HANDLE));
		long dataLong = cursor.getLong(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_DATA));
		int removidoInt = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_REMOVIDO));
		int qtd_metadados = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_QT_METADADOS));
		int qtd_arquivos = cursor.getInt(cursor.getColumnIndexOrThrow(AcervoAppContract.Item.COLUMN_QT_ARQUIVOS));

		int fl_status = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_STATUS));
		int in_transferindo = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_IN_TRANSFERINDO));
		int idGrupo = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Item.COLUMN_ID_GRUPO));
		ArrayList<Metadado> metadados = (ArrayList<Metadado>) repMetadado.get(_id);
		ArrayList<Arquivo> arquivos = (ArrayList<Arquivo>) repArquivo.get(_id);

		i = new Item(_id, codigoItem, handle, DateUtil.convertDate(dataLong), removidoInt, qtd_metadados, metadados, qtd_arquivos, arquivos, fl_status);
		i.setIn_transferindo(in_transferindo);
		i.setGrupo(idGrupo==0 ? -1 : idGrupo);
		return i;

	}

	public List<Integer> getItensCodigos(){
		List<Integer> codigos = new ArrayList<Integer>();

		Cursor cItemCodigos = dao.select(new String[]{AcervoAppContract.Item.COLUMN_CODIGO},
				AcervoAppContract.Item.COLUMN_STATUS + "=?", new String[]{Item.FLAG_ITEM_BAIXADO+""}, null, null, null);


		DebugLog.d(this, cItemCodigos.getCount()+" total de reusltados");

		if(cItemCodigos.moveToFirst()){
			do{
				codigos.add(cItemCodigos.getInt(cItemCodigos.getColumnIndex(AcervoAppContract.Item.COLUMN_CODIGO)));
			}while(cItemCodigos.moveToNext());
		}
		cItemCodigos.close();
		return codigos;
	}

	public ArrayList<Item> getItensBusca(String tipo, ArrayList<String> termos){

		ArrayList<Integer> idList = repMetadado.getItensIDsBusca(tipo, termos);

		DebugLog.d(this, idList.size()+" - idsSize");
		ArrayList<Item> itemList = new ArrayList<Item>();
		if(idList.size() != 0) {
			for (Integer id : idList) {
				Item it = this.get("_id=?", id.toString()).get(0);
				itemList.add(it);
			}
		}
		return getItensByCodigos(idList);
	}


	private ArrayList<Item> getItensByCodigos(ArrayList<Integer> ids){
		ArrayList<Item> itens = new ArrayList<Item>();
		StringBuilder stringIds = new StringBuilder();
		if(ids.size()>1){
			for(int i = 0; i<ids.size()-1; i++){
				stringIds.append(ids.get(i)).append(",");
			}
			stringIds.append(ids.get(ids.size()-1));
		} else if(ids.size() ==1 ) {
			stringIds.append(ids.get(0));
		}
		String query = "select * from " + AcervoAppContract.Item.TABLE_NAME + " where " + AcervoAppContract.Item._ID + " in (" + stringIds.toString() +");";
		DebugLog.d(this, query);
		Cursor c = dao.rawQuery(query);
		if(c.moveToFirst()){
			do{
				itens.add(getItem(c));
			}while(c.moveToNext());
		}
		c.close();
		return itens;
	}

}
