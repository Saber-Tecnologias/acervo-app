package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

public enum LogItemAcaoEnum {
ADICIONAR, DOWNLOAD, AVALIAR, FAVORITAR, EXCLUIR, REMOVER, AGRUPAR, DESAGRUPAR, ABRIR;
	
	public String toString(){
		switch (this) {
		case ADICIONAR:
			return "ADICIONAR";
		
		case DOWNLOAD:
			return "DOWNLOAD";
			
		case AVALIAR:
			return "AVALIAR";
		
		case FAVORITAR:
			return "FAVORITAR";
			
		case EXCLUIR:
			return "EXCLUIR";
			
		case REMOVER:
			return "REMOVER";
		
		case AGRUPAR:
			return "AGRUPAR";
			
		case DESAGRUPAR:
			return "DESAGRUPAR";
		
		case ABRIR:
			return "ABRIR";
			
		default:
			return null;
		}
	}
}
