package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public enum JsonObjectFieldsEnum {
	LOG,SESSAO,TIMESTAMP,TIMESTAMP_END,TIMESTAMP_START,DATETIME,TIMEZONE,CONEXAO,FALHA,ACAO,COMPLEMENTO,ITEM, USER_ID, DEVICE_ID, SIGNATURE, LISTALOG, NOME, UUID, COR, TEXTO;
	
	public String toString(){
		switch (this) {
			case LOG:
				return "log";
			case SESSAO:
				return "sessao";
			case TIMESTAMP:
				return "timestamp";
			case TIMESTAMP_START:
				return "timestamp_start";
			case TIMESTAMP_END:
				return "timestamp_end";
			case DATETIME:
				return "Datetime";
			case TIMEZONE:
				return "Timezone";
			case CONEXAO:
				return "conexao";
			case FALHA:
				return "falha";
			case ACAO:
				return "acao";
			case COMPLEMENTO:
				return "complemento";
			case TEXTO:
				return "Texto";
			case NOME:
				return "nome";
			case UUID:
				return "id";
			case COR:
				return "cor";
			case ITEM:
				return "item";
			case LISTALOG:
				return "listalog";

			//TODO remover esses atributos
			case USER_ID:
				return "USER_ID";
			case DEVICE_ID:
				return "DEVICE_ID";
			case SIGNATURE:
				return "SIGNATURE";


			default:
				return null;
		}
	}
}
