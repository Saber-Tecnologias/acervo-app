package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.ufpe.sabertecnologias.acervoapp.modelo.util.Scripts;

public class AcervoAppSQLiteHelper2 extends SQLiteOpenHelper{

	private static AcervoAppSQLiteHelper2 instance;
	
	private static final String DATABASE_NAME = "__acervoappDB.db";
	private static final int DATABASE_VERSION = 3;//BuildConfig.DEBUG ? 2 : 1;
		
	//Singleton
	public static AcervoAppSQLiteHelper2 getInstance(Context context){
		if(instance == null){
			instance = new AcervoAppSQLiteHelper2(context);
		}
		return instance;
	}
	
	private AcervoAppSQLiteHelper2(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Scripts.executeScripts(db,0 ,DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Scripts.executeScripts(db, oldVersion, newVersion);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.execSQL("PRAGMA foreign_keys=ON");

	}

	@Override
	public void onConfigure(SQLiteDatabase db) {
		super.onConfigure(db);

	}
}
