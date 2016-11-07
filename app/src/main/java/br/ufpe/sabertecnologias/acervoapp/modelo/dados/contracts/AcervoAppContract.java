package br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts;

import android.provider.BaseColumns;

public final class AcervoAppContract {
	public AcervoAppContract() {}
	
	public static abstract class Grupo implements BaseColumns{
		public static final String TABLE_NAME = "grupo";
		public static final String COLUMN_ID_GRUPO = "uuid_grupo";
		public static final String COLUMN_NOME = "nome";
		public static final String COLUMN_COR = "cor";
	}
	public static abstract class Item implements BaseColumns{
		public static final String TABLE_NAME = "item";
		public static final String COLUMN_CODIGO = "codigo";
		public static final String COLUMN_HANDLE = "handle";
		public static final String COLUMN_DATA = "data";
		public static final String COLUMN_REMOVIDO = "removido";
		public static final String COLUMN_QT_METADADOS = "qt_metadados";
		public static final String COLUMN_QT_ARQUIVOS = "qt_arquivos";
		public static final String COLUMN_ID_GRUPO = "id_grupo";
		public static final String COLUMN_STATUS = "fl_status";
		public static final String INDEX_NAME = "item_index";
		public static final String COLUMN_IN_TRANSFERINDO ="in_transferindo";

	}
	public static abstract class Metadado implements BaseColumns{
		public static final String TABLE_NAME = "metadado";
		public static final String COLUMN_NOME = "nome";
		public static final String COLUMN_VALOR = "valor";
		public static final String COLUMN_IDIOMA = "idioma";
		public static final String COLUMN_AUTORIDADE = "autoridade";
		public static final String COLUMN_ID_ITEM = "id_item";
		public static final String INDEX_NAME = "metadado_index";
	}
	public static abstract class Arquivo implements BaseColumns{
		public static final String TABLE_NAME = "arquivo";
		public static final String COLUMN_NOME = "nome";
		public static final String COLUMN_MIMETYPE = "mimetype";
		public static final String COLUMN_TAMANHO_BYTES = "tamanho_bytes";
		public static final String COLUMN_CHECKSUM_MD5 = "checksum_md5";
		public static final String COLUMN_URL = "url";
		public static final String COLUMN_ID_ITEM = "id_item";
		public static final String INDEX_NAME = "arquivo_index";
	}

	public static abstract class Usuario implements BaseColumns{
		public static final String TABLE_NAME = "usuario";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_USERNAME = "username";
		public static final String COLUMN_EMAIL = "email";
		public static final String COLUMN_TOKEN = "token";
		}
	
	public static abstract class LogInicioSessao implements BaseColumns{
		public static final String TABLE_NAME = "log_inicio_sessao";
		public static final String COLUMN_LOG = "INICIO_SESSAO";
		//public static final String COLUMN_SESSAO = "sessao";
		public static final String COLUMN_TIMESTAMP_DATETIME = "datetime";
		public static final String COLUMN_TIMESTAMP_TIMEZONE = "timezone";
		public static final String COLUMN_CONEXAO = "conexao";
		
	}
	
	public static abstract class LogFimSessao implements BaseColumns{
		public static final String TABLE_NAME = "log_fim_sessao";
		public static final String COLUMN_LOG = "FIM_SESSAO";
		//public static final String COLUMN_SESSAO = "sessao";
		public static final String COLUMN_TIMESTAMP_DATETIME = "datetime";
		public static final String COLUMN_TIMESTAMP_TIMEZONE = "timezone";
		public static final String COLUMN_TIMESTAMP_DATETIME_START = "datetime_start";
		public static final String COLUMN_TIMESTAMP_TIMEZONE_START = "timezone_start";
		public static final String COLUMN_FALHA = "falha";
	}
	
	public static abstract class LogItem implements BaseColumns{
		public static final String TABLE_NAME = "log_item";
		public static final String COLUMN_LOG = "LOG_ITEM";
		//public static final String COLUMN_SESSAO = "sessao";
		public static final String COLUMN_TIMESTAMP_DATETIME = "datetime";
		public static final String COLUMN_TIMESTAMP_TIMEZONE = "timezone";
		public static final String COLUMN_ITEM = "item";
		public static final String COLUMN_ACAO = "acao";
		public static final String COLUMN_COMPLEMENTO = "complemento";
	}
	
	public static abstract class LogGrupo implements BaseColumns{
		public static final String TABLE_NAME = "log_grupo";
		public static final String COLUMN_LOG = "GRUPO";
		//public static final String COLUMN_SESSAO = "sessao";
		public static final String COLUMN_TIMESTAMP_DATETIME = "datetime";
		public static final String COLUMN_TIMESTAMP_TIMEZONE = "timezone";
		public static final String COLUMN_ACAO = "acao";
		public static final String COLUMN_NOME = "nome";
		public static final String COLUMN_UUID = "uuid";
		public static final String COLUMN_COR = "cor";
	}

	public static abstract class LogFeedback implements BaseColumns{
		public static final String TABLE_NAME = "log_feedback";
		public static final String COLUMN_LOG = "FEEDBACK";
		//public static final String COLUMN_SESSAO = "sessao";
		public static final String COLUMN_TIMESTAMP_DATETIME = "datetime";
		public static final String COLUMN_TIMESTAMP_TIMEZONE = "timezone";
		public static final String COLUMN_ACAO = "acao";
		public static final String COLUMN_TEXTO = "texto";
	}
	
	public static abstract class SessaoControle implements BaseColumns{
		public static final String TABLE_NAME = "sessaocontrole";
		public static final String COLUMN_NUMSESSAO = "numsessao";
		public static final String COLUMN_STATUS = "status";
	}
	
	public static abstract class AppConfig implements BaseColumns{
		public static final String TABLE_NAME = "t_app_config";
		public static final String COLUMN_API_VERSION = "api_version";
		public static final String COLUMN_FLAG_HAS_ATT = "f_has_att";
		public static final String COLUMN_FLAG_HAS_TUTORIAL = "f_has_tutorial";
		public static final String COLUMN_SETUP_TMP_MAX = "setup_tp_max";
		public static final String COLUMN_SETUP_INITIAL_TIME = "initial_time";
	}
}
