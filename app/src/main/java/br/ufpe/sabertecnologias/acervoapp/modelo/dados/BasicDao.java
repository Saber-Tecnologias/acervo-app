package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class BasicDao {
	AcervoAppSQLiteHelper2 helper;
	SQLiteDatabase database;
	
	public BasicDao(Context context) {
		helper = AcervoAppSQLiteHelper2.getInstance(context);
		database = helper.getWritableDatabase();
	}
	
	protected int insert(String table, ContentValues values){
		return (int) database.insertOrThrow(table, null, values);
	}

	protected int inserOrUpdate(String table, ContentValues values, int conflictAlgorithm) {
		return (int) database.insertWithOnConflict(table, null, values, conflictAlgorithm);
	}
	
	protected int update(String table, ContentValues values, String whereClause, String[] whereArgs){
		return database.update(table, values, whereClause, whereArgs);
	}
	
	protected int delete(String table, String whereClause, String[] whereArgs){
		return database.delete(table, whereClause, whereArgs);
	}
	
	protected Cursor select(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		return database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	public Cursor rawQuery(String query){
		return  database.rawQuery(query,null);
	}
	
	public void beginTransaction(){
		database.beginTransaction();
	}
	
	public void endTransaction(){
		database.endTransaction();
	}
	
	public void setTransactionSuccessful(){
		database.setTransactionSuccessful();
	}
	

	
	
}

