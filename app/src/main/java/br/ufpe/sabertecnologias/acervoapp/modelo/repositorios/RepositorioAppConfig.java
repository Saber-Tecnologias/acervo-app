package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.AppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;

public class RepositorioAppConfig {
	private static RepositorioAppConfig singleton;
	private Dao<AppConfig> dao;
	
	
	public static RepositorioAppConfig getInstance(Context ctx) {
		if(singleton == null) {
			singleton = new RepositorioAppConfig(ctx);
		}
		
		return singleton;
	}
	
	private RepositorioAppConfig(Context ctx) {
		dao = new Dao<AppConfig>(ctx, AcervoAppContract.AppConfig.TABLE_NAME);
	}
	
	public AppConfig get(){
		AppConfig appConfig = null;
		Cursor c = dao.select(null, null, null, null, null, null);
		if(c.moveToFirst()){
			String api_version = c.getString(c.getColumnIndex(AcervoAppContract.AppConfig.COLUMN_API_VERSION));
			int has_att = c.getInt(c.getColumnIndex(AcervoAppContract.AppConfig.COLUMN_FLAG_HAS_ATT));
			int has_tutorial = c.getInt(c.getColumnIndex(AcervoAppContract.AppConfig.COLUMN_FLAG_HAS_TUTORIAL));
			int setupTPMax = c.getInt(c.getColumnIndex(AcervoAppContract.AppConfig.COLUMN_SETUP_TMP_MAX));
			int setupTPMin = c.getInt(c.getColumnIndex(AcervoAppContract.AppConfig.COLUMN_SETUP_INITIAL_TIME));
			appConfig = new AppConfig(api_version, has_att, has_tutorial, setupTPMax, setupTPMin);
			c.close();
		}	
		return appConfig;
	}
	
	public void update(AppConfig appConfig){
		try{
			dao.update(appConfig, 0, AcervoAppContract.AppConfig._ID+"=1", null);
		}catch (Exception e){
			Log.e("DEBUG", e.getMessage());
		}
	}

}
