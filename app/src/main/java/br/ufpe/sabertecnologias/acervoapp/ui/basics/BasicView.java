package br.ufpe.sabertecnologias.acervoapp.ui.basics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract class BasicView {
	
	protected Context mContext;
	protected View mView;
	
	public BasicView(LayoutInflater inflater, ViewGroup container, ViewListener listener){
		mContext = container.getContext();
		createView(inflater, container, listener);
	}
	
	
	
	abstract void createView(LayoutInflater inflater, ViewGroup container, ViewListener listener);
	
	abstract void setListenerOnViews(ViewListener viewListener);
	
	abstract void viewCreated(View view, ViewListener listener);
	
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container);
	protected abstract void onViewCreated(View view); 

	public View getView(){
		return mView;	
	}
	
	public Context getContext(){
		return mContext;
	}
	
	
	
	
	
}
