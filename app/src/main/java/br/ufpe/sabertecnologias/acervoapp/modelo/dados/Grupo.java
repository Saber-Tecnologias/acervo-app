package br.ufpe.sabertecnologias.acervoapp.modelo.dados;

import java.util.ArrayList;
import java.util.List;

public class Grupo {

	private int id;
	private String uuid;
	private String nome;
	private int cor;	
	private List<Item> itens;

	public Grupo(String nome, int cor) {
		this.nome = nome;
		this.cor = cor;
		this.itens = new ArrayList<Item>();
	}

	public Grupo(String nome, int cor, List<Item> itens) {
		this.nome = nome;
		this.cor = cor;
		this.itens = itens;
	}

	public Grupo(int id, String nome, int cor, List<Item> itens) {
		this(nome, cor, itens);
		this.id = id;
	}

	public int getID() {
		return id;
	}
	public String getNome() {
		return nome;
	}
	public int getCor() {
		return cor;
	}
	public List<Item> getItens() {
		return itens;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setCor(int cor) {
		this.cor = cor;
	}

	public String getUUID_grupo() {
		return uuid;
	}

	public void setId_grupo(String id_grupo) {
		this.uuid = id_grupo;
	}

	public String getCorHexa(){
		return String.format("#%06X", (0xFFFFFF & cor));
	}


}
