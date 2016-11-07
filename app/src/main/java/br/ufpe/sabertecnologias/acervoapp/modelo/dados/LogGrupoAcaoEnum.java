package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public enum LogGrupoAcaoEnum {
ADICIONAR, REMOVER, ALTERAR;
	
	public String toString(){
		switch (this) {
		
		case ADICIONAR:
			return "ADICIONAR";
			
		case REMOVER:
			return "REMOVER";

		case ALTERAR:
				return "ALTERAR";
		default:
			return null;
		}
	}
}
