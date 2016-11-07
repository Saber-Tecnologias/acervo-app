package br.ufpe.sabertecnologias.acervoapp.modelo.repositorios;

import android.content.Context;
import android.database.Cursor;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogBase;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupoAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Timestamp;

public class RepositorioLogGrupo extends RepositorioLog{

	public RepositorioLogGrupo(Context context) {
		super(context, AcervoAppContract.LogGrupo.TABLE_NAME);
	}

	@Override
	LogBase getLogFromCursor(Cursor cursor) {
		int ID = cursor.getInt(cursor.getColumnIndex(AcervoAppContract.LogGrupo._ID));
		String log = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogGrupo.COLUMN_LOG));
		String datetime = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogGrupo.COLUMN_TIMESTAMP_DATETIME));
		String timezone = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogGrupo.COLUMN_TIMESTAMP_TIMEZONE));
		LogGrupoAcaoEnum acao = resolveStringEnum(cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogGrupo.COLUMN_ACAO)));
		String nome = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogGrupo.COLUMN_NOME));
		String uuid = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogGrupo.COLUMN_UUID));
		String cor = cursor.getString(cursor.getColumnIndex(AcervoAppContract.LogGrupo.COLUMN_COR));
		return new LogGrupo(ID,log,new Timestamp(datetime, timezone), acao, nome, uuid, cor);
	}
	
	private LogGrupoAcaoEnum resolveStringEnum(String str){
		LogGrupoAcaoEnum ret = null;
		if (str.equals(LogGrupoAcaoEnum.ADICIONAR.toString())){
			ret = LogGrupoAcaoEnum.ADICIONAR;
		}else if (str.equals(LogGrupoAcaoEnum.REMOVER.toString())){
			ret = LogGrupoAcaoEnum.REMOVER;
		} else if(str.equals(LogGrupoAcaoEnum.ALTERAR.toString())){
			ret = LogGrupoAcaoEnum.ALTERAR;
		}
		return ret;
	}
}
