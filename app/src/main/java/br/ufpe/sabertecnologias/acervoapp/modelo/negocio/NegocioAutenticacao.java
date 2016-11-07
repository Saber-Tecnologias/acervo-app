package br.ufpe.sabertecnologias.acervoapp.modelo.negocio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import java.util.Calendar;

import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Task;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.User;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.MockData;
import br.ufpe.sabertecnologias.acervoapp.ui.autenticacao.AuthSessionExpiredReceiver;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.ufpe.sabertecnologias.acervoapp.util.JsonConvert;


public class NegocioAutenticacao extends BaseNegocio{

	public static final long DEFAULT_ULTIMA_ATUALIZACAO = 946695600900l;

	public static final int RESULT_SUCESS = 1357;
	public static final int RESULT_FAIL = 7531;
	public static final int RESULT_USER_DIFFERENT = 3175;

	private final int AUTH_SESSION_EXPIRE_TIME_MINUTES = 60; //EM MINUTOS [1m - 60m]
	private final int AUTH_SESSION_EXPIRE_TIME_HOURS = 24 * 20; //EM HORAS [1h - Xh]
	private final String NEGOCIO_AUTENTICACAO_PREFERENCES = "negocio_autenticacao_preferences_name";
	private final String USUARIO_LOGADO = "hasUsuarioLogado";

	private SharedPreferences preferences;

	private AlarmManager alarme;

	private static NegocioAutenticacao singleton;

	public static NegocioAutenticacao getInstance(Context ctx) {
		if(singleton == null)
			singleton = new NegocioAutenticacao(ctx);
		
		return singleton;
	}
	
	private NegocioAutenticacao(Context ctx){
		super(ctx);
		this.preferences = ctx.getSharedPreferences(NEGOCIO_AUTENTICACAO_PREFERENCES, Context.MODE_PRIVATE);
	}

	public User getUsuario() {
		return usuario;
	}

	private int addUsuario(User newUser) {
		int result = RESULT_FAIL;

		User oldUser = usuario;

		if(oldUser == null){ //NAO TEM USUARIO CADASTRADO NO SISTEMA
			result = repositorioUser.insertTransaction(newUser) ? RESULT_SUCESS : RESULT_FAIL;
		} else { //JA TEM UM USUARIO CADASTRADO NO SISTEMA
			if(oldUser.getUsername().equals(newUser.getUsername())){
				result = repositorioUser.update(newUser) ? RESULT_SUCESS : RESULT_FAIL;
			} else{ /// CADASTRO DE UM USUÁRIO DIFERENTE DO JÁ EXISTENTE
				result = RESULT_USER_DIFFERENT;
			}
		}

		if (result == RESULT_SUCESS)  {
			preferences.edit().putBoolean(USUARIO_LOGADO, true).commit();
		}

		return result;
	}


	public boolean existeUsuarioLogado() {
		return preferences.getBoolean(USUARIO_LOGADO, false);
	}

	public void login(final String username, final String password, IExecutor executor){


		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener) {

				//TODO autenticar o usario no servidor de autenticacao e adicionar no banco o resultado da autetnicacao


				String mockUser = MockData.MOCK_USER;

				User user = JsonConvert.jsonToUser(mockUser);


				return addUsuario(user);
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				//TODO VERFICAR SE DEVE HABILITAR O ALARME DE SESSAO
				// habilitarAlarme();
				if (executorListener != null) {
					executorListener.postResult(obj);
				}
			}
		};

		executor.execute(task);
		//
	}

	public void logout() {
		preferences.edit().putBoolean(USUARIO_LOGADO, false).commit();
	}

	private void habilitarAlarme() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		alarme = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(mContext, AuthSessionExpiredReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		alarme.set(	AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + 
				(1000*60*AUTH_SESSION_EXPIRE_TIME_MINUTES*AUTH_SESSION_EXPIRE_TIME_HOURS),
				alarmIntent);

		DebugLog.d(this, "ALARME HABILITADO");
	}

}
