package br.ufpe.sabertecnologias.acervoapp.util;

public class FormatoConvert {

	private static final String PDF = "application/pdf";
	private static final String DOC = "application/msword";
	
	private static final String ZIP = "application/zip";
	private static final String OCTET_STREAM = "application/octet-stream";

	private static final String JPEG = "image/jpeg";
	private static final String PNG = "image/png";
	 
	private static final String MP3 = "audio/mpeg";
	private static final String WMA = "audio/x-ms-wma";
	
	private static final String MP4 = "video/mp4";
	private static final String FLV = "video/x-flv";

	public static String covertFormato(String formato){
		String retorno;
		if(formato.equals(PDF)){
			retorno =  "PDF";
		} else if(formato.equals(DOC)){
			retorno =  "DOC";
		}else if(formato.equals(JPEG)){
			retorno =  "JPEG";
		}else if(formato.equals(PNG)){
			retorno =  "PNG";
		}else if(formato.equals(MP3)){
			retorno =  "MP3";
		}else if(formato.equals(WMA)){
			retorno =  "WMA";
		}else if(formato.equals(MP4)){
			retorno =  "MP4";
		} else if(formato.equals(FLV)){
			retorno =  "FLV";
		} else if(formato.equals(ZIP)){
			retorno = "ZIP";
		}else if(formato.equals(OCTET_STREAM)){
			return "octet-stream";
		}else{
			retorno = "";
		}
		return retorno;
	}


	public static String convertFormatoToTipo(String formato) {

		String retorno;
		if(formato.equals("PDF")) {
			retorno = "Texto";
		}else if(formato.equals("JPEG")){
			retorno =  "Imagem";
		}else if(formato.equals("PNG")){
			retorno =  "Imagem";
		}else if(formato.equals("MP3")){
			retorno =  "Áudio";
		}else if(formato.equals("MP4")){
			retorno =  "Vídeo";
		}else{
			retorno = "";
		}
		return retorno;
	}
}
