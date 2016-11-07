package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInicioSessao extends LogBase{
	private String conexao;

	public LogInicioSessao(
			int ID,
			String log, 
			Timestamp timestamp, 
			String conexao) {
		super(ID, log, timestamp);
		this.conexao = conexao;
	}
	public LogInicioSessao(
			String log, 
			Timestamp timestamp,
			String conexao) {
		this(0, log, timestamp, conexao);
	}

	public String getConexao() {
		return conexao;
	}

	public void setConexao(String conexao) {
		this.conexao = conexao;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JsonObjectFieldsEnum.LOG.toString(), JsonObjectNamesEnum.INICIO_SESSAO.toString());
		JSONObject timestampJSONObject = new JSONObject();
		timestampJSONObject.put(JsonObjectFieldsEnum.DATETIME.toString(), getTimestamp().getDatetime());
		timestampJSONObject.put(JsonObjectFieldsEnum.TIMEZONE.toString(), getTimestamp().getTimezone());
		json.put(JsonObjectFieldsEnum.TIMESTAMP_START.toString(), timestampJSONObject);
		json.put(JsonObjectFieldsEnum.CONEXAO.toString(),getConexao());
		return json;
	}
	
}
