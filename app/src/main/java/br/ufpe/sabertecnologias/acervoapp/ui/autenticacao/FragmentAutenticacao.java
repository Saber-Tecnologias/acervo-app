package br.ufpe.sabertecnologias.acervoapp.ui.autenticacao;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.NegocioAutenticacao;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;

public class FragmentAutenticacao extends Fragment {

	private View container_view;
	private ProgressBar progress;
	private CallbackAutenticacao callback;
	private Button bt_login;
	private ViewListener mListener;
	private AutenticacaoView mView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = (CallbackAutenticacao)activity;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (mView == null) {
			mListener = new AutenticacaoListener();
			mView = new AutenticacaoView(inflater, container, mListener);
		}
		return mView.getView();
	}

	class AutenticacaoListener extends ViewListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
				case R.id.bt_login:
					mView.showProgress();
					ExecutorListener.ExecutorResult executorResult = new ExecutorListener.ExecutorResult() {
						@Override
						public void onExecutorResult(Object obj) {
							Integer result = (Integer)obj;
							switch(result){
								case NegocioAutenticacao.RESULT_SUCESS:

									callback.iniciarSessao();
									break;
								case NegocioAutenticacao.RESULT_FAIL:
									// Toast.makeText(getActivity(), "Algo inesperado aconteceu, tente novamente!", Toast.LENGTH_LONG).show();
									mView.onFail();
									break;
								case NegocioAutenticacao.RESULT_USER_DIFFERENT:

									//TODO: Mostrar um dialog informando ao usuario que ele se autenticou
									// com uma conta diferente dá que já estava cadastrada
									// e por isso todos os dados antigos serão apagados.

									break;

							}

						}
					};

					String username  = mView.getUsername();
					String password = mView.getPassword();

					callback.login(username, password, new AsyncExecutor(executorResult));

					break;
				default:
					// ???
			}

		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		}
	}
}
