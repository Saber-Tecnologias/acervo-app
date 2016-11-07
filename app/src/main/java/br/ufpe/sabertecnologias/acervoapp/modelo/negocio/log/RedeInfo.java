package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.log;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

public class RedeInfo {

	public RedeInfo() {
	}

	public static String getNetworkClass(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo info = cm.getActiveNetworkInfo();
		String netClass = "";

		if(info==null || !info.isConnected()) {
			netClass = netClass + "Desconectado";
		} else if(info.getType() == ConnectivityManager.TYPE_WIFI) {
			netClass = netClass + "WIFI";
		}else if(info.getType() == ConnectivityManager.TYPE_MOBILE){
			int networkType = info.getSubtype();
			switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_EDGE: netClass = netClass + "EDGE(2G)";break;
				case TelephonyManager.NETWORK_TYPE_CDMA: netClass = netClass + "CDMA(2G)";break;
				case TelephonyManager.NETWORK_TYPE_1xRTT: netClass = netClass + "1xRTT(2G)";break;
				case TelephonyManager.NETWORK_TYPE_IDEN: netClass = netClass + "IDEN(2G)";break;//api<8 : replace by 11
				case TelephonyManager.NETWORK_TYPE_UMTS:netClass = netClass + "UMTS(3G)";break;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:netClass = netClass + "EVDO_0(3G)";break;
				case TelephonyManager.NETWORK_TYPE_EVDO_A:netClass = netClass + "EVDO_A(3G)";break;
				case TelephonyManager.NETWORK_TYPE_HSDPA:netClass = netClass + "HSDPA(3G)";break;
				case TelephonyManager.NETWORK_TYPE_HSUPA:netClass = netClass + "HSUPA(3G)";break;
				case TelephonyManager.NETWORK_TYPE_HSPA:netClass = netClass + "HSPA(3G)";break;
				case TelephonyManager.NETWORK_TYPE_EVDO_B:netClass = netClass + "EVDO_B(3G)";break; //api<9 : replace by 14
				case TelephonyManager.NETWORK_TYPE_EHRPD: netClass = netClass + "EHRPD(3G)"; break;//api<11 : replace by 12
				case TelephonyManager.NETWORK_TYPE_HSPAP: netClass = netClass + "HSPAP(3G)";break; //api<13 : replace by 15
				case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
					netClass = netClass + "LTE(4G)";break;
				default:
					netClass = netClass + "Desconhecido";
			}
		}
		else netClass = netClass + "Desconhecido";

		return netClass;
	}

}
