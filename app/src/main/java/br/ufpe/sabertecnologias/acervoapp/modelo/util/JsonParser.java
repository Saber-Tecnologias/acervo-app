package br.ufpe.sabertecnologias.acervoapp.modelo.util;

import android.util.JsonReader;
import android.util.JsonToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Arquivo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Metadado;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.ResponseItens;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.ArquivoContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.ItemContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.MetadadoContract;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class JsonParser {

	private final static String QTD_ITENS = "qt_itens";
	private final static String QTD_ITENS_FILTRADOS = "qt_itens_filtrados";
	private final static String QTD_ITENS_RETORNADOS = "qt_itens_retornados";
	private final static String ITENS = "itens";



	public static ResponseItens parseItem(JSONObject json) throws IOException{
		StringReader reader = new StringReader(json.toString());
		JsonReader jsonReader = new JsonReader(reader);
		ResponseItens responseItens = parseItem(jsonReader);
		reader.close();

		return responseItens;
	}

	public static ResponseItens parseItem(JsonReader jsonReader) throws IOException {
		int qtdItem = 0;
		int qtdItensFiltrados = 0;
		int qtdItensRetornados = 0;
		ArrayList<Item> itens = new ArrayList<Item>();
		jsonReader.beginObject();
		while(jsonReader.hasNext()){
			String name = jsonReader.nextName();

			if(name.equals(QTD_ITENS)){
				qtdItem = jsonReader.nextInt();
			}else if(name.equals(QTD_ITENS_FILTRADOS)){
				qtdItensFiltrados = jsonReader.nextInt();
			}else if(name.equals(QTD_ITENS_RETORNADOS)){
				qtdItensRetornados = jsonReader.nextInt();
			}else if(name.equals(ITENS)){
				if(jsonReader.peek() != JsonToken.NULL){
					jsonReader.beginArray();
					while(jsonReader.hasNext()){
						Item i = readItem(jsonReader);
						try{
							if(Integer.parseInt(i.getTamanho())>0){
								itens.add(i);
							} else {
								DebugLog.d("item handle = "+i.getHandle());
								DebugLog.d("tamanho = 0");
							}
						} catch (NumberFormatException e){
							DebugLog.d(e.getMessage());
						}
					}
					jsonReader.endArray();
				}else{
					jsonReader.nextNull();
				}
			}else{
				jsonReader.skipValue();
			}
		}
		jsonReader.endObject();
		return new ResponseItens(qtdItem, qtdItensFiltrados, qtdItensRetornados, itens);
	}



	private static Item readItem(JsonReader reader) throws IOException {
		int codigo = 0;
		int handle = 0;
		String data = "";
		int removido = 0;
		int qtdMetadados = 0;
		ArrayList<Metadado> metadados = new ArrayList<Metadado>();
		int qtdArquivos = 0;
		ArrayList<Arquivo> arquivos = new ArrayList<Arquivo>();

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(ItemContract.ATTR_CODIGO)){
				codigo = reader.nextInt();
			}else if(name.equals(ItemContract.ATTR_HANDLE)){
				handle = reader.nextInt();
			}else if(name.equals(ItemContract.ATTR_DATA)){
				data = reader.nextString();
			}else if(name.equals(ItemContract.ATTR_REMOVIDO)){
				removido = reader.nextBoolean() ? 1 : 0;
			}else if(name.equals(ItemContract.ATTR_QT_METADADOS)){
				qtdMetadados = reader.nextInt();
			}else if(name.equals(ItemContract.ATTR_METADADOS)){
				if(reader.peek() != JsonToken.NULL){
					reader.beginArray();
					while(reader.hasNext()){
						metadados.add(readMetadado(reader));
					}
					reader.endArray();
				}
				else{
					reader.nextNull();
				}
			}else if(name.equals(ItemContract.ATTR_QT_ARQUIVOS)){
				qtdArquivos = reader.nextInt();
			}else if(name.equals(ItemContract.ATTR_ARQUIVOS)){
				if(reader.peek() != JsonToken.NULL){
					reader.beginArray();
					while (reader.hasNext()) {
						arquivos.add(readArquivo(reader));
					}
					reader.endArray();
				} else {
					reader.nextNull();
				}
			}else{
				reader.skipValue();
			}

		}
		reader.endObject();

		return new Item(codigo, handle, data, removido, qtdMetadados, metadados, qtdArquivos, arquivos, Item.FLAG_ITEM_INDISPONIVEL);
	}

	private static Arquivo readArquivo(JsonReader reader) throws IOException {
		String nome = "";
		String mimetype = "";
		String tamanho_bytes = "";
		String checksum_md5 = "";
		String url = "";

		reader.beginObject();
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals(ArquivoContract.ATTR_NOME)){
				nome = reader.nextString();
			} else if(name.equals(ArquivoContract.ATTR_MIMETYPE)){
				mimetype = reader.nextString();
			}else if(name.equals(ArquivoContract.ATTR_BYTES)){
				tamanho_bytes = reader.nextString();
			}else if(name.equals(ArquivoContract.ATTR_CHECKSUM)){
				checksum_md5 = reader.nextString();
			}else if(name.equals(ArquivoContract.ATTR_URL)){
				url = reader.nextString();
			}else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return new Arquivo(nome, mimetype, tamanho_bytes, checksum_md5, url);
	}

	private static Metadado readMetadado(JsonReader reader) throws IOException {
		String nome = "";
		String valor = "";
		String idioma = null;
		String autoridade = null;

		reader.beginObject();
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals(MetadadoContract.ATTR_NOME)){
				nome = reader.nextString();
			}else if(name.equals(MetadadoContract.ATTR_VALOR)){
				valor = reader.nextString();
			}else if(name.equals(MetadadoContract.ATTR_AUTORIDADE)){
				if(reader.peek() != JsonToken.NULL){
					autoridade = reader.nextString();
				}else{
					reader.nextNull();
				}
			}else if(name.equals(MetadadoContract.ATTR_IDIOMA)){
				if(reader.peek() != JsonToken.NULL){
					idioma = reader.nextString();
				}else{
					reader.nextNull();
				}
			}else{
				reader.skipValue();
			}
		}

		reader.endObject();

		return new Metadado(nome, valor, idioma, autoridade);

	}


	public static JSONObject getJsonObjectFromReader(JsonReader jsonReader) throws IOException, JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonReader.beginObject();

		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();

			if(name.equals(QTD_ITENS)){
				jsonObject.put(QTD_ITENS, jsonReader.nextInt());
			}else if(name.equals(QTD_ITENS_FILTRADOS)){
				jsonObject.put(QTD_ITENS_FILTRADOS, jsonReader.nextInt());
			}else if(name.equals(QTD_ITENS_RETORNADOS)){
				jsonObject.put(QTD_ITENS_RETORNADOS, jsonReader.nextInt());
			}else if(name.equals(ITENS)){
				if(jsonReader.peek() != JsonToken.NULL){
					jsonReader.beginArray();
					JSONArray itens = new JSONArray();
					while(jsonReader.hasNext()){
						JSONObject i = readJsonObjectItemFromReader(jsonReader);
						try{
							itens.put(i);
						} catch (NumberFormatException e){
							DebugLog.d(e.getMessage());
						}
					}

					jsonObject.put(ITENS, itens);
					jsonReader.endArray();
				}else{
					jsonReader.nextNull();
				}
			}else{
				jsonReader.skipValue();
			}
		}

		return jsonObject;
	}

	public static JSONObject readJsonObjectItemFromReader(JsonReader reader) throws IOException, JSONException {
		JSONObject jsonObject  = new JSONObject();

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(ItemContract.ATTR_CODIGO)){
				jsonObject.put(ItemContract.ATTR_CODIGO, reader.nextInt());
			}else if(name.equals(ItemContract.ATTR_HANDLE)){
				jsonObject.put(ItemContract.ATTR_HANDLE, reader.nextInt());
			}else if(name.equals(ItemContract.ATTR_DATA)){
				jsonObject.put(ItemContract.ATTR_DATA, reader.nextString());
			}else if(name.equals(ItemContract.ATTR_REMOVIDO)){
				jsonObject.put(ItemContract.ATTR_REMOVIDO, reader.nextBoolean());
			}else if(name.equals(ItemContract.ATTR_QT_METADADOS)){
				jsonObject.put(ItemContract.ATTR_QT_METADADOS, reader.nextInt());
			}else if(name.equals(ItemContract.ATTR_METADADOS)){
				if(reader.peek() != JsonToken.NULL){
					reader.beginArray();
					JSONArray metadados = new JSONArray();
					while(reader.hasNext()){
						metadados.put(readJsonObjectMetadadoFromReader(reader));
					}
					jsonObject.put(ItemContract.ATTR_METADADOS, metadados);
					reader.endArray();
				}
				else{
					reader.nextNull();
				}
			}else if(name.equals(ItemContract.ATTR_QT_ARQUIVOS)){
				jsonObject.put(ItemContract.ATTR_QT_ARQUIVOS, reader.nextInt());
			}else if(name.equals(ItemContract.ATTR_ARQUIVOS)){
				if(reader.peek() != JsonToken.NULL){
					reader.beginArray();
					JSONArray arquivos = new JSONArray();
					while (reader.hasNext()) {
						arquivos.put(readJsonObjectArquivoFromReader(reader));
					}
					jsonObject.put(ItemContract.ATTR_ARQUIVOS, arquivos);
					reader.endArray();
				} else {
					reader.nextNull();
				}
			}else{
				reader.skipValue();
			}

		}
		reader.endObject();

		return jsonObject;
	}



	public static JSONObject readJsonObjectMetadadoFromReader(JsonReader reader) throws IOException, JSONException {

		JSONObject jsonObject = new JSONObject();

		reader.beginObject();
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals(MetadadoContract.ATTR_NOME)){
				jsonObject.put(MetadadoContract.ATTR_NOME, reader.nextString());
			}else if(name.equals(MetadadoContract.ATTR_VALOR)){
				jsonObject.put(MetadadoContract.ATTR_VALOR, reader.nextString());
			}else if(name.equals(MetadadoContract.ATTR_AUTORIDADE)){
				if(reader.peek() != JsonToken.NULL){
					jsonObject.put(MetadadoContract.ATTR_AUTORIDADE, reader.nextString());
				}else{
					jsonObject.put(MetadadoContract.ATTR_AUTORIDADE,"");
					reader.nextNull();
				}
			}else if(name.equals(MetadadoContract.ATTR_IDIOMA)){
				if(reader.peek() != JsonToken.NULL){
					jsonObject.put(MetadadoContract.ATTR_IDIOMA, reader.nextString());

				}else{
					jsonObject.put(MetadadoContract.ATTR_IDIOMA, "");
					reader.nextNull();
				}
			}else{
				reader.skipValue();
			}
		}

		reader.endObject();

		return jsonObject;
	}

	public static JSONObject readJsonObjectArquivoFromReader(JsonReader reader) throws IOException, JSONException {
		JSONObject jsonObject = new JSONObject();

		reader.beginObject();
		while(reader.hasNext()){
			String name = reader.nextName();
			if(name.equals(ArquivoContract.ATTR_NOME)){
				jsonObject.put(ArquivoContract.ATTR_NOME, reader.nextString());
			} else if(name.equals(ArquivoContract.ATTR_MIMETYPE)){
				jsonObject.put(ArquivoContract.ATTR_MIMETYPE, reader.nextString());
			}else if(name.equals(ArquivoContract.ATTR_BYTES)){
				jsonObject.put(ArquivoContract.ATTR_BYTES, reader.nextString());
			}else if(name.equals(ArquivoContract.ATTR_CHECKSUM)){
				jsonObject.put(ArquivoContract.ATTR_CHECKSUM, reader.nextString());
			}else if(name.equals(ArquivoContract.ATTR_URL)){
				jsonObject.put(ArquivoContract.ATTR_URL, reader.nextString());
			}else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return jsonObject;
	}
}

