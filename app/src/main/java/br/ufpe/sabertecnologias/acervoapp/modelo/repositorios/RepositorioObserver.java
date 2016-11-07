package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.os.Handler;
import android.os.Message;

public class RepositorioObserver extends Handler {
		
		
		public static final int INSERT = 0;
		public static final int UPDATE = 1;
		public static final int DELETE = 2;
		public static final int SYNC = 3;
		public static final int SYNC_GRUPOS = 4;
		public static final int SYNC_ITENS = 5;

	private RepositorioObserverCallback callback;
		
		public interface RepositorioObserverCallback{
			public void updateFromRepositorio(int method, Object obj);
		}
		
		public RepositorioObserver(RepositorioObserverCallback callback) {
			this.callback = callback;
		}
				
		public void notify(int method, Object obj) {
			Message msg = Message.obtain();
			
			msg.obj = obj;
			msg.what = method;
			sendMessage(msg);

		}
		
		@Override
		public void handleMessage(Message msg) {
			callback.updateFromRepositorio(msg.what, msg.obj);
		}

		
}
