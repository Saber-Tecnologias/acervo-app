package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import br.ufpe.sabertecnologias.acervoapp.util.DateUtil;

public abstract class LogBase implements Comparable{
	private String log;
	private Timestamp timestamp;
	private int ID;

	public LogBase(int ID, String log, Timestamp timestamp) {
		this.log = log;
		this.timestamp = timestamp;
		this.ID = ID;
	}


	public int getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}


	public String getLog() {
		return log;
	}


	public void setLog(String log) {
		this.log = log;
	}

	@Override
	public int compareTo(Object another) {
		long tmL = DateUtil.getMS(this.getTimestamp());
		long tmR = DateUtil.getMS(((LogBase) another).getTimestamp());

		if( tmL < tmR ) {
			return -1;
		} else if ( tmL == tmR){
			return 0;
		} else {
			return 1;
		}
	}
}
