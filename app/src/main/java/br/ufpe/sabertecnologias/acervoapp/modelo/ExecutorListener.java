package br.ufpe.sabertecnologias.acervoapp.modelo;

public class ExecutorListener {
	
	public interface ExecutorResult{
		public void onExecutorResult(Object obj);
	}
	
	ExecutorResult callback;
	
	public ExecutorListener(ExecutorResult callback) {
		this.callback = callback;
	}
	
	public void postResult(Object obj){
		if(callback != null)
			callback.onExecutorResult(obj);
	}
	
}
