package br.ufpe.sabertecnologias.acervoapp.ui.basics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;


public abstract class BasicListView extends BasicView{

	public BasicListView(LayoutInflater inflater, ViewGroup container,
			ViewListener listener) {
		super(inflater, container, listener);
	}	
	
	@Override
	void createView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
		mView = onCreateView(inflater, container);
		viewCreated(mView, listener);
	}
	
	
	@Override
	void viewCreated(View view, ViewListener listener) {
		onSetAdapter();
		setListenerOnListViews(listener);
		onViewCreated(view);
	}

	protected abstract void onSetAdapter();

	@Override
	void setListenerOnViews(ViewListener viewListener) {
		setListenerOnListViews(viewListener);
	}
	
	public abstract void setListenerOnListViews(OnItemClickListener viewListener);
}

