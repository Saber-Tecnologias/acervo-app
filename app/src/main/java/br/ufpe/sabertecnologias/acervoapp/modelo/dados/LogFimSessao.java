package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import org.json.JSONException;
import org.json.JSONObject;

public class LogFimSessao extends LogBase{
	private boolean falha;
	private Timestamp timestamp_start;
	
	public LogFimSessao(
			int ID,
			String log, 
			Timestamp timestamp_end,//timestamp_end
			Timestamp timestamp_start,
			boolean falha) {
		super(ID, log, timestamp_end);
		this.falha = falha;
		this.timestamp_start = timestamp_start;
	}
	public LogFimSessao(
			String log, 
			Timestamp timestamp_end,
			Timestamp timestamp_start,
			boolean falha) {
		this(0, log, timestamp_end, timestamp_start,falha);
	}

	public void setFalha(boolean falha) {
		this.falha = falha;
	}

	public boolean isFalha() {
		return falha;
	}
	
	public void setTimeStampSTART(Timestamp timestampstart){
		this.timestamp_start = timestampstart;
	}
	
	public Timestamp getTimestampSTART(){
		return this.timestamp_start;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(JsonObjectFieldsEnum.LOG.toString(), JsonObjectNamesEnum.FIM_SESSAO.toString());

		//para o timestamp_end
		JSONObject timestampJSONObject = new JSONObject();
		timestampJSONObject.put(JsonObjectFieldsEnum.DATETIME.toString(), getTimestamp().getDatetime());
		timestampJSONObject.put(JsonObjectFieldsEnum.TIMEZONE.toString(), getTimestamp().getTimezone());

		json.put(JsonObjectFieldsEnum. TIMESTAMP_END.toString(), timestampJSONObject);
		
		//para o timestamp_start
		JSONObject timestampJSONObjectStart = new JSONObject();
		timestampJSONObjectStart.put(JsonObjectFieldsEnum.DATETIME.toString(), getTimestampSTART().getDatetime());
		timestampJSONObjectStart.put(JsonObjectFieldsEnum.TIMEZONE.toString(), getTimestampSTART().getTimezone());
		json.put(JsonObjectFieldsEnum. TIMESTAMP_START.toString(), timestampJSONObjectStart);


		
		json.put(JsonObjectFieldsEnum.FALHA.toString(),isFalha());


		return json;
	}
	
}
