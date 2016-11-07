package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.os.Parcel;
import android.os.Parcelable;

public class Metadado implements Parcelable{
	
	private int id;
	private String nome;
	private String valor;
	private String idioma;
	private String autoridade;
	
	
	
	public Metadado(int id, String nome, String valor, String idioma,
			String autoridade) {
		this.id = id;
		this.nome = nome;
		this.valor = valor;
		this.idioma = idioma;
		this.autoridade = autoridade;
	}
	
	public Metadado(String nome, String valor, String idioma,
			String autoridade) {
		this.nome = nome;
		this.valor = valor;
		this.idioma = idioma;
		this.autoridade = autoridade;
	}

	public int getID() {
		return id;
	}
	public String getNome() {
		return nome;
	}
	public String getValor() {
		return valor;
	}
	public String getIdioma() {
		return idioma;
	}
	public String getAutoridade() {
		return autoridade;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.nome);
		dest.writeString(this.valor);
		dest.writeString(this.idioma);
		dest.writeString(this.autoridade);
	}

	public static final Creator<Metadado> CREATOR = new Creator<Metadado>() {
		@Override
		public Metadado createFromParcel(Parcel source) {
			return new Metadado(source);
		}

		@Override
		public Metadado[] newArray(int size) {
			return new Metadado[size];
		}
	};


	private Metadado(Parcel source){
		this.id = source.readInt();
		this.nome = source.readString();
		this.valor = source.readString();
		this.idioma = source.readString();
		this.autoridade = source.readString();
	}
}
