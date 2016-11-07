package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.content.Context;
import android.database.Cursor;

import br.ufpe.sabertecnologias.acervoapp.util.ClassContentValues;

public class Dao<T> extends BasicDao{

	private String tablename;
	
	public Dao(Context context, String tablename) {
		super(context);
		this.tablename = tablename;
	}
	
	public int insert(T object, int idFK){
		return super.insert(tablename, ClassContentValues.getContentValues(object, idFK, true));
	}
	public int update(T object, int idFK, String whereClause, String[] whereArgs){
		return super.update(tablename, ClassContentValues.getContentValues(object, idFK, false), whereClause, whereArgs);
	}
	public int delete(String whereClause, String[] whereArgs){
		return super.delete(tablename, whereClause, whereArgs);
	}
	public Cursor select(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		return super.select(tablename, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	public int inserOrUpdate(T object, int idFK, int conflictAlgorithm){
		return super.inserOrUpdate(tablename, ClassContentValues.getContentValues(object, idFK, true), conflictAlgorithm);
	}


}
