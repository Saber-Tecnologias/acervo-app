package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogBase;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogInicioSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Timestamp;

public class RepositorioLogInicioSessao extends RepositorioLog{

	public RepositorioLogInicioSessao(Context context) {
		super(context, AcervoAppContract.LogInicioSessao.TABLE_NAME);
	}

	@Override
	LogBase getLogFromCursor(Cursor cursor) {
		int ID = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.LogInicioSessao._ID));
		String log = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogInicioSessao.COLUMN_LOG));
		String datetime = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogInicioSessao.COLUMN_TIMESTAMP_DATETIME));
		String timezone = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogInicioSessao.COLUMN_TIMESTAMP_TIMEZONE));
		String conexao = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogInicioSessao.COLUMN_CONEXAO));
		return new LogInicioSessao(ID,log, new Timestamp(datetime,timezone),conexao);
	}
	
}
