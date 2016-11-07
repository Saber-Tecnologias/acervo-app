package br.ufpe.sabertecnologias.acervoapp.modelo.negocio;

import android.content.Context;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Task;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.AppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioAppConfig;

public class NegocioAppConfig {
	
	private RepositorioAppConfig repositorio;
	
	private static NegocioAppConfig instance;

	public static NegocioAppConfig getInstance(Context context) {
		if(instance == null){
			instance = new NegocioAppConfig(context);
		}


		return instance;
	}

	private NegocioAppConfig(Context context) {
		repositorio = RepositorioAppConfig.getInstance(context);
		
	}
	
	
	public void getAppConfig(IExecutor executor){
		Task task = new Task(){

			@Override
			public Object run(ExecutorListener executorListener) {
				return repositorio.get();

			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener != null)
					executorListener.postResult(obj);
			}
			
		};
		
		executor.execute(task);
	}

	public void attAppConfig(IExecutor executor, final AppConfig appConfig) {
		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {
				repositorio.update(appConfig);
				return null;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener != null)
					executorListener.postResult("");
			}
			
		};
		
		executor.execute(task);
	}
}
