package br.ufpe.sabertecnologias.acervoapp.modelo;

public class Executor extends IExecutor {

	
	public Executor(ExecutorListener.ExecutorResult executorResult) {
		super(executorResult);
	}

	@Override
	public Object execute(Task task) {

		Object obj = task.run(this.executorListener);
		task.postRun(executorListener, obj);

		return obj;
	}
	
	
	
	
	
}
