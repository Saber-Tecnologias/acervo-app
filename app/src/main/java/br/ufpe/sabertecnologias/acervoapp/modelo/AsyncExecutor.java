package br.ufpe.sabertecnologias.acervoapp.modelo;

import android.os.AsyncTask;


public class AsyncExecutor extends IExecutor {

	public AsyncExecutor(ExecutorListener.ExecutorResult executorResult) {
		super(executorResult);
	}

	@Override
	public Object execute(final Task task) {
		new AsyncTask<Void, Void, Object>(){

			@Override
			protected Object doInBackground(Void... params) {
				return task.run(executorListener);
			}
			
			
			protected void onPostExecute(Object result) {
				task.postRun(executorListener, result);
				
			}
			
		}.execute();
		
		
		
		return null;
	}

	
	
}
