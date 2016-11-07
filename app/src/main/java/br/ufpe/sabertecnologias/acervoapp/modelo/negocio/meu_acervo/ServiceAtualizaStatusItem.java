package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo;

import android.app.IntentService;
import android.content.Intent;

import br.ufpe.sabertecnologias.acervoapp.modelo.Executor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.saber.downloadservice.Downloader;

public class ServiceAtualizaStatusItem extends IntentService {

	Facade facade;

	public ServiceAtualizaStatusItem() {
		super("SERVICE_ATUALIZA_STATUS_ITEM");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		facade = (Facade) getApplication();

		int item_codigo = intent.getIntExtra(Downloader.CURRENT_ID, -9);
		int status = intent.getIntExtra("status", Item.FLAG_ITEM_ENFILEIRADO);
		long downloadTime = intent.getLongExtra(Downloader.CURRENT_TIME, 0);
		
		Executor executor = new Executor(null);

		//Log.d("DEBUG", "Item ID = "+item_codigo);
		//Log.d("DEBUG", "Item Status = "+status);

		Item i = facade.getItemByCodigo(item_codigo, executor);
		
		i.setStatus(status);
		
		facade.attItem(i, -1, executor);


		switch(status){
		case Item.FLAG_ITEM_ENFILEIRADO:
			//facade.downloadItem(item_codigo, i.getUrl(), i.getFormato(), i.getChecksum());
			break;
		case Item.FLAG_ITEM_BAIXADO:
			executor = new Executor(null);

			facade.empilhaLogItem(executor, i.getCodigo(), LogItemAcaoEnum.DOWNLOAD, downloadTime, this.getApplicationContext());
			break;
			
		} 
	}

}
