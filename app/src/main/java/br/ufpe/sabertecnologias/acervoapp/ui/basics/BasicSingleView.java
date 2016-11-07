package br.ufpe.sabertecnologias.acervoapp.ui.basics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


public abstract class BasicSingleView extends BasicView{

	
	public BasicSingleView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
		super(inflater, container, listener);
	}

	@Override
	void createView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
		mView = onCreateView(inflater, container);
		viewCreated(mView, listener);
	}
	
	@Override
	void viewCreated(View view, ViewListener listener) {
		setListenerOnViews(listener);
		onViewCreated(view);	
	}
	
	
	@Override
	void setListenerOnViews(ViewListener viewListener) {
		setListenerOnViews((OnClickListener)viewListener);
	}
	
	
	public abstract void setListenerOnViews(OnClickListener listener);



}
