package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogBase;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogFeedback;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Timestamp;

public class RepositorioLogFeedback extends RepositorioLog{

	public RepositorioLogFeedback(Context context) {
		super(context, AcervoAppContract.LogFeedback.TABLE_NAME);
	}

	@Override
	LogBase getLogFromCursor(Cursor cursor) {
		int ID = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.LogFeedback._ID));
		String log = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFeedback.COLUMN_LOG));
		String datetime = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFeedback.COLUMN_TIMESTAMP_DATETIME));
		String timezone = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFeedback.COLUMN_TIMESTAMP_TIMEZONE));
		String acao = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFeedback.COLUMN_ACAO));
		String texto = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFeedback.COLUMN_TEXTO));
		return new LogFeedback(ID,log,new Timestamp(datetime, timezone), acao, texto);
	}
}
