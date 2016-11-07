package br.ufpe.sabertecnologias.acervoapp.ui.autenticacao;

import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;

public interface CallbackAutenticacao {

	void iniciarSessao();
	void login(String username, String password, IExecutor executor);
}
