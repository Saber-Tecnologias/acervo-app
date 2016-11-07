package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import android.os.Parcel;
import android.os.Parcelable;

public class AppConfig implements Parcelable {

	private String api_version;
	private int has_att;
	private int has_tutorial;
	private int setupTPMax;
	private long initialTime;


	public AppConfig(String api_version, int has_att, int has_tutorial, int setupTPMax, long initialTime) {
		this.api_version = api_version;
		this.has_att = has_att;
		this.has_tutorial = has_tutorial;
		this.setupTPMax = setupTPMax;
		this.initialTime = initialTime;
	}
	
	private AppConfig(Parcel source) {
		this.api_version  = source.readString();
		this.has_att = source.readInt();
		this.has_tutorial = source.readInt();
		this.setupTPMax = source.readInt();
		this.initialTime = source.readLong();
		
	}

	public String getApi_version() {
		return api_version;
	}
	public void setApi_version(String api_version) {
		this.api_version = api_version;
	}
	public int getHas_att() {
		return has_att;
	}
	public void setHas_att(int has_att) {
		this.has_att = has_att;
	}
	public int getHas_tutorial() {
		return has_tutorial;
	}
	public void setHas_tutorial(int has_tutorial) {
		this.has_tutorial = has_tutorial;
	}


	public int getSetupTPMax() {
		return setupTPMax;
	}

	public long getInitialTime() {
		return initialTime;
	}

	public void setSetupTPMax(int setupTPMax) {
		this.setupTPMax = setupTPMax;
	}

	public void setInitialTime(long initialTime) {
		this.initialTime = initialTime;
	}
	
	 public static final Parcelable.Creator<AppConfig> CREATOR = new Parcelable.Creator<AppConfig>() {

		@Override
		public AppConfig createFromParcel(Parcel source) {
			
			return new AppConfig(source);
		}

		@Override
		public AppConfig[] newArray(int size) {
			return new AppConfig[size];
		}
		
	};

	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.api_version);
		dest.writeInt(this.has_att);
		dest.writeInt(this.has_tutorial);
		dest.writeInt(this.setupTPMax);
		dest.writeLong(this.initialTime);
	}
}
