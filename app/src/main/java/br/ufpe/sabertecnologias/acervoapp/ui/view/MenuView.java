package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TagsController;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import jpttrindade.widget.tagview.Tag;

public class MenuView  extends BasicSingleView {
	private int selectedTipoID;

	public static enum TYPE{MEUACERVO, ACERVOREMOTO};

	public static final int ID_NOVO_GRUPO = 100;
	public static final int ID_MEUS_GRUPOS = 0;
	public static final int ID_TEXTO = 1;
	public static final int ID_AUDIO = 2;
	public static final int ID_VIDEO = 3;
	public static final int ID_IMAGEM = 4;
	public static final int ID_TRANSFERENCIAS = 5;

	private int selectedItem;

	private int totalTranferindo;

	private String[] TITULOS_;

	private static final int[] IMAGE_= {
			R.drawable.ic_meus_grupos,
			R.drawable.ic_filtro_texto,
			R.drawable.ic_filtro_audio,
			R.drawable.ic_filtro_video,
			R.drawable.ic_filtro_image,
			R.drawable.ic_filtro_transferindo};

	public static final int[] TAG_IMAGE_= {
			0,
			R.drawable.ic_filtro_texto_white,
			R.drawable.ic_filtro_audio_white,
			R.drawable.ic_filtro_video_white,
			R.drawable.ic_filtro_image_white,
			0};

	public static final boolean[] HAS_TAG_ = {false, true,  true, true, true, false};
	public static final boolean[] HAS_DIVIDER_ = {true, false, false, false, true, false};

	public static class MenuMeuAcervo {
		public static final int[] VISIBILIDADE_= {
				View.VISIBLE,
				View.VISIBLE,
				View.VISIBLE,
				View.VISIBLE,
				View.VISIBLE,
				View.VISIBLE,
		};
	}

	public static class MenuAcervoRemoto {
		public static final int[] VISIBILIDADE_= {
				View.GONE,
				View.VISIBLE,
				View.VISIBLE,
				View.VISIBLE,
				View.VISIBLE,
				View.VISIBLE,
		};
	}

	private View[] mViews;
	private OnClickListener listener;
	private TextView tv_counter;

	public MenuView(LayoutInflater inflater, ViewGroup container,
					ViewListener listener) {
		super(inflater, container, listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		totalTranferindo = 0;
		View v = inflater.inflate(R.layout.menu_view, container, false);
		return v;
	}

	@Override
	protected void onViewCreated(View view) {}

	@Override
	public void setListenerOnViews(OnClickListener listener) {}

	public void setContent(TYPE type, final OnClickListener listener){
		this.listener = listener;
		TITULOS_ = getContext().getResources().getStringArray(R.array.menu_textos);
		switch (type) {
			case MEUACERVO:
				createMenuMeuAcervo();
				break;
			case ACERVOREMOTO:
				createMenuAcervoRemoto();
				break;
			default:
				break;
		}
		tv_counter = (TextView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_counter);
		tv_counter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tv_filtro = (TextView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_filtro);
				if(tv_filtro != null){
					listener.onClick(tv_filtro);
				}else {
					listener.onClick(mViews[ID_TRANSFERENCIAS].findViewById(R.id.iv_filtro));
				}
			}
		});
	}

	private void createMenuAcervoRemoto() {
		mViews  = new View[TITULOS_.length];
		ViewGroup container = (ViewGroup) getView();
		container.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for(int i=0 ; i < TITULOS_.length; i++){
			View v = inflater.inflate(R.layout.filtros_item, container, false);
			TextView tv = (TextView) v.findViewById(R.id.tv_filtro);
			ImageView iv = (ImageView) v.findViewById(R.id.iv_filtro);
			Tag tag = null;
			if(HAS_TAG_[i]){
				tag = new Tag(TITULOS_[i],
						Color.parseColor("#f2a32c"),
						TAG_IMAGE_[i], false);
				tag.setType(TagsController.TYPE_TIPO);
				tag.setID(i);
				v.setTag(tag);
			}

			if(tv!= null){
				tv.setText(TITULOS_[i]);
				iv.setImageResource(IMAGE_[i]);
				if(tag != null) {
					tv.setTag(tag);
					iv.setTag(tag);
				}else {
					tv.setTag(i);
					iv.setTag(i);
				}
				iv.setOnClickListener(listener);
				tv.setOnClickListener(listener);
			} else {
				iv.setImageResource(IMAGE_[i]);
				if(tag != null)
					iv.setTag(tag);
				else
					iv.setTag(i);
				iv.setOnClickListener(listener);
			}

			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView tv_filtro = (TextView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_filtro);
					if(tv_filtro != null){
						listener.onClick(tv_filtro);
					}else {
						listener.onClick(mViews[ID_TRANSFERENCIAS].findViewById(R.id.iv_filtro));
					}
				}
			});

			mViews[i] = v;
			mViews[i].setVisibility(MenuAcervoRemoto.VISIBILIDADE_[i]);
			container.addView(v);
			if(HAS_DIVIDER_[i] && MenuAcervoRemoto.VISIBILIDADE_[i] == View.VISIBLE ){
				container.addView(inflater.inflate(R.layout.separator, container, false));
			}
		}
	}

	private void createMenuMeuAcervo() {
		mViews  = new View[TITULOS_.length];
		ViewGroup container = (ViewGroup) getView();
		container.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for(int i=0 ; i < TITULOS_.length; i++){
			View v = inflater.inflate(R.layout.filtros_item, container, false);
			TextView tv = (TextView) v.findViewById(R.id.tv_filtro);
			ImageView iv = (ImageView) v.findViewById(R.id.iv_filtro);
			Tag tag = null;
			if(HAS_TAG_[i]){
				tag = new Tag(TITULOS_[i],
						Color.parseColor("#f2a32c"),
						TAG_IMAGE_[i], false);
				tag.setType(TagsController.TYPE_TIPO);
				tag.setID(i);
				v.setTag(tag);
			}
			iv.setImageResource(IMAGE_[i]);
			iv.setOnClickListener(listener);
			if (tv != null){
				tv.setText(TITULOS_[i]);
				if(tag != null) {
					tv.setTag(tag);
					iv.setTag(tag);
				}else {
					tv.setTag(i);
					iv.setTag(i);
				}
				tv.setOnClickListener(listener);
			} else {
				if (tag != null){
					iv.setTag(tag);
				}else{
					iv.setTag(i);
				}
			}
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView tv_filtro = (TextView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_filtro);
					if(tv_filtro != null){
						listener.onClick(tv_filtro);
					}else {
						listener.onClick(mViews[ID_TRANSFERENCIAS].findViewById(R.id.iv_filtro));
					}
				}
			});
			mViews[i] = v;
			mViews[i].setVisibility(MenuMeuAcervo.VISIBILIDADE_[i]);
			container.addView(v);
			if(HAS_DIVIDER_[i] && MenuMeuAcervo.VISIBILIDADE_[i] == View.VISIBLE ){
				container.addView(inflater.inflate(R.layout.separator, container, false));
			}
		}
	}

	public Tag selectFiltro(int id) {

		Tag old_tag = selectedTipoID != 0 ? (Tag)mViews[selectedTipoID].getTag() : null;
		switch (id) {
			case ID_TRANSFERENCIAS:
				mViews[ID_MEUS_GRUPOS].setSelected(false);
				mViews[ID_TRANSFERENCIAS].setSelected(true);
				selectedItem = ID_TRANSFERENCIAS;
				break;
			case ID_MEUS_GRUPOS:
				mViews[ID_MEUS_GRUPOS].setSelected(true);
				mViews[ID_TRANSFERENCIAS].setSelected(false);
				selectedItem = ID_MEUS_GRUPOS;
				break;
			case ID_TEXTO:
				mViews[ID_TEXTO].setSelected(true);
				mViews[ID_AUDIO].setSelected(false);
				mViews[ID_VIDEO].setSelected(false);
				mViews[ID_IMAGEM].setSelected(false);
				selectedTipoID = ID_TEXTO;
				break;
			case ID_AUDIO:
				mViews[ID_TEXTO].setSelected(false);
				mViews[ID_AUDIO].setSelected(true);
				mViews[ID_VIDEO].setSelected(false);
				mViews[ID_IMAGEM].setSelected(false);
				selectedTipoID = ID_AUDIO;
				break;
			case ID_VIDEO:
				mViews[ID_TEXTO].setSelected(false);
				mViews[ID_AUDIO].setSelected(false);
				mViews[ID_VIDEO].setSelected(true);
				mViews[ID_IMAGEM].setSelected(false);
				selectedTipoID = ID_VIDEO;
				break;
			case ID_IMAGEM:
				mViews[ID_TEXTO].setSelected(false);
				mViews[ID_AUDIO].setSelected(false);
				mViews[ID_VIDEO].setSelected(false);
				mViews[ID_IMAGEM].setSelected(true);
				selectedTipoID = ID_IMAGEM;
				break;
			default:
				mViews[id].setSelected(true);
				break;
		}
		return old_tag;
	}

	public Tag deselectFiltro(int id) {
		if(id != -1){
			//Desseleciona a view especifica;
			mViews[id].setSelected(false);
			switch (id){
				case ID_TEXTO:
				case ID_AUDIO:
				case ID_VIDEO:
				case ID_IMAGEM:
					selectedTipoID = 0;
					break;
				case ID_TRANSFERENCIAS:
					selectedItem = -1;
					break;
			}
			return (Tag) mViews[id].getTag();
		} else {
			//Desseleciona a view que estava selecionada
			if(selectedItem!=-1) {
				mViews[selectedItem].setSelected(false);
				switch (id) {
					case ID_TEXTO:
					case ID_AUDIO:
					case ID_VIDEO:
					case ID_IMAGEM:
						selectedTipoID = 0;
						break;
					case ID_TRANSFERENCIAS:
						selectedItem = -1;
						break;
				}
				return (Tag) mViews[selectedItem].getTag();
			}else {
				return null;
			}
		}
	}

	public int getSelectedFiltro() {
		return selectedItem;
	}

	public void addTranferencia(){
		ImageView iv_filtro = (ImageView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.iv_filtro);
		TextView tv_filtro = (TextView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_filtro);
		if(totalTranferindo >= 0){
			tv_counter.setVisibility(View.VISIBLE);
			iv_filtro.setVisibility(View.GONE);
			if(tv_filtro != null){
				tv_filtro.setText(R.string.filtro_transferindo);
			}
		}
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.qtd_transferencias_anim);
		totalTranferindo++;
		tv_counter.setText(totalTranferindo+"");
		tv_counter.startAnimation(anim);
	}

	public void setTransferencias(int transferencias) {
		totalTranferindo = transferencias;
		TextView tv_filtro = (TextView)mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_filtro);
		TextView tv_counter = (TextView)mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_counter);
		ImageView iv_filtro = (ImageView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.iv_filtro);
		if(totalTranferindo == 0){
			tv_counter.setVisibility(View.GONE);
			iv_filtro.setVisibility(View.VISIBLE);
			if(tv_filtro!= null) {
				tv_filtro.setText(R.string.filtro_transferidos);
			}
		} else {
			tv_counter.setVisibility(View.VISIBLE);
			iv_filtro.setVisibility(View.GONE);
			tv_counter.setText(totalTranferindo + "");
			if(tv_filtro!= null) {
				tv_filtro.setText(R.string.filtro_transferindo);
			}
		}
	}

	public void finishTransferencia(){
		TextView tv_counter = (TextView)mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_counter);
		ImageView iv_filtro = (ImageView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.iv_filtro);
		TextView tv_filtro = (TextView) mViews[ID_TRANSFERENCIAS].findViewById(R.id.tv_filtro);
		totalTranferindo--;
		if(totalTranferindo == 0){
			tv_counter.setVisibility(View.GONE);
			iv_filtro.setVisibility(View.VISIBLE);
			if(tv_filtro != null){
				tv_filtro.setText(R.string.filtro_transferidos);
			}
		}
		tv_counter.setText(totalTranferindo+"");
	}
}
