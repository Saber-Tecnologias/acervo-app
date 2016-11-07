package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public enum LogItemComplementoEnum {
	LOCAL, 
	BAIXAREMTODOS, 
	SUCESSO, 
	FALHA, 
	CANCELADO, 
	EXCLUIREMTODOS, 
	FAVORITAR, 
	DESFAVORITAR,
	AVALIAR1,
	AVALIAR2,
	AVALIAR3,
	AVALIAR4,
	AVALIAR5;

	public String toString(){
		switch (this) {
		case LOCAL:
			return "LOCAL";

		case BAIXAREMTODOS:
			return "BAIXAR_EM_TODOS";

		case SUCESSO:
			return "SUCESSO";

		case FALHA:
			return "FALHA";

		case CANCELADO:
			return "CANCELADO";

		case EXCLUIREMTODOS:
			return "EXCLUIR_EM_TODOS";

		case FAVORITAR:
			return "+";

		case DESFAVORITAR:
			return "-";
		
		case AVALIAR1:
			return "1";
		
		case AVALIAR2:
			return "2";
		
		case AVALIAR3:
			return "3";
		
		case AVALIAR4:
			return "4";
		
		case AVALIAR5:
			return "5";
		
		default:
			return null;
		}
	}
}
