package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DetalhesItemController;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;

public class DetalhesItemView extends BasicSingleView {

	private View cor;
	private TextView tv_titulo, tv_descricao, tv_tipo, tv_tamanho, tv_instituicao;
	private AppCompatButton bt_abrir, bt_excluir;
	private Item item;
	private OnClickListener mListener;
	private DetalhesItemController.DetalhesContext detalhesContext;


	public DetalhesItemView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
		super(inflater, container, listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View v = inflater.inflate(R.layout.detalhesitem_view, container, false);
		tv_titulo = (TextView) v.findViewById(R.id.tv_titulo);
		tv_descricao = (TextView) v.findViewById(R.id.tv_descricao);
		tv_tipo = (TextView) v.findViewById(R.id.tv_tipo);
		tv_instituicao = (TextView) v.findViewById(R.id.tv_instituicao);
		tv_tamanho = (TextView) v.findViewById(R.id.tv_tamanho);
		bt_abrir = (AppCompatButton) v.findViewById(R.id.tv_abrir);
		bt_excluir = (AppCompatButton) v.findViewById(R.id.bt_excluir);
		cor = v.findViewById(R.id.cor);
		return v;
	}

	@Override
	protected void onViewCreated(View view) {}

	
	@Override
	public void setListenerOnViews(OnClickListener listener) {
		mListener = listener;
	}

	public void setItem(Item item, int vCor, DetalhesItemController.DetalhesContext detalhesContext){
		this.item = item;
		this.detalhesContext = detalhesContext;
		tv_titulo.setText(item.getNome());
		tv_descricao.setText(Html.fromHtml("<i>"+item.getDescricao()+"</i>"));
		tv_tipo.setText(item.getType());
		tv_instituicao.setText(item.getInstituicoes());
		cor.setBackgroundColor(vCor);
		tv_tamanho.setText(UIUtils.tamanhoFormat(item.getTamanho()));
		bt_abrir.setOnClickListener(mListener);
		bt_abrir.setTag(item);
		bt_excluir.setOnClickListener(mListener);
		bt_excluir.setTag(item);
		updateBotao();
	}

	private void updateBotao() {
		if(item.getId() == 0){
			bt_excluir.setVisibility(View.GONE);
		} else {
			bt_excluir.setVisibility(View.VISIBLE);
		}
		ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{Color.parseColor("#f79674")});
		bt_excluir.setSupportBackgroundTintList(csl);
		switch (item.getStatus()) {
		case Item.FLAG_ITEM_INDISPONIVEL:
		case Item.FLAG_ITEM_DISPONIVEL:
			bt_abrir.setText(R.string.bt_transferir_item_text);
			bt_abrir.setTextColor(Color.WHITE);
			ColorStateList csl1 = new ColorStateList(new int[][]{new int[0]}, new int[]{Color.parseColor("#149b9e")});
			bt_abrir.setSupportBackgroundTintList(csl1);
			break;
		case Item.FLAG_ITEM_BAIXADO:
			bt_abrir.setText(R.string.bt_abrir_item_text);
			bt_abrir.setTextColor(Color.WHITE);
			ColorStateList csl2 = new ColorStateList(new int[][]{new int[0]}, new int[]{Color.parseColor("#8dbf67")});
			bt_abrir.setSupportBackgroundTintList(csl2);
			break;
		case Item.	FLAG_ITEM_ENFILEIRADO:
		case Item.FLAG_ITEM_BAIXANDO:
			bt_abrir.setText(R.string.progresso_transferindo);
			bt_abrir.setTextColor(Color.parseColor("#149b9e"));
			ColorStateList csl3 = new ColorStateList(new int[][]{new int[0]}, new int[]{ContextCompat.getColor(mContext,android.R.color.transparent)});
			bt_abrir.setSupportBackgroundTintList(csl3);
			bt_excluir.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	public void nofityItemUpdated(Item i) {
		updateBotao();
	}
}
