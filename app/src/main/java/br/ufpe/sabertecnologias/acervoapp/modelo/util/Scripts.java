package br.ufpe.sabertecnologias.acervoapp.modelo.util;

import android.database.sqlite.SQLiteDatabase;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.AppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.Arquivo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.LogFeedback;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.LogFimSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.LogGrupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.LogInicioSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.LogItem;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.Metadado;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.SessaoControle;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.contracts.AcervoAppContract.Usuario;

public class Scripts {


	private static final String CREATE_CLAUSE = "CREATE TABLE ";
	private static final String CREATE_INDEX = "CREATE INDEX ";
	private static final String ON_CONNECTOR = " ON ";
	private static final String DROP_CLAUSE = "DROP TABLE ";
	private static final String IF_NOT_EXISTS_CLAUSE = "IF NOT EXISTS ";
	private static final String CONSTRAINT_PK = " PRIMARY KEY";
	private static final String CONSTRAINT_NULL = " NULL";
	private static final String CONSTRAINT_NOT = " NOT";
	private static final String CONSTRAINT_UNIQUE = " UNIQUE";
	private static final String CONSTRAINT_FK = "FOREIGN KEY";
	private static final String CONSTRAINT_REFERENCES = " REFERENCES ";
	private static final String CONSTRAINT_AUTOINCREMENT = " AUTOINCREMENT";
	private static final String CONSTRAINT_ON_DELETE_CASCADE = "  ON DELETE CASCADE";

	private static final String TYPE_INTEGER = " INTEGER";
	private static final String TYPE_REAL = " REAL";
	private static final String TYPE_TEXT = " TEXT";


	private final static String SCRIPT_USUARIO = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + Usuario.TABLE_NAME + "(" +
			Usuario._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			Usuario.COLUMN_USERNAME + TYPE_TEXT + CONSTRAINT_UNIQUE + ", " +
			Usuario.COLUMN_NAME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Usuario.COLUMN_EMAIL + TYPE_TEXT + CONSTRAINT_UNIQUE + ", " +
			Usuario.COLUMN_TOKEN + TYPE_TEXT + CONSTRAINT_UNIQUE + ");";

	private static final String SCRIPT_GRUPO = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + Grupo.TABLE_NAME + "(" + 
			Grupo._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			Grupo.COLUMN_ID_GRUPO + TYPE_TEXT + CONSTRAINT_UNIQUE + ", " +
			Grupo.COLUMN_NOME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL  +  ", " +
			Grupo.COLUMN_COR + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL + ");";

	private static final String SCRIPT_ITEM = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + Item.TABLE_NAME + "(" +
			Item._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			Item.COLUMN_CODIGO + TYPE_INTEGER + CONSTRAINT_UNIQUE + ", " +
			Item.COLUMN_DATA + TYPE_REAL + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Item.COLUMN_HANDLE + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Item.COLUMN_QT_ARQUIVOS + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Item.COLUMN_QT_METADADOS + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Item.COLUMN_REMOVIDO + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Item.COLUMN_ID_GRUPO + TYPE_INTEGER + ", " +
			Item.COLUMN_STATUS + TYPE_INTEGER + ", " +
			Item.COLUMN_IN_TRANSFERINDO + TYPE_INTEGER + ", " +
			CONSTRAINT_FK + "(" + Item.COLUMN_ID_GRUPO + ")" + CONSTRAINT_REFERENCES + Grupo.TABLE_NAME + "(" + Grupo._ID+ ")"+
			CONSTRAINT_ON_DELETE_CASCADE +
			");";

	private static final String SCRIPT_ARQUIVO = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + Arquivo.TABLE_NAME + "(" +
			Arquivo._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			Arquivo.COLUMN_CHECKSUM_MD5 + TYPE_TEXT + ", " +
			Arquivo.COLUMN_MIMETYPE + TYPE_TEXT + ", " +
			Arquivo.COLUMN_NOME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Arquivo.COLUMN_TAMANHO_BYTES + TYPE_TEXT + ", " + 
			Arquivo.COLUMN_URL + TYPE_TEXT + CONSTRAINT_UNIQUE + ", " + 
			Arquivo.COLUMN_ID_ITEM + TYPE_INTEGER + ", " + 
			CONSTRAINT_FK + "(" + Arquivo.COLUMN_ID_ITEM + ")" + CONSTRAINT_REFERENCES + Item.TABLE_NAME + "(" + Item._ID + ")" +
			CONSTRAINT_ON_DELETE_CASCADE +
			");";

	private static final String SCRIPT_METADADO = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + Metadado.TABLE_NAME + "(" +
			Metadado._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			Metadado.COLUMN_AUTORIDADE + TYPE_TEXT + ", " +
			Metadado.COLUMN_IDIOMA + TYPE_TEXT + ", " +
			Metadado.COLUMN_NOME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Metadado.COLUMN_VALOR + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			Metadado.COLUMN_ID_ITEM + TYPE_INTEGER + ", " +
			CONSTRAINT_FK + "(" + Metadado.COLUMN_ID_ITEM + ")" + CONSTRAINT_REFERENCES + Item.TABLE_NAME + "(" + Item._ID + ")" +
			CONSTRAINT_ON_DELETE_CASCADE +
			");";

	private static final String SCRIPT_LOG_INICIO_SESSAO = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + LogInicioSessao.TABLE_NAME + "(" +
			LogInicioSessao._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			LogInicioSessao.COLUMN_CONEXAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogInicioSessao.COLUMN_LOG + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			//LogInicioSessao.COLUMN_SESSAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogInicioSessao.COLUMN_TIMESTAMP_DATETIME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogInicioSessao.COLUMN_TIMESTAMP_TIMEZONE + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ");";


	private static final String SCRIPT_LOG_FIM_SESSAO = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + LogFimSessao.TABLE_NAME + "(" +
			LogFimSessao._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			LogFimSessao.COLUMN_FALHA + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFimSessao.COLUMN_LOG + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			//LogFimSessao.COLUMN_SESSAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFimSessao.COLUMN_TIMESTAMP_DATETIME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFimSessao.COLUMN_TIMESTAMP_TIMEZONE + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + "," +
			LogFimSessao.COLUMN_TIMESTAMP_DATETIME_START + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFimSessao.COLUMN_TIMESTAMP_TIMEZONE_START + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ");";

	private static final String SCRIPT_LOG_ITEM = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + LogItem.TABLE_NAME + "(" +
			LogItem._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			LogItem.COLUMN_ITEM + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogItem.COLUMN_ACAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogItem.COLUMN_LOG + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogItem.COLUMN_COMPLEMENTO + TYPE_TEXT + ", " +
			//LogItem.COLUMN_SESSAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogItem.COLUMN_TIMESTAMP_DATETIME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogItem.COLUMN_TIMESTAMP_TIMEZONE + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ");";

	private static final String SCRIPT_LOG_FEEDBACK = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + LogFeedback.TABLE_NAME + "(" +
			LogFeedback._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			LogFeedback.COLUMN_ACAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFeedback.COLUMN_LOG + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFeedback.COLUMN_TEXTO + TYPE_TEXT + ", " +
			//LogItem.COLUMN_SESSAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFeedback.COLUMN_TIMESTAMP_DATETIME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogFeedback.COLUMN_TIMESTAMP_TIMEZONE + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ");";

	private static final String SCRIPT_LOG_GRUPO = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + LogGrupo.TABLE_NAME + "(" +
			LogGrupo._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			LogGrupo.COLUMN_ACAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogGrupo.COLUMN_NOME + TYPE_TEXT + ", " +
			LogGrupo.COLUMN_UUID + TYPE_TEXT + ", " +
			LogGrupo.COLUMN_COR + TYPE_TEXT + ", " +
			//LogGrupo.COLUMN_SESSAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogGrupo.COLUMN_LOG + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			LogGrupo.COLUMN_TIMESTAMP_DATETIME + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL  + ", " +
			LogGrupo.COLUMN_TIMESTAMP_TIMEZONE + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL  + ");";

	private static final String SCRIPT_SESSAO_CONTROLE = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + SessaoControle.TABLE_NAME + "(" +
			SessaoControle._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			SessaoControle.COLUMN_NUMSESSAO + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			SessaoControle.COLUMN_STATUS + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ");";

	private static final String SCRIPT_INDEX_ITEM = CREATE_INDEX + Item.INDEX_NAME +  ON_CONNECTOR + Item.TABLE_NAME + "(" + Item.COLUMN_CODIGO + ");";
	private static final String SCRIPT_INDEX_ARQUIVO = CREATE_INDEX + Arquivo.INDEX_NAME + ON_CONNECTOR + Arquivo.TABLE_NAME + "(" + Arquivo.COLUMN_ID_ITEM + ");";
	private static final String SCRIPT_INDEX_METADADO = CREATE_INDEX + Metadado.INDEX_NAME + ON_CONNECTOR + Metadado.TABLE_NAME + "(" + Metadado.COLUMN_NOME + ", " + Metadado.COLUMN_ID_ITEM + ");";


	private static final String SCRIPT_APP_CONFIG = CREATE_CLAUSE + IF_NOT_EXISTS_CLAUSE + AppConfig.TABLE_NAME + "("+
			AppConfig._ID + TYPE_INTEGER + CONSTRAINT_PK + CONSTRAINT_AUTOINCREMENT + ", " +
			AppConfig.COLUMN_API_VERSION + TYPE_TEXT + CONSTRAINT_NOT + CONSTRAINT_NULL + ", " +
			AppConfig.COLUMN_FLAG_HAS_ATT + TYPE_INTEGER + ", " +
			AppConfig.COLUMN_FLAG_HAS_TUTORIAL+ TYPE_INTEGER + ", " +
			AppConfig.COLUMN_SETUP_TMP_MAX + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL  + "," +
			AppConfig.COLUMN_SETUP_INITIAL_TIME + TYPE_INTEGER + CONSTRAINT_NOT + CONSTRAINT_NULL  +
			");";

	private static final String SCRIPT_INSERT= "insert into %s(%s) values(%s);";


	private static final String SCRIPT_APP_CONFIG_INSERT_1_COLUMNS =  
			AppConfig.COLUMN_API_VERSION + ", " +
					AppConfig.COLUMN_FLAG_HAS_ATT+ ", " +
					AppConfig.COLUMN_FLAG_HAS_TUTORIAL +", "+
					AppConfig.COLUMN_SETUP_TMP_MAX +", "+
					AppConfig.COLUMN_SETUP_INITIAL_TIME;

	private static final String SCRIPT_APP_CONFIG_INSERT_1_VALUES = "'v1', 0, 1, 24, -1";

	private static final String SCRIPT_APP_CONFIG_INSERT_1 = String.format(SCRIPT_INSERT,
			AppConfig.TABLE_NAME,
			SCRIPT_APP_CONFIG_INSERT_1_COLUMNS,
			SCRIPT_APP_CONFIG_INSERT_1_VALUES
			);


	public static void executeScripts(SQLiteDatabase db, int oldVersion,int newVersion){

		if(newVersion != oldVersion){

		}

		for(int i=oldVersion+1; i<= newVersion; i++){
			switch(i){
			case 1:
				db.execSQL(SCRIPT_USUARIO);
				db.execSQL(SCRIPT_GRUPO);
				db.execSQL(SCRIPT_ITEM);
				db.execSQL(SCRIPT_ARQUIVO);
				db.execSQL(SCRIPT_METADADO);
				db.execSQL(SCRIPT_INDEX_ITEM);
				db.execSQL(SCRIPT_INDEX_ARQUIVO);
				db.execSQL(SCRIPT_INDEX_METADADO);
				db.execSQL(SCRIPT_LOG_INICIO_SESSAO);
				db.execSQL(SCRIPT_LOG_FIM_SESSAO);
				db.execSQL(SCRIPT_LOG_ITEM);
				db.execSQL(SCRIPT_LOG_FEEDBACK);
				db.execSQL(SCRIPT_LOG_GRUPO);
				db.execSQL(SCRIPT_SESSAO_CONTROLE);
				db.execSQL(SCRIPT_APP_CONFIG);
				db.execSQL(SCRIPT_APP_CONFIG_INSERT_1);
				break;
			case 2:
				break;
			case 3:
//				db.execSQL(SCRIPT_APP_CONFIG_UPDATE_1);
//				db.execSQL(SCRIPT_APP_CONFIG_UPDATE_2);
				break;
			}


		}
		db.execSQL("PRAGMA foreign_keys=ON");

	}


}
