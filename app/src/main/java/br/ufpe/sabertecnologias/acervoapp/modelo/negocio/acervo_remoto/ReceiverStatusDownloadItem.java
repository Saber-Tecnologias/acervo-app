package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo.ServiceAtualizaStatusItem;
import br.saber.downloadservice.Downloader;

public class ReceiverStatusDownloadItem extends BroadcastReceiver {


	private	String action;
	private	int id;
	private long downloadTime;


	//	private	String path;
	//	private	boolean hasNext;


	@Override
	public void onReceive(Context context, Intent intent) {

		action = intent.getAction();

		downloadTime = intent.getLongExtra(Downloader.CURRENT_TIME, 0);

		Intent newIntent = new Intent();

		newIntent.setClass(context, ServiceAtualizaStatusItem.class);


		if(action.equals(Downloader.DOWNLOAD_STARTED_ACTION)){

			id = Integer.parseInt(intent.getStringExtra(Downloader.CURRENT_ID));
			newIntent.putExtra(Downloader.CURRENT_ID, id);


			newIntent.putExtra("status", Item.FLAG_ITEM_BAIXANDO);

			context.startService(newIntent);


		}else if(action.equals(Downloader.DOWNLOAD_TERMINATED_ACTION)){


			id = Integer.parseInt(intent.getStringExtra(Downloader.CURRENT_ID));
			newIntent.putExtra(Downloader.CURRENT_ID, id);
			newIntent.putExtra("status", Item.FLAG_ITEM_BAIXADO);
			newIntent.putExtra(Downloader.CURRENT_TIME, downloadTime );

			context.startService(newIntent);

		}
		else if(action.equals(Downloader.DOWNLOAD_CANCELED_ACTION))			
		{			
			//DO NOTHING
		} /*else if(action.equals(DownloadService.STORAGE_FULL_ACTION)){


		}*/



	}

}

