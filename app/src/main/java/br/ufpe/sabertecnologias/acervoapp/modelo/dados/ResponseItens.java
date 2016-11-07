package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import java.util.ArrayList;

public class ResponseItens {
	private ArrayList<Item> itens;
	private int qtdItem;
	private int qtdItensFiltrados;
	private int qtdItensRetornados;
	
	public ResponseItens(int qtdItem, int qtdItensFiltrados, int qtdItensRetornados, ArrayList<Item> itens) {
		this.itens = itens;
		this.qtdItem = qtdItem;
		this.qtdItensFiltrados = qtdItensFiltrados;
		this.qtdItensRetornados = qtdItensRetornados;
	}

	public ArrayList<Item> getItens() {
		return itens;
	}

	public int getQtdItem() {
		return qtdItem;
	}

	public int getQtdItensFiltrados() {
		return qtdItensFiltrados;
	}

	public int getQtdItensRetornados() {
		return qtdItensRetornados;
	}
	
	
}
