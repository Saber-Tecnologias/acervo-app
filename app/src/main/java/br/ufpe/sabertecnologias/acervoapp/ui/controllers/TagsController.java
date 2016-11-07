package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.TipoTermoBusca;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.TagsCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.TagsView;
import jpttrindade.widget.tagview.Tag;
import jpttrindade.widget.tagview.TagView;
import jpttrindade.widget.tagview.TagView.OnTagClickListener;

public class TagsController extends Fragment implements OnTagClickListener {

	public static String TAG = "tagsController";

	public static final int TYPE_TIPO = 0;
	public static final int TYPE_TERMO = 1;

	public static final boolean TYPE_TERMO_EDITABLE = true;

	TagsView mView;
	TagsCallback callback;
	ViewListener listener;
	Facade facade;
	private ContextualType type;

	public static enum ContextualType{MEUACERVO, ACERVOREMOTO};


	public TagsController init(ContextualType type){
		this.type = type;
		setRetainInstance(true);
		return this;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		callback = (TagsCallback) context;
		facade = (Facade)context.getApplicationContext();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mView == null){
			listener = new TagsControllerListener();
			mView = new TagsView(inflater, container, listener);
			mView.setOnTagClickListener(this);
		}
		return mView.getView();
	}

	public boolean addTag(Tag tag){
		boolean error = mView.addTag(tag);
		if(!error){
			onAddTag(tag);
		} 
		return error;
	}

	private void onAddTag(Tag tag) {
		TipoTermoBusca tipo = null;
		switch (tag.type) {
		case TYPE_TIPO:
			tipo = TipoTermoBusca.TIPO;
			break;
		default:
			tipo = TipoTermoBusca.NORMAL;
			break;
		}
		switch (type){
			case MEUACERVO:
				facade.addTermoBuscaMeuAcervo(tag.text, tipo);
				break;
			case ACERVOREMOTO:
				facade.addTermoBuscaAcervoRemoto(tag.text, tipo);
				break;
		}
	}

	public void removeTag(Tag tag) {
		boolean isEmpty = mView.removeTag(tag);
		onRemoveTag(tag, isEmpty);
	}

	private void onRemoveTag(Tag tag, boolean isEmpty) {
		TipoTermoBusca tipo = null;
		switch (tag.type) {
		case TYPE_TIPO:
			tipo = TipoTermoBusca.TIPO;
			break;
		default:
			tipo = TipoTermoBusca.NORMAL;
			break;
		}
		switch (type){
			case MEUACERVO:
				facade.removerTermoBuscaMeuAcervo(tag.text, tipo);
				break;
			case ACERVOREMOTO:
				facade.removeTermo(tag.text, tipo);
				break;
		}
		if(isEmpty)
			callback.onEmptyTags();
	}
	
	public void removeAll(){
		switch (type){
			case MEUACERVO:
				facade.removeAllTermosMeuAcervo();
				break;
			case ACERVOREMOTO:
				facade.removeAllTermos();
				break;
		}
		callback.onEmptyTags();
		ArrayList<Tag> tags = mView.getAll();
		for(int i = tags.size()-1; i>=0; i--){
			Tag t = tags.get(i);
			callback.deselectFiltro(t);
			mView.removeTag(t);
		}
	}

	public ArrayList<Tag> getTags(){
		return mView.getAll();
	}

	public ArrayList<Tag> getTagsByTipo(int tipo){
		ArrayList<Tag> tags;
		if(mView != null) {
			tags = mView.getAll();
		}else{
			tags = new ArrayList<Tag>();
		}
		ArrayList<Tag> typedTags = new ArrayList<Tag>();
		for(Tag tag : tags){
			if(tag.type == tipo)
				typedTags.add(tag);
		}
		return typedTags;
	}

	@Override
	public void onTagClick(Tag tag, int position, int clickType) {
		switch (clickType) {
		case TagView.ONCLICK_REMOVE:
			if(position>=0){
				removeTag(tag);
				callback.deselectFiltro(tag);
			}
			break;
		case TagView.ONCLICK_EDIT:
			if(tag.editable){
				removeTag(tag);
				callback.editTag(tag);
			}
			break;
		default:
			break;
		}
	}

	class TagsControllerListener extends ViewListener{
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.bt_removeall:
				removeAll();
				break;
			default:
				break;
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		}
	}
}
