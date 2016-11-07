package br.ufpe.sabertecnologias.acervoapp.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.User;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.UserContract;

public class JsonConvert {

	public static User jsonToUser(String sJson){
		User u = null;
		String useraname = null;
		String name = null;
		String email = null;
		String token = null;
		try {
			JSONObject json = new JSONObject(sJson);

			useraname = json.getString(UserContract.ATTR_USERNAME);
			name = json.getString(UserContract.ATTR_NAME);
			email = json.getString(UserContract.ATTR_EMAIL);
			token = json.getString(UserContract.ATTR_TOKEN);

			u = new User(useraname, name, email);
			u.setToken(token);

		} catch (JSONException e) {
			Log.e("ERROR", "Falha na conversao do JSON: "+sJson);
		}
		return u;
	}
}
