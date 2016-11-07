package br.ufpe.sabertecnologias.acervoapp.util;

import android.content.ContentValues;

import java.util.Date;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.AppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Arquivo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.Usuario;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogFeedback;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogFimSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogInicioSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItem;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Metadado;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.SessaoControle;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.User;

public class ClassContentValues {
	public static ContentValues getContentValues(Object object, int idFK, boolean insert){
		ContentValues values = new ContentValues();
		if(object instanceof User){
			if(!insert){
				values.put(Usuario._ID, ((User) object).getId());
			}
			values.put(Usuario.COLUMN_NAME, ((User)object).getName());
			values.put(Usuario.COLUMN_USERNAME, ((User)object).getUsername());
			values.put(Usuario.COLUMN_EMAIL, ((User)object).getEmail());
			values.put(Usuario.COLUMN_TOKEN,((User)object).getToken());
		}
		else if(object instanceof Grupo){
			Grupo g = (Grupo)object;
			if(!insert){
				values.put(AcervoAppContract.Grupo._ID, g.getID());
			}
			values.put(AcervoAppContract.Grupo.COLUMN_ID_GRUPO, g.getUUID_grupo());
			values.put(AcervoAppContract.Grupo.COLUMN_NOME, g.getNome());
			values.put(AcervoAppContract.Grupo.COLUMN_COR, g.getCor());

		}
		else if(object instanceof Item){
			Item i = (Item) object;
			Date d;
			if(!insert){
				values.put(AcervoAppContract.Item._ID, i.getId());
			}
			values.put(AcervoAppContract.Item.COLUMN_CODIGO, i.getCodigo());
			values.put(AcervoAppContract.Item.COLUMN_DATA, DateUtil.convertDate(i.getData()));
			values.put(AcervoAppContract.Item.COLUMN_HANDLE, i.getHandle());
			values.put(AcervoAppContract.Item.COLUMN_QT_METADADOS, i.getQt_metadados());
			values.put(AcervoAppContract.Item.COLUMN_QT_ARQUIVOS, i.getQt_arquivos());
			values.put(AcervoAppContract.Item.COLUMN_STATUS, i.getStatus());
			if(idFK >= 0){
				values.put(AcervoAppContract.Item.COLUMN_ID_GRUPO, idFK);
			}	
			values.put(AcervoAppContract.Item.COLUMN_IN_TRANSFERINDO, i.getIn_transferindo());
			switch(i.isRemovido()){
			case 1:
				values.put(AcervoAppContract.Item.COLUMN_REMOVIDO, 1);
				break;
			case 0:
				values.put(AcervoAppContract.Item.COLUMN_REMOVIDO, 0);
				break;
			default:
				//do nothing
			}
		}
		else if(object instanceof Metadado){
			Metadado m = (Metadado) object;
			String idioma = m.getIdioma();
			String autoridade = m.getAutoridade();
			values.put(AcervoAppContract.Metadado.COLUMN_ID_ITEM, idFK);
			values.put(AcervoAppContract.Metadado.COLUMN_NOME,  m.getNome());
			values.put(AcervoAppContract.Metadado.COLUMN_VALOR, m.getValor());
			if(!insert){
				values.put(AcervoAppContract.Metadado._ID,  m.getID());
			}
			if(idioma != null){
				values.put(AcervoAppContract.Metadado.COLUMN_IDIOMA, idioma);
			}
			if(autoridade != null){			
				values.put(AcervoAppContract.Metadado.COLUMN_AUTORIDADE, autoridade);
			}
		}else if(object instanceof Arquivo){
			Arquivo a = (Arquivo) object; 
			String mimetype = a.getMimetype();
			String tamanho = a.getTamanho_bytes(); 
			String checksum = a.getChecksum_md5();
			values.put(AcervoAppContract.Arquivo.COLUMN_NOME, a.getNome());
			if(!insert){
				values.put(AcervoAppContract.Arquivo._ID, a.getID());
			}
			values.put(AcervoAppContract.Arquivo.COLUMN_URL, a.getUrl());
			values.put(AcervoAppContract.Arquivo.COLUMN_ID_ITEM, idFK);
			if(mimetype != null){
				values.put(AcervoAppContract.Arquivo.COLUMN_MIMETYPE, mimetype);
			}
			if(tamanho != null){
				values.put(AcervoAppContract.Arquivo.COLUMN_TAMANHO_BYTES, tamanho);
			}
			if(checksum != null){
				values.put(AcervoAppContract.Arquivo.COLUMN_CHECKSUM_MD5, checksum);
			}
		}else if(object instanceof LogInicioSessao){
			LogInicioSessao l = (LogInicioSessao) object;
			if(!insert){
				values.put(AcervoAppContract.LogInicioSessao._ID, l.getID());
			}
			values.put(AcervoAppContract.LogInicioSessao.COLUMN_LOG, l.getLog());
			values.put(AcervoAppContract.LogInicioSessao.COLUMN_TIMESTAMP_DATETIME, l.getTimestamp().getDatetime());
			values.put(AcervoAppContract.LogInicioSessao.COLUMN_TIMESTAMP_TIMEZONE, l.getTimestamp().getTimezone());
			values.put(AcervoAppContract.LogInicioSessao.COLUMN_CONEXAO, l.getConexao());
		}else if(object instanceof LogFimSessao){
			LogFimSessao l = (LogFimSessao) object;
			if(!insert){
				values.put(AcervoAppContract.LogFimSessao._ID, l.getID());
			}
			values.put(AcervoAppContract.LogFimSessao.COLUMN_LOG, l.getLog());
			values.put(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_DATETIME, l.getTimestamp().getDatetime());
			values.put(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_TIMEZONE, l.getTimestamp().getTimezone());
			values.put(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_DATETIME_START, l.getTimestampSTART().getDatetime());
			values.put(AcervoAppContract.LogFimSessao.COLUMN_TIMESTAMP_TIMEZONE_START, l.getTimestampSTART().getTimezone());
			values.put(AcervoAppContract.LogFimSessao.COLUMN_FALHA, (l.isFalha()?1:0));
		}else if(object instanceof LogItem){
			LogItem l = (LogItem) object;
			if(!insert){
				values.put(AcervoAppContract.LogItem._ID, l.getID());
			}
			values.put(AcervoAppContract.LogItem.COLUMN_LOG, l.getLog());
			values.put(AcervoAppContract.LogItem.COLUMN_TIMESTAMP_DATETIME, l.getTimestamp().getDatetime());
			values.put(AcervoAppContract.LogItem.COLUMN_TIMESTAMP_TIMEZONE, l.getTimestamp().getTimezone());
			values.put(AcervoAppContract.LogItem.COLUMN_ITEM, l.getItem());
			values.put(AcervoAppContract.LogItem.COLUMN_ACAO, l.getAcao().toString());
			String complemento = l.getComplemento();
			if(complemento != null){
				values.put(AcervoAppContract.LogItem.COLUMN_COMPLEMENTO, l.getComplemento());
			}
		}else if(object instanceof LogFeedback){
			LogFeedback l = (LogFeedback) object;
			if(!insert){
				values.put(AcervoAppContract.LogFeedback._ID, l.getID());
			}
			values.put(AcervoAppContract.LogFeedback.COLUMN_LOG, l.getLog());
			values.put(AcervoAppContract.LogFeedback.COLUMN_TIMESTAMP_DATETIME, l.getTimestamp().getDatetime());
			values.put(AcervoAppContract.LogFeedback.COLUMN_TIMESTAMP_TIMEZONE, l.getTimestamp().getTimezone());
			values.put(AcervoAppContract.LogFeedback.COLUMN_ACAO, l.getAcao());
			String texto = l.getTexto();
			if(texto != null){
				values.put(AcervoAppContract.LogFeedback.COLUMN_TEXTO, l.getTexto());
			}
		}else if(object instanceof LogGrupo){
			LogGrupo l = (LogGrupo) object;
			if(!insert){
				values.put(AcervoAppContract.LogItem._ID, l.getID());
			}
			values.put(AcervoAppContract.LogGrupo.COLUMN_LOG, l.getLog());
			values.put(AcervoAppContract.LogGrupo.COLUMN_TIMESTAMP_DATETIME, l.getTimestamp().getDatetime());
			values.put(AcervoAppContract.LogGrupo.COLUMN_TIMESTAMP_TIMEZONE, l.getTimestamp().getTimezone());
			values.put(AcervoAppContract.LogGrupo.COLUMN_ACAO, l.getAcao().toString());
			values.put(AcervoAppContract.LogGrupo.COLUMN_NOME, l.getNome());
			values.put(AcervoAppContract.LogGrupo.COLUMN_UUID, l.getUuid());
			values.put(AcervoAppContract.LogGrupo.COLUMN_COR, l.getCor());
		}else if (object instanceof SessaoControle){
			SessaoControle l = (SessaoControle) object;
			values.put(AcervoAppContract.SessaoControle.COLUMN_NUMSESSAO, l.getSessaoMilis());
			values.put(AcervoAppContract.SessaoControle.COLUMN_STATUS, l.getStatus());
		} else if(object instanceof AppConfig){
			AppConfig appConfig = (AppConfig) object;
			values.put(AcervoAppContract.AppConfig.COLUMN_API_VERSION, appConfig.getApi_version());
			values.put(AcervoAppContract.AppConfig.COLUMN_FLAG_HAS_ATT, appConfig.getHas_att());
			values.put(AcervoAppContract.AppConfig.COLUMN_FLAG_HAS_TUTORIAL, appConfig.getHas_tutorial());
			values.put(AcervoAppContract.AppConfig.COLUMN_SETUP_TMP_MAX, appConfig.getSetupTPMax());
			values.put(AcervoAppContract.AppConfig.COLUMN_SETUP_INITIAL_TIME, appConfig.getInitialTime());
		}
		return values;
	}
}