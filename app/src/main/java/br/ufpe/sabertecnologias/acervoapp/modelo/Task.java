package br.ufpe.sabertecnologias.acervoapp.modelo;

public interface Task{
	public Object run(ExecutorListener executorListener);
	public void postRun(ExecutorListener executorListener, Object obj);
}
