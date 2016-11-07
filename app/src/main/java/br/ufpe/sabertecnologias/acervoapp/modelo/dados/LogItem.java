package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class LogItem extends LogBase{
	private Context mContext;
	private int item;
	private LogItemAcaoEnum acao;
	private String complemento;

	public LogItem(int ID,
			String log,
			Timestamp timestamp,
			int item,
			LogItemAcaoEnum acao,
			String complemento) {
		super(ID,log, timestamp);
		this.item = item;
		this.acao = acao;
		this.complemento = complemento;
	}
	public LogItem(
			String log,
			Timestamp timestamp,
			int item,
			LogItemAcaoEnum acao,
			String complemento) {
		this(0, log, timestamp, item, acao, complemento);
	}


	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public void setAcao(LogItemAcaoEnum acao) {
		this.acao = acao;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public LogItemAcaoEnum getAcao() {
		return acao;
	}

	public String getComplemento() {
		return complemento;
	}

	 public JSONObject toJSONObject(final Context ctx) throws JSONException {
		JSONObject json = new JSONObject();

		json.put(JsonObjectFieldsEnum.LOG.toString(), JsonObjectNamesEnum.ITEM.toString());
		
		json.put(JsonObjectFieldsEnum.DATETIME.toString(), getTimestamp().getDatetime());

		json.put(JsonObjectFieldsEnum.ITEM.toString(), getItem());

		 String acaoTitulo = JsonObjectFieldsEnum.ACAO.toString();

		 String acaoString = this.acao.toString();

		 json.put(acaoTitulo, acaoString);

		switch (getAcao())
		{
		case AVALIAR:
			json.put(JsonObjectFieldsEnum.COMPLEMENTO.toString(),Integer.parseInt(getComplemento()));
			break;
		case DOWNLOAD:
			if (complemento.equals(LogItemComplementoEnum.FALHA.toString())){
				json.put(JsonObjectFieldsEnum.COMPLEMENTO.toString(),getComplemento());
			}else if(complemento.equals(LogItemComplementoEnum.CANCELADO.toString())){
				json.put(JsonObjectFieldsEnum.COMPLEMENTO.toString(),getComplemento());
			}else{

				String[] complements = complemento.split(" ");
				long tempo_ms = Long.parseLong(complements[0]);
				String conexao = complements[1];

				JSONObject jsonComplemento = new JSONObject();

				jsonComplemento.put("conexao",  conexao);
				jsonComplemento.put("tempo_ms", tempo_ms);

				json.put(JsonObjectFieldsEnum.COMPLEMENTO.toString(), jsonComplemento.toString());

				DebugLog.d(this, "JSON - " + json.toString());
			}
			break;
		default:
			String mComplemento = getComplemento();
			if(mComplemento!=null) {
				json.put(JsonObjectFieldsEnum.COMPLEMENTO.toString(), getComplemento());
			} else {
				json.put(JsonObjectFieldsEnum.COMPLEMENTO.toString(), JSONObject.NULL);
			}
			break;
		}

		 DebugLog.d(this, json.toString());
		return json;

	 }


}
