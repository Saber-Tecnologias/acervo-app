package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import org.json.JSONException;
import org.json.JSONObject;

public class LogGrupo extends LogBase{
	private LogGrupoAcaoEnum acao;
	private String nome;
	private String uuid;
	private String cor;
	
	public LogGrupo(
			int ID,
			String log,
			Timestamp timestamp, 
			LogGrupoAcaoEnum acao, 
			String nome,
			String uuid,
			String cor) {
		super(ID, log, timestamp);
		this.acao = acao;
		this.nome = nome;
		this.uuid = uuid;
		this.cor = cor;
	}
	
	public LogGrupo(
			String log,
			Timestamp timestamp, 
			LogGrupoAcaoEnum acao, 
			String nome,
			String uuid,
			String cor) {
		this(0, log, timestamp, acao, nome, uuid, cor);
	}

	public void setAcao(LogGrupoAcaoEnum acao) {
		this.acao = acao;
	}

	public LogGrupoAcaoEnum getAcao() {
		return acao;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(JsonObjectFieldsEnum.LOG.toString(), JsonObjectNamesEnum.GRUPO.toString());

		json.put(JsonObjectFieldsEnum.DATETIME.toString(),getTimestamp().getDatetime());
		
		json.put(JsonObjectFieldsEnum.ACAO.toString(),getAcao().toString());
		
		json.put(JsonObjectFieldsEnum.NOME.toString(),getNome());
	
		json.put(JsonObjectFieldsEnum.UUID.toString(),getUuid());
		
		json.put(JsonObjectFieldsEnum.COR.toString(),getCor());

		return json;

	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

}
