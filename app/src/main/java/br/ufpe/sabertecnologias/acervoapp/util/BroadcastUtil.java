package br.ufpe.sabertecnologias.acervoapp.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class BroadcastUtil {
	public static void setBroadcastEnableState(Context context, int enable, Class clazz) {
		if (enable == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
			DebugLog.d("Enable receiver - " + clazz);
		}	 else if (enable == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
			DebugLog.d("Disable receiver - " + clazz);
		}
		ComponentName receiver = new ComponentName(context, clazz);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver, enable, PackageManager.DONT_KILL_APP);
	}
}
