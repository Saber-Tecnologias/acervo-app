package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import jpttrindade.widget.tagview.Tag;
import jpttrindade.widget.tagview.TagView;
import jpttrindade.widget.tagview.TagView.OnTagClickListener;

public class TagsView extends BasicSingleView {

	TagView mTagView;
	ImageButton bt_removeAll;

	private int mHeight;
	public TagsView(LayoutInflater inflater, ViewGroup container,
			ViewListener listener) {
		super(inflater, container, listener);
	}

	public void setOnTagClickListener(OnTagClickListener tagListener){
		mTagView.setOnTagClickListener(tagListener);
	}

	@Override
	public void setListenerOnViews(OnClickListener listener) {
		bt_removeAll.setOnClickListener(listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View v =  inflater.inflate(R.layout.tags_view, container, false);
		mTagView = (TagView) v.findViewById(R.id.tagview);
		bt_removeAll = (ImageButton) v.findViewById(R.id.bt_removeall);
		mHeight=v.getLayoutParams().height;
		v.getLayoutParams().height = 0;
		return v;
	}

	public boolean addTag(Tag newTag){
		if(getView().getLayoutParams().height == 0){
			getView().getLayoutParams().height = mHeight;
		}
		return mTagView.addTag(newTag);
	}

	public boolean removeTag(Tag tag){
		boolean empty = false;
		if(mTagView.getCount() == 1){
			getView().getLayoutParams().height = 0;
			empty = true;
		}
		mTagView.removeTag(tag);
		return empty;
	}

	@Override
	protected void onViewCreated(View view) {}

	public ArrayList<Tag> getAll() {
		return mTagView.getAll();
	}
}
