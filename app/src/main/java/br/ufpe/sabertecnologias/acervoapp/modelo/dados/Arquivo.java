package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.os.Parcel;
import android.os.Parcelable;

public class Arquivo implements Parcelable {
	private int _id;
	private String nome;
	private String mimetype;
	private String tamanho_bytes;
	private String checksum_md5;
	private String url;

	public Arquivo(int _id, String nome, String mimetype,
			String tamanho_bytes, String checksum_md5, String url) {
		this._id = _id;
		this.nome = nome;
		this.mimetype = mimetype;
		this.tamanho_bytes = tamanho_bytes;
		this.checksum_md5 = checksum_md5;
		this.url = url;
	}
	
	
	public Arquivo(String nome2, String mimetype2, String tamanho_bytes2,
			String checksum_md52, String url2) {
		this(-1, nome2, mimetype2,  tamanho_bytes2, checksum_md52, url2);
	}


	public int getID() {
		return _id;
	}
	public String getNome() {
		return nome;
	}
	public String getMimetype() {
		return mimetype;
	}

	public String getTamanho_bytes() {
		return tamanho_bytes;
	}
	public String getChecksum_md5() {
		return checksum_md5;
	}
	public String getUrl() {
		return url;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._id);
		dest.writeString(this.nome);
		dest.writeString(this.mimetype);
		dest.writeString(this.tamanho_bytes);
		dest.writeString(this.checksum_md5);
		dest.writeString(this.url);
	}

	public static final Creator<Arquivo> CREATOR = new Creator<Arquivo>() {
		@Override
		public Arquivo createFromParcel(Parcel source) {
			return new Arquivo(source);
		}

		@Override
		public Arquivo[] newArray(int size) {
			return new Arquivo[size];
		}
	};

	private Arquivo(Parcel source){
		this._id = source.readInt();
		this.nome = source.readString();
		this.mimetype = source.readString();
		this.tamanho_bytes = source.readString();
		this.checksum_md5 = source.readString();
		this.url = source.readString();
	}
}
