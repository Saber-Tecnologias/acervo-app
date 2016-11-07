package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerInfoResponse {

	public String versao;
	public String horario_servidor;
	public String[] protocolos_suportados;
	public String[] alertas;

	
	public static ServerInfoResponse parse(String json){
		ServerInfoResponse response = new ServerInfoResponse();
		
		try {
			JSONObject jsonObject = new JSONObject(json);
			response.versao = jsonObject.getString("versao");
			response.horario_servidor = jsonObject.getString("horario_servidor"); 
			response.protocolos_suportados = getProtocolosSuportados(jsonObject.getJSONArray("protocolos_suportados"));
			response.alertas = getAlertas(jsonObject.getJSONArray("alertas"));
			
		} catch (JSONException e) {

		}
		
		return response;
	}

	private static String[] getAlertas(JSONArray jsonArray) {
		return new String[1];
	}

	private static String[] getProtocolosSuportados(JSONArray json) throws JSONException {
		String[]  protocolos = new String[json.length()];
		for(int i=0; i<protocolos.length; i++) {
			protocolos[i] = json.getString(i);
		}
		return protocolos;
	}
}
