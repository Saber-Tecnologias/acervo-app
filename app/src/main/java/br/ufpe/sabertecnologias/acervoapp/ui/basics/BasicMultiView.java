package br.ufpe.sabertecnologias.acervoapp.ui.basics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

public  abstract class BasicMultiView extends BasicView {

	public BasicMultiView(LayoutInflater inflater, ViewGroup container,
			ViewListener listener) {
		super(inflater, container, listener);
	}
	
	@Override
	void createView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
		mView = onCreateView(inflater, container);
		viewCreated(mView, listener);
	}
	
	@Override
	void viewCreated(View view, ViewListener listener){
		onSetAdapters();
		setListenerOnViews(listener);
		onViewCreated(view);
		
	}
	
	protected abstract void onSetAdapters();

	@Override
	void setListenerOnViews(ViewListener viewListener) {
		setListenerOnViews((OnClickListener) viewListener);
		setListenerOnViews((OnItemClickListener)viewListener);
		
	}

	public abstract void setListenerOnViews(OnClickListener listener);
	public abstract void setListenerOnViews(OnItemClickListener listener);

}
