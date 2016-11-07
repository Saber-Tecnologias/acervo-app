package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public enum JsonObjectNamesEnum {
INICIO_SESSAO,FIM_SESSAO,GRUPO,ITEM,CONEXAO, FEEDBACK;
	
	public String toString(){
		switch (this) {


			case INICIO_SESSAO:
				return "INICIO_SESSAO";

			case FIM_SESSAO:
				return "FIM_SESSAO";

			case GRUPO:
				return "GRUPO";

			case ITEM:
				return "ITEM";
				
			case CONEXAO:
				return "CONEXAO";

			case FEEDBACK:
				return "FEEDBACK";

			default:
			return null;
		}
	}
}
