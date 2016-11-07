package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import org.json.JSONException;
import org.json.JSONObject;

import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class LogFeedback extends LogBase{
	private String texto;
	private String acao;


	public LogFeedback(int ID,
					   String log,
					   Timestamp timestamp,
					   String acao,
					   String texto) {
		super(ID,log, timestamp);
		this.texto = texto;
		this.acao = acao;
	}
	public LogFeedback(
			String log,
			Timestamp timestamp,
			String acao,
			String texto) {
		this(0, log, timestamp,  acao, texto);
	}

	public LogFeedback(
			String log,
			Timestamp timestamp,
			String texto) {
		this(0, log, timestamp,  "ENVIAR", texto);
	}


	public void setAcao(String acao) {
		this.acao = acao;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getAcao() {
		return acao;
	}

	public String getTexto() {
		return texto;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(JsonObjectFieldsEnum.LOG.toString(), JsonObjectNamesEnum.FEEDBACK.toString());

		json.put(JsonObjectFieldsEnum.DATETIME.toString(), getTimestamp().getDatetime());

		json.put(JsonObjectFieldsEnum.ACAO.toString(), getAcao());

		json.put(JsonObjectFieldsEnum.TEXTO.toString(), getTexto());


		DebugLog.d(this, json.toString());
		return json;

	}


}
