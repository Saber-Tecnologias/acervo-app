package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Dao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.User;

public class RepositorioUser2  extends Repositorio{
	Dao<User> dao;
	private final String username = AcervoAppContract.Usuario.COLUMN_USERNAME +"=?";

	private static RepositorioUser2 singleton;

	public static RepositorioUser2 getInstance(Context ctx) {
		if(singleton == null)
			singleton = new RepositorioUser2(ctx);
		
		return singleton;
	}
	
	private RepositorioUser2(Context context) {
		super();
		dao = new Dao<User>(context, AcervoAppContract.Usuario.TABLE_NAME);

	}
	
	public boolean insert(User user){
		return dao.insert(user, 0) ==-1?false:true;
	}

	public boolean insertTransaction(User user) {
		boolean retorno = true;
		try{
			dao.beginTransaction();
			retorno = retorno && insert(user);
			dao.setTransactionSuccessful();
		}catch (Exception e){
			Log.e("ERROR", e.getMessage());
		}finally{
			dao.endTransaction();
		}
		return retorno; 
	}

	public boolean insertTransaction(List<User> users) {
		boolean retorno = true;
		try{
			dao.beginTransaction();
			for(User u : users){
				retorno = retorno && insert(u);
			}
			dao.setTransactionSuccessful();
		}finally{
			dao.endTransaction();
		}
		return retorno; 
	}
	
	public boolean update(User user){
		String[] whereArgs = new String[]{user.getUsername()};
		boolean retorno = true;
		try{
			dao.beginTransaction();
			retorno = retorno && (dao.update(user, 0, username, whereArgs)==-1?false:true);
			dao.setTransactionSuccessful();
		}finally{
			dao.endTransaction();
		}
		return retorno;
	}
	
	public List<User> get() {
		List<User> users = new ArrayList<User>();
		Cursor cursor = dao.select(null, null, null, null, null, null);
		User u;
		if(cursor.moveToFirst()){
			do{
				String username = cursor.getString(cursor.getColumnIndex(AcervoAppContract.Usuario.COLUMN_USERNAME));
				String nome = cursor.getString(cursor.getColumnIndex(AcervoAppContract.Usuario.COLUMN_NAME));
				String email = cursor.getString(cursor.getColumnIndex(AcervoAppContract.Usuario.COLUMN_EMAIL));
				String token = cursor.getString(cursor.getColumnIndex(AcervoAppContract.Usuario.COLUMN_TOKEN));
				int _id = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.Usuario._ID));
				u = new User(_id, username, nome, email);
				u.setToken(token);
				users.add(u);
			}while(cursor.moveToNext());
		}
		return users;
	}

}
