package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public class SessaoControle {
	private String sessaoMilis;
	private String status;

	public SessaoControle(String sessaoMilis,
			String status) {
		this.sessaoMilis = sessaoMilis;
		this.status = status;
	}
	public String getSessaoMilis() {
		return sessaoMilis;
	}

	public void setSessaoMilis(String numSessao) {
		this.sessaoMilis = numSessao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
