package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public class Timestamp {
	private String datetime;
	private String timezone;
	
	public Timestamp(String datetime,String timezone) {
		this.datetime = datetime;
		this.timezone = timezone;
	}
	public String getTimezone() {
		return timezone;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
}
