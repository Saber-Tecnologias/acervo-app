package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogBase;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Timestamp;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogFimSessao;

public class RepositorioLogFimSessao extends RepositorioLog{

	public RepositorioLogFimSessao(Context context) {
		super(context, AcervoAppContract.LogFimSessao.TABLE_NAME);
	}

	@Override
	LogBase getLogFromCursor(Cursor cursor) {
		int ID = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.LogFimSessao._ID));
		String log = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFimSessao.COLUMN_LOG));
		String datetime = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_DATETIME));
		String timezone = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_TIMEZONE));
		String datetime_start = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_DATETIME));
		String timezone_start = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_TIMEZONE));
		int falha = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.LogFimSessao.COLUMN_FALHA));
		return new LogFimSessao(ID,log, new Timestamp(datetime, timezone),new Timestamp(datetime_start, timezone_start), (falha==1?true:false));
	}

}
