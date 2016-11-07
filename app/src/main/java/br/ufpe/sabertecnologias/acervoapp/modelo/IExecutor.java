package br.ufpe.sabertecnologias.acervoapp.modelo;

import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener.ExecutorResult;


public abstract class IExecutor{
	
	protected ExecutorListener executorListener;
	
	public IExecutor(ExecutorResult executorResult) {
		if(executorResult != null)
			executorListener = new ExecutorListener(executorResult);
	}	
	
	
	public abstract Object execute(Task task);
	
		
}
