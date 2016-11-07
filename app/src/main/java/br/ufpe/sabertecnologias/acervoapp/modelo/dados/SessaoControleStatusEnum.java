package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public enum SessaoControleStatusEnum {
FALHA, OK, ABERTO;
	
	public String toString(){
		switch (this) {
		
		case FALHA:
			return "FALHA";
			
		case OK:
			return "OK";
		case ABERTO:
			return "ABERTO";
		
		default:
			return null;
		}
	}
}
