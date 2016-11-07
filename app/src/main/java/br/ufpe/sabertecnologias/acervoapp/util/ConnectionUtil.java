package br.ufpe.sabertecnologias.acervoapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtil {
	public static final int TYPE_WIFI = 1;
	public static final int TYPE_MOBILE = 2;
	public static final int TYPE_NOT_CONNECTED = 0;
	
	public static int getConnectivityStatus(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null) {
			if(info.getType() == ConnectivityManager.TYPE_WIFI){
				return TYPE_WIFI;
			}
			if(info.getType() == ConnectivityManager.TYPE_MOBILE){
				return TYPE_MOBILE;
			}
		}
		return TYPE_NOT_CONNECTED;
	}
	
	public static String  getConnectivityStatusString(Context context){
		int connection = ConnectionUtil.getConnectivityStatus(context);
		String status = null;
		if(connection == ConnectionUtil.TYPE_WIFI){
			status = "wifi enabled";
		}
		else if(connection == ConnectionUtil.TYPE_MOBILE){
			status = "mobile data enable";
		}
		else if(connection == ConnectionUtil.TYPE_NOT_CONNECTED){
			status = "no internet conncetion";
		}
		return status;
	}
	

}
