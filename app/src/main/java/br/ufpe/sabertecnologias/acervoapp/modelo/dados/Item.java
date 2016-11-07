package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.ArquivoContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.ItemContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.MetadadoContract;

public class Item implements Parcelable {

	public static final int FLAG_ITEM_INDISPONIVEL = -1;
	public static final int FLAG_ITEM_DISPONIVEL = 0;
	public static final int FLAG_ITEM_ENFILEIRADO = 1;
	public static final int FLAG_ITEM_BAIXANDO = 2;
	public static final int FLAG_ITEM_BAIXADO = 3;
	public static final int FLAG_ITEM_REMOVIDO = 4;
	public static final int FLAG_ITEM_REMOVIDO_E_EXISTELOCAL = 5;
	public static final int IN_TRANSFERINDO_TRUE = 0;
	public static final int IN_TRANSFERINDO_FALSE = 1;

	private int id;
	private int codigo;
	private int handle;
	private String data;
	private int removido;
	private int qt_metadados;
	private ArrayList<Metadado> metadados;
	private int qt_arquivos;
	private ArrayList<Arquivo> arquivos;
	private int status;
	private int idGrupo;
	private int in_transferindo;

	public Item(int codigo, int handle, String data, int removido,
			int qt_metadados, ArrayList<Metadado> metadados, int qt_arquivos,
			ArrayList<Arquivo> arquivos, int status) {
		this.codigo = codigo;
		this.handle = handle;
		this.data = data;
		this.removido = removido;
		this.qt_metadados = qt_metadados;
		this.metadados = metadados;
		this.qt_arquivos = qt_arquivos;
		this.arquivos = arquivos;
		this.status = status;
	}

	public Item(int id, int codigo, int handle, String data, int removido,
			int qt_metadados, ArrayList<Metadado> metadados, int qt_arquivos,
			ArrayList<Arquivo> arquivos, int status) {
		this.id = id;
		this.codigo = codigo;
		this.handle = handle;
		this.data = data;
		this.removido = removido;
		this.qt_metadados = qt_metadados;
		this.metadados = metadados;
		this.qt_arquivos = qt_arquivos;
		this.arquivos = arquivos;
		this.status = status;
	}

	public void setGrupo(int idGrupo){
		this.idGrupo = idGrupo;
	}

	public int getId() {
		return id;
	}
	public int getCodigo() {
		return codigo;
	}
	public int getHandle() {
		return handle;
	}
	public String getData() {
		return data;
	}
	public int isRemovido() {
		return removido;
	}
	public int getQt_metadados() {
		return qt_metadados;
	}
	public ArrayList<Metadado> getMetadados() {
		return metadados;
	}
	public int getQt_arquivos() {
		return qt_arquivos;
	}
	public ArrayList<Arquivo> getArquivos() {
		return arquivos;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getNome() {
		Metadado m;
		for(int i=0; i<metadados.size(); i++){
			if((m=metadados.get(i)).getNome().equals(MetadadoContract.TITLE)){
				return m.getValor();
			}
		}
		return "";
	}

	public String getFormato() {
		Arquivo a;
		for(int i=0; i<arquivos.size();){
			a = arquivos.get(i);
			return a.getMimetype();
		}
		return "";
	}

	public String getType() {
		String type = "";
		Metadado m;
		for(int i=0; i<metadados.size(); i++){
			m = metadados.get(i);
			if(m.getNome().equals(MetadadoContract.TYPE)){

				return m.getValor();
			}
		}
		return "";
	}

	public String getTamanho() {
		Arquivo a;
		for(int i=0; i<arquivos.size(); i++){
			a = arquivos.get(i);
			return (a.getTamanho_bytes().equals("")?"0": a.getTamanho_bytes());
		}
		return "0";
	}

	public String getTema(){
		String tema = "";
		Metadado m;
		for(int i=0; i<metadados.size(); i++){
			m = metadados.get(i);
			if(m.getNome().equals(MetadadoContract.CLASSIFICATION)){

				return m.getValor();
			}
		}


		return tema;
	}

	public ArrayList<String> getTemas(){
		ArrayList<String> temas = new ArrayList<String>();
		Metadado m;
		for(int i=0; i<metadados.size(); i++){
			m = metadados.get(i);
			if(m.getNome().equals(MetadadoContract.CLASSIFICATION)){
				temas.add(m.getValor());
			}
		}
		return temas;
	}

	public String getUrl() {
		String url = "";
		Arquivo a = arquivos.get(0);
		url = a.getUrl();
		return url;
	}

	public String getChecksum() {
		String checksum = "";
		Arquivo a = arquivos.get(0);
		checksum = a.getChecksum_md5();
		return checksum;
	}

	public int getIn_transferindo() {
		return in_transferindo;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public void setIn_transferindo(int in_transferindo) {
		this.in_transferindo = in_transferindo;
	}

	public int getIDGrupo() {
		
		return idGrupo;
	}
	
	public String getDescricao(){
		String descricao = "Sem descriçao";
		Metadado m;
		for (int i=0; i<metadados.size(); i++){
			
			m = metadados.get(i);
			

			if(m.getNome().equals(MetadadoContract.DESCRIPTION)){
				descricao = m.getValor();
			}
			
		}
		
		return descricao;
	}
	
	public String getInstituicoes(){
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		Metadado m;
		for(int i=0; i<metadados.size(); i++){
			m = metadados.get(i);
			if(m.getNome().equals(MetadadoContract.MEDIATOR)){
				if(isFirst){
					sb.append(m.getValor());
					isFirst = false;
				} else{
					sb.append(", ").append(m.getValor());
				}
			}
		}
		
		return sb.toString();
	}

	public static Item getItemFromJSON (JSONObject itemJSON, List<Item> itemListOld) throws JSONException {
		Item ret = null;

        ArrayList<Metadado> metadados = new ArrayList<Metadado>();
        ArrayList<Arquivo> arquivos = new ArrayList<Arquivo>();

		JSONObject detalhe =  itemJSON.getJSONObject(ItemContract.ATTR_DETALHE);
		JSONArray metadadosJSONArray =detalhe.getJSONArray(ItemContract.ATTR_METADADOS);
		JSONArray arquivosJSONArray = detalhe.getJSONArray(ItemContract.ATTR_ARQUIVOS);
		String situacaoJSON = itemJSON.getString(ItemContract.ATTR_SITUACAO);
		int codigo= itemJSON.getInt(ItemContract.ATTR_CODIGO);


		for(int i = 0; i<metadadosJSONArray.length();i++)
        {

            JSONObject tempJsonMet = metadadosJSONArray.getJSONObject(i);
            Metadado tempMet = new Metadado(tempJsonMet.getString(MetadadoContract.ATTR_NOME),
                    tempJsonMet.getString(MetadadoContract.ATTR_VALOR),
                    tempJsonMet.getString(MetadadoContract.ATTR_IDIOMA),
                    tempJsonMet.getString(MetadadoContract.ATTR_AUTORIDADE));

            metadados.add(tempMet);
        }


        for(int i = 0; i<arquivosJSONArray.length(); i++)
        {
            JSONObject tempJsonArq = arquivosJSONArray.getJSONObject(i);
            Arquivo arq = new Arquivo(tempJsonArq.getString(ArquivoContract.ATTR_NOME),
                    tempJsonArq.getString(ArquivoContract.ATTR_MIMETYPE),
                    tempJsonArq.getString(ArquivoContract.ATTR_BYTES),
                    tempJsonArq.getString(ArquivoContract.ATTR_CHECKSUM),
                    tempJsonArq.getString(ArquivoContract.ATTR_URL));

            arquivos.add(arq);
        }

		int situacao = FLAG_ITEM_DISPONIVEL;
		int transferindo = IN_TRANSFERINDO_FALSE;
		boolean inListOld = false;

		Item itemOld = null;

		for (int i =0; i < itemListOld.size(); i++){

			itemOld = itemListOld.get(i);

			if(itemOld.getCodigo() == codigo){
				situacao = itemOld.getStatus();
				transferindo = itemOld.getIn_transferindo();
				inListOld = true;
				i = itemListOld.size();
			}
		}


		if(ItemContract.SITUACAO_REMOVIDO.equals(situacaoJSON)){
			if(inListOld){
				situacao = FLAG_ITEM_REMOVIDO_E_EXISTELOCAL;
			} else {
				situacao = FLAG_ITEM_REMOVIDO;
			}
		}
        ret = new Item(itemJSON.getInt(ItemContract.ATTR_CODIGO),
				detalhe.getInt(ItemContract.ATTR_HANDLE),
				detalhe.getString(ItemContract.ATTR_DATA),
				detalhe.getBoolean(ItemContract.ATTR_REMOVIDO)?1:0,
				detalhe.getInt(ItemContract.ATTR_QT_METADADOS),
                metadados,
				detalhe.getInt(ItemContract.ATTR_QT_ARQUIVOS),
				arquivos,
                situacao);
		ret.setIn_transferindo(transferindo);

        return ret;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(codigo);
		dest.writeInt(handle);
		dest.writeString(data);
		dest.writeInt(removido);
		dest.writeInt(qt_metadados);
		dest.writeTypedList(metadados);
		dest.writeInt(qt_arquivos);
		dest.writeTypedList(arquivos);
		dest.writeInt(status);
		dest.writeInt(idGrupo);
	}


	public  static final Parcelable.Creator<Item> CREATOR = new Creator<Item>() {
		@Override
		public Item createFromParcel(Parcel source) {
			return new Item(source);
		}

		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};


	private Item(Parcel source){
		this.id = source.readInt();
		this.codigo = source.readInt();
		this.handle = source.readInt();
		this.data = source.readString();;
		this.removido = source.readInt();;
		this.qt_metadados = source.readInt();;
		this.metadados = new ArrayList<Metadado>();
		source.readTypedList(this.metadados, Metadado.CREATOR);
		this.qt_arquivos = source.readInt();;
		this.arquivos = new ArrayList<Arquivo>();
		source.readTypedList(this.arquivos, Arquivo.CREATOR);
		this.status = source.readInt();
		this.idGrupo = source.readInt();

	}
}
