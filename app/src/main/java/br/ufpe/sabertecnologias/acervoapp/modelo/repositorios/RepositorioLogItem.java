package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogBase;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItem;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Timestamp;

public class RepositorioLogItem extends RepositorioLog{

	public RepositorioLogItem(Context context) {
		super(context, AcervoAppContract.LogItem.TABLE_NAME);
	}

	@Override
	LogBase getLogFromCursor(Cursor cursor) {
		int ID = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.LogItem._ID));
		String log = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogItem.COLUMN_LOG));
		String datetime = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogItem.COLUMN_TIMESTAMP_DATETIME));
		String timezone = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogItem.COLUMN_TIMESTAMP_TIMEZONE));
		LogItemAcaoEnum acao = resolveStringEnum( cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogItem.COLUMN_ACAO)) );
		String complemento = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogItem.COLUMN_COMPLEMENTO));
		int item = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.LogItem.COLUMN_ITEM));
		return new LogItem(ID,log,new Timestamp(datetime, timezone), item, acao, complemento);
	}

	private LogItemAcaoEnum resolveStringEnum(String str){
		LogItemAcaoEnum ret = null;
		if (str.equals(LogItemAcaoEnum.ADICIONAR.toString())){
			ret = LogItemAcaoEnum.ADICIONAR;
		}else if (str.equals(LogItemAcaoEnum.DOWNLOAD.toString())){
			ret = LogItemAcaoEnum.DOWNLOAD;
		}else if (str.equals(LogItemAcaoEnum.AVALIAR.toString())){
			ret = LogItemAcaoEnum.AVALIAR;
		}else if (str.equals(LogItemAcaoEnum.FAVORITAR.toString())){
			ret = LogItemAcaoEnum.FAVORITAR;
		}else if (str.equals(LogItemAcaoEnum.EXCLUIR.toString())){
			ret = LogItemAcaoEnum.EXCLUIR;
		}else if (str.equals(LogItemAcaoEnum.REMOVER.toString())){
			ret = LogItemAcaoEnum.REMOVER;
		}else if (str.equals(LogItemAcaoEnum.AGRUPAR.toString())){
			ret = LogItemAcaoEnum.AGRUPAR;
		}else if (str.equals(LogItemAcaoEnum.DESAGRUPAR.toString())){
			ret = LogItemAcaoEnum.DESAGRUPAR;
		} else if(str.equals(LogItemAcaoEnum.ABRIR.toString())){
			ret = LogItemAcaoEnum.ABRIR;

		}
		return ret;
	}

}
